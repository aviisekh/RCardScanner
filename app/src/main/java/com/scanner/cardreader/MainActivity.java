package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.Console;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView beforeImageView;
    ImageView afterImageView;
    Bitmap bmSource;
    Button grayButton;
    Button thresholdButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bmSource = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
        grayButton = (Button) findViewById(R.id.bgrayscale);
        thresholdButton = (Button) findViewById(R.id.bthreshold);

        beforeImageView = (ImageView) findViewById(R.id.ivbefore);
        beforeImageView.setImageResource(R.drawable.pin);

        afterImageView = (ImageView) findViewById(R.id.ivafter);
        //afterImageView.setImageResource(R.drawable.colorone);

        grayButton.setOnClickListener(this);
        thresholdButton.setOnClickListener(this);

    }


    public static Bitmap doGrayScale(Bitmap bmSource) {
        //ITU-R recommendation value
        final double GS_RED = 0.299;
        final double GS_GREEN = 0.587;
        final double GS_BLUE = 0.114;

        Bitmap bmDisplay = Bitmap.createBitmap(bmSource.getWidth(), bmSource.getHeight(), bmSource.getConfig());

        int A, R, G, B;
        int pixel;

        //image size
        int width = bmSource.getWidth();
        int height = bmSource.getHeight();

        Log.d("pixel value", Integer.toString(bmSource.getPixel(50, 50)));

        //scan pixel
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = bmSource.getPixel(x, y);

                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
                bmDisplay.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bmDisplay;
    }

    public static Bitmap grayscaleToBin(Bitmap grayscaleBitmap) {
        Bitmap tempBitMap;
        tempBitMap = grayscaleBitmap.copy(Bitmap.Config.RGB_565, true);
        final int width = grayscaleBitmap.getWidth();
        final int height = grayscaleBitmap.getHeight();


        int pixel1, pixel2, pixel3, pixel4, A, R;
        int[] pixels;
        pixels = new int[width * height];

        grayscaleBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int size = width * height;
        int s = width / 8;
        int s2 = s >> 1;
        double t = 0.15;
        double it = 1.0 - t;
        int[] integral = new int[size];
        int[] threshold = new int[size];
        int i, j, diff, x1, y1, x2, y2, ind1, ind2, ind3;
        int sum = 0;
        int ind = 0;
        while (ind < size) {
            sum += pixels[ind] & 0xFF;
            integral[ind] = sum;
            ind += width;
        }
        x1 = 0;

        for (i = 1; i < width; ++i) {
            sum = 0;
            ind = i;
            ind3 = ind - s2;
            if (i > s) {
                x1 = i - s;
            }
            diff = i - x1;
            for (j = 0; j < height; ++j) {
                sum += pixels[ind] & 0xFF;
                integral[ind] = integral[(int) (ind - 1)] + sum;
                ind += width;
                if (i < s2) continue;
                if (j < s2) continue;

                y1 = (j < s ? 0 : j - s);
                ind1 = y1 * width;
                ind2 = j * width;

                if (((pixels[ind3] & 0xFF) * (diff * (j - y1))) < ((integral[(int) (ind2 + i)] - integral[(int) (ind1 + i)] - integral[(int) (ind2 + x1)] + integral[(int) (ind1 + x1)]) * it)) {
                    threshold[ind3] = 0x00;
                } else {
                    threshold[ind3] = 0xFFFFFF;
                }
                ind3 += width;
            }
        }


        y1 = 0;
        for (j = 0; j < height; ++j) {
            i = 0;
            y2 = height - 1;
            if (j < height - s2) {
                i = width - s2;
                y2 = j + s2;
            }

            ind = j * width + i;
            if (j > s2) {
                y1 = j - s2;
            }
                ind1 = y1 * width;
                ind2 = y2 * width;
                diff = y2 - y1;

                for (; i < width; ++i, ++ind) {
                    x1 = (i < s2 ? 0 : i - s2);
                    x2 = i + s2;

                    //check border
                    if (x2 >= width) {
                        x2 = width - 1;
                    }

                    if (((pixels[ind] & 0xFF) * ((x2 - x1) * diff)) < ((integral[(int) (ind2 + x2)] - integral[(int) (ind1 + x2)] - integral[(int) (ind2 + x1)] + integral[(int) (ind1 + x1)]) * it)) {
                        threshold[ind] = 0x00;
                    } else {
                        threshold[ind] = 0xFFFFFF;
                    }
                }


        }
        tempBitMap.setPixels(threshold, 0, width, 0, 0, width, height);

        return tempBitMap;


    }


    @Override
    public void onClick(View v) {
        Bitmap afterGrayScale =doGrayScale(bmSource);

        switch (v.getId()) {
            case R.id.bgrayscale:
                afterImageView.setImageBitmap(afterGrayScale);
                break;

            case R.id.bthreshold:
                afterImageView.setImageBitmap(grayscaleToBin(afterGrayScale));
                break;


        }
    }
}
