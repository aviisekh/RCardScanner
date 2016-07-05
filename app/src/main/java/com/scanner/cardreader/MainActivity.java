package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.BradleyLocalThreshold;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView beforeImageView;
    ImageView afterImageView, afterImageViewFramework;

    private Bitmap bmSource;
    private Bitmap bmResult;

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
        grayButton.setOnClickListener(MainActivity.this);
        thresholdButton.setOnClickListener(this);


        beforeImageView = (ImageView) findViewById(R.id.ivbefore);
        beforeImageView.setImageResource(R.drawable.pin);
        afterImageView = (ImageView) findViewById(R.id.ivafter);
        afterImageViewFramework = (ImageView) findViewById(R.id.ivafter_framwork);

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
                        //ITURGrayScale grayScale= new ITURGrayScale(bmSource,MainActivity.this);
                        bmResult = grayScale.grayScale();
                        Log.d("thread", Thread.currentThread().toString());
                        bmSource = bmResult;
                        Message message = Message.obtain();
                        message.obj = bmResult;
                        handler.sendMessage(message);
                    }
                });
                grayScaleThread.start();
                break;

            case R.id.bthreshold:
                FastBitmap fb = new FastBitmap(bmSource);
                fb.toGrayscale();
                BradleyLocalThreshold bradley = new BradleyLocalThreshold();
                bradley.applyInPlace(fb);
                Bitmap bitmap = fb.toBitmap();
                afterImageViewFramework.setImageBitmap(bitmap);

                Thread thresholdingThread;
                thresholdingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Threshold threshold = new BradleyThreshold();
                        bmResult = threshold.threshold(bmResult);
                        Log.d("thread", Thread.currentThread().toString());
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
