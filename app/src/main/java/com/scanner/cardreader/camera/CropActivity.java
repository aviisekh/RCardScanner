package com.scanner.cardreader.camera;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.scanner.cardreader.R;
import com.scanner.cardreader.Splash;
import com.scanner.cardreader.classifier.NNMatrix;
import com.scanner.cardreader.classifier.NeuralNetwork;
import com.scanner.cardreader.interfaces.GrayScale;
import com.scanner.cardreader.interfaces.MedianFilter;
import com.scanner.cardreader.interfaces.Rotate;
import com.scanner.cardreader.interfaces.SkewChecker;
import com.scanner.cardreader.interfaces.Threshold;
import com.scanner.cardreader.preprocessing.BradleyThreshold;
import com.scanner.cardreader.preprocessing.GammaCorrection;
import com.scanner.cardreader.preprocessing.HoughLineSkewChecker;
import com.scanner.cardreader.preprocessing.ITURGrayScale;
import com.scanner.cardreader.preprocessing.ImageWriter;
import com.scanner.cardreader.preprocessing.NonLocalMedianFilter;
import com.scanner.cardreader.preprocessing.RotateNearestNeighbor;
import com.scanner.cardreader.segmentation.BinaryArray;
import com.scanner.cardreader.segmentation.CcLabeling;
import com.scanner.cardreader.segmentation.ComponentImages;
import com.scanner.cardreader.segmentation.PrepareImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CropActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    public static ArrayList<Bitmap> componentBitmaps;

    private Vibrator heptics;

    public static ImageView capturedImage;
    public ClippingWindow clippingWindow;

    public ImageButton redoButton, cropButton, rechargeBtn;
    public EditText ocrResultEV;

    public Bitmap image;
    private Bitmap croppedImage;
    private static Handler imageHandler, ocrResultHandler;

    public Bitmap bmResult;
    public String outputRechargeString;

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
                ocrResultEV.setText(result);
            }
        };


    }

    @Override
    public void onClick(View v) {
        heptics.vibrate(50);
        switch (v.getId()) {
            case R.id.cropBtn:
                crop();
                break;

            case R.id.redoBtn:
                onBackPressed();
                break;

        /*    case R.id.backToCrop:
                backToCrop();
                break;*/

            case R.id.rechargeBtn:
                break;

        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {

            case R.id.cropBtn:
                Toast.makeText(this, "Crop", Toast.LENGTH_LONG).show();
                break;

            case R.id.redoBtn:
                Toast.makeText(this, "Redo", Toast.LENGTH_LONG).show();
                break;

            case R.id.rechargeBtn:
                Toast.makeText(this, "Recharge", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }


    public void instantiate() {

        image = CameraAccess.getBitmapImage();

        //image = BitmapFactory.decodeResource(getResources(), R.drawable.ntc_test);


        rechargeBtn = (ImageButton) findViewById(R.id.rechargeBtn);
        redoButton = (ImageButton) findViewById(R.id.redoBtn);
        cropButton = (ImageButton) findViewById(R.id.cropBtn);
        ocrResultEV = (EditText) findViewById(R.id.ocrResult);
        heptics = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        capturedImage = (ImageView) findViewById(R.id.imageView);
        capturedImage.setImageBitmap(image);

        clippingWindow = (ClippingWindow) findViewById(R.id.clipping);
        clippingWindow.setVisibility(View.VISIBLE);


        rechargeBtn.setOnClickListener(this);
        redoButton.setOnClickListener(this);
        cropButton.setOnClickListener(this);
//        crop();

        if (Splash.SIM == "NTC") {
            outputRechargeString = "*412*";
        } else if (Splash.SIM == "NCELL") {
            outputRechargeString = "*102*";
        } else {
            outputRechargeString = "*505*";
        }

    }


    public void crop() {
        //weightReader.read(this.getApplicationContext());'
        rechargeBtn.setVisibility(View.INVISIBLE);
        //threshBtn.setVisibility(View.VISIBLE);
        cropButton.setVisibility(View.INVISIBLE);
        clippingWindow.setVisibility(View.INVISIBLE);
        ocrResultEV.setVisibility(View.VISIBLE);
        croppedImage = clippingWindow.getCroppedImage();

//        croppedImage = BitmapFactory.decodeResource(getResources(), R.drawable.ntc_skewtest);
//        capturedImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        capturedImage.setImageBitmap(croppedImage);
        rechargeBtn.setVisibility(View.VISIBLE);
        Thread ocrThread;
        //TODO see if you can avoid creating threads yourself. acquire form somewhere
        ocrThread = new Thread(new Runnable() {
            @Override
            public void run() {

//              remove jar which is not for android
                Bitmap sourceBitmap = Bitmap.createBitmap(croppedImage);
                ImageWriter imageWriter = new ImageWriter(CropActivity.this);

                long startGamma = System.currentTimeMillis() / 1000;
                GammaCorrection gc = new GammaCorrection(1.0);
                bmResult = gc.correctGamma(sourceBitmap);
                long stopGamma = System.currentTimeMillis() / 1000;
                System.out.println("gamma:" + (stopGamma - startGamma));
                imageWriter.writeImage(bmResult, false, "aftergamma", "01_gamma");


                long startGrayscale = System.currentTimeMillis() / 1000;
                GrayScale grayScale = new ITURGrayScale(bmResult);
                bmResult = grayScale.grayScale();
                long stopGrayscale = System.currentTimeMillis() / 1000;
                System.out.println("grayscale:" + (stopGrayscale - startGrayscale));
                imageWriter.writeImage(bmResult, false, "aftergrayscale", "02_grayscale");


                long startSkew = System.currentTimeMillis() / 1000;
                SkewChecker skewChecker = new HoughLineSkewChecker();
                double angle = skewChecker.getSkewAngle(croppedImage);
                long stopSkew = System.currentTimeMillis() / 1000;
                System.out.println("angle:" + angle + " skew time:" + (stopSkew - startSkew));


                long startRotate = System.currentTimeMillis() / 1000;
                Rotate rotator = new RotateNearestNeighbor(angle);
                bmResult = rotator.rotateImage(bmResult);
                long stopRotate = System.currentTimeMillis() / 1000;
                System.out.println("rotate:" + (stopRotate - startRotate));
                imageWriter.writeImage(bmResult, false, "afterrotate", "04_rotate");


                long startMedian = System.currentTimeMillis() / 1000;
                MedianFilter medianFilter = new NonLocalMedianFilter(3);
                bmResult = medianFilter.applyMedianFilter(bmResult);
                long stopMedian = System.currentTimeMillis() / 1000;
                System.out.println("median:" + (stopMedian - startMedian));
                imageWriter.writeImage(bmResult, false, "aftermedian", "05_median");


//                RotateByMatrix rotate = new RotateByMatrix(croppedImage.getWidth(), croppedImage.getHeight(), angle);
//                bmResult = rotate.applyMedianFilter(bmResult);

                long startThreshold = System.currentTimeMillis() / 1000;
                Threshold threshold = new BradleyThreshold();
                bmResult = threshold.threshold(bmResult);
                long stopThreshold = System.currentTimeMillis() / 1000;
                System.out.println("threshold:" + (stopThreshold - startThreshold));
                imageWriter.writeImage(bmResult, false, "afterthreshold", "03_threshold");

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

                        if (pixel == -16777216) {
                            booleanImage[index] = true;
                        }

                        index++;
                    }
                    CcLabeling ccLabeling = new CcLabeling();
                    ComponentImages componentImages = new ComponentImages();
                    componentBitmaps = componentImages.CreateImageFromComponents(ccLabeling.CcLabels(booleanImage, width));

//                    writing segment into media
                    for (int i = 0; i < componentBitmaps.size(); i++) {
                        imageWriter.writeImage(componentBitmaps.get(i), true, "segment" + i, "06_segmentation");
                    }

                    List<double[][]> binarySegmentList = BinaryArray.CreateBinaryArray(componentBitmaps);
                    List<int[]> binarySegmentList1D = BinaryArray.CreateBinaryArrayOneD(componentBitmaps);

                    int counter = 0;
                    for (int[] segment : binarySegmentList1D) {
                        for (int i = 0; i < 256; i++) {
                            if (segment[i] == 1) segment[i] = -1;
                            else segment[i] = -16777216;
                        }
                        Bitmap bitmap = Bitmap.createBitmap(segment, 16, 16, Bitmap.Config.RGB_565);
                        imageWriter.writeImage(bitmap, false, "segment" + counter++, "07_binarysegment");

                    }


                    List<Integer> ocrResult = generateOutput(binarySegmentList);
                    for (int a : ocrResult) {
                        outputRechargeString = outputRechargeString + Integer.toString(a);
                    }
                    outputRechargeString = outputRechargeString + "#";

                    Message ocrResultToUIThread = Message.obtain();
                    ocrResultToUIThread.obj = outputRechargeString;
                    ocrResultHandler.sendMessage(ocrResultToUIThread);

                }
//

            }
        });
        ocrThread.start();

    }


    int[] createPixelArray(int width, int height, Bitmap thresholdImage) {

        int[] pixels = new int[width * height];
        thresholdImage.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;

    }

    public void threshold() {

    }


    List<Integer> generateOutput(List<double[][]> binarySegmentList) {


        //WeightReader.setWeights(this.getApplicationContext());// reader = new WeightReader();
        NeuralNetwork net = new NeuralNetwork();
        List<Integer> recognizedList = new ArrayList<Integer>();
        for (double[][] binarySegment : binarySegmentList) {
            NNMatrix input = new NNMatrix(binarySegment);
            NNMatrix output = net.FeedForward(input);
            //output.showOutputArray();
            int i = output.showOutput();

            if (i != -1)
                recognizedList.add(i);


        }

        return recognizedList;

        // Log.d("output", recognizedList.toString());


    }


}
