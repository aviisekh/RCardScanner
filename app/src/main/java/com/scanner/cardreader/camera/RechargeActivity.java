package com.scanner.cardreader.camera;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Mean;

/*
*Created by aviisekh on 8/11/16.
*/

public class RechargeActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    //public Bitmap image;
    private ImageButton rechargeBtn, redoButton;
    private EditText ocrResultTV;
    private ImageView rechargeImView;
    private RelativeLayout relativeLayout;
    //    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private Vibrator haptics;
    private final int HAPTICS_CONSTANT = 50;


    private final int BLACK = -16777216;
    private final int WHITE = -1;

    private Bitmap croppedImage;

    private ArrayList<Bitmap> componentBitmaps = new ArrayList<>();

    private Bitmap bmResult;
    private String ocrResult;
    private ImageWriter imageWriter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                //Toast.makeText(RechargeActivity.this, "RechargePressed", Toast.LENGTH_SHORT).show();
                recharge();
                break;

        }
    }

    private void recharge() {
        String prefix;
        if (Splash.SIM == "NTC" | Splash.SIM == "NCELL") {

            if (Splash.SIM == "NTC") prefix = "*412*";
            else  prefix = "*102*";
            String dial = prefix + ocrResultTV.getText().toString() + "#";
            dial = dial.replace("*", Uri.encode("*")).replace("#", Uri.encode("#"));
            Uri data = Uri.parse("tel:" + dial);
            Intent dialIntent = new Intent(Intent.ACTION_CALL, data);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(dialIntent);
        }

        else{
            Toast.makeText(this,"No SIM detected", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.rechargeBtn:
                Toast.makeText(this, "Crop", Toast.LENGTH_LONG).show();
                break;


            case R.id.redoFromRecharge:
                Toast.makeText(this, "Redo", Toast.LENGTH_LONG).show();
                break;

        }
        return true;
    }


    public void instantiate() {
        progressDialog = ProgressDialog.show(this, "", "Scanning", true);
        imageWriter = new ImageWriter(RechargeActivity.this);

        rechargeBtn = (ImageButton) findViewById(R.id.rechargeBtn);
        redoButton = (ImageButton) findViewById(R.id.redoFromRecharge);
        haptics = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        ocrResultTV = (EditText) findViewById(R.id.ocrResult);
        rechargeImView = (ImageView) findViewById(R.id.imageView2);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout2);

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        rechargeBtn.setOnClickListener(this);
        redoButton.setOnClickListener(this);

        new MyTask().execute();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        private Bitmap thresholdedImage;

        @Override
        protected Void doInBackground(Void... params) {
            processImage();
            return null;
        }


        @Override
        protected void onPostExecute(Void params) {
//            progressBar.setVisibility(View.INVISIBLE);
            relativeLayout.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
            ocrResultTV.setVisibility(View.VISIBLE);
            rechargeImView.setImageBitmap(thresholdedImage);
            ocrResultTV.setText(ocrResult);
        }

        public void processImage() {

            //Bitmap sourceBitmap = Bitmap.createBitmap(CropActivity.croppedImage);
            bmResult = gammaCorrect(croppedImage);
            imageWriter.writeImage(bmResult, false, "aftergrayscale", "02_grayscale");

            bmResult = grayScale(bmResult);
            bmResult = skewCorrect(bmResult);
            imageWriter.writeImage(bmResult, false, "afterrotate", "03_rotate");

            //bmResult = medianFilter(bmResult);
            //imageWriter.writeImage(bmResult, false, "aftermedianfilter", "04_medianfilter");

            bmResult = threshold(bmResult);
            imageWriter.writeImage(bmResult, false, "afterthreshold", "05_threshold");

            thresholdedImage = bmResult;

            componentBitmaps = getSegmentArray(bmResult);
            for (int i = 0; i < componentBitmaps.size(); i++) {
                imageWriter.writeImage(componentBitmaps.get(i), true, "segment" + i, "06_segmentation");
            }

            List<double[][]> binarySegmentList = BinaryArray.CreateBinaryArray(componentBitmaps);
            //generateBinarySegmentedImages();

            ocrResult = generateOutput(binarySegmentList);
        }

        private Bitmap gammaCorrect(Bitmap bmp) {
            GammaCorrection gc = new GammaCorrection(1.0);
            bmp = gc.correctGamma(bmp);
            imageWriter.writeImage(bmp, false, "aftergamma", "01_gamma");
            return bmp;
        }

        private Bitmap grayScale(Bitmap bmp) {
            GrayScale grayScale = new ITURGrayScale(bmp);
            bmp = grayScale.grayScale();
            return bmp;
        }

        private Bitmap skewCorrect(Bitmap bmp) {
            SkewChecker skewChecker = new HoughLineSkewChecker();
            double angle = skewChecker.getSkewAngle(bmp);

            Rotate rotator = new RotateNearestNeighbor(angle);
            bmp = rotator.rotateImage(bmp);
            return bmp;
        }

        private Bitmap medianFilter(Bitmap bmp) {
            MedianFilter medianFilter = new NonLocalMedianFilter(3);
            bmp = medianFilter.applyMedianFilter(bmp);
            return bmp;
        }

        private Bitmap threshold(Bitmap bmp) {
            Threshold threshold = new BradleyThreshold();
            bmp = threshold.threshold(bmp);
            return bmp;
        }

        private ArrayList<Bitmap> getSegmentArray(Bitmap bmp) {
            ArrayList<Bitmap> segments;
            bmp = PrepareImage.addBackgroundPixels(bmp);
            int height = bmp.getHeight();
            int width = bmp.getWidth();

//                        get value of pixels from binary image
            int[] pixels = createPixelArray(width, height, bmp);

//                       Create a binary array called booleanImage using pixel values in threshold bitmap
            boolean[] booleanImage = new boolean[width * height];


//                      false if pixel is a background pixel, else true
            int index = 0;
            for (int pixel : pixels) {
                if (pixel == BLACK) {
                    booleanImage[index] = true;
                }
                index++;
            }

            CcLabeling ccLabeling = new CcLabeling();
            ComponentImages componentImages = new ComponentImages(RechargeActivity.this);
            segments = componentImages.CreateComponentImages(ccLabeling.CcLabels(booleanImage, width));
            return segments;
        }

        private void generateBinarySegmentedImages() {
            List<int[]> binarySegmentList1D = BinaryArray.CreateBinaryArrayOneD(componentBitmaps);

            int counter = 0;
            for (int[] segment : binarySegmentList1D) {
                for (int i = 0; i < 256; i++) {
                    if (segment[i] == 1) segment[i] = WHITE;
                    else segment[i] = BLACK;
                }
                Bitmap bitmap = Bitmap.createBitmap(segment, 16, 16, Bitmap.Config.RGB_565);
                imageWriter.writeImage(bitmap, false, "segment" + counter++, "07_binarysegment");

            }

        }


        private int[] createPixelArray(int width, int height, Bitmap thresholdImage) {

            int[] pixels = new int[width * height];
            thresholdImage.getPixels(pixels, 0, width, 0, 0, width, height);
            return pixels;
        }


        private String generateOutput(List<double[][]> binarySegmentList) {
            String ocrString = " ";
            NeuralNetwork net = new NeuralNetwork(Splash.bias_at_layer2, Splash.bias_at_layer3, Splash.weights_at_layer2, Splash.weights_at_layer3);
            List<Integer> recognizedList = new ArrayList<Integer>();
            for (double[][] binarySegment : binarySegmentList) {
                NNMatrix input = new NNMatrix(binarySegment);
                NNMatrix output = net.FeedForward(input);
                //output.showOutputArray();
                int filteredOutput = output.filterOutput();

                if (filteredOutput != -1)
                    recognizedList.add(filteredOutput);


            }

            for (int a : recognizedList) {
                ocrString = ocrString + Integer.toString(a);
            }

            return ocrString;

        }

    }
}
