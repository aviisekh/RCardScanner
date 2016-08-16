package com.scanner.cardreader.preprocessing;

import android.graphics.Bitmap;
import android.util.Log;

import com.scanner.cardreader.interfaces.SkewChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by anush on 7/19/2016.
 */

public class HoughLineSkewChecker implements SkewChecker {

    private static final int NO_OF_INTENSE_LINES =5 ;
    private static final double HOUGH_INTENSITY_LIMITER = 0.5D ;
    private static final int MIN_STEPS_PER_DEGREE=10;
    private final int MASKER = 255;
    private final int BORDER_COMPARATER = 128;
    private final double PI = 3.141592653589793D;
    private final double DOUBLE_MULTIPLIER = 2.0D;
    private final double ANGLE_NORMALIZER = 180.0D;
    private final int MINIMUM_HOUGHLINE_INTENSIY_FACTOR = 10;
    private final int DIVIDER=2;

    private final double minimumTheta = 0.0D;

    private double maxSkewToDetect = 90.0D;
    private int stepsPerDegree = 1;
    private int localPeakDistance = 4;

    int width;
    int height;
    private int houghHeight;
    private double thetaStep;
    private double[] sinMap;
    private double[] cosMap;
    private int[][] houghMap;
    HoughLine[] mostIntensiveLines;
    private int maxMapIntensity = 0;
    private List<HoughLine> lines = new ArrayList();
    double thetaAggregator = 0.0D;
    double relativeIntensityAggregator = 0.0D;

    public int getStepsPerDegree() {
        return this.stepsPerDegree;
    }

    public void setStepsPerDegree(int stepsPerDegree) {
        this.stepsPerDegree = Math.max(1, Math.min(MIN_STEPS_PER_DEGREE, stepsPerDegree));
    }

    public double getMaxSkewToDetect() {
        return this.maxSkewToDetect;
    }

    public void setMaxSkewToDetect(double maxSkewToDetect) {
        this.maxSkewToDetect = Math.max(0.0D, Math.min(45.0D, maxSkewToDetect));
    }

    public int getLocalPeakDistance() {
        return this.localPeakDistance;
    }

    public void setLocalPeakDistance(int localPeakDistance) {
        this.localPeakDistance = Math.max(1, Math.min(10, localPeakDistance));
    }

    public HoughLineSkewChecker() {
    }

    private int[] createPixelArray(int width, int height, Bitmap sourceImage) {
        int[] pixels = new int[width * height];
        sourceImage.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    private boolean isBorderPixel(int pixel, int belowPixel) {
//        System.out.println(Integer.toHexString(pixel).compareTo(Integer.toHexString(belowPixel)));
        return ((pixel & MASKER) < BORDER_COMPARATER && (belowPixel & MASKER) >= BORDER_COMPARATER);
    }


    private void buildHoughMap(int skewAngle, int houghWidth, int halfHoughWidth, int hls) {
        for (int theta = 0; theta < this.houghHeight; ++theta) {
            int sumIntensity = (int) (this.cosMap[theta] * (double) skewAngle - this.sinMap[theta] * (double) hls) + halfHoughWidth;
            if (sumIntensity >= 0 && sumIntensity < houghWidth) {
                ++this.houghMap[theta][sumIntensity];
            }
        }
    }


    private int getMaxMapIntensity(int hls, int skewAngle, int houghWidth) {
        int maxMapIntensity = 0;
        for (hls = 0; hls < this.houghHeight; ++hls) {
            for (skewAngle = 0; skewAngle < houghWidth; ++skewAngle) {
//                System.out.println("houghmap("+hls+","+skewAngle+")="+ this.houghMap[hls][skewAngle]);
                if (this.houghMap[hls][skewAngle] > this.maxMapIntensity) {
                    maxMapIntensity = this.houghMap[hls][skewAngle];
                }
            }
        }
        return maxMapIntensity;
    }


    private void aggregateHoughValues(int noOfItntensiveLines) {
        for (int counter = 0; counter < noOfItntensiveLines; ++counter) {
            HoughLine houghLine = mostIntensiveLines[counter];
            if (houghLine.getRelativeIntensity() >HOUGH_INTENSITY_LIMITER) {
//                Log.d("relative intensity>0.5:",
//                        " theta:" + String.valueOf(houghLine.getTheta())
//                                + " radius:" + String.valueOf(houghLine.getRadius())
//                                + " intensity:" + String.valueOf(houghLine.getIntensity())
//                                + " relative intensity:"
//                                + String.valueOf((double) houghLine.getIntensity() / (double) this.maxMapIntensity));

                thetaAggregator += houghLine.getTheta() * houghLine.getRelativeIntensity();
                relativeIntensityAggregator += houghLine.getRelativeIntensity();
            }
        }
        Log.d("aggregated theta ", String.valueOf(thetaAggregator));
        Log.d("aggregated r-intensity ", String.valueOf(relativeIntensityAggregator));
    }

    private void normalizeTheta(int noOfItntensiveLines) {
        aggregateHoughValues(noOfItntensiveLines);
        if (mostIntensiveLines.length > 0) {
            thetaAggregator /= relativeIntensityAggregator;
        }
        Log.d("theta/relIntensity", String.valueOf(thetaAggregator));
    }


    private double calculateTheta(double thetaAggregator) {
        Log.d("result", String.valueOf(thetaAggregator - 90.0D));
        if (thetaAggregator == 0) return thetaAggregator;
        else
            return thetaAggregator - 90.0D;
    }

    public double getSkewAngle(Bitmap sourceBitmap) {
        this.initializeHoughMap();
        int width = sourceBitmap.getWidth();
        int height = sourceBitmap.getHeight();
        int halfWidth = width / DIVIDER;
        int halfHeight = height / DIVIDER;
        int startX = -halfWidth; //column
        int startY = -halfHeight; //row
        int stopX = width - halfWidth;
        int stopY = height - halfHeight - 1;
        int halfHoughWidth = (int) Math.sqrt((double) (halfWidth * halfWidth + halfHeight * halfHeight));
        int houghWidth = halfHoughWidth * 2;
        houghMap = new int[this.houghHeight][houghWidth];

        int indexG = 0;
        int[] pixels = createPixelArray(sourceBitmap.getWidth(), sourceBitmap.getHeight(), sourceBitmap);
        int hls;//row
        int skewAngle = startX;//column
        for (hls = startY; hls < stopY; ++hls) {
            //column
            for (skewAngle = startX; skewAngle < stopX; ++indexG) {
                //different in same column
                if (isBorderPixel(pixels[indexG], pixels[indexG + width])) {
//                    System.out.println("border pixel found");
                    buildHoughMap(skewAngle, houghWidth, halfHoughWidth, hls);
                }
                ++skewAngle;
            }
        }
        maxMapIntensity = getMaxMapIntensity(hls, skewAngle, houghWidth);
        System.out.println("max intensity in hough map " + maxMapIntensity);
        mostIntensiveLines = this.getMostIntensiveLines(NO_OF_INTENSE_LINES, maxMapIntensity);
        int noOfItntensiveLines = mostIntensiveLines.length;
        normalizeTheta(noOfItntensiveLines);
        return calculateTheta(thetaAggregator);
    }

    private int shiftByOneBit(int number) {
        return number >> 1;
    }

    private boolean isHoughLineIntense(int theta, int distance, int intensity) {
        boolean foundGreater = false;
        int maxTheta = this.houghMap.length;
        int maxDistance = this.houghMap[0].length;
        int thetaMax = theta + this.localPeakDistance;
        int thetaMin = theta - this.localPeakDistance;
        //loop from theta-local to theta+local
        for (int currentTheta = thetaMin; currentTheta < thetaMax; ++currentTheta) {
            if (currentTheta >= 0) {
                if (currentTheta >= maxTheta || foundGreater) {
                    break;
                }
                int distanceMax = distance + this.localPeakDistance;
                for (int currentDistance = distance - this.localPeakDistance; currentDistance < distanceMax; ++currentDistance) {
                    if (currentDistance >= 0) {
                        if (currentDistance >= maxDistance) {
                            break;
                        }

                        if (this.houghMap[currentTheta][currentDistance] > intensity) {
                            foundGreater = true;
                            break;
                        }
                    }
                }
            }
        }
        return foundGreater;

    }

    //collect hough lines above minimum threshold
    private void collectHoughLines(int minLineIntensity, int maxMapIntensity) {
        int maxTheta = this.houghMap.length;
        int maxDistance = this.houghMap[0].length;
        int halfHoughWidth = shiftByOneBit(maxDistance);
        lines.clear();

        System.out.println("hough lines>" + minLineIntensity + " minimum intensity are:");
        for (int theta = 0; theta < maxTheta; ++theta) {
            for (int distance = 0; distance < maxDistance; ++distance) {
                int intensity = this.houghMap[theta][distance];
                if (intensity >= minLineIntensity) {

                    //if greater not found so add it to list or greater than min Intensity
                    if (isHoughLineIntense(theta, distance, intensity)) {
                        addHoughLine(theta, distance, halfHoughWidth, intensity, maxMapIntensity);
                    }
                }
            }
        }
        System.out.println(lines.size() + " hough lines>" + minLineIntensity + " minimum intensity added.");
        sortHoughLines();
    }

    private void sortHoughLines() {
        //max intense line at top
        Collections.sort(this.lines);
    }

    private void addHoughLine(int theta, int radius, int halfHoughWidth, int intensity, int maxMapIntensity) {
        lines.add(new HoughLine(90.0D - this.maxSkewToDetect + (double) theta / (double) this.stepsPerDegree,
                (double) (radius - halfHoughWidth), intensity, (double) intensity / (double) maxMapIntensity));
//        double th = 90.0D - this.maxSkewToDetect + (double) theta / (double) this.stepsPerDegree;
//        double ra = (double) (radius - halfHoughWidth);
//        Log.d("lines:", " theta:" + String.valueOf(th) + " radius:" + String.valueOf(ra) + " intensity:" + String.valueOf(intensity) + " relative intensity:" + String.valueOf((double) intensity / (double) this.maxMapIntensity));
    }

    private HoughLine[] getMostIntensiveLines(int count, int maxMapIntensity) {
        collectHoughLines(width / MINIMUM_HOUGHLINE_INTENSIY_FACTOR, maxMapIntensity);
        int n = Math.min(count, this.lines.size());
        HoughLine[] mostIntenseLines = new HoughLine[n];
        System.out.println("get first " + count + "most intensive line");
        for (int i = 0; i < n; ++i) {
            mostIntenseLines[i] = this.lines.get(i);
        }
        return mostIntenseLines;
    }

    private void initializeHoughMap() {
        this.houghHeight = (int) (DOUBLE_MULTIPLIER * this.maxSkewToDetect * (double) this.stepsPerDegree);
        this.thetaStep = DOUBLE_MULTIPLIER * this.maxSkewToDetect * PI / ANGLE_NORMALIZER / (double) this.houghHeight;
        this.sinMap = new double[this.houghHeight];
        this.cosMap = new double[this.houghHeight];

        prepareSinCosinMap(minimumTheta);
    }

    private void prepareSinCosinMap(double minimumTheta) {
        for (int i = 0; i < this.houghHeight; ++i) {
            this.sinMap[i] = Math.sin(minimumTheta * PI / ANGLE_NORMALIZER + (double) i * this.thetaStep);
            this.cosMap[i] = Math.cos(minimumTheta * PI / ANGLE_NORMALIZER + (double) i * this.thetaStep);
        }
    }
}
