package com.scanner.cardreader;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

/**
 * Created by anush on 7/18/2016.
 */

public class Rotate {
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


    public Rotate(int width, int height, double angle) {
        this.angle = angle;
        this.newWidth = width;
        this.newHeight = height;
    }

    public Bitmap applyInPlace(Bitmap fastBitmap) {

        // calculate the scale - in this case = 0.4f
        float scaleWidth = ((float) newWidth) / newWidth;
        float scaleHeight = ((float) newHeight) / newHeight;

        // createa matrix for the manipulation


//         resize the bit map
//        matrix.preScale(scaleWidth, scaleHeight);
//         rotate the Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate((float) angle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(fastBitmap, 0, 0,
                newWidth, newHeight, matrix, false);

        int oldWidth = fastBitmap.getWidth();
        int oldHeight = fastBitmap.getHeight();

        int newWidth = rotatedBitmap.getWidth();
        int newHeight = rotatedBitmap.getHeight();

        //cyclic order
        int xold0 = 0;
        int yold0 = 0;



        int xold3 = oldWidth;
        int yold3 = oldHeight;


        int xnew0 = (int) (xold0 * Math.cos(angle) - yold0 * Math.sin(angle));
        int ynew0 = (int) (yold0 * Math.cos(angle) + xold0 * Math.sin(angle));



        System.out.println(xold0+","+yold0+"->"+xnew0+","+ynew0);

//                      System.out.println("rotated values");
//                int w = resizedBitmap.getWidth();
//                int h = resizedBitmap.getHeight();
//
//                for (int i = 0; i < h; i++) {
//                    for (int j = 0; j < w; j++) {
//                        System.out.println("rotated(" + i + "," + j + ")" + Integer.toHexString(resizedBitmap.getPixel(i, j)));
//                    }
//                }


        for (int i = 0; i < rotatedBitmap.getWidth(); i++) {
            for (int j = 0; j < rotatedBitmap.getHeight(); j++) {
                int pixel = rotatedBitmap.getPixel(i, j);
                int A = Color.alpha(pixel);
                int R = Color.red(pixel);
                int G = Color.green(pixel);
                int B = Color.blue(pixel);
//
//                if (i == rotatedBitmap.getWidth()/2)
//                    System.out.println(i + "," + j + ": " + Color.alpha(pixel) + "," + Color.red(pixel) + "," + Color.green(pixel) + "," + Color.blue(pixel));
//                if ( (A==255)&&(R == 0) && (G == 0) && (B == 0)) {
//                    rotatedBitmap.setPixel(i, j, Color.WHITE);
//                }
            }
        }

//        Bitmap whiteBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
//        whiteBitmap.eraseColor(Color.WHITE);
//
//        Bitmap resultBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
//
//        Canvas c = new Canvas(resultBitmap);

//        c.drawBitmap(whiteBitmap,0f,0f,null);
//        c.drawBitmap(rotatedBitmap,newWidth,newHeight,null);

//
//        Threshold threshold = new BradleyThreshold();
//        rotatedBitmap = threshold.threshold(rotatedBitmap);

//        Drawable d1 = new BitmapDrawable(whiteBitmap);
//        Drawable d2 = new BitmapDrawable(rotatedBitmap);
//
//        d1.setBounds(0, 0, newWidth, newHeight);
//        d2.setBounds(100, 100, newWidth - 100, newHeight - 100);
//        d1.draw(c);
//        d2.draw(c);


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
