package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by anush on 7/19/2016.
 */

public class ImageSkewChecker {

    private int stepsPerDegree = 1;
    private int houghHeight;
    private double thetaStep;
    private double maxSkewToDetect = 90.0D;
    private double[] sinMap;
    private double[] cosMap;
    private int[][] houghMap;
    private int maxMapIntensity = 0;
    private int localPeakRadius = 4;
    private List<HoughLine> lines = new ArrayList();

    public int getStepsPerDegree() {
        return this.stepsPerDegree;
    }

    public void setStepsPerDegree(int stepsPerDegree) {
        this.stepsPerDegree = Math.max(1, Math.min(10, stepsPerDegree));
    }

    public double getMaxSkewToDetect() {
        return this.maxSkewToDetect;
    }

    public void setMaxSkewToDetect(double maxSkewToDetect) {
        this.maxSkewToDetect = Math.max(0.0D, Math.min(45.0D, maxSkewToDetect));
    }

    public int getLocalPeakRadius() {
        return this.localPeakRadius;
    }

    public void setLocalPeakRadius(int localPeakRadius) {
        this.localPeakRadius = Math.max(1, Math.min(10, localPeakRadius));
    }

    public ImageSkewChecker() {
    }

    private int[] createPixelArray(int width, int height, Bitmap sourceImage) {
        int[] pixels = new int[width * height];
        sourceImage.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }


    public double getSkewAngle(Bitmap fastBitmap) {

        this.InitializeHoughMap();
        int width = fastBitmap.getWidth();
        int height = fastBitmap.getHeight();
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int startX = -halfWidth; //column
        int startY = -halfHeight; //row
        int stopX = width - halfWidth;
        int stopY = height - halfHeight - 1;
        int halfHoughWidth = (int) Math.sqrt((double) (halfWidth * halfWidth + halfHeight * halfHeight));
        int houghWidth = halfHoughWidth * 2;
        this.houghMap = new int[this.houghHeight][houghWidth];
        int indexG = 0;

        int[] pixels = createPixelArray(fastBitmap.getWidth(), fastBitmap.getHeight(), fastBitmap);

        int hls;//row
        int skewAngle;//column

        //creating hough map
        //row
        System.out.println("Hough map indexG and indexG+width:");
        for (hls = startY; hls < stopY; ++hls) {
            //column
            for (skewAngle = startX; skewAngle < stopX; ++indexG) {
                //different in same column

                if ((pixels[indexG] & 255) < 128 && (pixels[indexG + width] & 255) >= 128) {

//                    System.out.println("("+hls+","+skewAngle+")"+" pixels indexG ="
//                            +(pixels[indexG] & 255)
//                            +" pixels indexG+width="+(pixels[indexG + width] & 255));

                    for (int theta = 0; theta < this.houghHeight; ++theta) {
                        int sumIntensity = (int) (this.cosMap[theta] * (double) skewAngle - this.sinMap[theta] * (double) hls) + halfHoughWidth;
                        if (sumIntensity >= 0 && sumIntensity < houghWidth) {
                            ++this.houghMap[theta][sumIntensity];
                        }
                    }
                }
                ++skewAngle;
            }
        }

        this.maxMapIntensity = 0;

        //calculate max intesity
        //row
        for (hls = 0; hls < this.houghHeight; ++hls) {
            for (skewAngle = 0; skewAngle < houghWidth; ++skewAngle) {
//                System.out.println("houghmap("+hls+","+skewAngle+")="+ this.houghMap[hls][skewAngle]);
                if (this.houghMap[hls][skewAngle] > this.maxMapIntensity) {
                    this.maxMapIntensity = this.houghMap[hls][skewAngle];
                }
            }
        }
        System.out.println("max intensity in hough map "+maxMapIntensity);

        this.CollectLines(width / 10);

        HoughLine[] mostIntensiveLines = this.GetMostIntensiveLines(5);
        double thetaAggregator = 0.0D;
        double relativeIntensityAggregator = 0.0D;
        //HoughLine[] var18 = mostIntensiveLines;
        int noOfItntensiveLines = mostIntensiveLines.length;
        //gather total of hough line (theta and intensity) if >0.5d
        System.out.println("most intensive lines with relative intensity >0.5D");
        for (int counter = 0; counter < noOfItntensiveLines; ++counter) {
            HoughLine houghLine = mostIntensiveLines[counter];
            if (houghLine.getRelativeIntensity() > 0.5D) {
                Log.d("relative intensity>0.5:",
                        " theta:" + String.valueOf(houghLine.getTheta())
                                + " radius:" + String.valueOf(houghLine.getRadius())
                                + " intensity:" + String.valueOf(houghLine.getIntensity())
                                + " relative intensity:"
                                + String.valueOf((double) houghLine.getIntensity() / (double) this.maxMapIntensity));

                thetaAggregator += houghLine.getTheta() * houghLine.getRelativeIntensity();
                relativeIntensityAggregator += houghLine.getRelativeIntensity();
            }
        }
        Log.d("aggregated theta ", String.valueOf(thetaAggregator));
        Log.d("aggregated r-intensity ", String.valueOf(relativeIntensityAggregator));

        if (mostIntensiveLines.length > 0) {
            thetaAggregator /= relativeIntensityAggregator;
        }
        Log.d("theta/relIntensity", String.valueOf(thetaAggregator));
        Log.d("result", String.valueOf(thetaAggregator - 90.0D));
        if (thetaAggregator==0) return thetaAggregator;
        else
        return thetaAggregator-90.0D;

    }

    //collect hough lines above minimum threshold
    private void CollectLines(int minLineIntensity) {
        int maxTheta = this.houghMap.length;
        int maxRadius = this.houghMap[0].length;
        int halfHoughWidth = maxRadius >> 1;
        this.lines.clear();

        System.out.println("hough lines>"+minLineIntensity+" minimum intensity are:");
        for (int theta = 0; theta < maxTheta; ++theta) {
            for (int radius = 0; radius < maxRadius; ++radius) {
                int intensity = this.houghMap[theta][radius];
                if (intensity >= minLineIntensity) {
                    boolean foundGreater = false;

                    int tempThetaMax = theta + this.localPeakRadius;
                    //loop from theta-local to theta+local
                    for (int tempTheta = theta - this.localPeakRadius; tempTheta < tempThetaMax; ++tempTheta) {

                        if (tempTheta >= 0) {
                            if (tempTheta >= maxTheta || foundGreater) {
                                break;
                            }

                            int tempRadiusMax = radius + this.localPeakRadius;
                            for (int tempRadius = radius - this.localPeakRadius; tempRadius < tempRadiusMax; ++tempRadius) {
                                if (tempRadius >= 0) {
                                    if (tempRadius >= maxRadius) {
                                        break;
                                    }

                                    if (this.houghMap[tempTheta][tempRadius] > intensity) {
                                        foundGreater = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    //if greater not found so add it to list or greater than min Intensity

                    if (!foundGreater) {
                        this.lines.add(new HoughLine(90.0D - this.maxSkewToDetect + (double) theta / (double) this.stepsPerDegree,
                                (double) (radius - halfHoughWidth), intensity, (double) intensity / (double) this.maxMapIntensity));
                        double th = 90.0D - this.maxSkewToDetect + (double) theta / (double) this.stepsPerDegree;
                        double ra = (double) (radius - halfHoughWidth);
                        Log.d("lines:", " theta:" + String.valueOf(th) + " radius:" + String.valueOf(ra) + " intensity:" + String.valueOf(intensity) + " relative intensity:" + String.valueOf((double) intensity / (double) this.maxMapIntensity));
                    }
                }
            }
        }
        System.out.println(lines.size()+" hough lines>"+minLineIntensity+" minimum intensity added.");
        //max intense line at top
        Collections.sort(this.lines);

    }

    private HoughLine[] GetMostIntensiveLines(int count) {
        int n = Math.min(count, this.lines.size());
        HoughLine[] mostIntenseLines = new HoughLine[n];

        System.out.println("get first "+count+"most intensive line");
        for (int i = 0; i < n; ++i) {
            mostIntenseLines[i] = this.lines.get(i);
            Log.d("hough lines:", " theta:" + String.valueOf(mostIntenseLines[i].getTheta())
                    + " radius:" + String.valueOf(mostIntenseLines[i].getRadius())
                    + " intensity:" + String.valueOf(mostIntenseLines[i].getIntensity())
                    + " relative intensity:" + String.valueOf(mostIntenseLines[i].getRelativeIntensity()));

        }

        return mostIntenseLines;
    }

    private void InitializeHoughMap() {
        this.houghHeight = (int) (2.0D * this.maxSkewToDetect * (double) this.stepsPerDegree);
        this.thetaStep = 2.0D * this.maxSkewToDetect * 3.141592653589793D / 180.0D / (double) this.houghHeight;
        this.sinMap = new double[this.houghHeight];
        this.cosMap = new double[this.houghHeight];
        double minimumTheta = 0.0D;
//                90.0D - this.maxSkewToDetect;

        //preparing sine cosine map for optimization
        for (int i = 0; i < this.houghHeight; ++i) {
            this.sinMap[i] = Math.sin(minimumTheta * 3.141592653589793D / 180.0D + (double) i * this.thetaStep);
            this.cosMap[i] = Math.cos(minimumTheta * 3.141592653589793D / 180.0D + (double) i * this.thetaStep);
        }
    }
}
