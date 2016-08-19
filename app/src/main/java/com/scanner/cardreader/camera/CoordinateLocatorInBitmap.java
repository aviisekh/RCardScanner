package com.scanner.cardreader.camera;

import android.graphics.Rect;
import android.widget.ImageView;

import com.scanner.cardreader.interfaces.BitmapCoordinates;

/**
 * Created by aviisekh on 8/12/16.
 */


public class CoordinateLocatorInBitmap implements BitmapCoordinates {
    private  ImageParametersInImageview imageParametersInImageview;

    @Override
    public Rect getCoordinates(ImageView imageView, Rect clippingWindowCoordinates) {
         Rect bitmapCoordinates = new Rect();

        imageParametersInImageview = new ImageParametersInImageview(imageView);

        bitmapCoordinates.left = (int) Math.max((clippingWindowCoordinates.left - imageParametersInImageview.getTransX()) / imageParametersInImageview.getScaleX(), 0);  //Since Image is translated and scaled in Imageview
        bitmapCoordinates.right =(int) Math.min((clippingWindowCoordinates.right - imageParametersInImageview.getTransX()) / imageParametersInImageview.getScaleX(),imageParametersInImageview.getImageWidth());
        bitmapCoordinates.top = (int) Math.max((clippingWindowCoordinates.top - imageParametersInImageview.getTransY()) / imageParametersInImageview.getScaleY(), 0);
        bitmapCoordinates.bottom = (int) Math.min((clippingWindowCoordinates.bottom - imageParametersInImageview.getTransY()) / imageParametersInImageview.getScaleY(), imageParametersInImageview.getImageHeight());

        return bitmapCoordinates;
    }
}
