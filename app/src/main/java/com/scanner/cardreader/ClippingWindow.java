package com.scanner.cardreader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class ClippingWindow extends View {


    private final int paintColor = Color.argb(100, 255, 0, 0);

    private Paint drawRect;
    //private Paint drawLine;
    Rect rect = new Rect();

    boolean draggable;
    boolean croppable, topCroppable, bottomCroppable, rightCroppable, leftCroppable;
    boolean eventIgnore = false;

    static int parentWidth;
    static int parentHeight;
    final int TOLERENCE = 20;

    int pointX;
    int pointY;
    int prevX;
    int prevY;
    int moveX;
    int moveY;
    static int left, top, right, bottom;

    public ClippingWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(false);
        setupPaint();
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }


    void init() {
        left = parentWidth / 4;
        top = parentHeight / 4;
        right = 3 * parentWidth / 4;
        bottom = 3 * parentHeight / 4;
        rect.set(left, top, right, bottom);
    }

    private void setupPaint() {
        // Setup paint with color and stroke styles
        drawRect = new Paint();
        drawRect.setColor(paintColor);
        drawRect.setAntiAlias(true);
        drawRect.setStrokeWidth(5);
        drawRect.setStyle(Paint.Style.FILL);
        drawRect.setStrokeJoin(Paint.Join.ROUND);
        drawRect.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        pointX = (int) event.getX(); //get the touch position
        pointY = (int) event.getY();
        //Checks for the event that occurs

        if (eventIgnore)
            return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                croppable = topCroppable = bottomCroppable = leftCroppable = rightCroppable = false;
                prevX = pointX;
                prevY = pointY;
                if (prevX > left & prevX < right) {
                    if (prevY < top + TOLERENCE & prevY > top - TOLERENCE) {
                        topCroppable = true;
                        croppable = true;
                    } else if (prevY < bottom + TOLERENCE & prevY > bottom - TOLERENCE) {
                        bottomCroppable = true;
                        croppable = true;
                    }
                }

                if (prevY > top & prevY < bottom) {
                    if (prevX < left + TOLERENCE & prevX > left - TOLERENCE) {
                        leftCroppable = true;
                        croppable = true;
                    }

                    if (prevX < right + TOLERENCE & prevX > right - TOLERENCE) {
                        rightCroppable = true;
                        croppable = true;
                    }
                }

                if (rect.contains(prevX, prevY) & !croppable) {
                    draggable = true;               //if touch begins from outside the crop window
                } else if (!rect.contains(prevX, prevY) | croppable) {
                    draggable = false;
                }
                break;


            case MotionEvent.ACTION_MOVE:
                moveX = (pointX - prevX); // find the direction of movement with units of movement
                moveY = (pointY - prevY);
                Log.d("move", Integer.toString(moveX));

                if (draggable) {
                    left = left + moveX;
                    right = right + moveX;
                    top = top + moveY;
                    bottom = bottom + moveY;
                    rect.set(left, top, right, bottom);
                    Log.d("area:", "inside");
                } else {
                    if (topCroppable) {
                        top = top + moveY;
                    }
                    if (bottomCroppable) {
                        bottom = bottom + moveY;
                    }
                    if (rightCroppable) {
                        right = right + moveX;
                    }
                    if (leftCroppable) {
                        left = left + moveX;
                    }
                }
                rect.set(left, top, right, bottom);
                prevX = pointX;  // Remember this touch position for the next move event
                prevY = pointY;
                break;

            case MotionEvent.ACTION_UP:
                Log.d("mouse", "Uped");
                return false;

            default:
                return false;

        }
        // Force a view to draw again
        invalidate();
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(rect, drawRect);
    }
}


