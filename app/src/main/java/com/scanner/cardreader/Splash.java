package com.scanner.cardreader;

/**
 * Created by aviisekh on 8/5/16.
 *
 * This the Splash screen that loads with out main logo.
 * Reading the Json File for weights and detecting the SIM information is done in this
 * activity since these tasks can be done prior to the  run time.
 *
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.scanner.cardreader.camera.CameraAccess;
import com.scanner.cardreader.camera.CropActivity;
import com.scanner.cardreader.classifier.JsonContentReader;
import com.scanner.cardreader.classifier.WeightReader;

public class Splash extends Activity {
    public static String SIM;

    public static double [][] biases_at_layer2;
    public static double [][] weight_at_layer2;
    public static double [][] weight_at_layer3;
    public static double [][] biases_at_layer3;


    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    private TextView siminfo ;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

       siminfo = (TextView)findViewById(R.id.splashSimInfo) ;

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){


            @Override
            public void run() {

                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                    if(telephonyManager.getNetworkOperatorName().toUpperCase().trim() == "NCELL"){

                        SIM = "NCELL";

                    }
                    else{
                        SIM = "NTC";
                    }
                }
                else
                {
                    SIM ="No SIM detected";
                }

                siminfo.setText(SIM);


                JsonContentReader jsonContentReader = new JsonContentReader();
                String jsonContent = jsonContentReader.getJsonString(getApplicationContext());

                WeightReader weightReader = new WeightReader();
                biases_at_layer2=weightReader.getWeights(jsonContent,"layer_1_bias");
                weight_at_layer2=weightReader.getWeights(jsonContent,"layer_1_weight");
                weight_at_layer3=weightReader.getWeights(jsonContent,"layer_2_weight");
                biases_at_layer3=weightReader.getWeights(jsonContent,"layer_2_bias");



                /* Create an Intent that will start the Camera Activity. */
                Intent mainIntent = new Intent(Splash.this,CropActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }


        }, SPLASH_DISPLAY_LENGTH);
    }
}
