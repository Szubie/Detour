package com.example.oluwole.historyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Oluwole on 17/01/2016.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {

    public NetworkBroadcastReceiver(){
        //Log.i("Network","THE NETWORK state has changed");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MapsActivity.isNetworkEnabled=!MapsActivity.isNetworkEnabled;
        if (MapsActivity.isNetworkEnabled){
            //if (MapsActivity.mGoogleApiClient!=null)
            //MapsActivity.mMapsApi_connected=!MapsActivity.mMapsApi_connected;

            //Log.i("NETWORK Status", "res:" + MapsActivity.mGoogleApiClient.isConnected());
            //if (!MapsActivity.mMapsApi_connected) {
            if (MapsActivity.mGoogleApiClient!=null) {
                Log.i("Network","THE NETWORK state has changed");
                if (MapsActivity.mGoogleApiClient.isConnected())
                    MapsActivity.mGoogleApiClient.disconnect();
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                super.run();
                                    //TODO put a handler to create a toast message
                                    sleep(5000);  //Delay of 5 seconds

                            } catch (Exception e) {

                            } finally {
                                //if (MapsActivity.isGpsEnabled) {
                                    if (MapsActivity.mGoogleApiClient != null)
                                        MapsActivity.mGoogleApiClient.connect();
                                //}
                            }
                        }
                    }.start();

            }
        }
    }
}
