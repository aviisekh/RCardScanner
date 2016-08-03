package com.scanner.cardreader.preprocessing;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by anush on 7/20/2016.
 */

public class GammaCorrection {

    private double gamma;

    public GammaCorrection() {
    }

    public GammaCorrection(double gamma) {
        this.gamma = gamma;
    }

    public double getGamma() {
        return this.gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }
    private int [] createPixelArray(int width,int height,Bitmap sourceImage){
        int [] pixels= new int[width*height];
        sourceImage.getPixels(pixels, 0, width, 0, 0, width, height);
        return  pixels;
    }

    public Bitmap correctGamma(Bitmap sourceBitmap) {
        int width= sourceBitmap.getWidth();
        int height=sourceBitmap.getHeight();
        Bitmap afterGammaCorrection= Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            this.gamma = this.gamma < 0.1D?0.1D:this.gamma;
            this.gamma = this.gamma > 5.0D?5.0D:this.gamma;


            double gamma_new = 1.0D / this.gamma;
            int[] gamma_LUT = gamma_LUT(gamma_new);
            int[] pixels = createPixelArray(sourceBitmap.getWidth(),sourceBitmap.getHeight(),sourceBitmap);

            for(int i = 0; i < pixels.length; ++i) {
                int r = pixels[i] >> 16 & 255;
                int g = pixels[i] >> 8 & 255;
                int b = pixels[i] & 255;
                r = gamma_LUT[r];
                g = gamma_LUT[g];
                b = gamma_LUT[b];
                pixels[i] = Color.rgb(r,g,b);
            }
        afterGammaCorrection.setPixels(pixels, 0, width, 0, 0, width, height);


        return afterGammaCorrection;
        }


    private static int[] gamma_LUT(double gamma_new) {
        int[] gamma_LUT = new int[256];
        for(int i = 0; i < gamma_LUT.length; ++i) {
            gamma_LUT[i] = (int)(255.0D * Math.pow((double)i / 255.0D, gamma_new));
        }
        return gamma_LUT;
    }
}


