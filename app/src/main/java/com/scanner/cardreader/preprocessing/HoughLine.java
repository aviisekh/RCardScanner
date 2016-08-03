package com.scanner.cardreader.preprocessing;

import android.graphics.Bitmap;

/**
 * Created by anush on 7/19/2016.
 */

public class HoughLine implements Comparable {
    private double theta;
    private double radius;
    private int intensity;
    private double relativeIntensity;

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double r) {
        this.radius = r;
    }

    public double getTheta() {
        return this.theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public int getIntensity() {
        return this.intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public double getRelativeIntensity() {
        return this.relativeIntensity;
    }

    public void setRelativeIntensity(double relativeIntensity) {
        this.relativeIntensity = relativeIntensity;
    }

    public HoughLine() {
    }

    public HoughLine(double theta, double radius, int intensity, double relativeIntensity) {
        this.theta = theta;
        this.radius = radius;
        this.intensity = intensity;
        this.relativeIntensity = relativeIntensity;
    }

    //TODO draw hough lines
    public void DrawLineGray(Bitmap fastBitmap, int gray) {

        int height = fastBitmap.getHeight();
        int width = fastBitmap.getWidth();
        int houghHeight = (int) (Math.sqrt(2.0D) * (double) Math.max(height, width)) / 2;
        float centerX = (float) (width / 2);
        float centerY = (float) (height / 2);
        double tsin = Math.sin(this.theta);
        double tcos = Math.cos(this.theta);
        int x;
        int y;
        if (this.theta >= 0.7853981633974483D && this.theta <= 2.356194490192345D) {
            for (x = 0; x < height; ++x) {
                y = (int) ((this.radius - (double) houghHeight - (double) ((float) x - centerX) * tcos) / tsin + (double) centerY);
                if (y < width && y >= 0) {
                    fastBitmap.setPixel(x, y, gray);
                }
            }
        } else {
            for (x = 0; x < width; ++x) {
                y = (int) ((this.radius - (double) houghHeight - (double) ((float) x - centerY) * tsin) / tcos + (double) centerX);
                if (y < height && y >= 0) {
                    fastBitmap.setPixel(y, x, gray);
                }
            }
        }

    }

    public int compareTo(Object o) {
        HoughLine hl = (HoughLine) o;
        return this.intensity > hl.intensity ? -1 : (this.intensity < hl.intensity ? 1 : 0);
    }
}
