package com.example.oluwole.historyapp;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Oluwole on 26/12/2015.
 */
public class GPSreceiver extends BroadcastReceiver {

    private final static String WAIT_TEXT="Just a sec..";
    private final static String GPS_OFF="Please activate the location services";
    public GPSreceiver(){
        Log.i("BroadcastReceiver", "INIT Receiver");
    }
    //TODO when the app is running in the background there are some problems when the user switch off the GPS

    @Override
    public void onReceive(final Context context, Intent intent) {
        MapsActivity.isGpsEnabled=!MapsActivity.isGpsEnabled;
        //only if the user presses cancel on the GPSdialog
        //if (!MapsActivity.mMapsApi_connected) {
            if (MapsActivity.isGpsEnabled) ;
            else {
                //When the GPS is enabled for the first time it needs time to get the users location
                if (MapsActivity.mGoogleApiClient.isConnected())
                    MapsActivity.mGoogleApiClient.disconnect();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            super.run();
                            //TODO put a handler to create a toast message
                            sleep(5000);  //Delay of 10 seconds
                        } catch (Exception e) {

                        } finally {
                            if (MapsActivity.isNetworkEnabled) {
                                if (MapsActivity.mGoogleApiClient != null)
                                    MapsActivity.mGoogleApiClient.connect();
                            }
                        }
                    }
                }.start();
            }
        }
   }

//}