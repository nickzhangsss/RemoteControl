package com.zxl.remotecontrol;

import java.io.Serializable;

/**
 * Created by Xulong on 01-Apr-15.
 */
public class RightClick implements Serializable {

    private static final long serialVersionUID = 1L;
    private float x;
    private float y;

    public RightClick(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}
