package com.scanner.cardreader.classifier;

import android.content.Context;
import android.support.annotation.Nullable;

import com.scanner.cardreader.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * Created by aviisekh on 7/29/16.
 */
public class WeightReader {
    double [][] weightsArr;  //Contains weights and bias array

    public double [][] getWeights(String jsonContent, String weights) {

        try {
            JSONObject jsonRootObject = new JSONObject(jsonContent);
            JSONArray jsonWeightArray = jsonRootObject.optJSONArray(weights);

            int rows = jsonWeightArray.length();
            int columns = jsonWeightArray.getJSONArray(0).length();

            weightsArr = new double[rows][columns];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    weightsArr[i][j] = jsonWeightArray.getJSONArray(i).getDouble(j);

                }
            }

        } catch (JSONException e1) {
            e1.printStackTrace();

        }

        return weightsArr;


    }

}




