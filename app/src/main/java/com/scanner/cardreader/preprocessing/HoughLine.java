package com.scanner.cardreader.preprocessing;

/**
 * Created by anush on 7/19/2016.
 */

public class HoughLine implements Comparable {
    private double theta;
    private double radius;
    private int intensity;
    private double relativeIntensity;

    private final double MIN_THETA=0.7853981633974483D;
    private final double MAX_THETA=2.356194490192345D;

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


    public HoughLine(double theta, double radius, int intensity, double relativeIntensity) {
        this.theta = theta;
        this.radius = radius;
        this.intensity = intensity;
        this.relativeIntensity = relativeIntensity;
    }

    public int compareTo(Object houghLine) {
        HoughLine comparingHoughLine = (HoughLine) houghLine;
        return this.intensity > comparingHoughLine.intensity ? -1 : (this.intensity < comparingHoughLine.intensity ? 1 : 0);
    }
}
