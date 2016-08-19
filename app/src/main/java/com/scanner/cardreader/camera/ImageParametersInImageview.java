package com.scanner.cardreader.camera;

import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by aviisekh on 8/19/16.
 */
public class ImageParametersInImageview {
    private static float scaleX;
    private static float scaleY;
    private static float transX;
    private static float transY;
    private static int imageWidth;
    private static int imageHeight;

    public ImageParametersInImageview(ImageView imageView)
    {
        Drawable drawable;
        drawable = imageView.getDrawable();

        final float[] matrixValues = new float[9];
        imageView.getImageMatrix().getValues(matrixValues);

        scaleX = matrixValues[Matrix.MSCALE_X];
        scaleY = matrixValues[Matrix.MSCALE_Y];
        transX = matrixValues[Matrix.MTRANS_X];
        transY = matrixValues[Matrix.MTRANS_Y];

        imageWidth = drawable.getIntrinsicWidth();
        imageHeight = drawable.getIntrinsicHeight();
    }

    public static float getScaleX()
    {
        return scaleX;
    }

    public static float getScaleY()    {
        return scaleY;
    }

    public static float getTransY()
    {
        return transY;
    }

    public static float getTransX()
    {
        return transX;
    }

    public static int getImageHeight()
    {
        return imageHeight;
    }

    public static int getImageWidth()
    {
        return imageWidth;
    }
}

