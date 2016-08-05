package com.scanner.cardreader.camera;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by aviisekh on 8/4/16.
 */
public class BottomBorderOverlay extends View {
    private Paint drawRect;

    private Rect rect = new Rect();       //dark overlay on the top of clipping window

    static public int parentWidth,parentHeight,top,left,bottom,right;
    public BottomBorderOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setFocusable(false);
//        setFocusableInTouchMode(false);

        setupPaint();
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }


    public void init()
    {
        left = 0;
        top = 0;
        right = parentWidth;
        bottom = parentHeight;
    }

    private void setupPaint() {
        // Setup paint with color and stroke styles
        drawRect = new Paint();

        drawRect.setColor(Color.argb(200, 0, 0, 0));
        drawRect.setAntiAlias(true);
        drawRect.setStrokeWidth(5);
        drawRect.setStyle(Paint.Style.FILL);
        drawRect.setStrokeJoin(Paint.Join.ROUND);
        drawRect.setStrokeCap(Paint.Cap.ROUND);


    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRectangle(canvas);
    }
    public void drawRectangle(Canvas canvas) {

        rect.set(0, 0, parentWidth, parentHeight );
        canvas.drawRect(rect, drawRect);
    }

}
