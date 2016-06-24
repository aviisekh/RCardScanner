package com.scanner.cardreader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CropActivity extends AppCompatActivity {

    ImageView capturedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        capturedImage = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = CameraActivity.getBitmapImage();
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
                Intent i = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(i);



            }
        });












    }
}
