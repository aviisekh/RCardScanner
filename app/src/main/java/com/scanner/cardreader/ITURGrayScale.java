package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.graphics.Color;
<<<<<<< HEAD
=======
import android.util.Log;
>>>>>>> thresholding

/**
 * Created by anush on 6/21/2016.
 */

public class ITURGrayScale implements GrayScale {

<<<<<<< HEAD
    private final Bitmap sourceImageBitmap;
    private final int width;
    private final int height;

    public ITURGrayScale(Bitmap sourceImage) {
        sourceImageBitmap = sourceImage;
        width = sourceImage.getWidth();
        height = sourceImage.getHeight();
=======
    Bitmap bmSource;

    public ITURGrayScale(Bitmap sourceImage) {
        bmSource = sourceImage;
>>>>>>> thresholding
    }


    @Override
    public Bitmap grayScale() {
        //ITU-R recommendation value
        final double GS_RED = 0.299;
        final double GS_GREEN = 0.587;
        final double GS_BLUE = 0.114;

<<<<<<< HEAD
        Bitmap afterGrayScaleImage = Bitmap.createBitmap(sourceImageBitmap.getWidth(), sourceImageBitmap.getHeight(), sourceImageBitmap.getConfig());
=======
        Bitmap bmDisplay = Bitmap.createBitmap(bmSource.getWidth(), bmSource.getHeight(), bmSource.getConfig());

>>>>>>> thresholding
        int A, R, G, B;
        int pixel;

        //image size
<<<<<<< HEAD

        //Log.d("pixel value", Integer.toString(sourceImageBitmap.getPixel(50, 50)));

        //scan pixel
        for (int row = 0; row < width; ++row) {
            for (int column = 0; column < height; ++column) {
                // get one pixel color
                pixel = sourceImageBitmap.getPixel(row, column);
=======
        int width = bmSource.getWidth();
        int height = bmSource.getHeight();

        //Log.d("pixel value", Integer.toString(bmSource.getPixel(50, 50)));

        //scan pixel
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = bmSource.getPixel(x, y);
>>>>>>> thresholding

                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
<<<<<<< HEAD
                afterGrayScaleImage.setPixel(row, column, Color.argb(A, R, G, B));
            }
        }
        return afterGrayScaleImage;
    }

=======
                bmDisplay.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        //someDoGray.doGrayScaling(bmDisplay);
        return bmDisplay;
    }


>>>>>>> thresholding
}
