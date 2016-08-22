package com.scanner.cardreader.camera;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.widget.ImageView;

import com.scanner.cardreader.interfaces.Coordinates;

/**
 * Created by aviisekh on 8/12/16.
 */


public class CoordinateLocatorInBitmap implements Coordinates {
    private Rect bitmapCoordinates = new Rect();


    public CoordinateLocatorInBitmap(ImageView imageView, Rect clippingWindowCoordinates) {
        /*
        * Initializes the object to get the coordinates of the image when imageView and clippingWindow is available.
        * i.e. sets the imagecoordinate when we manually crop the image
        * */
        ImageParametersInImageview imageParametersInImageview = new ImageParametersInImageview(imageView);

        bitmapCoordinates.left = (int) Math.max((clippingWindowCoordinates.left - imageParametersInImageview.getTransX()) / imageParametersInImageview.getScaleX(), 0);  //Since Image is translated and scaled in Imageview
        bitmapCoordinates.right = (int) Math.min((clippingWindowCoordinates.right - imageParametersInImageview.getTransX()) / imageParametersInImageview.getScaleX(), imageParametersInImageview.getImageWidth());
        bitmapCoordinates.top = (int) Math.max((clippingWindowCoordinates.top - imageParametersInImageview.getTransY()) / imageParametersInImageview.getScaleY(), 0);
        bitmapCoordinates.bottom = (int) Math.min((clippingWindowCoordinates.bottom - imageParametersInImageview.getTransY()) / imageParametersInImageview.getScaleY(), imageParametersInImageview.getImageHeight());

    }

    public CoordinateLocatorInBitmap(Bitmap image, Rect cameraOverlayWindow) {
        /*
        * Initializes the object to get the coordinates of the image when bitmap image and coordinates of rectangle over
        * surfaceview is given.
        * i.e. sets the imagecoordinate automatically to get the image that is focused inside rectangle in camera
        * */
        bitmapCoordinates.left = (image.getWidth() * cameraOverlayWindow.left) / CameraOverlay.getParentWidth();
        bitmapCoordinates.right = (image.getWidth() * cameraOverlayWindow.right) / CameraOverlay.getParentWidth();
        bitmapCoordinates.top = (image.getHeight() * cameraOverlayWindow.top) / CameraOverlay.getParentHeight();
        bitmapCoordinates.bottom = (image.getHeight() * cameraOverlayWindow.bottom) / CameraOverlay.getParentHeight();

    }


    @Override
    public Rect getCoordinates() {
        return bitmapCoordinates;
    }
}
