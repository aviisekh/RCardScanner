package com.scanner.cardreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by aviisekh on 6/27/16.
 */
public class ClippingWindow extends ImageView {

    Bitmap horizontal;
    int x,y;
    ClippingWindow(Context context)
    {
        super(context);
        horizontal= BitmapFactory.decodeResource(getResources(),R.drawable.horizontal);
        x=0;
        y=0;

    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);

//        Rect ourRect = new Rect();
//        ourRect.set(0,0,canvas.getWidth(),canvas.getHeight()/2);
//
//        Paint blue = new Paint();
//        blue.setColor(Color.BLUE);
//        blue.setStyle(Paint.Style.FILL);
//
//        canvas.drawRect(ourRect,blue);

        Paint p = new Paint();
        canvas.drawBitmap(horizontal,x,y,p);


    }
}
