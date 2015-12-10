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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Circle circle;//user position
    private CircleOption mOptions = new CircleOption();
    private static StoreLocation[] LOCATIONS = new StoreLocation[] {
            new StoreLocation(new LatLng(51.523231, -0.040399), new String("Queens' Building")),
            new StoreLocation(new LatLng(51.52446209938, -0.0392165780067444), new String("Jewish Cemetry")),
            new StoreLocation(new LatLng(51.391151, -0.287265), new String("Testing Geofences")),

    };

    public static final String PASSING="Loc";
    ArrayList<Geofence> mGeofenceList = new ArrayList<Geofence>();
    private static final int GEOFENCERADIUS =50; //50 meters
    private static final String LOCATION_KEY = "Location";
    public static final String LENGTH= "Locations_array_length";
    private static boolean block_camera=false;

    //51.527820, -0.021114
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Firebase.setAndroidContext(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();


        updateValuesFromBundle(savedInstanceState);

        // starts ExlporeLocation activity
        Button explore_button = (Button) findViewById(R.id.button);
        explore_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startExploreList();
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
    //this method is used  when the button Explore Locations is clicked
    private void startExploreList(){
        Intent intent = new Intent(getApplicationContext(), ExploreLocations.class);
        int t=0;
        for (int i=0;i<LOCATIONS.length;i++){
            if (LOCATIONS[i]!=null){
                intent.putExtra(PASSING+i, LOCATIONS[i]);
                t++;
            }
        }

        intent.putExtra(LENGTH, t);//passing the number of location to the next activity

        //onSaveInstanceState(savedInstanceState);
        startActivity(intent);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
            updateUserPosition();
        }
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
        final int CIRCLE_SPEED=12;//the circle speed is the rate at wich the circle will shrink when the user zooms in,
        // an higher number means increasing the  shrinking rate, so lower number may make the cicle shrink slower
        // but if it's too low it may be not shrink enough. (just try it ;) and find which one is better)

        CameraUpdate position= CameraUpdateFactory.newLatLng(london);// camera points the default location
        final int CIRCLE_RADIUS = 50;// 50 is the defualt value for the radius of the circle(you can change it)
        final int CIRCLE_SPEED_OUT=15;// the higher the faster the circle growes, CAN't GO under 14


        // Add a default user position in London (queen mary) and move the camera


        mOptions.setOption(new CircleOptions()
                .center(london)
                .radius(CIRCLE_RADIUS)//
                .strokeColor(Color.WHITE)
                .fillColor(Color.BLUE));


        circle = mMap.addCircle(mOptions.getOption());

        mMap.moveCamera(position);
        mMap.moveCamera(init_zoom);

        //callback method when the user clicks in the small in the info window.
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast toast = Toast.makeText(getApplicationContext(), marker.getTitle(), Toast.LENGTH_LONG);
                toast.show();

                //we can use this to start a new activity or fragment, to display daital about the locations
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                block_camera = true;

                return false;
            }
        });
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
            private void ZoomCircle(Circle c, double zoom, double circle_speed) {
                final double COEFFICIENT = 700;
                c.setRadius(COEFFICIENT / (zoom + circle_speed));
            }

            //use ZoomOutCircle to make the circle grow
            private void ZoomOutCircle(Circle c, double zoom, double circle_speed) {
                c.setRadius((CIRCLE_RADIUS /3) * (circle_speed - zoom));
            }

            private void zoomIn(Circle c) {
                double inc = (mMap.getCameraPosition().zoom - ref_zoom);
                if (inc >= 0.0001) {
                    ZoomCircle(c, mMap.getCameraPosition().zoom, CIRCLE_SPEED * inc);//set the radius of the user's circle depending on the zoom
                } else {
                    c.setRadius(CIRCLE_RADIUS);// i used the circle.setRadius because i know the standart value
                }

            }

            private void zoomOut(Circle c) {
                double inc = (ref_zoom - mMap.getCameraPosition().zoom);
                if (inc >= 1) {
                    ZoomOutCircle(c, mMap.getCameraPosition().zoom, inc * CIRCLE_SPEED_OUT);//set the radius of the user's circle depending on the zoom
                } else {
                    c.setRadius(c.getRadius());// i used the circle.setRadius because i know the standart value
                }

            }

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                block_camera = false;// unlock the camera

                if (cameraPosition.zoom >= ref_zoom) {
                    //the user is zooming in, so the zoom increases, so it will be greater than the ref_zoom
                    zoomIn(circle);
                } else {
                    //if the user is zooming out
                    zoomOut(circle);
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
                    .setExpirationDuration(5000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());

        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
        //creategeofences should be after retriving data
        createGeofences();
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,getGeofencingRequest(),getGeofencePendingIntent()).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                //Log.i("APPPP", "It works");

            }
        });
        Log.i("TOOL", mGeofenceList.size() + "");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, createLocationRequest(), this);
    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
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
        //mcircleOptions.center(current);//update user current position in the map
        circle.setCenter(current);
        Log.i("ZOOM:", " " + mMap.getCameraPosition().zoom);
        if(!block_camera)
            mMap.animateCamera(CameraUpdateFactory.newLatLng(current));//move map to the user's positio
        updateMap();
    }
    /*
    Updates the objects inside the map(geofences) and the user circle.
     */
    private void updateMap(){

        //set markers based on the return objects of the geoquery
        for (int ix = 0; ix < LOCATIONS.length; ix++) {
            mMap.addMarker(new MarkerOptions()
                    .snippet("YOLO")
                    .title(LOCATIONS[ix].getTitle())
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
