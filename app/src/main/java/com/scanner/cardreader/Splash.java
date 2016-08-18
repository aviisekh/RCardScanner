package com.scanner.cardreader;

/**
 * Created by aviisekh on 8/5/16.
 * <p/>
 * This the Splash screen that loads with out main logo.
 * Reading the Json File for weights and detecting the SIM information is done in this
 * activity since these tasks can be done prior to the  run time.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.scanner.cardreader.camera.CameraAccess;
import com.scanner.cardreader.classifier.JsonContentReader;
import com.scanner.cardreader.classifier.NNMatrix;
import com.scanner.cardreader.classifier.WeightReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Splash extends Activity {
    public static String SIM;
    public static NNMatrix weights_at_layer2, weights_at_layer3;
    public static NNMatrix bias_at_layer2, bias_at_layer3;

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    boolean isPermissionAsked = false;
    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    private TextView siminfo;


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void permissionWrapper() {
        List<String> permissionNeeded = new ArrayList<String>();
        final List<String> permissionList = new ArrayList<String>();

        if (!addPermission(permissionList, android.Manifest.permission.CALL_PHONE)) {
            permissionNeeded.add("CALL");
        }
        if (!addPermission(permissionList, Manifest.permission.CAMERA)) {
            permissionNeeded.add("CAMERA");
        }
        if (!addPermission(permissionList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionNeeded.add("MOUNT FOLDER");
        }
        if (!addPermission(permissionList, Manifest.permission.READ_PHONE_STATE)) {
            permissionNeeded.add("SIME");
        }
        if (!addPermission(permissionList, Manifest.permission.VIBRATE)) {
            permissionNeeded.add("HAPTICS");
        }


        if (permissionList.size() > 0) {
            if (permissionNeeded.size() > 0) {
                //need rationale
//                String message = "Please grant access to " + permissionNeeded.get(0);
//                for (int i = 1; i < permissionNeeded.size(); i++) {
//                    message = message + "," + permissionNeeded.get(i);
//                }

//                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
//                    @TargetApi(Build.VERSION_CODES.M)
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        requestPermissions(permissionList.toArray(new String[permissionList.size()]),
//                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
//                    }
//                });
//                return;
            }
            requestPermissions(permissionList.toArray(new String[permissionList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

            isPermissionAsked = true;
            return;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(permission);

            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.VIBRATE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    Toast.makeText(this, "ALll Permission granted", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission De
                    // ied
                    Toast.makeText(this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        siminfo = (TextView) findViewById(R.id.splashSimInfo);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/

            if (!isPermissionAsked)
                permissionWrapper();

        runThread();
    }

    private void runThread(){
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {

                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                    if (telephonyManager.getNetworkOperatorName().toUpperCase().trim() == "NCELL") {

                        SIM = "NCELL";

                    } else {
                        SIM = "NTC";
                    }
                } else {
                    SIM = "No SIM detected";
                }

                siminfo.setText(SIM);


                JsonContentReader jsonContentReader = new JsonContentReader();
                String jsonContent = jsonContentReader.getJsonString(getApplicationContext());

                WeightReader weightReader = new WeightReader();
                bias_at_layer2 = new NNMatrix(weightReader.getWeights(jsonContent, "layer_1_bias"));
                weights_at_layer2 = new NNMatrix(weightReader.getWeights(jsonContent, "layer_1_weight"));
                weights_at_layer3 = new NNMatrix(weightReader.getWeights(jsonContent, "layer_2_weight"));
                bias_at_layer3 = new NNMatrix(weightReader.getWeights(jsonContent, "layer_2_bias"));

                        /* Create an Intent that will start the Camera Activity. */
                Intent mainIntent = new Intent(Splash.this, CameraAccess.class);
                Splash.this.startActivity(mainIntent);
//                Splash.this.finish();


            }

//        }, SPLASH_DISPLAY_LENGTH);

//    }
}
