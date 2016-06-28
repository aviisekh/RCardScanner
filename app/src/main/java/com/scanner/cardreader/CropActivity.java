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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CropActivity extends AppCompatActivity {

    ImageView capturedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCrgieate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        capturedImage = (ImageView) findViewById(R.id.imageView);
        Bitmap myBitmap = CameraActivity.getBitmapImage();

        //capturedImage.setImageBitmap(bitmap);
        Button scanBtn = (Button) findViewById(R.id.scanBtn);
        Button rechargeBtn = (Button) findViewById(R.id.rechargeBtn);
        Button redoButton = (Button) findViewById(R.id.redoBtn);


        Paint myPaint = new Paint();
        myPaint.setColor(Color.argb(100,255,0,0));
        myPaint.setStyle(Paint.Style.FILL);
       // myPaint.setStrokeWidth(10);

        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);

        tempCanvas.drawBitmap(myBitmap, 0, 0, null);
        RectF myRect = new RectF(100,100,myBitmap.getWidth()-20,myBitmap.getHeight()-20);

        tempCanvas.drawRect(myRect,  myPaint);

        capturedImage.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        //Draw here



//        ClippingWindow win= new ClippingWindow(this);
//        setContentView(win);

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
