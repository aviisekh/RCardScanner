package com.scanner.cardreader.preprocessing;

import android.graphics.Bitmap;

import com.scanner.cardreader.interfaces.Threshold;

/**
 * Created by anush on 6/21/2016.
 */

public class BradleyThreshold implements Threshold {

    private int sourceImageWidth;
    private int sourceImageHeight;
    private final int MASKER = 0xFF;
    private final float PIXEL_BRIGHTNESS_DIFF_LIMIT = 0.15F;
    private final float PIXEL_BRIGHTNESS_MAX = 1.0F;
    private final int THRESHOLD_BLACK = 0x00;
    private final int THRESHOLD_WHITE = 0xFFFFFF;
    private final int FRAME_SIZE_RATIO = 8;
    private final int startX = 0;
    private final int startY = 0;
    private final int offSet = 0;

    int currentPixel;
    int previousPixel;
    int noPixelInFrame;
    int sumOfFramePixels;
    int negativeXValue;
    int positiveXValue;
    int negativeYValue;
    int positiveYValue;


    public void setNegativeXValue(int negativeXValue) {
        this.negativeXValue = negativeXValue;
    }

    public void setNegativeYValue(int negativeYValue) {
        this.negativeYValue = negativeYValue;
    }

    public void setPositiveXValue(int positiveXValue) {
        this.positiveXValue = positiveXValue;
    }

    public void setPositiveYValue(int positiveYValue) {
        this.positiveYValue = positiveYValue;
    }

    public int getNegativeXValue() {
        return negativeXValue;
    }

    public int getNegativeYValue() {
        return negativeYValue;
    }

    public int getPositiveXValue() {
        return positiveXValue;
    }

    public int getPositiveYValue() {
        return positiveYValue;
    }


    private int[] createIntegralImage(int[] pixels) {
        int sumofRow;
        int[] integralImage = new int[sourceImageWidth * sourceImageHeight];

        for (int row = 0; row < sourceImageWidth; row++) {
            sumofRow = 0;
            for (int column = 0; column < sourceImageHeight; column++) {
                currentPixel = row + column * sourceImageWidth;
                previousPixel = currentPixel - 1;
                sumofRow += pixels[currentPixel] & MASKER;
                if (row == 0) {
                    integralImage[currentPixel] = sumofRow;
                } else {
                    integralImage[currentPixel] = integralImage[previousPixel] + sumofRow;
                }
            }
        }
        return integralImage;
    }

    private int boundPositiveXBorder(int xValue) {
        return (xValue >= sourceImageWidth) ? sourceImageWidth - 1 : xValue;
    }

    private int boundNegativeXBorder(int xValue) {
        return xValue < 0 ? 0 : xValue;
    }

    private int boundPositiveYBorder(int yValue) {
        return yValue >= sourceImageHeight ? sourceImageHeight - 1 : yValue;
    }

    private int boundNegativeYBorder(int yValue) {
        return yValue < 0 ? 0 : yValue;
    }

    private int shiftByOneBit(int number) {
        return number >> 1;
    }

    private int getHalfOfFrame(int width) {
        return shiftByOneBit(width / FRAME_SIZE_RATIO);
    }

    private int[] bitmapToArray(int width, int height, Bitmap sourceImage) {
        int[] sourceImagePixels = new int[width * height];
        sourceImage.getPixels(sourceImagePixels, startX, width, startY, offSet, width, height);
        return sourceImagePixels;
    }


    private int calculateNoOfPixels(int positiveXValue, int negativeXValue, int positiveYValue, int negativeYValue) {
        return (positiveXValue - negativeXValue) * (positiveYValue - negativeYValue);
    }


    private boolean isBorderFixed(int row, int column, int halfFrameSize) {
        //check frame and check border
        negativeXValue = boundNegativeXBorder(row - halfFrameSize);
        positiveXValue = boundPositiveXBorder(row + halfFrameSize);
        negativeYValue = boundNegativeYBorder(column - halfFrameSize);
        positiveYValue = boundPositiveYBorder(column + halfFrameSize);

       return(negativeXValue >= 0 && negativeYValue >= 0 && positiveXValue >= 0 && positiveYValue >= 0);
    }


    private int getSumOfFramePixels(int [] integralImage){
        return (integralImage[positiveYValue * sourceImageWidth + positiveXValue]
                - integralImage[negativeYValue * sourceImageWidth + positiveXValue]
                - integralImage[positiveYValue * sourceImageWidth + negativeXValue]
                + integralImage[negativeYValue * sourceImageWidth + negativeXValue]);

    }


    @Override
    public Bitmap threshold(Bitmap sourceImage) {
        sourceImageWidth = sourceImage.getWidth();
        sourceImageHeight = sourceImage.getHeight();

        int[] pixels = bitmapToArray(sourceImageWidth, sourceImageHeight, sourceImage);
        Bitmap afterThresholding = Bitmap.createBitmap(sourceImageWidth, sourceImageHeight, Bitmap.Config.RGB_565);
        int[] integralImage = createIntegralImage(pixels);
        int halfFrameSize = getHalfOfFrame(sourceImageWidth);

        for (int row = 0; row < sourceImageWidth; ++row) {
            for (int column = 0; column < sourceImageHeight; ++column) {

                if(isBorderFixed(row,column,halfFrameSize)){
                    currentPixel = column * sourceImageWidth + row;
                    noPixelInFrame = calculateNoOfPixels(positiveXValue, negativeXValue, positiveYValue, negativeYValue);
                }
                sumOfFramePixels = getSumOfFramePixels(integralImage);

                if (((pixels[currentPixel] & MASKER) * noPixelInFrame) < (sumOfFramePixels * (PIXEL_BRIGHTNESS_MAX - PIXEL_BRIGHTNESS_DIFF_LIMIT))) {
                    pixels[currentPixel] = THRESHOLD_BLACK;
                    //blackPixelCounter++;
                } else {
                    pixels[currentPixel] = THRESHOLD_WHITE;
                    //whitePixelCounter++;
                }
            }
        }

        afterThresholding.setPixels(pixels, offSet, sourceImageWidth, startX, startY, sourceImageWidth, sourceImageHeight);
        return afterThresholding;

        /*        System.out.println("white:"+whitePixelCounter+",black:"+blackPixelCounter);
        if(whitePixelCounter>blackPixelCounter){
            System.out.println("white more");
            afterThresholding.setPixels(pixels, 0, sourceImageWidth, 0, 0, sourceImageWidth, sourceImageHeight);

        }else{
            System.out.println("black more");
            for(int i=0;i<pixels.length;i++){
                if(Integer.toHexString(pixels[i]).equals("0x00")) pixels[i]=0xFFFFFF;
                else pixels[i]=0x00;
            }
            afterThresholding.setPixels(pixels, 0, sourceImageWidth, 0, 0, sourceImageWidth, sourceImageHeight);

        }*/
    }
}
