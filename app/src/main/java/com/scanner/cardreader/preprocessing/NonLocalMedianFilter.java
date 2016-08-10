package com.scanner.cardreader.preprocessing;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.scanner.cardreader.interfaces.MedianFilter;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by anush on 7/20/2016.
 */

public class NonLocalMedianFilter implements MedianFilter {
    private int frameSize = 1;
    private Bitmap resultBitmap;
    private int width;
    private int height;
    int xMin;
    int xMax;
    int yMin;
    int ymax;


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
        if (noOfPixels % 2 == 1)
            return framePixels[noOfPixels / 2];
        else
            return ((framePixels[noOfPixels / 2] + framePixels[noOfPixels / 2 - 1]) / 2);
    }


    private void calculateFrameEdges(int x, int y) {
        xMin = x - frameSize;
        xMax = x + frameSize;
        yMin = y - frameSize;
        ymax = y + frameSize;
    }

    private void fixFrameEdges(int width, int height){
        //special edge cases
        if (xMin < 0)
            xMin = 0;
        if (xMax > (width - 1))

            xMax = width - 1;
        if (yMin < 0)
            yMin = 0;
        if (ymax > (height - 1))
            ymax = height - 1;
    }

    public ArrayList<Integer> getFramePixels(Bitmap bitmap, int x, int y) {
        ArrayList<Integer> framePixels = new ArrayList<Integer>();
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        calculateFrameEdges(x, y);
        fixFrameEdges(width,height);
        //the actual number of pixels to be considered
        int noOfPixelsFrame = (xMax - xMin + 1) * (ymax - yMin + 1);
        int counter = 0;
        String a = "";
        for (int i = xMin; i < xMax; i++) {
            a = "";
            for (int j = yMin; j < ymax; j++) {
                framePixels.add(counter, bitmap.getPixel(i, j));
//                a = a + bitmap.getPixel(i, j) + ",";
                counter++;
            }
        }
//        System.out.println(counter);
        return framePixels;
    }

    public Bitmap applyMedianFilter(final Bitmap sourceBitmap) {
//        resultBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        resultBitmap = sourceBitmap;
        width = sourceBitmap.getWidth();
        height = sourceBitmap.getHeight();
        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(cores * 16, cores * 16, 300L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

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
//                        find the median for each color
                        int R = calculateMedian(red);
                        int G = calculateMedian(green);
                        int B = calculateMedian(blue);
//                System.out.println("median"+R+","+G+","+B);
                        resultBitmap.setPixel(row, column, Color.rgb(R, G, B));
                    }
                }
            }
        });
        return resultBitmap;
    }
}






