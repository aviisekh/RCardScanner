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

    public Button threshBtn, rechargeBtn, redoButton, cropButton;

    public Bitmap image;
    private Bitmap croppedImage;
    private static Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        instantiate();


//        linked with message queue of main thread
        handler = new Handler() {
            //executed when msg arrives from thread
            @Override
            public void handleMessage(Message msg) {
                Log.d("imageview", String.valueOf(capturedImage.getWidth()));
                Bitmap result = (Bitmap) msg.obj;
//                capturedImage.setLayerType(View.LAYER_TYPE_SOFTWARE,new Paint(0xFFFFFF));
//                Log.d("drawable", String.valueOf(capturedImage.getDrawable().getIntrinsicWidth()));
                capturedImage.setImageBitmap(result);
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
//                remove jar which is not for android
//                GammaCorrection gc = new GammaCorrection(4.5);

//                gc.applyInPlace(croppedImage);
//                Deskew d = new Deskew();
//                double angle = d.doIt(croppedImage);
//
//                Median m = new Median(2);
//                m.applyInPlace(bmResult);

                GrayScale grayScale = new ITURGrayScale(croppedImage);
                Bitmap bmResult = grayScale.grayScale();


                Threshold threshold = new BradleyThreshold();
                bmResult = threshold.threshold(bmResult);

                ImageSkewChecker ds = new ImageSkewChecker();
                double angle = ds.getSkewAngle(croppedImage);
                Log.d("angle", String.valueOf(angle));

//
//                Rotate rotate = new Rotate(croppedImage.getWidth(), croppedImage.getHeight(), angle);
//                bmResult = rotate.applyInPlace(bmResult);

                RotateNearestNeighbor rn = new RotateNearestNeighbor(angle);
                bmResult = rn.applyInPlace(bmResult);

//
                Message msgToUIThread = Message.obtain();
                msgToUIThread.obj = bmResult;
                handler.sendMessage(msgToUIThread);

                //threshold();
                break;
        }
    }


    public void instantiate() {

//        image = getRotatedImage(CameraActivity.getBitmapImage());
        image = BitmapFactory.decodeResource(getResources(), R.drawable.skewtest);

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
        crop();

    }

    public void crop() {
        rechargeBtn.setVisibility(View.INVISIBLE);
        threshBtn.setVisibility(View.VISIBLE);
        cropButton.setVisibility(View.INVISIBLE);
        clippingWindow.setVisibility(View.INVISIBLE);
//        croppedImage = clippingWindow.getCroppedImage();

        croppedImage = BitmapFactory.decodeResource(getResources(), R.drawable.skewtest);
//        capturedImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        capturedImage.setImageBitmap(croppedImage);

    }


    Bitmap getRotatedImage(Bitmap bmp) {
        Matrix returnImage = new Matrix();
        returnImage.postRotate(90);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), returnImage, true);
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
    }
}
