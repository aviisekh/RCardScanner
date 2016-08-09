package com.scanner.cardreader;

/**
 * Created by aviisekh on 8/5/16.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.scanner.cardreader.camera.CameraAccess;

public class Splash extends Activity {
    public static String SIM;

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    TextView siminfo ;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
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

                        SIM = "NCELL SIM detected";

                    }
                    else{
                        SIM = "NTC SIM etected";
                    }
                }
                else
                {
                    SIM ="No SIM detected";
                }

                siminfo.setText(SIM);
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(Splash.this,CameraAccess.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
