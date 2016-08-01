package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ViewSegments extends AppCompatActivity {


    ImageView segmentImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(ViewSegments.this, "switched to view segment", Toast.LENGTH_SHORT).show();
        ArrayList<Bitmap> comBitmaps = CropActivity.getBitmapImage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_segments);
        segmentImageView = (ImageView) findViewById(R.id.segmentImageView);
        segmentImageView.setImageBitmap(comBitmaps.get(1));


//        segmentImageView.setImageBitmap(Bitmap.createScaledBitmap(comBitmaps.get(0), 16, 16, true));
//        saveImage(comBitmaps);
//        File txtFile = Environment.getExternalStorageDirectory();
//        File text = new File(txtFile, "/values.txt");

//        File filesDir = getFilesDir();
//        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        File text = new File(file, "/binarvv.txt");
//        Log.d("file", "sdfs");
//        try {
//            if (!text.exists()) {
//                if (text.createNewFile())
//                    Toast.makeText(ViewSegments.this, "new file created", Toast.LENGTH_SHORT).show();
//                else Toast.makeText(ViewSegments.this, "Not created", Toast.LENGTH_SHORT).show();
//            }
//            FileWriter fileWriter = new FileWriter(text.getName(), true);
//            BufferedWriter bw = new BufferedWriter(fileWriter);
//            bw.write("asdfsdf");
//            bw.flush();
//            bw.close();
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }


//        for(int i = 0; i<comBitmaps.size(); i++){
//            Bitmap scaledBitmap = Bitmap.createScaledBitmap(comBitmaps.get(i), 16, 16, true);
//            segmentImageView.setImageBitmap(scaledBitmap);
//            int[] pixels = createPixelArray(scaledBitmap.getWidth(), scaledBitmap.getHeight(), scaledBitmap);

//
//                   FileOutputStream fout = openFileOutput("binary.csv", MODE_APPEND);
//                    OutputStreamWriter out = new OutputStreamWriter(fout);
//writeToFile("binary.csv", 's');
//                    for (int j = 0; j < pixels.length; j++) {
//                        if (pixels[i] == -1) {
//
//                           writeToFile("binary.csv",'0');
//                           writeToFile("binary.csv",',');
//
//                        } else {
//                            writeToFile("binary.csv",'1');
//                            writeToFile("binary.csv",',');
//                        }
//                    }


//        }
//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(comBitmaps.get(i), 16, 16, true);
//        segmentImageView.setImageBitmap(scaledBitmap);
//        int[] pixels = createPixelArray(scaledBitmap.getWidth(), scaledBitmap.getHeight(), scaledBitmap);
//        try {
//
//            FileWriter fileWriter = new FileWriter(text);
//            for (int i = 0; i < pixels.length; i++) {
//                if (pixels[i] == -1) {
//                    fileWriter.append('0');
//                    fileWriter.append(',');
//
//                } else {
//                    fileWriter.append('1');
//                    fileWriter.append(',');
//                }
//            }
//
//            fileWriter.flush();
//            fileWriter.close();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }


//        imageBitmap[0].setImageBitmap(comBitmaps.get(0));


//        int pixels[] = createPixelArray(comBitmaps.get(0).getWidth(), comBitmaps.get(0).getHeight(), comBitmaps.get(0));
//       for(int i = 0; i<pixels.length;i++){
//           if(pixels[i] !=-1){
//               pixels[i] = 1;
//           }
//           else{
//               pixels[i] = 0;
//           }
//       }


//        for (int pixel : pixels
//             ) {
//            System.out.println(pixel);
//
//        }


    }

    int[] createPixelArray(int width, int height, Bitmap thresholdImage) {

        int[] pixels = new int[width * height];
        thresholdImage.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;

    }


//    private void saveImage(ArrayList<Bitmap> bitmapArrayList) {
//
//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/testSetsForClassifier");
//
//        if (!myDir.mkdir()) {
//            Toast.makeText(ViewSegments.this, "Could not create directory. Access Denied!!", Toast.LENGTH_SHORT).show();
//        }
//        Random generator = new Random();
//        for (int i = 0; i < bitmapArrayList.size(); i++) {
//            String fname = "Img-" + generator.nextInt(10000) + ".jpg";
//            File file = new File(myDir, fname);
//            try {
//                FileOutputStream out = new FileOutputStream(file);
//                Bitmap scaled = Bitmap.createScaledBitmap(bitmapArrayList.get(i), 16, 16, true);
//                scaled.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                out.flush();
//                out.close();
//
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//            }
//
//
//        }
//    }
//        for (int i = 0; i < bitmapArrayList.size(); i++) {
//            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            bitmapArrayList.get(i).compress(Bitmap.CompressFormat.JPEG, 40, bytes);
//
////you can create a new file name "test.jpg" in sdcard folder.
//            File f = new File(Environment.getExternalStorageDirectory()
//                    + File.separator + String.valueOf(i)+".jpg");
//            try {
//                f.createNewFile();
////write the bytes in file
//                FileOutputStream fo = new FileOutputStream(f);
//                fo.write(bytes.toByteArray());
//
//// remember close de FileOutput
//                fo.close();
//            }
//            catch(Exception e){
//
//            }
//        }

//    }


    public boolean writeToFile(String filename, char data) {
        try {
            FileOutputStream fos = openFileOutput(filename, 0);
            OutputStreamWriter out = new OutputStreamWriter(fos);
            out.write(data);
            out.close();
            return true;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.out.println("-----problem in writeToFile()--------");
            return false;
        }
    }
}





