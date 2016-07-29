package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by anush on 7/19/2016.
 */

public class RotateNearestNeighbor {
    private double angle;
    private boolean keepSize;
    private int newWidth;
    private int newHeight;
    private int fillRed = 0;
    private int fillGreen = 0;
    private int fillBlue = 0;
    private int fillGray = 255;

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

    public void setFillColor(int red, int green, int blue) {
        this.fillRed = red;
        this.fillGreen = green;
        this.fillBlue = blue;
    }

    public void setFillColor(int gray) {
        this.fillGray = gray;
    }

    public RotateNearestNeighbor(double angle) {
        this.angle = -angle;
        this.keepSize = false;
    }

    public RotateNearestNeighbor(double angle, boolean keepSize) {
        this.angle = -angle;
        this.keepSize = keepSize;
    }

    public Bitmap applyInPlace(Bitmap sourceBitmap) {
        int width;
        int height;
        double oldIradius;
        double oldJradius;
        Bitmap rotatedBitmap;
        double newIradius;
        double newJradius;
        double angleRad;
        double angleCos;
        double angleSin;
        double ci;
        double cj;
        int oi;
        int oj;
        int i;
        int j;
        int[] sourceImagePixels;
        int[] resultImagePixels;
        width = sourceBitmap.getWidth();
        height = sourceBitmap.getHeight();
        oldIradius = (double) (height - 4) / 2.0D;
        oldJradius = (double) (width - 4) / 2.0D;
        this.CalculateNewSize(sourceBitmap);
        rotatedBitmap = Bitmap.createBitmap(this.newWidth, this.newHeight, Bitmap.Config.ARGB_8888);
        newIradius = (double) (this.newHeight - 4) / 2.0D;
        newJradius = (double) (this.newWidth - 4) / 2.0D;
        angleRad = this.angle * 3.141592653589793D / 180.0D;
        angleCos = Math.cos(angleRad);
        angleSin = Math.sin(angleRad);
        sourceImagePixels= createPixelArray(sourceBitmap.getWidth(),sourceBitmap.getHeight(),sourceBitmap);
        resultImagePixels = createPixelArray(rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), rotatedBitmap);
        ci = -newIradius;


        System.out.println("values mapped to previous frame");
        for (i = 0; i < this.newHeight; ++i) {
            cj = -newJradius;

            for (j = 0; j < this.newWidth; ++j) {
                oi = (int) (angleCos * ci + angleSin * cj + oldIradius);
                oj = (int) (-angleSin * ci + angleCos * cj + oldJradius);
//                System.out.println("x:" + oi + "<" + sourceBitmap.getWidth() + " ,y:"  + oj +"<" + sourceBitmap.getHeight());
                if (oi >= 0 && oj >= 0 && oi < height && oj < width) {
                    rotatedBitmap.setPixel(j,i,sourceBitmap.getPixel(oj,oi));
                } else {
                    rotatedBitmap.setPixel(j,i, Color.WHITE);
                }
                ++cj;
            }

            ++ci;
        }
        System.out.println("rotated height:" + newHeight + " prev height" + height);
        System.out.println("rotated width:" + newWidth + " prev width" + width);

        //sourceBitmap.setImage(rotatedBitmap);

        System.out.println("rotated values");
        int w = rotatedBitmap.getWidth();
        int h = rotatedBitmap.getHeight();


//        for (int x = 0; x < h; x++) {
//            for (int y = 0; y < w; y++) {
//                System.out.println("nn(" + x + "," + y + ")"
//                        + Integer.toHexString(rotatedBitmap.getPixel(x, y)));
//            }
//        }

//        rotatedBitmap.setPixels(resultImagePixels, 0, newWidth, 0, 0, newWidth, newHeight);


        return rotatedBitmap;
        //rotatedBitmap.recycle();

    }

    private int[] createPixelArray(int width, int height, Bitmap sourceImage) {
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


            halfWidth = Math.max(Math.max(cx1, cx2), Math.max(cx3, cx4))
                    - Math.min(Math.min(cx1, cx2), Math.min(cx3, cx4));
            halfHeight = Math.max(Math.max(cy1, cy2), Math.max(cy3, cy4))
                    - Math.min(Math.min(cy1, cy2), Math.min(cy3, cy4));
            this.newWidth = (int) (halfWidth * 2.0D + 0.5D);
            this.newHeight = (int) (halfHeight * 2.0D + 0.5D);
        }
    }
}
