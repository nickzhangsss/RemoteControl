package com.zxl.remotecontrol;

/**
 * Created by Xulong on 01-Apr-15.
 */
public class Command {

    private Object o;

    public Command(Object o) {
        this.o = o;
    }

    public boolean execute() {
        if (null != o) {
            new Thread(new CommandTransmissionRunnable(o)).start();
            return true;
        } else {
            return false;
        }
    }

}
