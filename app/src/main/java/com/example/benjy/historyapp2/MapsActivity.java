package com.example.benjy.historyapp2;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Button;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.Geofence;

import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Circle circle;//user position
    private CircleOption mOptions = new CircleOption();

    private static final StoreLocation[] LOCATIONS = new StoreLocation[] {
            new StoreLocation(new LatLng(51.523231, -0.040399), new String("Queens' Building")),
            new StoreLocation(new LatLng(51.52446209938, -0.0392165780067444), new String("Jewish Cemetry")),

    };

    ArrayList<Geofence> mGeofenceList = new ArrayList<Geofence>();
    private static final int GEOFENCERADIUS =50; //50 meters



    //51.527820, -0.021114
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map
     * is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;//get map object in a global variable
        LatLng london = new LatLng(51.523, -0.0402);//default location
        final CameraUpdate init_zoom=CameraUpdateFactory.zoomTo(14);// default map's zoom
        final float ref_zoom=14;//this var stores the current zoom value(init_zoom)
        final int CIRCLE_SPEED=7;//the circle speed is the rate at wich the circle will shrink when the user zooms in,
        // an higher number means increasing the  shrinking rate, so lower number may make the cicle shrink slower
        // but if it's too low it may be not shrink enough. (just try it ;) and find which one is better)

        CameraUpdate position= CameraUpdateFactory.newLatLng(london);// camera points the default location
        final int CIRCLE_RADIUS = 35;// 35 is the defualt value for the radius of the circle(you can change it)
        final int CIRCLE_SPEED_OUT=15;// the higher the faster the circle growes

        // Add a default user position in London (queen mary) and move the camera


        mOptions.setOption(new CircleOptions()
                .center(london)
                .radius(CIRCLE_RADIUS)//
                .strokeColor(Color.WHITE)
                .fillColor(Color.BLUE));


        circle = mMap.addCircle(mOptions.getOption());

        mMap.moveCamera(position);
        mMap.moveCamera(init_zoom);

        //it will be use to understand if the user is zooming out or in.
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            /*
            Use this method to decrease the circle radius
            zoom is the actual screen zoom, it goes from 2(min zoom, you can see the all globe)
            to 20(max zoom), 14 is the default value (it's better than 12).
            the increment is the value to add to the zoom in order to increase(circle speed) the speed at which
            the circle shrinks.
            the COEFFICIENT is 490 because 35*14= 490, 35 is the default radius of the center and 14 is
            the default zoom.
             */
            private void ZoomCircle(double zoom, double circle_speed){
                final double COEFFICIENT=490;
                circle.setRadius(COEFFICIENT / (zoom + circle_speed));
            }
            //use ZoomOutCircle to make the circle grow
            private void ZoomOutCircle(double zoom, double circle_speed){
                final double COEFFICIENT=14;
                circle.setRadius(COEFFICIENT*(circle_speed-zoom));
            }

            private void zoomIn(){
                double inc = (mMap.getCameraPosition().zoom - ref_zoom);
                if (inc>=1){
                    ZoomCircle(mMap.getCameraPosition().zoom, CIRCLE_SPEED*inc);//set the radius of the user's circle depending on the zoom
                }
                else {
                    circle.setRadius(CIRCLE_RADIUS);// i used the circle.setRadius because i know the standart value
                }

            }
            private void zoomOut(){
                double inc = (ref_zoom-mMap.getCameraPosition().zoom);
                if (inc>=1){
                    ZoomOutCircle(mMap.getCameraPosition().zoom,inc*CIRCLE_SPEED_OUT);//set the radius of the user's circle depending on the zoom
                }
                else {
                    circle.setRadius(CIRCLE_RADIUS);// i used the circle.setRadius because i know the standart value
                }

            }
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {


                if (cameraPosition.zoom > ref_zoom) {
                    //the user is zooming in, so the zoom increases, so it will be greater than the ref_zoom
                    zoomIn();
                }
                else {
                    //if the user is zooming out
                    zoomOut();
                }

            }
        });
    }

    public void createGeofences () {

        for (int i = 0; i < LOCATIONS.length; i++) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(String.valueOf(i))

                    .setCircularRegion(
                            LOCATIONS[i].getPosition().latitude,
                            LOCATIONS[i].getPosition().longitude,
                            GEOFENCERADIUS
                    )
                    .setExpirationDuration(30000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());

        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.i("Connected", "Location services connected.");
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
        //creategeofences should be used after retriving data from the database
        createGeofences();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(7000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        return mLocationRequest;
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, createLocationRequest(), this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateUserPosition();// it updates the user's circle inside the map
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,getGeofencingRequest(),getGeofencePendingIntent()).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                //Log.i("APPPP", "It works");
            }
        });
        Log.i("WORKs", "YAEH");
    }
    /*
    This method will update the position of the user on the map, the value 'current' rapresents the
    current position of the user, the ruturn
     */
    private void updateUserPosition()
    {
        LatLng current = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        circle.setCenter(current);
        Log.i("ZOOM:", " " + mMap.getCameraPosition().zoom);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));//move map to the user's position
        updateMap();
    }
    /*
    Updates the objects inside the map(geofences) and the user circle.
     */
    private void updateMap(){
        mMap.clear();
        // this instruction clears the Map object from the other object, it's needed in orther to display
        //the right current geofences without having the previous ones still on screen

        mOptions.setOption(mOptions.getOption().center(circle.getCenter()));
        mOptions.setOption(mOptions.getOption().radius(circle.getRadius()));


        circle=mMap.addCircle(mOptions.getOption());//i need to add again the user circle object on screen

        //TODO have to implement settings
        //set markers based on the return objects of the geoquery
        for (int ix = 0; ix < LOCATIONS.length; ix++) {
            mMap.addMarker(new MarkerOptions()
                    .position(LOCATIONS[ix].getPosition()));
        }
    }

    private GeofencingRequest getGeofencingRequest () {

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent () {
        // Reuse the PendingIntent if we already have it.

        Intent intent = new Intent(this, GeofenceReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    /*
    use get set methods to set and retreive circleoptions radius,center etc..
    I've put CircleOptions because in order to add a circle on the map I need
    circleOptions but from an object circle i can't get the OBJECT circleOptions
    , so I decided to use CircleOptions  in order to update and modify a circle, inside the method
    updateMap().

     */
    private class CircleOption{
        CircleOptions mCircleOptions;

        public CircleOption(){

        }

        public void setOption(CircleOptions op){
            mCircleOptions=op;
        }

        public CircleOptions getOption(){
            return mCircleOptions;
        }
    }
}
