package com.scanner.cardreader.camera;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.widget.ImageView;

/**
 * Created by aviisekh on 8/12/16.
 */
public class CroppingCoordinates {

    public static Rect getCroppingCoordinates(ImageView imview, Rect clippingWindowCoordinates)

    {
        Rect bitmapCoordinates= new Rect();
        Drawable drawable = imview.getDrawable();
        if (drawable == null || !(drawable instanceof BitmapDrawable)) {
            return null;
        }

        // Get image matrix values and place them in an array.
        final float[] matrixValues = new float[9];
        imview.getImageMatrix().getValues(matrixValues);

        // Extract the scale and translation values. Note, we currently do not handle any other transformations (e.g. skew).
        final float scaleX = matrixValues[Matrix.MSCALE_X];
        final float scaleY = matrixValues[Matrix.MSCALE_Y];
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        bitmapCoordinates.left = (int) Math.max((clippingWindowCoordinates.left - transX) / scaleX, 0);  //Since Image is translated and scaled in Imageview
        bitmapCoordinates.right =(int) Math.min((clippingWindowCoordinates.right - transX) / scaleX, drawable.getIntrinsicWidth());
        bitmapCoordinates.top = (int) Math.max((clippingWindowCoordinates.top - transY) / scaleY, 0);
        bitmapCoordinates.bottom = (int) Math.min((clippingWindowCoordinates.bottom - transY) / scaleY, drawable.getIntrinsicHeight());


        return bitmapCoordinates;
    }
}
