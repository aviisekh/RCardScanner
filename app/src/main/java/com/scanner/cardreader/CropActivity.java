package com.scanner.cardreader;

import android.content.Intent;
import android.graphics.Bitmap;
<<<<<<< HEAD
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
=======
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
>>>>>>> master
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

<<<<<<< HEAD
public class CropActivity extends AppCompatActivity {

    ImageView capturedImage;
=======
import java.io.ByteArrayOutputStream;

public class CropActivity extends AppCompatActivity {

    static ImageView capturedImage;
>>>>>>> master

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        capturedImage = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = CameraActivity.getBitmapImage();
<<<<<<< HEAD
=======
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 8;
//        Bitmap resized = BitmapFactory.decodeStream()
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 10,byteArrayOutputStream);
//        byte[] data = byteArrayOutputStream.toByteArray();
//        Bitmap resizedBitmap = BitmapFactory.decodeByteArray(data, 0 , data.length);
>>>>>>> master
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
