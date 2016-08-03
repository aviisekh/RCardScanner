package com.scanner.cardreader.segmentation;

import android.graphics.Bitmap;
import java.util.ArrayList;

/**
 * Created by mandy on 8/1/16.
 */
public class BinaryArray {

    public static ArrayList<double[][]> CreateBinaryArray(ArrayList<Bitmap> comBitmaps) {
        ArrayList<double[][]> binaryArrays = new ArrayList<>();
        for (Bitmap comBitmap : comBitmaps) {
            double[][] binaryArray = new double[256][1];
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(comBitmap, 16, 16, true);
            int pixels[] = createPixelArray(scaledBitmap.getWidth(), scaledBitmap.getHeight(), scaledBitmap);
//            Log.d("size", String.valueOf(pixels.length));
            for (int i = 0; i < pixels.length; i++) {
                if (pixels[i] == -1) {
                    pixels[i] = 1;
                } else {
                    pixels[i] = 0;
                }
                binaryArray[i][0] = pixels[i];
            }
            binaryArrays.add(binaryArray);
        }
        return binaryArrays;
    }
    public static ArrayList<int[]> CreateBinaryArrayOneD(ArrayList<Bitmap> comBitmaps) {
        ArrayList<int[]> binaryArrays = new ArrayList<>();
        for (Bitmap comBitmap : comBitmaps) {
            int[] binaryArray = new int[256];
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(comBitmap, 16, 16, true);
            int pixels[] = createPixelArray(scaledBitmap.getWidth(), scaledBitmap.getHeight(), scaledBitmap);
//            Log.d("size", String.valueOf(pixels.length));
            for (int i = 0; i < pixels.length; i++) {
                if (pixels[i] == -1) {
                    pixels[i] = 1;
                } else {
                    pixels[i] = 0;
                }
                binaryArray[i] = pixels[i];
            }
            binaryArrays.add(binaryArray);
        }
        return binaryArrays;
    }






    static int[] createPixelArray(int width, int height, Bitmap bitmap) {

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;

    }

}
