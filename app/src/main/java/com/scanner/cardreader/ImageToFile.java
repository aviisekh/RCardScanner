package com.scanner.cardreader;

import android.graphics.Bitmap;
import android.os.Environment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by mandy on 8/3/16.
 */
public class ImageToFile {

    private static boolean SaveImageToFile(Bitmap bitmapImage, boolean scale, String imageName, String folder) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (scale) {
            Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 16, 16, true);
            scaled.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        } else {
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        }
        File f = new File(Environment.getExternalStorageDirectory() + folder + File.separator + imageName);
        try {
            if (f.createNewFile()) {
                return true;
            }

            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
