package com.scanner.cardreader.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by aviisekh on 8/2/16.
 */
public class CameraOverlay extends View {

    private Paint drawRect;
    private Paint drawLineBoundary;
    private Paint drawLineGrid;

    //Rect rect = new Rect();
    private Rect rectTop = new Rect();       //dark overlay on the top of clipping window
    private Rect rectBottom = new Rect();
    private Rect rectLeft = new Rect();
    private Rect rectRight = new Rect();

    static public int parentWidth,parentHeight,top,left,bottom,right;
    public CameraOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setFocusable(false);
//        setFocusableInTouchMode(false);

        setupPaint();
    }


    private void setupPaint() {
        // Setup paint with color and stroke styles
        drawRect = new Paint();
        drawLineBoundary = new Paint();
        drawLineGrid = new Paint();

        drawRect.setColor(Color.argb(150, 0, 0, 0));
        drawRect.setAntiAlias(true);
        drawRect.setStrokeWidth(5);
        drawRect.setStyle(Paint.Style.FILL);
        drawRect.setStrokeJoin(Paint.Join.ROUND);
        drawRect.setStrokeCap(Paint.Cap.ROUND);


        drawLineBoundary.setColor(Color.argb(255, 255, 255, 255));
        drawLineBoundary.setStrokeWidth(5);

        drawLineGrid.setColor(Color.argb(200, 255, 255, 255));
        drawLineGrid.setStrokeWidth(2);
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRectangle(canvas);
        drawLine(canvas);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }

    public void init()
    {
        left = parentWidth / 7;
        top = parentHeight / 6;
        right = 6*parentWidth / 7;
        bottom = parentHeight / 4;
    }

    public void drawRectangle(Canvas canvas) {

        rectTop.set(0, 0, parentWidth, top);
        rectBottom.set(0, bottom, parentWidth, parentHeight);
        rectLeft.set(0, top, left, bottom);
        rectRight.set(right, top, parentWidth, bottom);

        canvas.drawRect(rectTop, drawRect);
        canvas.drawRect(rectBottom, drawRect);
        canvas.drawRect(rectLeft, drawRect);
        canvas.drawRect(rectRight, drawRect);
    }

    public void drawLine(Canvas canvas) {
        canvas.drawLine(left + (right - left) / 3, top, left + (right - left) / 3, bottom, drawLineGrid);
        canvas.drawLine(left + 2 * (right - left) / 3, top, left + 2 * (right - left) / 3, bottom, drawLineGrid);
        canvas.drawLine(left, top + (bottom - top) / 3, right, top + (bottom - top) / 3, drawLineGrid);
        canvas.drawLine(left, top + 2 * (bottom - top) / 3, right, top + 2 * (bottom - top) / 3, drawLineGrid);

        canvas.drawLine(left, top, right, top, drawLineBoundary);
        canvas.drawLine(left, top, left, bottom, drawLineBoundary);
        canvas.drawLine(left, bottom, right, bottom, drawLineBoundary);
        canvas.drawLine(left, bottom, right, bottom, drawLineBoundary);
        canvas.drawLine(right, top, right, bottom, drawLineBoundary);


    }
}