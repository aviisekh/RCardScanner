package com.scanner.cardreader;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.Arrays;


public class CropActivity extends AppCompatActivity implements View.OnClickListener {


    public static ImageView capturedImage;
    public ClippingWindow clippingWindow;

    public Button scanBtn, rechargeBtn, redoButton, cropButton;

    public Bitmap image;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        instantiate();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cropBtn:
                crop();
                break;

            case R.id.redoBtn:
                onBackPressed();
                break;

            case R.id.rechargeBtn:
                break;

        }
    }

    public void instantiate() {
        image = CameraActivity.getBitmapImage();
        //image = BitmapFactory.decodeResource(getResources(),R.drawable.horizontal);
        scanBtn = (Button) findViewById(R.id.scanBtn);
        rechargeBtn = (Button) findViewById(R.id.rechargeBtn);
        redoButton = (Button) findViewById(R.id.redoBtn);
        cropButton = (Button) findViewById(R.id.cropBtn);

        capturedImage = (ImageView) findViewById(R.id.imageView);
        capturedImage.setImageBitmap(image);


        clippingWindow = (ClippingWindow) findViewById(R.id.clipping);
        clippingWindow.setVisibility(View.VISIBLE);


        scanBtn.setOnClickListener(this);
        rechargeBtn.setOnClickListener(this);
        redoButton.setOnClickListener(this);
        cropButton.setOnClickListener(this);


    }



    public void crop() {
        rechargeBtn.setVisibility(View.VISIBLE);
        cropButton.setVisibility(View.INVISIBLE);
        clippingWindow.setVisibility(View.INVISIBLE);
        Bitmap croppedImage = clippingWindow.getCroppedImage();
        capturedImage.setImageBitmap(croppedImage);
    }




}
