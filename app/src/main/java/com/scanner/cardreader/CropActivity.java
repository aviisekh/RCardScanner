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

import android.os.Handler;
import android.os.Message;
import android.support.design.internal.ForegroundLinearLayout;
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
    Bitmap croppedImage;
    private static Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        instantiate();


        //linked with message queue of main thread
        handler = new Handler() {

            //executed when msg arrives from thread
            @Override
            public void handleMessage(Message msg) {
                capturedImage.setImageBitmap((Bitmap) msg.obj);
            }
        };

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

                Thread grayScaleThread;
                //TODO see if you can avoid creating threads yourself. acquire form somewhere
                grayScaleThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        GrayScale grayScale = new ITURGrayScale(croppedImage);
                        //ITURGrayScale grayScale= new ITURGrayScale(sourceImageBitmap,MainActivity.this);
                        Bitmap bmResult = grayScale.grayScale();
                        //Log.d("thread", Thread.currentThread().toString());

                        Threshold threshold = new BradleyThreshold();
                        bmResult = threshold.threshold(bmResult);

                        Message msgToUIThread = Message.obtain();
                        msgToUIThread.obj = bmResult;
                        handler.sendMessage(msgToUIThread);
                    }
                });
                grayScaleThread.start();
                break;

        }
    }

    public void instantiate() {
        image = getRotatedImage(CameraActivity.getBitmapImage());

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
         croppedImage = clippingWindow.getCroppedImage();
        capturedImage.setImageBitmap(croppedImage);
    }



    Bitmap getRotatedImage(Bitmap bmp)
    {
        Matrix returnImage = new Matrix();
        returnImage.postRotate(90);
        return Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),returnImage,true);
    }
}
