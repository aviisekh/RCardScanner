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

    private final int WIDTH_OFFSET = 7;
    private final int HEIFHT_OFFSET = 6;
    private static int parentWidth,parentHeight,top,left,bottom,right;


    public CameraOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
    }


    private void setupPaint() {
        // Setup paint with color and stroke styles
        drawRect = new Paint();
        drawLineBoundary = new Paint();
        drawLineGrid = new Paint();

        drawRect.setColor(Color.argb(50, 0, 0, 0));


        drawLineBoundary.setColor(Color.argb(255, 12, 139, 86));
        drawLineBoundary.setStrokeWidth(5);

        drawLineGrid.setColor(Color.argb(50, 255, 255, 255));
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

    private void init()
    {
        left = parentWidth / WIDTH_OFFSET;
        top = parentHeight / HEIFHT_OFFSET;
        right = 6*parentWidth / WIDTH_OFFSET;
        bottom = 2*parentHeight / HEIFHT_OFFSET;
    }

    private void drawRectangle(Canvas canvas) {

        rectTop.set(0, 0, parentWidth, top);
        rectBottom.set(0, bottom, parentWidth, parentHeight);
        rectLeft.set(0, top, left, bottom);
        rectRight.set(right, top, parentWidth, bottom);

        canvas.drawRect(rectTop, drawRect);
        canvas.drawRect(rectBottom, drawRect);
        canvas.drawRect(rectLeft, drawRect);
        canvas.drawRect(rectRight, drawRect);
    }

    private void drawLine(Canvas canvas) {
        canvas.drawLine(left + (right - left) / 3, top, left + (right - left) / 3, bottom, drawLineGrid);
        canvas.drawLine(left + 2 * (right - left) / 3, top, left + 2 * (right - left) / 3, bottom, drawLineGrid);
        canvas.drawLine(left, top + (bottom - top) / 3, right, top + (bottom - top) / 3, drawLineGrid);
        canvas.drawLine(left, top + 2 * (bottom - top) / 3, right, top + 2 * (bottom - top) / 3, drawLineGrid);

        canvas.drawLine(left, top-2, left, top+20, drawLineBoundary);
        canvas.drawLine(left-2, top, left+20, top, drawLineBoundary);
        canvas.drawLine(left-2, bottom, left+20, bottom, drawLineBoundary);
        canvas.drawLine(left, bottom+2, left, bottom-20, drawLineBoundary);

        canvas.drawLine(right, top-2, right, top+20, drawLineBoundary);
        canvas.drawLine(right+2, top, right-20, top, drawLineBoundary);
        canvas.drawLine(right+2, bottom, right-20, bottom, drawLineBoundary);
        canvas.drawLine(right, bottom+2, right, bottom-20, drawLineBoundary);


    }

    public static Rect getRectangleCoordinates()
    {
        Rect rectangleCoordinates = new Rect();
        rectangleCoordinates.left = left;
        rectangleCoordinates.right = right;
        rectangleCoordinates.top = top;
        rectangleCoordinates.bottom = bottom;
        return rectangleCoordinates;
    }

    public static int getParentWidth()
    {
        return parentWidth;
    }

    public static int getParentHeight()
    {
        return parentHeight;
    }
}