package com.scanner.cardreader.preprocessing;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.scanner.cardreader.interfaces.GrayScale;

/**
 * Created by anush on 6/21/2016.
 */

public class ITURGrayScale implements GrayScale {

    private final Bitmap sourceImageBitmap;
    private final int width;
    private final int height;
    //ITU-R recommendation value
    final double GS_RED = 0.299;
    final double GS_GREEN = 0.587;
    final double GS_BLUE = 0.114;

    public ITURGrayScale(Bitmap sourceImage) {
        sourceImageBitmap = sourceImage;
        width = sourceImage.getWidth();
        height = sourceImage.getHeight();

    }

    @Override
    public Bitmap grayScale() {
        Bitmap afterGrayScaleImage = Bitmap.createBitmap(sourceImageBitmap.getWidth(), sourceImageBitmap.getHeight(), sourceImageBitmap.getConfig());
        int A, R, G, B;
        int pixel;

        //scan pixel
        for (int row = 0; row < width; ++row) {
            for (int column = 0; column < height; ++column) {
                // get one pixel color
                pixel = sourceImageBitmap.getPixel(row, column);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
                afterGrayScaleImage.setPixel(row, column, Color.argb(A, R, G, B));
            }
        }
        return afterGrayScaleImage;
    }
}
