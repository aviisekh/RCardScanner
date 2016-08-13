package com.scanner.cardreader.camera;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.scanner.cardreader.R;

import java.io.ByteArrayOutputStream;



public class CropActivity extends AppCompatActivity implements View.OnClickListener {
    private   ImageView cropImView;
    private ClippingWindow clippingWindow;

    private ImageButton redoButton,cropButton;

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




//        linked with message queue of main thread
//        imageHandler = new Handler() {
//            //executed when msg arrives from thread
//            @Override
//            public void handleMessage(Message msg) {
//                Bitmap result = (Bitmap) msg.obj;
//                cropImView.setImageBitmap(result);
//            }
//        };




    }

    @Override
    public void onClick(View v) {
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


    private void instantiate() {
        redoButton = (ImageButton) findViewById(R.id.redoFromCrop);
        cropButton = (ImageButton) findViewById(R.id.cropBtn);
        redoButton.setOnClickListener(this);
        cropButton.setOnClickListener(this);

        image = BitmapFactory.decodeResource(getResources(), R.drawable.ntc_target_test);

        cropImView = (ImageView) findViewById(R.id.imageView);
        cropImView.setImageBitmap(image);

//        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout1);
        clippingWindow= (ClippingWindow) findViewById(R.id.clipping);

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


        Rect croppingCoordinates = CroppingCoordinates.getCroppingCoordinates(cropImView);
        //croppedImage = clippingWindow.getCroppedImage();
        //cropImView.setImageBitmap(croppedImage);

        Bitmap originalBitmap = ((BitmapDrawable) cropImView.getDrawable()).getBitmap();
        croppedImage =  Bitmap.createBitmap(originalBitmap, croppingCoordinates.left, croppingCoordinates.top, croppingCoordinates.right - croppingCoordinates.left, croppingCoordinates.bottom - croppingCoordinates.top);

   }

    private void changeIntent()
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        croppedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Intent intent = new Intent(this, RechargeActivity.class).putExtra("image",byteArray);
        startActivity(intent);
    }


}
