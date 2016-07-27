package com.scanner.cardreader;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button proceedBtn;


    public static String SIM = "NTC"; //Global Variable to define the network carrier : NTC/NCELL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        proceedBtn = (Button) findViewById(R.id.proceedBtn);
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
                TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
                    String carrier = tm.getLine1Number().substring(0, 3);
                    if (carrier.equals("984")) {
                        SIM = "NTC";
                    } else {
                        SIM = "NCELL";
                    }
                } else {
                    SIM = "Unknown";
                }
            }
        });
        t.start();
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                startActivity(new Intent(getBaseContext(), CameraActivity.class));
                startActivity(new Intent(getBaseContext(), CropActivity.class));
            }
        });


    }

}
