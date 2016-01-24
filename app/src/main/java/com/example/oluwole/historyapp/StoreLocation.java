package com.example.oluwole.historyapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Oluwole on 21/11/2015.
 */
public class StoreLocation implements Parcelable{
    private LatLng position;//LatLon object where I can get latitude and longitude
    private String title;//the string that we want the user to view.
    private String description;
    private String tags;
    private long favourites;//rate is an object, with double total and long count variables
    private boolean seen;
    private boolean hasFavorite;//the user's favourite
    private double distanceFromUser;

    public StoreLocation(LatLng pos, String title, String description, String tags,long favourites,boolean seen,boolean hasFavorite){
        position=pos;
        this.title=title;
        this.description=description;
        this.tags=tags;
        this.favourites=favourites;
        this.seen=seen;
        this.hasFavorite=hasFavorite;
        distanceFromUser=0;
    }

    public long getFavourites(){
        return favourites;
    }
    public LatLng getPosition(){
        return position;
    }
    public boolean getHasFavourite(){
        return hasFavorite;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public String getTags(){
        return tags;
    }
    public boolean getSeen(){
        return seen;
    }
    public double getDistanceFromUser(){return distanceFromUser;}

    public void setPosition(LatLng latLng){
        position=latLng;
    }
    public void setTitle1(String s){
        title=s;
    }
    public void setDescription(String s){
        description=s;
    }
    public void setTags(String s){
        tags=s;
    }
    public void setSeen(boolean t){
        seen=t;
    }
    public void setFavourites(int favourites){
        this.favourites=favourites;
    }
    public void setHasFavorite(boolean b){
        hasFavorite=b;
    }
    public void setDistanceFromUser(double d){distanceFromUser=d;}

    @Override
    public boolean equals(Object object) {

        if (object != null && object instanceof StoreLocation) {
            StoreLocation o = (StoreLocation) object;
            if (title == null) {
                return (o.title==null);
            }
            else {
                return title.equals(o.title);
            }

        }

        return false;
    }


    protected StoreLocation(Parcel in) {
        this.position = in.readParcelable(LatLng.class.getClassLoader());
        this.title = in.readString();
        this.description=in.readString();
        this.tags=in.readString();
        this.favourites=in.readLong();
        this.seen=in.readByte() != 0;
        this.hasFavorite=in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(position, flags);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(tags);
        dest.writeLong(favourites);
        dest.writeByte((byte) (seen ? 1 : 0));     //if myBoolean == true, byte == 1
        dest.writeByte((byte) (hasFavorite ? 1 : 0));     //if myBoolean == true, byte == 1
    }

    public static final Creator<StoreLocation> CREATOR = new Creator<StoreLocation>() {
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
