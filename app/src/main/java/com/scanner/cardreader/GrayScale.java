package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by anush on 6/21/2016.
 */

public class GrayScale {

    Bitmap bmSource;
    DoGray someDoGray;


    public GrayScale(Bitmap sourceImage) {
        bmSource = sourceImage;
    }

/*    public GrayScale(Bitmap bmSource, DoGray someDoGray) {
        this(bmSource);
        this.someDoGray = someDoGray;
    }*/

    public Bitmap doGrayScale() {
        //ITU-R recommendation value
        final double GS_RED = 0.299;
        final double GS_GREEN = 0.587;
        final double GS_BLUE = 0.114;

        Bitmap bmDisplay = Bitmap.createBitmap(bmSource.getWidth(), bmSource.getHeight(), bmSource.getConfig());

        int A, R, G, B;
        int pixel;

        //image size
        int width = bmSource.getWidth();
        int height = bmSource.getHeight();

        Log.d("pixel value", Integer.toString(bmSource.getPixel(50, 50)));

        //scan pixel
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = bmSource.getPixel(x, y);

                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
                bmDisplay.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        //someDoGray.doGrayScaling(bmDisplay);

        return bmDisplay;
    }


}
