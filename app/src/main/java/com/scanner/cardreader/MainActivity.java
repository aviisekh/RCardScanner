package com.scanner.cardreader;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.scanner.cardreader.camera.CameraActivity;

public class MainActivity extends AppCompatActivity {
    private Uri fileUri;


    public static String SIM; //Global Variable to define the network carrier : NTC/NCELL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button proceedBtn = (Button) findViewById(R.id.proceedBtn);
        assert proceedBtn != null;
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(i);
            }
        });

        Thread t = new Thread(new Runnable() {
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
            }
        });
        t.start();

        proceedBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getBaseContext(), CameraActivity.class));
                //startActivity(new Intent(getBaseContext(), CropActivity.class));
            }
        });


    }
//    private void captureImage() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//
//        // start the image capture Intent
//        startActivityForResult(intent, CAchMERA_CAPTURE_IMAGE_REQUEST_CODE);
//    }

}
