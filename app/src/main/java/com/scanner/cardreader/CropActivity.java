package com.scanner.cardreader;

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
//                        GrayScale grayScale = new ITURGrayScale(BitmapFactory.decodeResource(getResources(),R.drawable.screen));

                        //ITURGrayScale grayScale= new ITURGrayScale(sourceImageBitmap,MainActivity.this);
                        Bitmap bmResult = grayScale.grayScale();
                        //Log.d("thread", Thread.currentThread().toString());
                        Threshold threshold = new BradleyThreshold();
                        bmResult = threshold.threshold(bmResult);
                        bmResult = PrepareImage.addBackgroundPixels(bmResult);
                        int height = bmResult.getHeight();
                        int width = bmResult.getWidth();

//                        get value of pixels from binary image
                        int[] pixels = createPixelArray(width,height,bmResult);

//                       Create a binary array called booleanImage using pixel values in threshold bitmap
                        boolean[] booleanImage = new boolean[width*height];
//                      false if pixel is a background pixel, else true
                        int index = 0;
                        for(int pixel : pixels){

                            if(pixel != -1){
                                booleanImage[index] = true;
                            }

                            index++;
                        }



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
//        image = getRotatedImage(CameraActivity.getBitmapImage());
        image = BitmapFactory.decodeResource(getResources(),R.drawable.noiseimage);
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

    int[] createPixelArray(int width, int height,Bitmap thresholdImage){

        int[] pixels = new int[width*height];
        thresholdImage.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }
}
