package com.scanner.cardreader;

import android.graphics.Bitmap;

/**
 * Created by anush on 6/21/2016.
 */

public class Thresholding {

    Bitmap grayscaleBitmap;

    public Thresholding(Bitmap sourceImage){
        this.grayscaleBitmap=sourceImage;
    }

    public Bitmap doThresholding() {
        Bitmap resultBitMap;
        //test
        resultBitMap = grayscaleBitmap.copy(Bitmap.Config.RGB_565, true);
        final int width = grayscaleBitmap.getWidth();
        final int height = grayscaleBitmap.getHeight();


        int pixel1, pixel2, pixel3, pixel4, A, R;
        int[] pixels;
        pixels = new int[width * height];

        grayscaleBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int size = width * height;
        int s = width / 8;
        int s2 = s >> 1;
        double t = 0.15;
        double it = 1.0 - t;
        int[] integral = new int[size];
        int[] threshold = new int[size];
        int i, j, diff, x1, y1, x2, y2, ind1, ind2, ind3;
        int sum = 0;
        int ind = 0;
        while (ind < size) {
            sum += pixels[ind] & 0xFF;
            integral[ind] = sum;
            ind += width;
        }
        x1 = 0;

        for (i = 1; i < width; ++i) {
            sum = 0;
            ind = i;
            ind3 = ind - s2;
            if (i > s) {
                x1 = i - s;
            }
            diff = i - x1;
            for (j = 0; j < height; ++j) {
                sum += pixels[ind] & 0xFF;
                integral[ind] = integral[(int) (ind - 1)] + sum;
                ind += width;
                if (i < s2) continue;
                if (j < s2) continue;

                y1 = (j < s ? 0 : j - s);
                ind1 = y1 * width;
                ind2 = j * width;

                if (((pixels[ind3] & 0xFF) * (diff * (j - y1))) < ((integral[(int) (ind2 + i)] - integral[(int) (ind1 + i)] - integral[(int) (ind2 + x1)] + integral[(int) (ind1 + x1)]) * it)) {
                    threshold[ind3] = 0x00;
                } else {
                    threshold[ind3] = 0xFFFFFF;
                }
                ind3 += width;
            }
        }


        y1 = 0;
        for (j = 0; j < height; ++j) {
            i = 0;
            y2 = height - 1;
            if (j < height - s2) {
                i = width - s2;
                y2 = j + s2;
            }

            ind = j * width + i;
            if (j > s2) {
                y1 = j - s2;
            }
            ind1 = y1 * width;
            ind2 = y2 * width;
            diff = y2 - y1;

            for (; i < width; ++i, ++ind) {
                x1 = (i < s2 ? 0 : i - s2);
                x2 = i + s2;

                //check border
                if (x2 >= width) {
                    x2 = width - 1;
                }

                if (((pixels[ind] & 0xFF) * ((x2 - x1) * diff)) < ((integral[(int) (ind2 + x2)] - integral[(int) (ind1 + x2)] - integral[(int) (ind2 + x1)] + integral[(int) (ind1 + x1)]) * it)) {
                    threshold[ind] = 0x00;
                } else {
                    threshold[ind] = 0xFFFFFF;
                }
            }


        }
        resultBitMap.setPixels(threshold, 0, width, 0, 0, width, height);

        return resultBitMap;


    }
}
