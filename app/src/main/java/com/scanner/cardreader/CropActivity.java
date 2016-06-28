package com.scanner.cardreader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CropActivity extends AppCompatActivity {

    public static ImageView capturedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        capturedImage = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = CameraActivity.getBitmapImage();

       // Log.d("bitmap", "w,h::"+ bitmap.getWidth()+","+bitmap.getHeight());
        capturedImage.setImageBitmap(bitmap);
        Button scanBtn = (Button) findViewById(R.id.scanBtn);
        Button rechargeBtn = (Button) findViewById(R.id.rechargeBtn);
        Button redoButton = (Button) findViewById(R.id.redoBtn);


        assert scanBtn != null;
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Call the gray scaling method in another thread

            }
        });

        assert rechargeBtn != null;
        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Use the extracted pin number and top up balance
            }
        });

        assert redoButton != null;
        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                load Camera Activity to re-take the image of recharge card
                onBackPressed();


            }
        });
    }




}
