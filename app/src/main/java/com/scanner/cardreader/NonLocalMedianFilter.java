package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by anush on 7/20/2016.
 */

public class NonLocalMedianFilter {
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


    public NonLocalMedianFilter(int frameSize) {
        this.setFrameSize(frameSize);
    }

    //sort the array, and return the median
    public int calculateMedian(int[] framePixels) {
//        System.out.println(framePixels.length);
        int noOfPixels = framePixels.length;
        //sort the array in increasing order

//        Arrays.sort(framePixels,0,noOfPixels);


//        //sort the array in increasing order
//        for (int i = 0; i < noOfPixels; i++)
//            for (int j = i+1; j < noOfPixels; j++)
//                if (framePixels[i] > framePixels[j]) {
//                    int temp = framePixels[i];
//                    framePixels[i] = framePixels[j];
//                    framePixels[j] = temp;
//                }
        //if it's odd
        if (noOfPixels % 2 == 1)
            return framePixels[noOfPixels / 2];
        else
            return ((framePixels[noOfPixels / 2] + framePixels[noOfPixels / 2 - 1]) / 2);
    }

    public ArrayList<Integer> getFramePixels(Bitmap bitmap, int x, int y) {
        //array must be initialized.
        ArrayList<Integer> framePixels = new ArrayList<Integer>();

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
        String a = "";
        for (int i = xmin; i < xmax; i++) {
            a = "";
            for (int j = ymin; j < ymax; j++) {
                framePixels.add(counter, bitmap.getPixel(i, j));

//                a = a + bitmap.getPixel(i, j) + ",";
                counter++;
            }

        }

//        System.out.println(counter);
        return framePixels;

    }


    public Bitmap applyInPlace(final Bitmap sourceBitmap) {
//        copy = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        copy=sourceBitmap;
        width = sourceBitmap.getWidth();
        height = sourceBitmap.getHeight();

        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor= new ThreadPoolExecutor(cores*16,cores*16,200L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                for (int row = 0; row < width; row++) {
                    for (int column = 0; column < height; column++) {

                        ArrayList<Integer> framePixels = getFramePixels(sourceBitmap, row, column);

                        int[] red = new int[framePixels.size()];
                        int[] green = new int[framePixels.size()];
                        int[] blue = new int[framePixels.size()];

                        for (int i = 0; i < framePixels.size(); i++) {

                            red[i] = Color.red(framePixels.get(i));
                            green[i] = Color.green(framePixels.get(i));
                            blue[i] = Color.blue(framePixels.get(i));
                        }
                        //find the median for each color
                        int R = calculateMedian(red);
                        int G = calculateMedian(green);
                        int B = calculateMedian(blue);

//                System.out.println("median"+R+","+G+","+B);
                        copy.setPixel(row, column, Color.rgb(R, G, B));
                    }
                }
            }
        });
        return copy;

    }

}






