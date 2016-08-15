package com.scanner.cardreader.camera;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by aviisekh on 8/11/16.
 */
public class ImageLocatorInImageview {

    public  static Rect getImageCoordinates(ImageView imView)
    {
        Rect imageCoordinates = new Rect();
        Drawable drawable = imView.getDrawable();

        Log.d("drawable bounds",drawable.getBounds().flattenToString());
        // Get image matrix values and place them in an array.
        final float[] matrixValues = new float[9];
        imView.getImageMatrix().getValues(matrixValues);

        // Extract the scale and translation values. Note, we currently do not handle any other transformations (e.g. skew).
        final float scaleX = matrixValues[Matrix.MSCALE_X];
        final float scaleY = matrixValues[Matrix.MSCALE_Y];
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        final int originalWidth = drawable.getIntrinsicWidth();
        final int originalHeight = drawable.getIntrinsicHeight();

        //Scaled dimensions in imageview
        final int scaledWidth = Math.round(originalWidth * scaleX);
        final int scaledHeight = Math.round(originalHeight * scaleY);

        //Log.d("new","transx" + Float.toString(scaleX)+"transy"+ Float.toString(scaleY));
        imageCoordinates.left = (int) Math.max(transX, 0);
        imageCoordinates.top = (int) Math.max(transY, 0);
        imageCoordinates.right = Math.min(imageCoordinates.left + scaledWidth, imView.getWidth());
        imageCoordinates.bottom= Math.min(imageCoordinates.top + scaledHeight, imView.getHeight());

        return imageCoordinates;


    }

}
