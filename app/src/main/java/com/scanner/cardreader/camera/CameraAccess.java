package com.scanner.cardreader.camera;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scanner.cardreader.MainActivity;
import com.scanner.cardreader.R;


public class CameraAccess extends Activity implements SurfaceHolder.Callback {

    private Camera camera;
    SurfaceHolder surfaceHolder;
    SurfaceView surfaceView;
    boolean isPreviewing = false;
    static int count = 1;
    CameraOverlay cameraOverlay;
    BottomBorderOverlay bottomBorderOverlay,animateView;

    android.support.design.widget.FloatingActionButton takePicture;
    TextView simInfo;

    public static Bitmap bitmap;

    int cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        cameraId = findRearFacingCamera();
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraOverlay = (CameraOverlay) findViewById(R.id.clipping);

        bottomBorderOverlay = (BottomBorderOverlay) findViewById(R.id.bottomBorder);
        animateView = (BottomBorderOverlay) findViewById(R.id.animate);


        simInfo = (TextView) findViewById(R.id.simInfo);
        simInfo.setText(MainActivity.SIM);




        final PictureCallback jpegPictureCallBack = new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                try {
                    if (bytes != null)
                    {
                        Intent i = new Intent(getApplicationContext(), CropActivity.class);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                        setBitmapImage(bitmap);
                        startActivity(i);
                        camera.stopPreview();
                        camera.release();
                        camera.setPreviewCallback(null);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        };

        takePicture = (android.support.design.widget.FloatingActionButton) findViewById(R.id.takepicture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(camera == null){
//                    Toast.makeText(CameraAccess.this, "No camera", Toast.LENGTH_SHORT).show();
                }
                else {
                    camera.takePicture(null,null,null,jpegPictureCallBack);
//                    Toast.makeText(CameraAccess.this, "Button CLicked :)", Toast.LENGTH_SHORT).show();
                }
            }
        });


        CameraOverlay previewBackground =  (CameraOverlay) findViewById(R.id.overlay);



        previewBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                takePicture.setEnabled(false);
                if (event.getAction() == event.ACTION_DOWN) {
                    try {
                        camera.autoFocus(autoFocusCallback);

                        if (count % 2 == 0) {
                            TranslateAnimation animate = new TranslateAnimation(0,0 - animateView.getWidth(), 0, 0);
                            animate.setDuration(500);
                            animate.setFillAfter(true);
                            animateView.startAnimation(animate);
                            animateView.setVisibility(View.GONE);
                            count++;
                        }
                        else
                        {
                            TranslateAnimation animate = new TranslateAnimation(0 - animateView.getWidth(), 0, 0, 0);

                            animate.setDuration(500);
                            //animate.setFillAfter(true);
                            animateView.startAnimation(animate);
                            animateView.setVisibility(View.VISIBLE);
                            count++;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;

            }


        });


    }

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean b, Camera camera) {
            takePicture.setEnabled(true);
        }
    };



//    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
//        @Override
//        public void onShutter() {
//
//        }
//    };

    public static Bitmap getBitmapImage()
    {
        bitmap=getRotatedImage(bitmap);
        int left = (bitmap.getWidth()*CameraOverlay.left)/CameraOverlay.parentWidth;
        int right  = (bitmap.getWidth()*CameraOverlay.right)/CameraOverlay.parentWidth;
        int top  = (bitmap.getHeight()*CameraOverlay.top)/CameraOverlay.parentHeight;
        int bottom  = (bitmap.getHeight()*CameraOverlay.bottom)/CameraOverlay.parentHeight;

        Bitmap bmp = Bitmap.createBitmap(bitmap, left,top,right-left,bottom-top);
/*        Log.d("bitmapWidth",Integer.toString(bitmap.getWidth()));
        Log.d("bitmapHeight",Integer.toString(bitmap.getHeight()));
        Log.d("left,right",Integer.toString(left)+" "+Integer.toString(right));*/
        return bmp;

    }

    static Bitmap getRotatedImage(Bitmap bmp) {
        Matrix returnImage = new Matrix();
        returnImage.postRotate(90);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), returnImage, true);

    }

    public void setBitmapImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        try {
            camera = Camera.open(cameraId);
            camera.setDisplayOrientation(90);
            camera.startPreview();
            isPreviewing = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("camera", "not found");
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (isPreviewing) {
            camera.stopPreview();
            isPreviewing = false;
        }

        if (camera != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
//                camera.enableShutterSound(true);
                camera.setDisplayOrientation(90);
                camera.startPreview();
                isPreviewing = true;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        try {
            camera.stopPreview();
            camera.release();
            camera.setPreviewCallback(null);
            isPreviewing = false;

            camera = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Check if a rear camera is available
    private int findRearFacingCamera() {
        int cameraId = 0;
//        Searching for the rear facing camera
        int noOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < noOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
}