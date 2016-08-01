package com.scanner.cardreader;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class ImageSurfaceView extends SurfaceView implements SurfaceHolder.Callback2
{
    public ImageSurfaceView(Context context, Camera camera) {
   super(context);


    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
