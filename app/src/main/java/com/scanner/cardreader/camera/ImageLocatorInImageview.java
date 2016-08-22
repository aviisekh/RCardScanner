package com.scanner.cardreader.camera;

import android.graphics.Rect;
import android.widget.ImageView;

import com.scanner.cardreader.interfaces.Coordinates;

/**
 * Created by aviisekh on 8/11/16.
 */

public class ImageLocatorInImageview implements Coordinates {
    Rect imageViewCoordinates = new Rect();

    public ImageLocatorInImageview(ImageView imageView){
        ImageParametersInImageview imageParametersInImageview = new ImageParametersInImageview(imageView);

        //Scaled dimensions in imageview
        final int scaledWidth = Math.round(imageParametersInImageview.getImageWidth() * imageParametersInImageview.getScaleX()); //Needs to make
        final int scaledHeight = Math.round(imageParametersInImageview.getImageHeight() * imageParametersInImageview.getScaleY());

        //Log.d("new","transx" + Float.toString(scaleX)+"transy"+ Float.toString(scaleY));
        imageViewCoordinates.left = (int) Math.max(imageParametersInImageview.getTransX(), 0);
        imageViewCoordinates.top = (int) Math.max(imageParametersInImageview.getTransY(), 0);
        imageViewCoordinates.right = Math.min(imageViewCoordinates.left + scaledWidth, imageView.getWidth());
        imageViewCoordinates.bottom= Math.min(imageViewCoordinates.top + scaledHeight, imageView.getHeight());

        //return imageViewCoordinates;

    }

    @Override
    public Rect getCoordinates() {
        return imageViewCoordinates;
    }


}
