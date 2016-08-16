package com.scanner.cardreader.preprocessing;


import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by anush on 7/18/2016.
 */

public class RotateByMatrix {
    private double angle;
    private int newWidth;
    private int newHeight;


    public double getAngle() {
        return -this.angle;
    }

    public void setAngle(double angle) {
        this.angle = -angle;
    }

    public RotateByMatrix(int width, int height, double angle) {
        this.angle = angle;
        this.newWidth = width;
        this.newHeight = height;
    }

    public Bitmap applyInPlace(Bitmap sourceImage) {
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate((float) angle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(sourceImage, 0, 0,
                newWidth, newHeight, rotationMatrix, false);
        return rotatedBitmap;
    }

    static private int[] createPixelArray(int width, int height, Bitmap sourceImage) {
        int[] pixels = new int[width * height];
        sourceImage.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }


    private void CalculateNewSize(Bitmap sourceImage) {
        this.newWidth = sourceImage.getWidth();
        this.newHeight = sourceImage.getHeight();
    }
}
