package com.zxl.remotecontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends Activity {

    float mLastTouchX = 0;
    float mLastTouchY = 0;
    long mLastTouchTime = 0;
    long mLastClickTime = 0;
    boolean wasClicked = false;
    private View view = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LayoutInflater li = LayoutInflater.from(this);
        view = li.inflate(R.layout.ip_dialog, null);

        new AlertDialog.Builder(this)
                .setTitle("Please enter IP address")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        EditText et = (EditText) view
                                .findViewById(R.id.editText_prompt);
                        CommandTransmissionRunnable.setIP(et.getText().toString());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                finish();
                            }
                        }).show();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int mActivePointerId = MotionEventCompat.getPointerId(event, 0);
        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {

                mLastTouchTime = System.currentTimeMillis();
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);

                mLastTouchX = x;
                mLastTouchY = y;

                // if click event just happened
                if (wasClicked && mLastTouchTime - mLastClickTime < 180) {
                    new Command(new Down()).execute();
                    wasClicked = false;
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(event, mActivePointerId);

                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);

                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                new Command(new Move(dx, dy)).execute();

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_UP:
            {
                // if the distance moved shorter than SENSITIVITY && keep a finger on the screen for less than 1 second
                // fire the click event
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(event, mActivePointerId);
                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;
                mLastClickTime = System.currentTimeMillis();
                final float SENSITIVITY = 30;
                if ((dx <= SENSITIVITY && dy <= SENSITIVITY) && (mLastClickTime - mLastTouchTime) < 200) {
                    new Command(new Click(mLastTouchX, mLastTouchY)).execute();
                    wasClicked = true;
                } else {
                    new Command(new Up()).execute();
                    new Command(new ScrollCancelled()).execute();
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                new Command(new Scroll()).execute();
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                if (System.currentTimeMillis() - mLastTouchTime < 200) {
                    new Command(new RightClick(mLastTouchX, mLastTouchY)).execute();
                } else {
                    new Command(new ScrollCancelled()).execute();
                }
                break;
            }
        }

        return super.onTouchEvent(event);
    }

}
