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

        //linked with message queue of main thread
        handler = new Handler() {
            //executed when msg arrives from thread
            @Override
            public void handleMessage(Message msg) {
                Log.d("imageview", String.valueOf(capturedImage.getWidth()));
                Bitmap result = (Bitmap) msg.obj;
//                capturedImage.setX(result.getWidth());
//                capturedImage.setY(result.getHeight());
                capturedImage.setImageBitmap(result);
                Log.d("drawable", String.valueOf(capturedImage.getDrawable().getIntrinsicWidth()));
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
                //remove jar which is not for android
//              GrayScale grayScale = new ITURGrayScales(croppedImage);
//              bmResult = grayScale.grayScale();

//                com.scanner.cardreader.FastBitmap fb = new com.scanner.cardreader.FastBitmap(croppedImage);
//                GammaCorrection gc = new GammaCorrection(1.5);
//                gc.applyInPlace(fb);


                //Log.d("angle", Double.toString(angle));
//                Bitmap temp = fb.toBitmap();

                GrayScale grayScale = new ITURGrayScale(croppedImage);
                Bitmap bmResult = grayScale.grayScale();

                //fb.toGrayscale();
//                System.out.println("grayscaled values");
                int width = bmResult.getWidth();
                int height = bmResult.getHeight();
//
//                for (int i = 0; i < height; i++) {
//                    for (int j = 0; j < width; j++) {
//
//                        System.out.println("after gray(" + i + "," + j + ")"
//                                + Integer.toHexString(bmResult.getPixel(i, j)));
//                    }
//                }


//                Deskew d = new Deskew();
//                double angle = d.doIt(croppedImage);
//
                DocumentSkewChecker ds = new DocumentSkewChecker();
                double angle = ds.getSkewAngle(bmResult);

                Log.d("angle", String.valueOf(angle));

                Median m = new Median(3);
                m.applyInPlace(bmResult);
                Threshold threshold = new BradleyThreshold();
                bmResult = threshold.threshold(bmResult);
//
//                RotateNearestNeighbor rn = new RotateNearestNeighbor(angle);
//                bmResult = rn.applyInPlace(bmResult);


                int newWidth = capturedImage.getWidth();
                int newHeight = capturedImage.getHeight();

                // calculate the scale - in this case = 0.4f
                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;

                // createa matrix for the manipulation
                Matrix matrix = new Matrix();
                // resize the bit map
                //matrix.preScale(scaleWidth, scaleHeight);
                // rotate the Bitmap
                matrix.preRotate((float) angle);

                // recreate the new Bitmap
                Bitmap resizedBitmap = Bitmap.createBitmap(bmResult, 0, 0,
                        width, height, matrix, true);
                bmResult=resizedBitmap;
//
//                System.out.println("rotated values");
//                int w = resizedBitmap.getWidth();
//                int h = resizedBitmap.getHeight();
//
//                for (int i = 0; i < h; i++) {
//                    for (int j = 0; j < w; j++) {
//                        System.out.println("rotated(" + i + "," + j + ")" + Integer.toHexString(resizedBitmap.getPixel(i, j)));
//                    }
//                }


                // center the Image
//                capturedImage.setScaleType(ImageView.ScaleType.CENTER);


//                RotateBicubic rb = new RotateBicubic(-angle, true);
//                RotateBilinear rbi = new RotateBilinear(-angle, true);
//                Log.d("bitmapbefore",String.valueOf(beforeSkew.getWidth()));
//
//                Log.d("bitmapafter",String.valueOf(fb.getWidth()));


//                Mean me= new Mean(3);
//                me.applyInPlace(fb);

//                ImageNormalization im = new ImageNormalization(100, 150);
//                im.applyInPlace(fb);

       /*         BradleyLocalThreshold bradley = new BradleyLocalThreshold();
                  bradley.applyInPlace(fb);
*/

//                AdaptiveContrastEnhancement ac= new AdaptiveContrastEnhancement(4,1,1,0.4,0.9);
//                ac.applyInPlace(fb);


                Message msgToUIThread = Message.obtain();
                msgToUIThread.obj = bmResult;
                handler.sendMessage(msgToUIThread);

                //threshold();
                break;
        }
    }


    public void instantiate() {

//        image = getRotatedImage(CameraActivity.getBitmapImage());
        image = BitmapFactory.decodeResource(getResources(), R.drawable.topleft);


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
//        croppedImage = clippingWindow.getCroppedImage();
        croppedImage = BitmapFactory.decodeResource(getResources(), R.drawable.skewtext);
        capturedImage.setImageBitmap(croppedImage);
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
                //ITURGrayScales grayScale= new ITURGrayScales(sourceImageBitmap,MainActivity.this);
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
