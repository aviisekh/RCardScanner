package com.scanner.cardreader.camera;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.scanner.cardreader.R;

import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class CropActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {

    private ImageView cropImView;
    private ClippingWindow clippingWindow;
    private Vibrator haptics;
    private final int HAPTICS_CONSTANT = 50;


    private ImageButton redoButton, cropButton;


    private Bitmap image;
    private Bitmap croppedImage;
    private RelativeLayout relativeLayout;
    private Rect imageCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        //byte[] byteArray = getIntent().getByteArrayExtra("image");
        //image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        instantiate();


    }

    @Override
    public void onClick(View v) {
        haptics.vibrate(HAPTICS_CONSTANT);
        switch (v.getId()) {
            case R.id.cropBtn:
                crop();
                changeIntent();
                break;

            case R.id.redoFromCrop:
                onBackPressed();
                break;


        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.cropBtn:
                Toast.makeText(this, "Crop", Toast.LENGTH_LONG).show();
                break;


            case R.id.redoFromCrop:
                Toast.makeText(this, "Redo", Toast.LENGTH_LONG).show();
                break;

        }
        return true;
    }


    private void instantiate() {
        redoButton = (ImageButton) findViewById(R.id.redoFromCrop);
        haptics = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        cropButton = (ImageButton) findViewById(R.id.cropBtn);

        redoButton.setOnClickListener(this);
        cropButton.setOnClickListener(this);

        //image = BitmapFactory.decodeResource(getResources(), R.drawable.ntc_target_test);
        image = CameraAccess.getBitmapImage();
        cropImView = (ImageView) findViewById(R.id.imageView);
        cropImView.setImageBitmap(image);

//        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout1);
        clippingWindow = (ClippingWindow) findViewById(R.id.clipping);

        ViewTreeObserver vto = cropImView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                imageCoordinates = ImageLocatorInImageview.getImageCoordinates(cropImView);
                //clippingWindow = new ClippingWindow(getApplicationContext(), imageCoordinates);
                clippingWindow.initializeBoundary(imageCoordinates);


            }
        });

//        relativeLayout.addView(clippingWindow);


    }


    private void crop() {


        Rect bitmapClippingCoordinates = CroppingCoordinates.getCroppingCoordinates(cropImView, clippingWindow.getClippingWindowCoordinates());
//        cropImView.setImageBitmap(croppedImage);
        Bitmap originalBitmap = ((BitmapDrawable) cropImView.getDrawable()).getBitmap();
        croppedImage = Bitmap.createBitmap(originalBitmap, bitmapClippingCoordinates.left, bitmapClippingCoordinates.top, bitmapClippingCoordinates.right - bitmapClippingCoordinates.left, bitmapClippingCoordinates.bottom - bitmapClippingCoordinates.top);

    }

    private void changeIntent() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        croppedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Intent intent = new Intent(this, RechargeActivity.class).putExtra("image", byteArray);
        startActivity(intent);
    }


}
