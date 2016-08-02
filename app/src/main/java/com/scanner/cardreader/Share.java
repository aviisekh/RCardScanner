package com.scanner.cardreader;

import android.graphics.Bitmap;

/**
 * Created by anush on 7/20/2016.
 */

public class Share {
    public Bitmap bitmap;
    public int startX;
    public int startY;
    public int endHeight;
    public int endWidth;
    public boolean lastThread = false;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Share(Bitmap bitmap, int startX, int endHeight) {
        this.bitmap = bitmap;
        this.startX = startX;
        this.endHeight = endHeight;
    }

    public Share(Bitmap bitmap, int startX, int endHeight, boolean lastThread) {
        this.bitmap = bitmap;
        this.startX = startX;
        this.endHeight = endHeight;
        this.lastThread = lastThread;
    }

    public Share(Bitmap bitmap, int startX, int startY, int endWidth, int endHeight) {
        this.bitmap = bitmap;
        this.startX = startX;
        this.startY = startY;
        this.endHeight = endHeight;
        this.endWidth = endWidth;
    }

    public Share(Bitmap bitmap, int startX, int startY, int endWidth, int endHeight, boolean lastThread) {
        this.bitmap = bitmap;
        this.startX = startX;
        this.startY = startY;
        this.endHeight = endHeight;
        this.endWidth = endWidth;
        this.lastThread = lastThread;
    }
}
