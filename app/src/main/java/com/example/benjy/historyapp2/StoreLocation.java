package com.example.benjy.historyapp2;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Benjy on 22/11/2015.
 */
public class StoreLocation {
    private LatLng position;
    private String explaination;



    public StoreLocation(LatLng pos, String explain){
        position=pos;
        explaination=explain;
    }

    public LatLng getPosition(){
        return position;
    }

    public String getExplaination(){
        return explaination;
    }
}