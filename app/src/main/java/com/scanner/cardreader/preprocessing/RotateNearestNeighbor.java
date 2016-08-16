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

    private double PI = Math.PI;
    private final double ANGLE_DIVIDER = 180.0D;
    private final int BOUNDRY_LIMIT = 4;
    private final int ROTATED_BOUNDRY_LIMIT = 1;
    private final int BACKGROUND_STARTX = 0;
    private final int BACKGROUND_STARTY = 0;
    private final int ROTATED_STARTX = 1;
    private final int ROTATED_STARTY = 1;
    private final double DIVIDER=2.0D;


    private double angle;
    private double angleRad;
    private int newWidth;
    private int newHeight;
    private int originalILocation;
    private int originalJLocation;
    private double oldIradius;
    private double oldJradius;
    private double newIradius;
    private double newJradius;

    public double getAngle() {
        return -this.angle;
    }

    public void setAngle(double angle) {
        this.angle = -angle;
    }


    public RotateNearestNeighbor(double angle) {
        this.angle = -angle;
    }

    public RotateNearestNeighbor(double angle, boolean keepSize) {
        this.angle = -angle;
    }

    private double convertToRadian(double angle) {
        return this.angle * PI / ANGLE_DIVIDER;
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
        oldIradius = (sourceImageHeight - BOUNDRY_LIMIT) / DIVIDER;
        oldJradius = (sourceImageWidth - BOUNDRY_LIMIT) / DIVIDER;
    }

    private void calculateNewRadius() {
        newIradius =(this.newHeight - BOUNDRY_LIMIT) /DIVIDER;
        newJradius =(this.newWidth - BOUNDRY_LIMIT) /DIVIDER;
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
        Drawable backgroundDrawable = new BitmapDrawable(backgroundBitmap);
        Drawable rotatedImageDrawable = new BitmapDrawable(rotatedBitmap);
        backgroundDrawable.setBounds(BACKGROUND_STARTX, BACKGROUND_STARTY, newWidth, newHeight);
        rotatedImageDrawable.setBounds(ROTATED_STARTX, ROTATED_STARTY, newWidth - ROTATED_BOUNDRY_LIMIT, newHeight - ROTATED_BOUNDRY_LIMIT);
        backgroundDrawable.draw(resultCanvas);
        rotatedImageDrawable.draw(resultCanvas);
        return resultBitmap;
    }

    private void calculateNewSize(Bitmap sourceBitmap) {
        this.newWidth = sourceBitmap.getWidth();
        this.newHeight = sourceBitmap.getHeight();
    }
}
