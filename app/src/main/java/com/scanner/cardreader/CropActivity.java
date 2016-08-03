package com.scanner.cardreader;



import android.graphics.Bitmap;
import android.graphics.Matrix;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class CropActivity extends AppCompatActivity implements View.OnClickListener {

    public static ArrayList<Bitmap> componentBitmaps;


    public static ImageView capturedImage;
    public ClippingWindow clippingWindow;

    public Button threshBtn, rechargeBtn, redoButton, cropButton;
    public TextView ocrResultTV;

    public Bitmap image;
    private Bitmap croppedImage;
    private static Handler imageHandler,ocrResultHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        instantiate();


//        linked with message queue of main thread
        imageHandler = new Handler() {
            //executed when msg arrives from thread
            @Override
            public void handleMessage(Message msg) {
                Bitmap result = (Bitmap) msg.obj;
//                capturedImage.setLayerType(View.LAYER_TYPE_SOFTWARE,new Paint(0xFFFFFF));
//                Log.d("drawable", String.valueOf(capturedImage.getDrawable().getIntrinsicWidth()));
                capturedImage.setImageBitmap(result);
            }
        };

        ocrResultHandler = new Handler() {
            //executed when msg arrives from thread
            @Override
            public void handleMessage(Message msg) {
                String result = (String) msg.obj;
//                capturedImage.setLayerType(View.LAYER_TYPE_SOFTWARE,new Paint(0xFFFFFF));
//                Log.d("drawable", String.valueOf(capturedImage.getDrawable().getIntrinsicWidth()));
                ocrResultTV.setText(result);
            }
        };



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cropBtn:
                crop();
                break;

            case R.id.redoBtn:
                onBackPressed();
                break;

            case R.id.rechargeBtn:
                break;

            case R.id.threshBtn:
                threshold();
                break;
        }
    }


    public void instantiate() {

        image = getRotatedImage(CameraActivity.getBitmapImage());

        //image = BitmapFactory.decodeResource(getResources(), R.drawable.test5b);

        threshBtn = (Button) findViewById(R.id.threshBtn);


        rechargeBtn = (Button) findViewById(R.id.rechargeBtn);
        redoButton = (Button) findViewById(R.id.redoBtn);
        cropButton = (Button) findViewById(R.id.cropBtn);
        ocrResultTV = (TextView) findViewById(R.id.ocrResult);

        capturedImage = (ImageView) findViewById(R.id.imageView);
        capturedImage.setImageBitmap(image);

        clippingWindow = (ClippingWindow) findViewById(R.id.clipping);
        clippingWindow.setVisibility(View.VISIBLE);


        threshBtn.setOnClickListener(this);
        rechargeBtn.setOnClickListener(this);
        redoButton.setOnClickListener(this);
        cropButton.setOnClickListener(this);
//        crop();

    }


    public void crop() {
        //weightReader.read(this.getApplicationContext());'
        rechargeBtn.setVisibility(View.INVISIBLE);
        threshBtn.setVisibility(View.VISIBLE);
        cropButton.setVisibility(View.INVISIBLE);
        clippingWindow.setVisibility(View.INVISIBLE);
        croppedImage = clippingWindow.getCroppedImage();

//        croppedImage = BitmapFactory.decodeResource(getResources(), R.drawable.skewtest);
//        capturedImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        capturedImage.setImageBitmap(croppedImage);

    }


    Bitmap getRotatedImage(Bitmap bmp) {
        Matrix returnImage = new Matrix();
        returnImage.postRotate(90);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), returnImage, true);

    }

    int[] createPixelArray(int width, int height, Bitmap thresholdImage) {

        int[] pixels = new int[width * height];
        thresholdImage.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;

    }

    public void threshold() {
        threshBtn.setVisibility(View.INVISIBLE);
        rechargeBtn.setVisibility(View.VISIBLE);
        Thread grayScaleThread;
        //TODO see if you can avoid creating threads yourself. acquire form somewhere
        grayScaleThread = new Thread(new Runnable() {
            @Override
            public void run() {

//              remove jar which is not for android
                Bitmap sourceBitmap = Bitmap.createBitmap(croppedImage);

                long startGamma = System.currentTimeMillis()/1000;
                GammaCorrection gc = new GammaCorrection(1.75);
                Bitmap bmResult = gc.applyInPlace(sourceBitmap);
                long stopGamma= System.currentTimeMillis()/1000;
                System.out.println("gamma:"+(stopGamma-startGamma));

                long startGrayscale = System.currentTimeMillis()/1000;
                GrayScale grayScale = new ITURGrayScale(bmResult);
                bmResult = grayScale.grayScale();
                long stopGrayscale= System.currentTimeMillis()/1000;
                System.out.println("grayscale:"+(stopGrayscale-startGrayscale));

                long startThreshold = System.currentTimeMillis()/1000;
                Threshold threshold = new BradleyThreshold();
                bmResult = threshold.threshold(bmResult);
                long stopThreshold= System.currentTimeMillis()/1000;
                System.out.println("threshold:"+(stopThreshold-startThreshold));

                long startSkew = System.currentTimeMillis()/1000;
                ImageSkewChecker ds = new ImageSkewChecker();
                double angle = ds.getSkewAngle(croppedImage);
                long stopSkew= System.currentTimeMillis()/1000;
                System.out.println("angle:"+angle+" skew time:"+(stopSkew-startSkew));

                long startRotate = System.currentTimeMillis()/1000;
                RotateNearestNeighbor rn = new RotateNearestNeighbor(angle);
                bmResult = rn.applyInPlace(bmResult);
                long stopRotate= System.currentTimeMillis()/1000;
                System.out.println("rotate:"+(stopRotate-startRotate));

                long startMedian = System.currentTimeMillis()/1000;
                NonLocalMedianFilter m = new NonLocalMedianFilter(3);
                bmResult = m.applyInPlace(bmResult);
                long stopMedian= System.currentTimeMillis()/1000;
                System.out.println("median:"+(stopMedian-startMedian));

//                RotateByMatrix rotate = new RotateByMatrix(croppedImage.getWidth(), croppedImage.getHeight(), angle);
//                bmResult = rotate.applyInPlace(bmResult);

                Message imageToUIThread = Message.obtain();
                imageToUIThread.obj = bmResult;
                imageHandler.sendMessage(imageToUIThread);


                bmResult = PrepareImage.addBackgroundPixels(bmResult);
                int height = bmResult.getHeight();
                int width = bmResult.getWidth();
                Log.d("width", String.valueOf(width));

//                        get value of pixels from binary image
                int[] pixels = createPixelArray(width, height, bmResult);

//                       Create a binary array called booleanImage using pixel values in threshold bitmap
                boolean[] booleanImage = new boolean[width * height];
                if (Arrays.asList(booleanImage).contains(false)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CropActivity.this, "Numbers Unreadable. Please, scan again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

//                      false if pixel is a background pixel, else true
                    int index = 0;
                    for (int pixel : pixels) {

                        if (pixel != -1) {
                            booleanImage[index] = true;
                        }

                        index++;
                    }
                    CcLabeling ccLabeling = new CcLabeling();
                    componentBitmaps = ComponentImages.CreateImageFromComponents(ccLabeling.CcLabels(booleanImage, width));
                    List<double[][]> binarySegmentList = BinaryArray.CreateBinaryArray(componentBitmaps);
                    String ocrResult = generateOutput(binarySegmentList);

                    Message ocrResultToUIThread = Message.obtain();
                    ocrResultToUIThread.obj = ocrResult;
                    ocrResultHandler.sendMessage(ocrResultToUIThread);

                }
//

            }
        });
        grayScaleThread.start();
    }


    String generateOutput(List<double[][]> binarySegmentList) {


        WeightReader.setWeights(this.getApplicationContext());// reader = new WeightReader();
        NeuralNetwork net = new NeuralNetwork();
        List<Integer> recognizedList = new ArrayList<Integer>();
        for (double[][] binarySegment : binarySegmentList) {
        NNMatrix input = new NNMatrix(binarySegment);
            NNMatrix output = net.FeedForward(input);
        //output.showOutputArray();
            int i = output.showOutput();

            if (i != -1 )
                recognizedList.add(i);


    }

        return recognizedList.toString().trim();

       // Log.d("output", recognizedList.toString());


    }
}
