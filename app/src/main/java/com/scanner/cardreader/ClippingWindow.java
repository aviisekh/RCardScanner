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


public class ClippingWindow extends View {

       private final int paintColor = Color.argb(100,255,0,0);
       private Paint drawPaint;
       float pointX;
       float pointY;
       float startX;
       float startY;

       public ClippingWindow(Context context, AttributeSet attrs) {
           super(context, attrs);
           //setFocusable(true);
           //setFocusableInTouchMode(true);
           setupPaint();
       }

       private void setupPaint() {
// Setup paint with color and stroke styles
           drawPaint = new Paint();
           drawPaint.setColor(paintColor);
           drawPaint.setAntiAlias(true);
           drawPaint.setStrokeWidth(5);
           drawPaint.setStyle(Paint.Style.FILL);
           drawPaint.setStrokeJoin(Paint.Join.ROUND);
           drawPaint.setStrokeCap(Paint.Cap.ROUND);
       }

       @Override
       public boolean onTouchEvent(MotionEvent event) {
           pointX = event.getX();
           pointY = event.getY();
           //Checks for the event that occurs
           switch (event.getAction()) {
               case MotionEvent.ACTION_DOWN :
                   startX = pointX;
                   startY = pointY;
                   break;

               /*case MotionEvent.ACTION_UP:
                   startX = pointX;
                   startY = pointY;
                   return true;*/

               case MotionEvent.ACTION_MOVE:
                   //return true;
                   break;

               default:
                   return false;
           }
            // Force a view to draw again
           postInvalidate();
           return true;
       }

       @Override
       protected void onDraw(Canvas canvas) {
           //canvas.drawRect(60, 60, 100, 100, drawPaint);
           //canvas.drawRect(startX=100, startY=100, pointX=500, pointY=500, drawPaint);
           canvas.drawRect(startX, startY, pointX, pointY, drawPaint);
       }
}

