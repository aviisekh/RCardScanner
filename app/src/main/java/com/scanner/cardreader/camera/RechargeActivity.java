package com.scanner.cardreader.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.scanner.cardreader.R;
import com.scanner.cardreader.Splash;
import com.scanner.cardreader.classifier.NNMatrix;
import com.scanner.cardreader.classifier.NeuralNetwork;
import com.scanner.cardreader.interfaces.GrayScale;
import com.scanner.cardreader.interfaces.Rotate;
import com.scanner.cardreader.interfaces.SkewChecker;
import com.scanner.cardreader.interfaces.Threshold;
import com.scanner.cardreader.preprocessing.BradleyThreshold;
import com.scanner.cardreader.preprocessing.GammaCorrection;
import com.scanner.cardreader.preprocessing.HoughLineSkewChecker;
import com.scanner.cardreader.preprocessing.ITURGrayScale;
import com.scanner.cardreader.preprocessing.ImageWriter;
import com.scanner.cardreader.preprocessing.RotateNearestNeighbor;
import com.scanner.cardreader.segmentation.BinaryArray;
import com.scanner.cardreader.segmentation.CcLabeling;
import com.scanner.cardreader.segmentation.ComponentImages;
import com.scanner.cardreader.segmentation.PrepareImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
*Created by aviisekh on 8/11/16.
*/

public class RechargeActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{
    //public Bitmap image;
    private ImageButton rechargeBtn,redoButton;
    private EditText ocrResultTV;
    private ImageView rechargeImView;
    private ProgressBar progressBar;
    private Vibrator haptics;
    private final int HAPTICS_CONSTANT=50;


    private Bitmap croppedImage;

    private ArrayList<Bitmap> componentBitmaps = new ArrayList<>();

    private Bitmap bmResult;
    private String ocrString="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        croppedImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        instantiate();

    }

    @Override
    public void onClick(View v) {
        haptics.vibrate(HAPTICS_CONSTANT);
        switch (v.getId()) {
            case R.id.redoFromRecharge:
                onBackPressed();
                break;

            case R.id.rechargeBtn:
                Toast.makeText(RechargeActivity.this, "RechargePressed", Toast.LENGTH_SHORT).show();

                break;

        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.cropBtn:
                Toast.makeText(this, "Crop", Toast.LENGTH_LONG).show();
                break;


            case R.id.redoFromCrop:
                Toast.makeText(this, "Redo", Toast.LENGTH_LONG).show();
                break;

        }
        return true;
    }


    public void instantiate() {

        //image = CameraAccess.getBitmapImage();

        //image = BitmapFactory.decodeResource(getResources(), R.drawable.ntc_test);
        rechargeBtn = (ImageButton) findViewById(R.id.rechargeBtn);
        redoButton = (ImageButton) findViewById(R.id.redoFromRecharge);
        haptics = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        ocrResultTV = (EditText) findViewById(R.id.ocrResult);

        //rechargePrefix = (EditText) findViewById(R.id.rechargePrefix);
        rechargeImView = (ImageView) findViewById(R.id.imageView2);


        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        rechargeBtn.setOnClickListener(this);
        redoButton.setOnClickListener(this);

        //processImage();
        new MyTask().execute();
        //myTask.cancel(true);
    }

    class MyTask extends AsyncTask<Void, Void, Void>
    {

        private Bitmap thresholdedImage;

        @Override
        protected Void doInBackground(Void... params) {
            processImage();
            return null;
        }


        @Override
        protected void onPostExecute(Void params){
            progressBar.setVisibility(View.INVISIBLE);
            ocrResultTV.setVisibility(View.VISIBLE);
            rechargeImView.setImageBitmap(thresholdedImage);
            ocrResultTV.setText(ocrString);
        }

        public void processImage() {

//            Log.d("guptaOut","gupta");
            //Bitmap sourceBitmap = Bitmap.createBitmap(CropActivity.croppedImage);
            ImageWriter imageWriter = new ImageWriter(RechargeActivity.this);

            //long startGamma = System.currentTimeMillis() / 1000;
            GammaCorrection gc = new GammaCorrection(1.0);
            bmResult = gc.correctGamma(croppedImage);
            //long stopGamma = System.currentTimeMillis() / 1000;
            imageWriter.writeImage(bmResult, false, "aftergamma", "01_gamma");


            //long startGrayscale = System.currentTimeMillis() / 1000;
            GrayScale grayScale = new ITURGrayScale(bmResult);
            bmResult = grayScale.grayScale();
            //long stopGrayscale = System.currentTimeMillis() / 1000;
            //System.out.println("grayscale:" + (stopGrayscale - startGrayscale));
            imageWriter.writeImage(bmResult, false, "aftergrayscale", "02_grayscale");


            //long startSkew = System.currentTimeMillis() / 1000;
            SkewChecker skewChecker = new HoughLineSkewChecker();
            double angle = skewChecker.getSkewAngle(bmResult);
            //long stopSkew = System.currentTimeMillis() / 1000;
            //System.out.println("angle:" + angle + " skew time:" + (stopSkew - startSkew));


            //long startRotate = System.currentTimeMillis() / 1000;
            Rotate rotator = new RotateNearestNeighbor(angle);
            bmResult = rotator.rotateImage(bmResult);
            //long stopRotate = System.currentTimeMillis() / 1000;
            //System.out.println("rotate:" + (stopRotate - startRotate));
            imageWriter.writeImage(bmResult, false, "afterrotate", "04_rotate");


/*
        long startMedian = System.currentTimeMillis() / 1000;
        MedianFilter medianFilter = new NonLocalMedianFilter(3);
        bmResult = medianFilter.applyMedianFilter(bmResult);
        long stopMedian = System.currentTimeMillis() / 1000;
        System.out.println("median:" + (stopMedian - startMedian));
        imageWriter.writeImage(bmResult, false, "aftermedian", "05_median");
*/


//                RotateByMatrix rotate = new RotateByMatrix(croppedImage.getWidth(), croppedImage.getHeight(), angle);
//                bmResult = rotate.applyMedianFilter(bmResult);

            //long startThreshold = System.currentTimeMillis() / 1000;
            Threshold threshold = new BradleyThreshold();
            bmResult = threshold.threshold(bmResult);
            //long stopThreshold = System.currentTimeMillis() / 1000;
            //System.out.println("threshold:" + (stopThreshold - startThreshold));
            imageWriter.writeImage(bmResult, false, "afterthreshold", "03_threshold");


            thresholdedImage = bmResult;
            //rechargeImView.setImageBitmap(bmResult);

            bmResult = PrepareImage.addBackgroundPixels(bmResult);
            int height = bmResult.getHeight();
            int width = bmResult.getWidth();

//                        get value of pixels from binary image
            int[] pixels = createPixelArray(width, height, bmResult);

//                       Create a binary array called booleanImage using pixel values in threshold bitmap
            boolean[] booleanImage = new boolean[width * height];
            if (Arrays.asList(booleanImage).contains(false)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RechargeActivity.this, "Numbers Unreadable. Please, scan again.", Toast.LENGTH_SHORT).show();
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
                ComponentImages componentImages = new ComponentImages(RechargeActivity.this);
                componentBitmaps = componentImages.CreateComponentImages(ccLabeling.CcLabels(booleanImage, width));

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
                    ocrString = ocrString + Integer.toString(a);
                }

                //ocrResultTV.setText(ocrString);

            }
        }


        private int[] createPixelArray(int width, int height, Bitmap thresholdImage) {

            int[] pixels = new int[width * height];
            thresholdImage.getPixels(pixels, 0, width, 0, 0, width, height);
            return pixels;
        }


        private List<Integer>  generateOutput(List<double[][]> binarySegmentList) {


            NeuralNetwork net = new NeuralNetwork(Splash.bias_at_layer2,Splash.bias_at_layer3,Splash.weights_at_layer2,Splash.weights_at_layer3);
            List<Integer> recognizedList = new ArrayList<Integer>();
            for (double[][] binarySegment : binarySegmentList) {
                NNMatrix input = new NNMatrix(binarySegment);
                NNMatrix output = net.FeedForward(input);
                //output.showOutputArray();
                int filteredOutput = output.filterOutput();

                if (filteredOutput != -1)
                    recognizedList.add(filteredOutput);


            }

            return recognizedList;

            // Log.d("output", recognizedList.toString());


        }

    }
}
