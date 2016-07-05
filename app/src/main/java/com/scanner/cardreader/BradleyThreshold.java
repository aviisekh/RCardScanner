package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by anush on 6/21/2016.
 */

public class BradleyThreshold {

    final private float limit = 0.15F;
    private final int FRAME_SIZE = 8;
    private int width;
    private int height;
    private int[] pixels;


    public BradleyThreshold(Bitmap sourceImage) {
        //no alpha and mutable
        //this.afterThresholding = sourceImage.copy(Bitmap.Config.RGB_565, true);
        width = sourceImage.getWidth();
        height = sourceImage.getHeight();
        pixels = new int[width * height];
        sourceImage.getPixels(pixels, 0, width, 0, 0, width, height);

    }

    private int[] createIntegralImage(int[] pixels) {

        int sum = 0;
        int currentPixel;
        /*
        Log.d("masked except blue", String.valueOf(pixels[100] & 0xFF));
        Log.d("blue", String.valueOf(Color.blue(pixels[100])));
        Log.d("masked except green", String.valueOf(pixels[100] >> 8 & 0xFF));
        Log.d("green", String.valueOf(Color.green(pixels[100])));
        Log.d("masked except red", String.valueOf(pixels[100] >> 16 & 0xFF));
        Log.d("red", String.valueOf(Color.red(pixels[100])));
        Log.d("unmasked", String.valueOf(pixels[100]));*/
        int[] integralImage = new int[width * height];

        for (int i = 0; i < width; i++) {
            sum = 0;
            for (int j = 0; j < height; j++) {
                currentPixel = i + j * width;
                sum += pixels[currentPixel] & 0xFF;
                if (i == 0) {
                    integralImage[currentPixel] = sum;
                } else {
                    integralImage[currentPixel] = integralImage[currentPixel - 1] + sum;
                }
            }
        }
        return integralImage;
    }


    public Bitmap threshold() {

        Bitmap afterThresholding= Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int[] integralImage = createIntegralImage(pixels);
        //Bradley AdaptiveThresholding
        int currentPixel, countInFrame, sum = 0;


        int s;
        int s2;
        int x1, x2, y1, y2;

        s = width / FRAME_SIZE;
        s2 = s >> 1;

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                currentPixel = j * width + i;

                //check sxs region
                x1 = i - s2;
                x2 = i + s2;
                y1 = j - s2;
                y2 = j + s2;
                //check the border
                if (x1 < 0) x1 = 0;
                if (x2 >= width) x2 = width - 1;
                if (y1 < 0) y1 = 0;
                if (y2 >= height) y2 = height - 1;
                countInFrame = (x2 - x1) * (y2 - y1);

                sum = integralImage[y2 * width + x2] -
                        integralImage[y1 * width + x2] -
                        integralImage[y2 * width + x1] +
                        integralImage[y1 * width + x1];

                pixels[currentPixel] = ((pixels[currentPixel] & 0xFF) * countInFrame) < (sum * (1.0F - this.limit)) ? 0x00 : 0xFFFFFF;
                //pixels[currentPixel] = -16777216 | pixels[currentPixel]<< 16 | pixels[currentPixel] << 8 | pixels[currentPixel];
            }
        }
        afterThresholding.setPixels(pixels, 0, width, 0, 0, width, height);
        return afterThresholding;
    }
}
