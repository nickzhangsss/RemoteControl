package com.zxl.remotecontrol;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Xulong on 29-Mar-15.
 */
public class CommandTransmissionRunnable implements Runnable {

    private Object o;

    private static final String IP = "192.168.0.5";
    private static final int PORT = 9695;

    private Socket socket = null;
    private OutputStream os = null;
    private ObjectOutputStream oos = null;

    public CommandTransmissionRunnable(Object o) {
        this.o = o;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(InetAddress.getByName(IP), PORT);
            os = socket.getOutputStream();
            oos = new ObjectOutputStream(os);
            oos.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != oos) {
                    oos.close();
                }
                if (null != os) {
                    os.close();
                }
                if (null != socket) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
