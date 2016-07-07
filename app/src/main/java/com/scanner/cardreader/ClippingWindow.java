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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;


public class ClippingWindow extends ImageView {


    private final int paintColor = Color.argb(100, 255, 0, 0);

    private Paint drawRect;

    Rect rect = new Rect();

    boolean draggable;
    boolean croppable, topCroppable, bottomCroppable, rightCroppable, leftCroppable;

    private static final int MIN_CLICK_DURATION = 1000;

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
        //setFocusable(true);
        //setFocusableInTouchMode(false);
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
        left = parentWidth / 2 - 200;
        top = parentHeight / 2 - 100;
        right = parentWidth / 2 + 200;
        bottom = parentHeight / 2 + 100;
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
                // Log.d("move", Integer.toString(moveX));

                if (draggable) {
                    left = left + moveX;
                    right = right + moveX;
                    top = top + moveY;
                    bottom = bottom + moveY;
                    rect.set(left, top, right, bottom);
                    //Log.d("area:", "inside");
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
                //Log.d("mouse", "Uped");
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
        super.onDraw(canvas);
        drawRectangle(canvas);
    }

    public void drawRectangle(Canvas canvas) {
        canvas.drawRect(rect, drawRect);

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



