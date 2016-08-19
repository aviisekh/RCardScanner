package com.scanner.cardreader.interfaces;

import android.graphics.Rect;
import android.media.Image;
import android.widget.ImageView;

/**
 * Created by aviisekh on 8/19/16.
 */
public interface BitmapCoordinates {
    public Rect getCoordinates(ImageView imageView, Rect rect);
}
