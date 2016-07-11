package com.scanner.cardreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;


public class ClippingWindow extends ImageView {

    private Paint drawRect;
    private Paint drawCircle;
    private Paint drawLineBoundary;
    private Paint drawLineGrid;

    Rect rect = new Rect();
    Rect rectTop = new Rect();       //dark overlay on the top of clipping window
    Rect rectBottom = new Rect();
    Rect rectLeft = new Rect();
    Rect rectRight = new Rect();


    boolean draggable, topDraggable, leftDraggable, rightDraggable, bottomDraggable;
    boolean croppable, topCroppable, bottomCroppable, rightCroppable, leftCroppable;
    

    static int parentWidth;         //The View's Width
    static int parentHeight;
    final int TOLERANCE = 30;       //Tolerence of Touch

    public int pointX;
    public int pointY;
    public int prevX;
    public int prevY;
    public int moveX;               //Movement of touch along X-axis
    public int moveY;
    static int left, top, right, bottom; //Coordinates of the clipping window
    static int imageLeft, imageRight, imageTop, imageBottom;    //Coordinates of image in imageview

    public ClippingWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setFocusable(false);
//        setFocusableInTouchMode(false);

        setupPaint();
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
        getImageLocation();
    }


    public void init() {
        left = parentWidth / 2 - 200;
        top = parentHeight / 2 - 200;
        right = parentWidth / 2 + 200;
        bottom = parentHeight / 2 + 200;
        rect.set(left, top, right, bottom);

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
        //Checks for the event that occurs

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
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
                moveX = (pointX - prevX); // find the direction of movement with units of movement
                moveY = (pointY - prevY);

                if (rect.contains(prevX, prevY) ) {
                    topDraggable = bottomDraggable = leftDraggable = rightDraggable = true;
                }

                // Log.d("move", Integer.toString(moveX));

               // if (left<=imageLeft | right >= imageRight | top <=imageTop | bottom >= imageBottom){
                    if (left <= imageLeft+10 & moveX<0) {
                        leftDraggable = false;
                        rightDraggable = false;
                        leftCroppable = false;
                        //draggable = true;
                    }

                    if (right >=imageRight-10 & moveX>0){
                        rightDraggable = false;
                        leftDraggable = false;
                        rightCroppable = false;
                        //draggable = true;
                    }
                    if (top <= imageTop+10 & moveY<0){
                        topDraggable = false;
                        bottomDraggable = false;
                        topCroppable = false;
                    }
                    if (bottom >= imageBottom-10 & moveY>0){
                        bottomDraggable = false;
                        topDraggable = false;
                        bottomCroppable = false;
                    }

                //conditions for minimal cropping window
                if (left + TOLERANCE + 30>= right & moveX > 0) leftCroppable = false;
                if (right - TOLERANCE - 30<= left & moveX < 0) rightCroppable = false;
                if (top + TOLERANCE + 30>= bottom & moveY > 0) topCroppable = false;
                if (bottom- TOLERANCE -30<= top & moveY <0) bottomCroppable = false;
                //if (right TOLERANCE >= right & moveX > 0) rightCroppable = false;

                if (draggable) {
                    if (leftDraggable) left = left + moveX;
                    if (rightDraggable) right = right + moveX;
                    if (topDraggable) top = top + moveY;
                    if (bottomDraggable) bottom = bottom + moveY;

                } else if (croppable){
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
    }

    public void drawRectangle(Canvas canvas) {

        rectTop.set(0,0,parentWidth,top);
        rectBottom.set(0,bottom,parentWidth,parentHeight);
        rectLeft.set(0,top,left,bottom);
        rectRight.set(right,top,parentWidth,bottom);

        canvas.drawRect(rectTop, drawRect);
        canvas.drawRect(rectBottom, drawRect);
        canvas.drawRect(rectLeft,drawRect);
        canvas.drawRect(rectRight,drawRect);
    }

    public  void drawLine(Canvas canvas)
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
    public void drawCircle(Canvas canvas)
    {
        //circle points of the top
        //canvas.drawCircle(left,top,20,drawCircle);
        canvas.drawCircle((left+right)/2,top,20,drawCircle);
        //canvas.drawCircle(right,top,20,drawCircle);

        //circle points on the bottom
        //canvas.drawCircle(left,bottom,20,drawCircle);
        canvas.drawCircle((left+right)/2,bottom,20,drawCircle);
        //canvas.drawCircle(right,bottom,20,drawCircle);

        //circle on left
        canvas.drawCircle(left,(top+bottom)/2, 20, drawCircle);
        canvas.drawCircle(right,(top+bottom)/2, 20, drawCircle);

    }


    public void getImageLocation()
    {
        final Drawable drawable = CropActivity.capturedImage.getDrawable();

        // Get image matrix values and place them in an array.
        final float[] matrixValues = new float[9];
        CropActivity.capturedImage.getImageMatrix().getValues(matrixValues);

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

        imageLeft =(int) Math.max(transX, 0);
        imageTop = (int) Math.max(transY, 0);
        imageRight =  Math.min(imageLeft + scaledWidth, getWidth());
        imageBottom = Math.min(imageTop + scaledHeight, getHeight());

    }

    public Bitmap getCroppedImage() {
        final Drawable drawable = CropActivity.capturedImage.getDrawable();
        if (drawable == null || !(drawable instanceof BitmapDrawable)) {
            return null;
        }

        // Get image matrix values and place them in an array.
        final float[] matrixValues = new float[9];
        CropActivity.capturedImage.getImageMatrix().getValues(matrixValues);

        // Extract the scale and translation values. Note, we currently do not handle any other transformations (e.g. skew).
        final float scaleX = matrixValues[Matrix.MSCALE_X];
        final float scaleY = matrixValues[Matrix.MSCALE_Y];
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        float mappedLeft = (left - transX) / scaleX;  //Since Image is translated and scaled in Imageview
        float mappedRight = (right - transX) / scaleX;
        float mappedTop = (top - transY) / scaleY;
        float mappedBottom = (bottom - transY) / scaleY;

        // Get the original bitmap object.
        final Bitmap originalBitmap = ((BitmapDrawable) drawable).getBitmap();
        return Bitmap.createBitmap(originalBitmap, (int) mappedLeft, (int) mappedTop, (int) (mappedRight - mappedLeft), (int) (mappedBottom - mappedTop));


    }


}



