package com.scanner.cardreader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class Camera extends AppCompatActivity {

    public static final int REQUEST_CAMERA = 1;

    private ImageSurfaceView imageSurfaceView;
    private Camera camera;
    private FrameLayout cameraPreviewLayout;
    private ImageView capturedImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        imageSurfaceView = new ImageSurfaceView(Camera.this, camera);

//        imageSurfaceView = (ImageSurfaceView) findViewById(R.id.cameraFrameLayout);


        cameraPreviewLayout = (FrameLayout) findViewById(R.id.cameraFrameLayout);


        }
    }





