package com.scanner.cardreader.preprocessing;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

/**
 * Created by Anush Shrestha on 8/3/2016.
 */

public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private MediaScannerConnection mediaScannerConnection;
    private File imageFile;

    public SingleMediaScanner() {

    }

    public void beginConnection(Context context,File imageFile){
        this.imageFile = imageFile;
        mediaScannerConnection = new MediaScannerConnection(context, this);
        mediaScannerConnection.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mediaScannerConnection.scanFile(imageFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mediaScannerConnection.disconnect();
    }
}
