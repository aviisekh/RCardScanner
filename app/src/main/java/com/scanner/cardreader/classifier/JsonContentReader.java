package com.scanner.cardreader.classifier;

import android.content.Context;

import com.scanner.cardreader.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by aviisekh on 8/11/16.
 */

public class JsonContentReader {
    public String getJsonString(Context context) {
        StringBuilder jsonContent = new StringBuilder();
        try {
            InputStream inputFile = context.getResources().openRawResource(R.raw.weights);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputFile));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonContent.append(line);
            }

            bufferedReader.close();
            return jsonContent.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
