package com.scanner.cardreader.preprocessing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.scanner.cardreader.interfaces.Rotate;

/**
 * Created by anush on 7/19/2016.
 */

public class RotateNearestNeighbor implements Rotate {

    private double PI = 3.141592653589793D;


    private double angle;
    private double angleRad;
    private boolean keepSize;
    private int newWidth;
    private int newHeight;
    int originalILocation;
    int originalJLocation;
    double oldIradius;
    double oldJradius;
    double newIradius;
    double newJradius;

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

    public RotateNearestNeighbor(double angle) {
        this.angle = -angle;
        this.keepSize = false;
    }

    public RotateNearestNeighbor(double angle, boolean keepSize) {
        this.angle = -angle;
        this.keepSize = keepSize;
    }

    private double convertToRadian(double angle) {
        return this.angle * PI / 180.0D;
    }

    private double calculateCosine(double angle) {
        return Math.cos(convertToRadian(angle));
    }

    private double calculateSine(double angle) {
        return Math.sin(convertToRadian(angle));
    }

    private void reverseMapping(double calculatedI, double calculatedJ) {
        double angleCos = calculateCosine(angle);
        double angleSin = calculateSine(angle);
        originalILocation = (int) (angleCos * calculatedI + angleSin * calculatedJ + oldIradius);
        originalJLocation = (int) (-angleSin * calculatedI + angleCos * calculatedJ + oldJradius);
    }


    private void calculateOldRadius(int sourceImageHeight, int sourceImageWidth) {
        oldIradius = (double) (sourceImageHeight - 4) / 2.0D;
        oldJradius = (double) (sourceImageWidth - 4) / 2.0D;
    }

    private void calculateNewRadius() {
        newIradius = (double) (this.newHeight - 4) / 2.0D;
        newJradius = (double) (this.newWidth - 4) / 2.0D;
    }

    private boolean isReverseMappingValid(int sourceImageHeight, int sourceImageWidth) {
        if (originalILocation >= 0 && originalJLocation >= 0 && originalILocation < sourceImageHeight && originalJLocation < sourceImageWidth)
            return true;

        return false;
    }

    private int extractOriginalPixel(Bitmap sourceBitmap) {
        return sourceBitmap.getPixel(originalJLocation, originalILocation);
    }

    public Bitmap rotateImage(Bitmap sourceBitmap) {
        int sourceImageWidth = sourceBitmap.getWidth();
        int sourceImageHeight = sourceBitmap.getHeight();
        calculateOldRadius(sourceImageHeight, sourceImageWidth);
        this.calculateNewSize(sourceBitmap);
        Bitmap rotatedBitmap = Bitmap.createBitmap(this.newWidth, this.newHeight, Bitmap.Config.ARGB_8888);
        calculateNewRadius();
        double calculatedI = -newIradius;
        double calculatedJ;
        System.out.println("values mapped to previous frame");
        for (int height = 0; height < this.newHeight; ++height) {
            calculatedJ = -newJradius;
            for (int width = 0; width < this.newWidth; ++width) {
                reverseMapping(calculatedI, calculatedJ);
//                System.out.println("x:" + originalILocation + "<" + sourceBitmap.getWidth() + " ,y:"  + originalJLocation +"<" + sourceBitmap.getHeight());
                if (isReverseMappingValid(sourceImageHeight, sourceImageWidth)) {
                    rotatedBitmap.setPixel(width, height, extractOriginalPixel(sourceBitmap));
                } else {
                    rotatedBitmap.setPixel(width, height, Color.WHITE);
                }
                ++calculatedJ;
            }
            ++calculatedI;
        }
//        System.out.println("rotated sourceImageHeight:" + newHeight + " prev sourceImageHeight" + sourceImageHeight);
//        System.out.println("rotated sourceImageWidth:" + newWidth + " prev sourceImageWidth" + sourceImageWidth);
        return prepareRotatedBitmap(rotatedBitmap);
    }

    private Bitmap prepareRotatedBitmap(Bitmap rotatedBitmap) {
        Bitmap backgroundBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        backgroundBitmap.eraseColor(Color.WHITE);
        Bitmap resultBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        Canvas resultCanvas = new Canvas(resultBitmap);
//        resultCanvas.drawBitmap(backgroundBitmap,0f,0f,null);
//        resultCanvas.drawBitmap(rotatedBitmap,newWidth,newHeight,null);
        Drawable backgroundDrawable = new BitmapDrawable(backgroundBitmap);
        Drawable rotatedImageDrawable = new BitmapDrawable(rotatedBitmap);
        backgroundDrawable.setBounds(0, 0, newWidth, newHeight);
        rotatedImageDrawable.setBounds(1, 1, newWidth - 1, newHeight - 1);
        backgroundDrawable.draw(resultCanvas);
        rotatedImageDrawable.draw(resultCanvas);
        return resultBitmap;
    }


    private int[] createPixelArray(int width, int height, Bitmap sourceImage) {
        int[] sourcePixels = new int[width * height];
        sourceImage.getPixels(sourcePixels, 0, width, 0, 0, width, height);
        return sourcePixels;
    }

    private void calculateNewSize(Bitmap sourceBitmap) {
        if (this.keepSize) {
            this.newWidth = sourceBitmap.getWidth();
            this.newHeight = sourceBitmap.getHeight();
        } else {
            double angleRad = -this.angle * 3.141592653589793D / 180.0D;
            double angleCos = Math.cos(angleRad);
            double angleSin = Math.sin(angleRad);
            double halfWidth = (double) sourceBitmap.getWidth() / 2.0D;
            double halfHeight = (double) sourceBitmap.getHeight() / 2.0D;

            double cx1 = halfWidth * angleCos;
            double cy1 = halfWidth * angleSin;
            double cx2 = halfWidth * angleCos - halfHeight * angleSin;
            double cy2 = halfWidth * angleSin + halfHeight * angleCos;
            double cx3 = -halfHeight * angleSin;
            double cy3 = halfHeight * angleCos;
            double cx4 = 0.0D;
            double cy4 = 0.0D;


            halfWidth = Math.max(Math.max(cx1, cx2), Math.max(cx3, cx4))
                    - Math.min(Math.min(cx1, cx2), Math.min(cx3, cx4));
            halfHeight = Math.max(Math.max(cy1, cy2), Math.max(cy3, cy4))
                    - Math.min(Math.min(cy1, cy2), Math.min(cy3, cy4));
            this.newWidth = (int) (halfWidth * 2.0D + 0.5D);
            this.newHeight = (int) (halfHeight * 2.0D + 0.5D);
        }
    }
}
