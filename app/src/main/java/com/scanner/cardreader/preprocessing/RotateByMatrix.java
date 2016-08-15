package com.scanner.cardreader.preprocessing;


import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by anush on 7/18/2016.
 */

public class RotateByMatrix {
    private double angle;
    private boolean keepSize;
    private int newWidth;
    private int newHeight;


    public double getAngle() {
        return -this.angle;
    }

    public void setAngle(double angle) {
        this.angle = -angle;
    }

    public boolean isKeepSize() {
        return this.keepSize;
    }

    public void setKeepSize(boolean keepSize) {
        this.keepSize = keepSize;
    }


    public RotateByMatrix(int width, int height, double angle) {
        this.angle = angle;
        this.newWidth = width;
        this.newHeight = height;
    }

    public Bitmap applyInPlace(Bitmap fastBitmap) {
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate((float) angle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(fastBitmap, 0, 0,
                newWidth, newHeight, rotationMatrix, false);
        return rotatedBitmap;
    }

    static private int[] createPixelArray(int width, int height, Bitmap sourceImage) {
        int[] pixels = new int[width * height];
        sourceImage.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }


    private void CalculateNewSize(Bitmap fastBitmap) {
        if (this.keepSize) {
            this.newWidth = fastBitmap.getWidth();
            this.newHeight = fastBitmap.getHeight();
        } else {
            double angleRad = -this.angle * 3.141592653589793D / 180.0D;
            double angleCos = Math.cos(angleRad);
            double angleSin = Math.sin(angleRad);
            double halfWidth = (double) fastBitmap.getWidth() / 2.0D;
            double halfHeight = (double) fastBitmap.getHeight() / 2.0D;
            double cx1 = halfWidth * angleCos;
            double cy1 = halfWidth * angleSin;
            double cx2 = halfWidth * angleCos - halfHeight * angleSin;
            double cy2 = halfWidth * angleSin + halfHeight * angleCos;
            double cx3 = -halfHeight * angleSin;
            double cy3 = halfHeight * angleCos;
            double cx4 = 0.0D;
            double cy4 = 0.0D;
            halfWidth = Math.max(Math.max(cx1, cx2), Math.max(cx3, cx4)) - Math.min(Math.min(cx1, cx2), Math.min(cx3, cx4));
            halfHeight = Math.max(Math.max(cy1, cy2), Math.max(cy3, cy4)) - Math.min(Math.min(cy1, cy2), Math.min(cy3, cy4));
            this.newWidth = (int) (halfWidth * 2.0D + 0.5D);
            this.newHeight = (int) (halfHeight * 2.0D + 0.5D);
        }
    }
}
