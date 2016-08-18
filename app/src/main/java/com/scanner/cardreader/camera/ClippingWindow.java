package com.scanner.cardreader.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

/**
 *  Created by aviisekh on 6/27/16.
 */

public class ClippingWindow extends View {

    private Paint drawRect;
    private Paint drawCircle;
    private Paint drawLineBoundary;
    private Paint drawLineGrid;

    private Rect rect = new Rect();
    private Rect rectTop = new Rect();       //dark overlay on the top of clipping window
    private Rect rectBottom = new Rect();
    private Rect rectLeft = new Rect();
    private Rect rectRight = new Rect();


    private boolean draggable, topDraggable, leftDraggable, rightDraggable, bottomDraggable;
    private boolean croppable, topCroppable, bottomCroppable, rightCroppable, leftCroppable;
    private VelocityTracker mVelocityTracker = null;

    private final int TOLERANCE = 30;       //Tolerence of Touch
    private final int BOUNDARY_INIT=20;
    private final int MINIMAL_CROP_AREA=10;
    private final int RADIUS = 10;

    private int pointX;
    private int pointY;
    private int prevX;
    private int prevY;
    private int moveX;               //Movement of touch along X-axis
    private int moveY;

    private int left, top, right, bottom; //Coordinates of the clipping window
    private int leftBoundary, rightBoundary, topBoundary, bottomBoundary;    //Coordinates of image in imageview

    public ClippingWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
    }

    public void initializeBoundary(Rect r) {
        this.leftBoundary =r.left;
        this.rightBoundary =r.right;
        this.topBoundary =r.top;
        this.bottomBoundary =r.bottom;

        this.left = r.left+BOUNDARY_INIT;
        this.right = r.right-BOUNDARY_INIT;
        this.top = r.top+BOUNDARY_INIT;
        this.bottom = r.bottom-BOUNDARY_INIT;

        rect.set(this.left, this.top, this.right, this.bottom);

    }

    private void setupPaint() {
        // Setup paint with color and stroke styles
        drawRect = new Paint();
        drawCircle = new Paint();
        drawLineBoundary = new Paint();
        drawLineGrid = new Paint();

        drawRect.setColor(Color.argb(150,0,0,0));  //Black with transparency
        drawRect.setAntiAlias(true);
        drawRect.setStrokeWidth(5);
        drawRect.setStyle(Paint.Style.FILL);
        drawRect.setStrokeJoin(Paint.Join.ROUND);
        drawRect.setStrokeCap(Paint.Cap.ROUND);

        drawCircle.setColor(Color.argb(255,255,255,255));

        drawLineBoundary.setColor(Color.argb(255,255,255,255));
        drawLineBoundary.setStrokeWidth(5);

        drawLineGrid.setColor(Color.argb(200,255,255,255));
        drawLineGrid.setStrokeWidth(2);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        pointX = (int) event.getX(); //get the touch position
        pointY = (int) event.getY();

        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);
        //Checks for the event that occurs

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }

                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);

                croppable = topCroppable = bottomCroppable = leftCroppable = rightCroppable = false;
                prevX = pointX;
                prevY = pointY;
                if (prevX > left-TOLERANCE & prevX < right+TOLERANCE) {       //FOR TOLERANCE
                    if (prevY < top + TOLERANCE & prevY > top - TOLERANCE) {
                        topCroppable = true;
                        croppable = true;
                    } else if (prevY < bottom + TOLERANCE & prevY > bottom - TOLERANCE) {
                        bottomCroppable = true;
                        croppable = true;
                    }
                }

                if (prevY > top-TOLERANCE & prevY < bottom+TOLERANCE) {
                    if (prevX < left + TOLERANCE & prevX > left - TOLERANCE) {
                        leftCroppable = true;
                        croppable = true;
                    }

                    if (prevX < right + TOLERANCE & prevX > right - TOLERANCE) {
                        rightCroppable = true;
                        croppable = true;
                    }
                }

                if (rect.contains(prevX, prevY) & !croppable) {
                    //if touch begins from outside the crop window
                    draggable = topDraggable = bottomDraggable = leftDraggable = rightDraggable = true;

                } else if (!rect.contains(prevX, prevY) | croppable) {
                    draggable = false;
                }
                break;


            case MotionEvent.ACTION_MOVE:

                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(10);
//                Log.d("velocity", Float.toString(VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId)));



                moveX = (pointX - prevX); // find the direction of movement with units of movement
                moveY = (pointY - prevY);

                if (rect.contains(prevX, prevY) &!croppable ) {
                    draggable =  topDraggable = bottomDraggable = leftDraggable = rightDraggable = true;
                }
                else draggable = false;
                if (Math.abs((VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId)))>30|Math.abs((VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId)))>30)
                {
                    leftCroppable=rightCroppable=topCroppable=bottomCroppable=false;
                    draggable = false;
                }


                if (left <= leftBoundary & moveX<0) {
                    leftCroppable = false;
                }

                if (right >= rightBoundary & moveX>0){
                    rightCroppable = false;
                }
                if (top <= topBoundary & moveY<0){
                    topCroppable = false;
                }
                if (bottom >= bottomBoundary & moveY>0){
                    bottomCroppable = false;
                }

                //conditions for minimal cropping window
                if (left + TOLERANCE + MINIMAL_CROP_AREA>= right & moveX > 0) leftCroppable = false;
                if (right - TOLERANCE - MINIMAL_CROP_AREA<= left & moveX < 0) rightCroppable = false;
                if (top + TOLERANCE + MINIMAL_CROP_AREA>= bottom & moveY > 0) topCroppable = false;
                if (bottom- TOLERANCE -MINIMAL_CROP_AREA<= top & moveY <0) bottomCroppable = false;
                int height = bottom-top;
                int width = right-left;
                if (draggable) {
                    if (leftDraggable) {left = Math.max(left + moveX, leftBoundary); if (left== leftBoundary) {right = left+ width;rightDraggable = false;}}
                    if (rightDraggable) {right = Math.min(right + moveX, rightBoundary);if (right== rightBoundary) {left = right- width;leftDraggable = false;}}
                    if (topDraggable) {top = Math.max(top + moveY, topBoundary); if (top== topBoundary) {bottom = top+ height;bottomDraggable= false;}}
                    if (bottomDraggable){ bottom = Math.min(bottom + moveY, bottomBoundary);if (bottom== bottomBoundary) {top = bottom-height;topDraggable = false; }}
                } else if (croppable ){
                    if (topCroppable) {
                        top =  Math.max(top + moveY, topBoundary);
                    }

                    if (bottomCroppable) {
                        bottom = Math.min(bottom + moveY, bottomBoundary);
                    }

                    if (leftCroppable) {
                        left = Math.max(left + moveX, leftBoundary);
                    }

                    if (rightCroppable) {
                        right = Math.min(right + moveX, rightBoundary);
                    }
//                    if (right - TOLERANCE - 30<= left & moveX < 0) {
//                        right = left + TOLERANCE + 30;
//                    }


                }

                rect.set(left, top, right, bottom);
                prevX = pointX;  // Remember this touch position for the next move event
                prevY = pointY;
                break;

            case MotionEvent.ACTION_UP:
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
        //super.onDraw(canvas);
        drawRectangle(canvas);
        drawCircle(canvas);
        drawLine(canvas);
        invalidate();
    }

    private void drawRectangle(Canvas canvas) {

        rectTop.set(leftBoundary,topBoundary,rightBoundary,top);
        rectBottom.set(leftBoundary,bottom,rightBoundary,bottomBoundary);
        rectLeft.set(leftBoundary,top,left,bottom);
        rectRight.set(right,top,rightBoundary,bottom);

        canvas.drawRect(rectTop, drawRect);
        canvas.drawRect(rectBottom, drawRect);
        canvas.drawRect(rectLeft,drawRect);
        canvas.drawRect(rectRight,drawRect);
    }

    private void drawLine(Canvas canvas)
    {
        canvas.drawLine(left+(right-left)/3, top,left+(right-left)/3, bottom,drawLineGrid);
        canvas.drawLine(left+2*(right-left)/3, top,left+2*(right-left)/3, bottom,drawLineGrid);
        canvas.drawLine(left,top+(bottom-top)/3,right, top+(bottom-top)/3,drawLineGrid);
        canvas.drawLine(left,top+2*(bottom-top)/3,right, top+2*(bottom-top)/3,drawLineGrid);

        canvas.drawLine(left-2,top,right+2,top,drawLineBoundary);
        canvas.drawLine(left,top-2,left,bottom+2,drawLineBoundary);
        canvas.drawLine(left-2,bottom,right+2,bottom,drawLineBoundary);
        canvas.drawLine(right,top-2,right,bottom+2,drawLineBoundary);


    }

    private void drawCircle(Canvas canvas)
    {
        canvas.drawCircle((left+right)/2,top,RADIUS,drawCircle);
        canvas.drawCircle((left+right)/2,bottom,RADIUS,drawCircle);
        canvas.drawCircle(left,(top+bottom)/2, RADIUS, drawCircle);
        canvas.drawCircle(right,(top+bottom)/2, RADIUS, drawCircle);
    }

    public Rect getClippingWindowCoordinates()
    {
        Rect coordinates = new Rect();
        coordinates.left = left;
        coordinates.right = right;
        coordinates.top = top;
        coordinates.bottom = bottom;
        return coordinates;
    }
}



