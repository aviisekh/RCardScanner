package com.scanner.cardreader.segmentation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Created by mandy on 7/11/16.
 */
public class PrepareImage {
    public static Bitmap addBackgroundPixels(Bitmap img) {
/*

if we do the ccl on right image the ccl out put will be one connected component
        to avoid this error we use this function that surrounds the image with background pixel */
        int w = img.getWidth();
        int h = img.getHeight();

        Bitmap resizedImage = Bitmap.createBitmap(w+2, h+2, Bitmap.Config.RGB_565);
        Canvas g = new Canvas();
        g.setBitmap(resizedImage);
        g.drawColor(Color.WHITE);
        g.drawBitmap(img,1,1,null);

        return resizedImage;
    }
}
