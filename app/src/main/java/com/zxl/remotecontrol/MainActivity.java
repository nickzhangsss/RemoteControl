package com.zxl.remotecontrol;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;


public class MainActivity extends Activity {

    float mLastTouchX = 0;
    float mLastTouchY = 0;
    long mLastTouchTime = 0;
    long mLastClickTime = 0;
    boolean wasClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
