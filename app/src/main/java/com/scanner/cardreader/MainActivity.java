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



    @Override
    public void onClick(View v) {
        Bitmap afterGrayScale =doGrayScale(bmSource);

        switch (v.getId()) {
            case R.id.bgrayscale:
                afterImageView.setImageBitmap(afterGrayScale);
                break;

            case R.id.bthreshold:

                break;


        }
    }
}
