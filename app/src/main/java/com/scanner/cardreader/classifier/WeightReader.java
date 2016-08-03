package com.scanner.cardreader.classifier;

import android.content.Context;

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
    static double [][] biases_at_layer2 = new double[20][1];
    static double [][] biases_at_layer3 = new double[10][1];
    static double [][] weight_at_layer2= new double[20][256];
    static double [][] weight_at_layer3= new double[10][20];

    static double [][] input = new double[256][1];
    public static void setWeights(Context context)  {  // throws IOException //throws IOException
        String jsonContent = loadJSONFromRaw(context);

        String inputString = loadJSONFromRawInput(context);
        try {
            JSONObject jsonRootObject = new JSONObject(jsonContent);
            JSONArray jsonBiasArray_1 = jsonRootObject.optJSONArray("layer_1_bias");
            Iterator<String> keys = jsonRootObject.keys();

           // while (keys.hasNext())
            //{
                //Log.d("keys",keys. );
            //}
            for (int i = 0;i<jsonBiasArray_1.length();i++)
            {
                for (int j =0;j<jsonBiasArray_1.getJSONArray(i).length();j++)
                {
                    biases_at_layer2[i][j] = jsonBiasArray_1.getJSONArray(i).getDouble(j);
                }
            }

            JSONArray jsonBiasArray_2 = jsonRootObject.optJSONArray("layer_2_bias");
            for (int i = 0;i<jsonBiasArray_2.length();i++)
            {
                for (int j =0;j<jsonBiasArray_2.getJSONArray(i).length();j++)
                {
                    biases_at_layer3[i][j] = jsonBiasArray_2.getJSONArray(i).getDouble(j);
                }
            }

            JSONArray jsonWeightArray_1 = jsonRootObject.optJSONArray("layer_1_weight");
            for (int i = 0;i<jsonWeightArray_1.length();i++)
            {
                for (int j =0;j<jsonWeightArray_1.getJSONArray(i).length();j++)
                {
                    weight_at_layer2[i][j] = jsonWeightArray_1.getJSONArray(i).getDouble(j);
                }
            }

            JSONArray jsonWeightArray_2 = jsonRootObject.optJSONArray("layer_2_weight");
            for (int i = 0;i<jsonWeightArray_2.length();i++)
            {
                for (int j =0;j<jsonWeightArray_2.getJSONArray(i).length();j++)
                {
                    weight_at_layer3[i][j] = jsonWeightArray_2.getJSONArray(i).getDouble(j);
                }
            }

            JSONObject jsonRootObjectInput = new JSONObject(inputString);
            JSONArray jsonInputArray = jsonRootObjectInput.optJSONArray("input");
            for (int i=0;i<jsonInputArray.length();i++)
            {
                input[i][0]= jsonInputArray.getDouble(i);
            }


            } catch (JSONException e1) {
            e1.printStackTrace();
        }



    }

    public static String loadJSONFromRaw( Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            InputStream inputFile = context.getResources().openRawResource(R.raw.weights);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputFile));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String loadJSONFromRawInput( Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            InputStream inputFile = context.getResources().openRawResource(R.raw.raw);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputFile));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}




