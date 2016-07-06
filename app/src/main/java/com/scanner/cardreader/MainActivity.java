package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView afterImageView;
    // --Commented out by Inspection (7/6/2016 2:28 PM):ImageView afterImageViewFramework;

    private Bitmap bmSource;
    private Bitmap bmResult;

    // --Commented out by Inspection (7/6/2016 2:28 PM):long start, time;
    private static Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button grayButton = (Button) findViewById(R.id.bgrayscale);
        Button thresholdButton = (Button) findViewById(R.id.bthreshold);
        grayButton.setOnClickListener(MainActivity.this);
        thresholdButton.setOnClickListener(this);


        ImageView beforeImageView = (ImageView) findViewById(R.id.ivbefore);
        beforeImageView.setImageResource(R.drawable.pin);
        bmSource= BitmapFactory.decodeResource(getResources(),R.drawable.pin);
        afterImageView = (ImageView) findViewById(R.id.ivafter);
        //afterImageViewFramework = (ImageView) findViewById(R.id.ivafter_framwork);

        //afterImageView.setImageResource(R.drawable.colorone);

        //linked with message queue of main thread
        handler = new Handler() {

            //executed when msg arrives from thread
            @Override
            public void handleMessage(Message msg) {
                afterImageView.setImageBitmap((Bitmap) msg.obj);
                //time = System.currentTimeMillis() - start;
                //Log.d("thread", String.valueOf(time));
            }
        };


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bgrayscale:
                //start = System.currentTimeMillis();
                Thread grayScaleThread;
                //TODO see if you can avoid creating threads yourself. acquire form somewhere
                grayScaleThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        GrayScale grayScale = new ITURGrayScale(bmSource);
                        //ITURGrayScale grayScale= new ITURGrayScale(sourceImageBitmap,MainActivity.this);
                        bmResult = grayScale.grayScale();
                        Log.d("thread", Thread.currentThread().toString());
                        bmSource = bmResult;
                        Message msgToGrayScale = Message.obtain();
                        msgToGrayScale.obj = bmResult;
                        handler.sendMessage(msgToGrayScale);
                    }
                });
                grayScaleThread.start();


                break;

            case R.id.bthreshold:
                /*FastBitmap fb = new FastBitmap(bmSource);
                fb.toGrayscale();
                BradleyLocalThreshold bradley = new BradleyLocalThreshold();
                bradley.applyInPlace(fb);
                Bitmap bitmap = fb.toBitmap();
                afterImageViewFramework.setImageBitmap(bitmap);*/

                Thread thresholdingThread;
                thresholdingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Threshold threshold = new BradleyThreshold();
                        bmResult = threshold.threshold(bmResult);
                        Log.d("thread", Thread.currentThread().toString());
                        Message msgToThreshold = Message.obtain();
                        msgToThreshold.obj = bmResult;
                        handler.sendMessage(msgToThreshold);
                    }
                });
                thresholdingThread.start();
                break;
        }
    }

}
