package com.scanner.cardreader.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageView;


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

    private static int parentWidth;         //The View's Width
    private static int parentHeight;
    private final int TOLERANCE = 30;       //Tolerence of Touch

    private int pointX;
    private int pointY;
    private int prevX;
    private int prevY;
    private int moveX;               //Movement of touch along X-axis
    private int moveY;

    static int left, top, right, bottom; //Coordinates of the clipping window
    private int leftBoundary, rightBoundary, topBoundary, bottomBoundary;    //Coordinates of image in imageview

    public ClippingWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
    }

    public ClippingWindow(Context context,  Rect r) {
        super(context);
        //initializeBoundary(r);
        setupPaint();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
    }


    public void initializeBoundary(Rect r) {
        this.leftBoundary =r.left;
        this.rightBoundary =r.right;
        this.topBoundary =r.top;
        this.bottomBoundary =r.bottom;

        //Log.d("abhishek","abhishek");
        this.left = r.left;
        this.right = r.right;
        this.top = r.top;
        this.bottom = r.bottom;
        rect.set(this.left, this.top, this.right, this.bottom);

    }

    private void setupPaint() {
        // Setup paint with color and stroke styles
        drawRect = new Paint();
        drawCircle = new Paint();
        drawLineBoundary = new Paint();
        drawLineGrid = new Paint();

        drawRect.setColor(Color.argb(150,0,0,0));
        drawRect.setAntiAlias(true);
        drawRect.setStrokeWidth(5);
        drawRect.setStyle(Paint.Style.FILL);
        drawRect.setStrokeJoin(Paint.Join.ROUND);
        drawRect.setStrokeCap(Paint.Cap.ROUND);

        drawCircle.setColor(Color.argb(220,255,255,255));

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
                if (prevX > left-30 & prevX < right+30) {       //FOR TOLERANCE
                    if (prevY < top + TOLERANCE & prevY > top - TOLERANCE) {
                        topCroppable = true;
                        croppable = true;
                    } else if (prevY < bottom + TOLERANCE & prevY > bottom - TOLERANCE) {
                        bottomCroppable = true;
                        croppable = true;
                    }
                }

                if (prevY > top-30 & prevY < bottom+30) {
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
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mVelocityTracker.computeCurrentVelocity(10   );
                Log.d("velocity", Float.toString(VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId)));



                moveX = (pointX - prevX); // find the direction of movement with units of movement
                moveY = (pointY - prevY);

                if (rect.contains(prevX, prevY) &!croppable ) {
                    draggable =  topDraggable = bottomDraggable = leftDraggable = rightDraggable = true;
                }
                else draggable = false;
                if (Math.abs((VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId)))>50|Math.abs((VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId)))>50)
                {
                    leftCroppable=rightCroppable=topCroppable=bottomCroppable=false;
                    draggable = false;
                }

                // Log.d("move", Integer.toString(moveX));

                // if (left<=leftBoundary | right >= rightBoundary | top <=topBoundary | bottom >= bottomBoundary){
                if (left <= leftBoundary & moveX<0) {
                    //leftDraggable = false;
                    //rightDraggable = false;
                    leftCroppable = false;
                    //draggable = true;
                }

                if (right >= rightBoundary & moveX>0){
                    //rightDraggable = false;
                    //leftDraggable = false;
                    rightCroppable = false;
                    //draggable = true;
                }
                if (top <= topBoundary & moveY<0){
                    //  topDraggable = false;
                    // bottomDraggable = false;
                    topCroppable = false;
                }
                if (bottom >= bottomBoundary & moveY>0){
                    //bottomDraggable = false;
                    //topDraggable = false;
                    bottomCroppable = false;
                }

                //conditions for minimal cropping window
                if (left + TOLERANCE + 30>= right & moveX > 0) leftCroppable = false;
                if (right - TOLERANCE - 30<= left & moveX < 0) rightCroppable = false;
                if (top + TOLERANCE + 30>= bottom & moveY > 0) topCroppable = false;
                if (bottom- TOLERANCE -30<= top & moveY <0) bottomCroppable = false;
                //if (right TOLERANCE >= right & moveX > 0) rightCroppable = false;
                int height = bottom-top;
                int width = right-left;
                if (draggable) {
                    if (leftDraggable) {left = Math.max(left + moveX, leftBoundary); if (left== leftBoundary) {right = left+ width;rightDraggable = false;}}
                    if (rightDraggable) {right = Math.min(right + moveX, rightBoundary);if (right== rightBoundary) {left = right- width;leftDraggable = false;}}
                    if (topDraggable) {top = Math.max(top + moveY, topBoundary); if (top== topBoundary) {bottom = top+ height;bottomDraggable= false;}}
                    if (bottomDraggable){ bottom = Math.min(bottom + moveY, bottomBoundary);if (bottom== bottomBoundary) {top = bottom-height;topDraggable = false; }}

//                    Log.d("topCroppable",Boolean.toString(topDraggable));
//                    Log.d("movey",Integer.toString(moveY));
//                    Log.d("croppable",Boolean.toString(croppable));

                } else if (croppable ){
                    if (topCroppable) {
                        top =  Math.max(top + moveY, topBoundary);
                    }
//                    if (top + TOLERANCE + 30>= bottom & moveY > 0) {
//                        top = bottom - TOLERANCE - 30 ;
//                    }


                    if (bottomCroppable) {
                        bottom = Math.min(bottom + moveY, bottomBoundary);
                    }
//                    if (bottom- TOLERANCE -30<= top & moveY <0) {
//                        bottom = top + TOLERANCE + 30;
//                    }


                    if (leftCroppable) {
                        left = Math.max(left + moveX, leftBoundary);
                    }
//                    if (left + TOLERANCE + 30>= right & moveX > 0){
//                        left = right -TOLERANCE-30;
//                    }


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

        rectTop.set(0,0,parentWidth,top);
        rectBottom.set(0,bottom,parentWidth,parentHeight);
        rectLeft.set(0,top,left,bottom);
        rectRight.set(right,top,parentWidth,bottom);

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

        canvas.drawLine(left,top,right,top,drawLineBoundary);
        canvas.drawLine(left,top,left,bottom,drawLineBoundary);
        canvas.drawLine(left,bottom,right,bottom,drawLineBoundary);
        canvas.drawLine(left,bottom,right,bottom,drawLineBoundary);
        canvas.drawLine(right,top,right,bottom,drawLineBoundary);


    }

    private void drawCircle(Canvas canvas)
    {
        canvas.drawCircle((left+right)/2,top,20,drawCircle);
        canvas.drawCircle((left+right)/2,bottom,20,drawCircle);
        canvas.drawCircle(left,(top+bottom)/2, 20, drawCircle);
        canvas.drawCircle(right,(top+bottom)/2, 20, drawCircle);
    }



/*

    public Bitmap getCroppedImage() {
        Drawable drawable = CropActivity.cropImView.getDrawable();
        if (drawable == null || !(drawable instanceof BitmapDrawable)) {
            return null;
        }

        // Get image matrix values and place them in an array.
        final float[] matrixValues = new float[9];
        CropActivity.cropImView.getImageMatrix().getValues(matrixValues);

        // Extract the scale and translation values. Note, we currently do not handle any other transformations (e.g. skew).
        final float scaleX = matrixValues[Matrix.MSCALE_X];
        final float scaleY = matrixValues[Matrix.MSCALE_Y];
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        float mappedLeft = Math.max((left - transX) / scaleX,0);  //Since Image is translated and scaled in Imageview
        float mappedRight = Math.min((right - transX) / scaleX,drawable.getIntrinsicWidth());
        float mappedTop = Math.max((top - transY) / scaleY,0);
        float mappedBottom = Math.min((bottom - transY) / scaleY,drawable.getIntrinsicHeight());



        // Get the original bitmap object.
        final Bitmap originalBitmap = ((BitmapDrawable) drawable).getBitmap();
        return Bitmap.createBitmap(originalBitmap, (int) mappedLeft, (int) mappedTop, (int) (mappedRight - mappedLeft), (int) (mappedBottom - mappedTop));


    }


    void getImageLocation(){
        Drawable drawable = CropActivity.cropImView.getDrawable();

        Log.d("drawable bounds",drawable.getBounds().flattenToString());
        // Get image matrix values and place them in an array.
        final float[] matrixValues = new float[9];
        CropActivity.cropImView.getImageMatrix().getValues(matrixValues);

        // Extract the scale and translation values. Note, we currently do not handle any other transformations (e.g. skew).
        final float scaleX = matrixValues[Matrix.MSCALE_X];
        final float scaleY = matrixValues[Matrix.MSCALE_Y];
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        final int originalWidth = drawable.getIntrinsicWidth();
        final int originalHeight = drawable.getIntrinsicHeight();

        //Scaled dimensions in imageview
        final int scaledWidth = Math.round(originalWidth * scaleX);
        final int scaledHeight = Math.round(originalHeight * scaleY);

        Log.d("new","transx" + Float.toString(scaleX)+"transy"+ Float.toString(scaleY));
        leftBoundary =(int) Math.max(transX, 0);
        topBoundary = (int) Math.max(transY, 0);
        rightBoundary =  Math.min(leftBoundary + scaledWidth, getWidth());
        bottomBoundary = Math.min(topBoundary + scaledHeight, getHeight());

    }


*/



}



