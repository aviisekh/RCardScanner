package com.scanner.cardreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Anush Shrestha on 8/3/2016.
 */

public class ImageWriter {
    Context context;

    public ImageWriter(Context context) {
        this.context = context;
    }

    public boolean writeImage(Bitmap printImage, boolean scale, String imageName, String folderName) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        if (scale) {
            Bitmap scaled = Bitmap.createScaledBitmap(printImage, 16, 16, true);
            scaled.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        } else {
            printImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        }
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        String path = Environment.getExternalStorageDirectory() + File.separator + folderName;

        File f = new File(path + File.separator + imageName + "_" + System.currentTimeMillis() / 1000 + ".jpg");
        try {
            if (f.createNewFile()) {
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();
                Log.d("file", "created" + imageName);
                // initiate media scan and put the new things into the path array to
// make the scanner aware of the location and the files you want to see

                new SingleMediaScanner(context, f);

                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
