package com.scanner.cardreader;

import android.graphics.Bitmap;

/**
 * Created by anush on 6/21/2016.
 */

public class BradleyThreshold implements Threshold {

    final private float LIMIT = 0.15F;
    private final int FRAME_SIZE_RATIO = 8;
    private int width;
    private int height;

    private int[] createIntegralImage(int[] pixels) {

        int sum = 0;
        int currentPixel;
        /*
        Log.d("masked except blue", String.valueOf(pixels[100] & 0xFF));
        Log.d("blue", String.valueOf(Color.blue(pixels[100])));
        Log.d("masked except green", String.valueOf(pixels[100] >> 8 & 0xFF));
        Log.d("green", String.valueOf(Color.green(pixels[100])));
        Log.d("masked except red", String.valueOf(pixels[100] >> 16 & 0xFF));
        Log.d("red", String.valueOf(Color.red(pixels[100])));
        Log.d("unmasked", String.valueOf(pixels[100]));*/
        int[] integralImage = new int[width * height];

        for (int i = 0; i < width; i++) {
            sum = 0;
            for (int j = 0; j < height; j++) {
                currentPixel = i + j * width;
                sum += pixels[currentPixel] & 0xFF;
                if (i == 0) {
                    integralImage[currentPixel] = sum;
                } else {
                    integralImage[currentPixel] = integralImage[currentPixel - 1] + sum;
                }
            }
        }
        return integralImage;
    }

    private int checkPositiveXBorder(int xValue) {
        xValue = xValue < 0 ? 0 : xValue;
        return xValue;
    }

    private int checkNegativeXBorder(int xValue) {
        xValue = xValue >= width ? width - 1 : xValue;
        return xValue;
    }

    private int checkPositiveYBorder(int yValue) {
        yValue = yValue < 0 ? 0 : yValue;
        return yValue;
    }

    private int checkNegativeYBorder(int yValue) {
        yValue = yValue >= height ? height - 1 : yValue;
        return yValue;
    }

    private int getHalfOfFrame(int width) {
        return (width / FRAME_SIZE_RATIO) >> 1;
    }


    @Override
    public Bitmap threshold(Bitmap sourceImage) {

        width = sourceImage.getWidth();
        height = sourceImage.getHeight();

        int[] pixels = new int[width * height];
        sourceImage.getPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap afterThresholding = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int[] integralImage = createIntegralImage(pixels);
        //Bradley AdaptiveThresholding
        int currentPixel, noPixelInFrame, sum = 0;
        int negativeXValue, positiveXValue, negativeYValue, positiveYValue;
        int halfFrameSize = getHalfOfFrame(width);

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                currentPixel = j * width + i;

                //check frame and check border
                negativeXValue = checkNegativeXBorder(i - halfFrameSize);
                positiveXValue = checkPositiveXBorder(i + halfFrameSize);
                negativeYValue = checkPositiveYBorder(j - halfFrameSize);
                positiveYValue = checkNegativeYBorder(j + halfFrameSize);

                noPixelInFrame = (positiveXValue - negativeXValue) * (positiveYValue - negativeYValue);

                sum = integralImage[positiveYValue * width + positiveXValue]
                        - integralImage[negativeYValue * width + positiveXValue]
                        - integralImage[positiveYValue * width + negativeXValue]
                        + integralImage[negativeYValue * width + negativeXValue];

                pixels[currentPixel] = ((pixels[currentPixel] & 0xFF) * noPixelInFrame) < (sum * (1.0F - this.LIMIT)) ? 0x00 : 0xFFFFFF;
                //pixels[currentPixel] = -16777216 | pixels[currentPixel]<< 16 | pixels[currentPixel] << 8 | pixels[currentPixel];
            }
        }
        afterThresholding.setPixels(pixels, 0, width, 0, 0, width, height);
        return afterThresholding;
    }
}
