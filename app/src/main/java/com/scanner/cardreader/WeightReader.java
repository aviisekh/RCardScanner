package com.scanner.cardreader;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by aviisekh on 7/29/16.
 */
public class WeightReader {
    public void read(Context context) //throws IOException
    {
        InputStream buildinginfo = context.getResources().openRawResource(R.raw.raw);
        InputStreamReader ir = new InputStreamReader(buildinginfo);
        BufferedReader myBuffer = new BufferedReader(ir);
        try {
            String firstString = myBuffer.readLine();
            Log.d("string", String.valueOf(firstString.charAt(1)));
        }
        catch (IOException e)
        {

        }
    }

}
