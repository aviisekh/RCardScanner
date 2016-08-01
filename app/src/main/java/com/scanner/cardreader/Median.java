package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Arrays;

/**
 * Created by anush on 7/20/2016.
 */

public class Median {
    private int frameSize = 1;
    private Bitmap copy;
    private int width;
    private int height;

    public int getFrameSize() {
        return this.frameSize;
    }

    public void setFrameSize(int frameSize) {
        this.frameSize = Math.max(1, frameSize);
    }


    public Median(int frameSize) {
        this.setFrameSize(frameSize);
    }

    //sort the array, and return the median
    public int calculateMedian(int[] framePixels) {
//        System.out.println(framePixels.length);
        int noOfPixels = framePixels.length;
        //sort the array in increasing order

        Arrays.sort(framePixels);

        //if it's odd
        if (noOfPixels % 2 == 1)
            return framePixels[noOfPixels / 2];
        else
            return ((framePixels[noOfPixels / 2] + framePixels[noOfPixels / 2 - 1]) / 2);
    }

    public int[] getFramePixels(Bitmap bitmap, int x, int y) {
        //array must be initialized.
        int[] framePixels = new int[9];

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        int xmin = x - frameSize;
        int xmax = x + frameSize;
        int ymin = y - frameSize;
        int ymax = y + frameSize;

        //special edge cases
        if (xmin < 0)
            xmin = 0;
        if (xmax > (width - 1))
            xmax = width - 1;
        if (ymin < 0)
            ymin = 0;
        if (ymax > (height - 1))
            ymax = height - 1;
        //the actual number of pixels to be considered
        int noOfPixelsFrame = (xmax - xmin + 1) * (ymax - ymin + 1);


        int counter = 0;

        for (int i = xmin; i < xmax; i++) {
            counter=0;
            for (int j = ymin; j < ymax; j++) {
                framePixels[counter] = bitmap.getPixel(i, j);
                counter++;
            }
        }
        return framePixels;

    }


    public Bitmap applyInPlace(Bitmap sourceBitmap) {
        copy = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        width = sourceBitmap.getWidth();
        height = sourceBitmap.getHeight();
        //ThreadPoolExecutor tpe= new ThreadPoolExecutor();

//        copy2 = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true);


        for (int row = 0; row < width; row++) {
            for (int column = 0; column < height; column++) {

                int[] framePixels = getFramePixels(sourceBitmap, row, column);

                int []red= new int [framePixels.length];
                int []green = new int [framePixels.length];
                int []blue = new int [framePixels.length];
                String a= "";
                for (int i = 0; i < framePixels.length; i++) {
                  a= a+ framePixels[i]+",";

                    red[i] = Color.red( framePixels[i] );
                    green[i] = Color.green( framePixels[i] );
                    blue[i] = Color.blue( framePixels[i] );
                }

                System.out.println(a);
                //find the median for each color
                int R = calculateMedian(red);
                int G = calculateMedian(green);
                int B = calculateMedian(blue);

//                System.out.println("median"+R+","+G+","+B);
                copy.setPixel(row, column, Color.rgb(R,G,B));
            }
        }

        return copy;

    }

}


