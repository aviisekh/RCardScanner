package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView beforeImageView;
    ImageView afterImageView;
    Bitmap bmSource;
    Bitmap bmResult;
    Button grayButton;
    Button thresholdButton;
    long start, time;
    Handler handler;

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

        //linked with message queue of main thread
        handler = new Handler() {

            //executed when msg arrives from thread
            @Override
            public void handleMessage(Message msg) {
                afterImageView.setImageBitmap((Bitmap) msg.obj);

                time = System.currentTimeMillis() - start;
                Log.d("thread", String.valueOf(time));
            }
        };

        grayButton.setOnClickListener(MainActivity.this);
        thresholdButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.bgrayscale:

                start = System.currentTimeMillis();
                Thread grayScaleThread;
                grayScaleThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        GrayScale grayScale = new GrayScale(bmSource);
                        //GrayScale grayScale= new GrayScale(bmSource,MainActivity.this);
                        bmResult = grayScale.doGrayScale();
                        Log.e("thread", Thread.currentThread().toString());
                        bmSource = bmResult;
                        Message message = Message.obtain();
                        message.obj = bmResult;
                        handler.sendMessage(message);
                    }
                });
                grayScaleThread.start();
                break;

            case R.id.bthreshold:
                Thread thresholdingThread;
                thresholdingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Thresholding thresholding = new Thresholding(bmSource);
                        bmResult = thresholding.doThresholding();
                        Log.e("thread", Thread.currentThread().toString());
                        Message message = Message.obtain();
                        message.obj = bmResult;
                        handler.sendMessage(message);
                    }
                });
                thresholdingThread.start();
                break;
        }
    }

}
