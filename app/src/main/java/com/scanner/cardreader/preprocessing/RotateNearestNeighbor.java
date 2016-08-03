package com.scanner.cardreader.preprocessing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by anush on 7/19/2016.
 */

public class RotateNearestNeighbor implements com.scanner.cardreader.interfaces.Rotate{
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

    public RotateNearestNeighbor(double angle) {
        this.angle = -angle;
        this.keepSize = false;
    }

    public RotateNearestNeighbor(double angle, boolean keepSize) {
        this.angle = -angle;
        this.keepSize = keepSize;
    }

    public Bitmap rotateImage(Bitmap sourceBitmap) {
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
        this.calculateNewSize(sourceBitmap);
        rotatedBitmap = Bitmap.createBitmap(this.newWidth, this.newHeight, Bitmap.Config.ARGB_8888);
        newIradius = (double) (this.newHeight - 4) / 2.0D;
        newJradius = (double) (this.newWidth - 4) / 2.0D;
        angleRad = this.angle * 3.141592653589793D / 180.0D;
        angleCos = Math.cos(angleRad);
        angleSin = Math.sin(angleRad);
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

//        for (int x = 0; x < h; x++) {
//            for (int y = 0; y < w; y++) {
//                System.out.println("nn(" + x + "," + y + ")"
//                        + Integer.toHexString(rotatedBitmap.getPixel(x, y)));
//            }
//        }

//        rotatedBitmap.setPixels(resultImagePixels, 0, newWidth, 0, 0, newWidth, newHeight);
        Bitmap whiteBitmap= Bitmap.createBitmap(newWidth,newHeight, Bitmap.Config.ARGB_8888);
        whiteBitmap.eraseColor(Color.WHITE);
        Bitmap resultBitmap =Bitmap.createBitmap(newWidth,newHeight, Bitmap.Config.ARGB_8888);


        Canvas resultCanvas = new Canvas(resultBitmap);

//        resultCanvas.drawBitmap(whiteBitmap,0f,0f,null);
//        resultCanvas.drawBitmap(rotatedBitmap,newWidth,newHeight,null);


        Drawable whiteDrawable= new BitmapDrawable(whiteBitmap);
        Drawable rotatedImageDrawable= new BitmapDrawable(rotatedBitmap);

        whiteDrawable.setBounds(0,0,newWidth,newHeight);
        rotatedImageDrawable.setBounds(1,1,newWidth-1,newHeight-1);
        whiteDrawable.draw(resultCanvas);
        rotatedImageDrawable.draw(resultCanvas);


//        float scaleWidth = ((float) newWidth) / 2;
//        float scaleHeight = ((float) newHeight) / 2;
//        create matrix for the manipulation
//         resize the bit map
//
//         rotate the Bitmap
//        Matrix matrix = new Matrix();
//        matrix.setScale(scaleWidth, scaleHeight);
//        Bitmap lastBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
//                newWidth, newHeight, matrix, false);

//        Bitmap lastBitmap = Bitmap.createScaledBitmap(sourceBitmap,sourceBitmap.getWidth()-200,sourceBitmap.getHeight()-200, true);
        return resultBitmap;
        //rotatedBitmap.recycle();

    }

    private int[] createPixelArray(int width, int height, Bitmap sourceImage) {
        int[] sourcePixels = new int[width * height];
        sourceImage.getPixels(sourcePixels, 0, width, 0, 0, width, height);
        return sourcePixels;
    }

    private void calculateNewSize(Bitmap fastBitmap) {
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
