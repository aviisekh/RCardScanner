package com.scanner.cardreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


public class ClippingWindow extends ImageView {

    Paint myRect = new Paint();
    Path path = new Path();

    public ClippingWindow(Context context) {
        super(context);
        init(null, 0);
        //horizontal= BitmapFactory.decodeResource(getResources(),R.drawable.horizontal);
//        x=0;
//        y=0;
    }

    public ClippingWindow(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs,0);
    }

    public ClippingWindow(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }






    public void init(AttributeSet attrs, int defStyle)
    {
        //myRect.setAlpha(100);
        myRect.setColor(Color.RED);
        myRect.setAlpha(100);
    }

    public void onDraw(Canvas canvas)
    {


        super.onDraw(canvas);
 /*       BitmapDrawable drw = (BitmapDrawable) CropActivity.capturedImage.getDrawable();
        Bitmap bmp = drw.getBitmap();

        Log.d("drawable: ", Integer.toString(bmp.getHeight()));
        Log.d("canvas: ",Integer.toString(canvas.getHeight()));

        int topLeftX = (canvas.getWidth()-bmp.getWidth())/2;
        int topLeftY = (canvas.getHeight()/2)-(bmp.getHeight()/2);
//
        int bottomRightX = (canvas.getWidth()/2)+(bmp.getWidth()/2);
        int bottomRightY = (canvas.getHeight()/2)+(bmp.getHeight()/2);*/

        //canvas.drawLine(0,0,getWidth(),getHeight(),myRect);
        canvas.drawPath(path,myRect);


    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        float touchX=motionEvent.getX();
        float touchY=motionEvent.getY();

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(touchX,touchY);
                break;

            case MotionEvent.ACTION_MOVE:
                path.lineTo(touchX,touchY);
                break;

            case MotionEvent.ACTION_UP:
                break;
        }

        invalidate();
        return true;

    }

}
