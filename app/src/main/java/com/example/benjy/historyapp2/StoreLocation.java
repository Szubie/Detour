package com.example.benjy.historyapp2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Benjy on 22/11/2015.
 */
public class StoreLocation implements Parcelable{
    private LatLng position;//LatLon object where I can get latitude and longitude
    private String title;//the string that we want the user to view.
    private String Description;



    public StoreLocation(LatLng pos, String explain){
        position=pos;
        title=explain;
    }

    public LatLng getPosition(){
        return position;
    }

    public String getTitle(){
        return title;
    }


    protected StoreLocation(Parcel in) {
        this.position = in.readParcelable(LatLng.class.getClassLoader());
        this.title = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(position,flags);
        dest.writeString(title);
    }
    public static final Parcelable.Creator<StoreLocation> CREATOR = new Parcelable.Creator<StoreLocation>() {
        @Override
        public StoreLocation createFromParcel(Parcel in) {
            return new StoreLocation(in);
        }

        @Override
        public StoreLocation[] newArray(int size) {
            return new StoreLocation[size];
        }
    };


}