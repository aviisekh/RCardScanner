package com.scanner.cardreader.preprocessing;

import android.graphics.Bitmap;

import com.scanner.cardreader.interfaces.Threshold;

/**
 * Created by anush on 6/21/2016.
 */

public class BradleyThreshold implements Threshold {

    private int width;
    private int height;

    private int[] createIntegralImage(int[] pixels) {

        int sumofRow;
        int currentPixel;

        int[] integralImage = new int[width * height];

        for (int row = 0; row < width; row++) {
            sumofRow = 0;
            for (int column = 0; column < height; column++) {
                currentPixel = row + column * width;
                sumofRow += pixels[currentPixel] & 0xFF;
                if (row == 0) {
                    integralImage[currentPixel] = sumofRow;
                } else {
                    integralImage[currentPixel] = integralImage[currentPixel - 1] + sumofRow;
                }
            }
        }
        return integralImage;
    }

    private int checkPositiveXBorder(int xValue) {
        xValue = (xValue >= width) ? width - 1 : xValue;
        return xValue;
    }

    private int checkNegativeXBorder(int xValue) {
        xValue = xValue < 0 ? 0 : xValue;
        return xValue;

    }

    private int checkPositiveYBorder(int yValue) {
        yValue = yValue >= height ? height - 1 : yValue;
        return yValue;
    }

    private int checkNegativeYBorder(int yValue) {
        yValue = yValue < 0 ? 0 : yValue;
        return yValue;
    }

    private int getHalfOfFrame(int width) {
        int FRAME_SIZE_RATIO = 8;
        return (width / FRAME_SIZE_RATIO) >> 1;
    }

    private int [] createPixelArray(int width,int height,Bitmap sourceImage){
        int [] pixels= new int[width*height];
        sourceImage.getPixels(pixels, 0, width, 0, 0, width, height);
        return  pixels;
    }



    @Override
    public Bitmap threshold(Bitmap sourceImage) {
        width = sourceImage.getWidth();
        height = sourceImage.getHeight();


        float PIXEL_BRIGHTNESS_DIFF_LIMIT = 0.15F;
        int[] pixels = createPixelArray(width,height,sourceImage);
        Bitmap afterThresholding = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

/*

        Log.d("masked except blue", String.valueOf(pixels[100] & 0xFF));
        Log.d("blue", String.valueOf(Color.blue(pixels[100])));
        Log.d("masked except green", String.valueOf(pixels[100] >> 8 & 0xFF));
        Log.d("green", String.valueOf(Color.green(pixels[100])));
        Log.d("masked except red", String.valueOf(pixels[100] >> 16 & 0xFF));
        Log.d("red", String.valueOf(Color.red(pixels[100])));
        Log.d("unmasked", String.valueOf(pixels[100]));
*/


        int[] integralImage = createIntegralImage(pixels);
        //Bradley AdaptiveThresholding
        int currentPixel, noPixelInFrame, sumOfLocalPixels;
        int negativeXValue, positiveXValue, negativeYValue, positiveYValue;
        int halfFrameSize = getHalfOfFrame(width);

        for (int row = 0; row < width; ++row) {
            for (int column = 0; column < height; ++column) {
                currentPixel = column * width + row;
                //check frame and check border
                negativeXValue = checkNegativeXBorder(row - halfFrameSize);
                positiveXValue = checkPositiveXBorder(row + halfFrameSize);
                negativeYValue = checkNegativeYBorder(column - halfFrameSize);
                positiveYValue = checkPositiveYBorder(column + halfFrameSize);

                noPixelInFrame = (positiveXValue - negativeXValue) * (positiveYValue - negativeYValue);

                sumOfLocalPixels = integralImage[positiveYValue * width + positiveXValue]
                        - integralImage[negativeYValue * width + positiveXValue]
                        - integralImage[positiveYValue * width + negativeXValue]
                        + integralImage[negativeYValue * width + negativeXValue];

                pixels[currentPixel] = ((pixels[currentPixel] & 0xFF) * noPixelInFrame) < (sumOfLocalPixels * (1.0F - PIXEL_BRIGHTNESS_DIFF_LIMIT)) ? 0x00 : 0xFFFFFF;
                //pixels[currentPixel] = -16777216 | pixels[currentPixel]<< 16 | pixels[currentPixel] << 8 | pixels[currentPixel];
            }
        }
        afterThresholding.setPixels(pixels, 0, width, 0, 0, width, height);
        return afterThresholding;
    }
}
