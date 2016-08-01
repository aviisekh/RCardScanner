package com.scanner.cardreader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class CropActivity extends AppCompatActivity implements View.OnClickListener {

    public static ArrayList<Bitmap> componentBitmaps;


    public static ImageView capturedImage;
    public ClippingWindow clippingWindow;

    public Button threshBtn, rechargeBtn, redoButton, cropButton;

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
                break;

            case R.id.threshBtn:
                threshold();
                break;

        }
    }

    public void instantiate() {
//        image = getRotatedImage(CameraActivity.getBitmapImage());


        image = BitmapFactory.decodeResource(getResources(), R.drawable.joker_leto);


        threshBtn = (Button) findViewById(R.id.threshBtn);


        rechargeBtn = (Button) findViewById(R.id.rechargeBtn);
        redoButton = (Button) findViewById(R.id.redoBtn);
        cropButton = (Button) findViewById(R.id.cropBtn);

        capturedImage = (ImageView) findViewById(R.id.imageView);
        capturedImage.setImageBitmap(image);

        clippingWindow = (ClippingWindow) findViewById(R.id.clipping);
        clippingWindow.setVisibility(View.VISIBLE);


        threshBtn.setOnClickListener(this);
        rechargeBtn.setOnClickListener(this);
        redoButton.setOnClickListener(this);
        cropButton.setOnClickListener(this);

    }


    public void crop() {
        rechargeBtn.setVisibility(View.INVISIBLE);
        threshBtn.setVisibility(View.VISIBLE);
        cropButton.setVisibility(View.INVISIBLE);
        clippingWindow.setVisibility(View.INVISIBLE);
        croppedImage = clippingWindow.getCroppedImage();
        capturedImage.setImageBitmap(croppedImage);
    }


    Bitmap getRotatedImage(Bitmap bmp) {
        Matrix returnImage = new Matrix();
        returnImage.postRotate(90);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), returnImage, true);
    }

    int[] createPixelArray(int width, int height, Bitmap thresholdImage) {

        int[] pixels = new int[width * height];
        thresholdImage.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;

    }

    public void threshold() {
        threshBtn.setVisibility(View.INVISIBLE);
        rechargeBtn.setVisibility(View.VISIBLE);
        Thread grayScaleThread;
        //TODO see if you can avoid creating threads yourself. acquire form somewhere
        grayScaleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                GrayScale grayScale = new ITURGrayScale(BitmapFactory.decodeResource(getResources(), R.drawable.test5b));

                //ITURGrayScale grayScale= new ITURGrayScale(sourceImageBitmap,MainActivity.this);
                Bitmap bmResult = grayScale.grayScale();
                //Log.d("thread", Thread.currentThread().toString());

                Threshold threshold = new BradleyThreshold();
                bmResult = threshold.threshold(bmResult);

                bmResult = PrepareImage.addBackgroundPixels(bmResult);
                int height = bmResult.getHeight();
                int width = bmResult.getWidth();
                Log.d("width" , String.valueOf(width));

//                        get value of pixels from binary image
                int[] pixels = createPixelArray(width, height, bmResult);

//                       Create a binary array called booleanImage using pixel values in threshold bitmap
                boolean[] booleanImage = new boolean[width * height];
                if (Arrays.asList(booleanImage).contains(false)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CropActivity.this, "Numbers Unreadable. Please, scan again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

//                      false if pixel is a background pixel, else true
                    int index = 0;
                    for (int pixel : pixels) {

                        if (pixel != -1) {
                            booleanImage[index] = true;
                        }

                        index++;
                    }
                    CcLabeling ccLabeling = new CcLabeling();
                    componentBitmaps = ComponentImages.CreateImageFromComponents(ccLabeling.CcLabels(booleanImage, width));
//
//                    Intent viewSegmentIntent = new Intent(getApplicationContext(),ViewSegments.class);
//                    startActivity(viewSegmentIntent);
                }
                Message msgToUIThread = Message.obtain();
                msgToUIThread.obj = bmResult;
                handler.sendMessage(msgToUIThread);
            }
        });
        grayScaleThread.start();
    }


    public static ArrayList<Bitmap> getBitmapImage() {
        return componentBitmaps;
    }


}
