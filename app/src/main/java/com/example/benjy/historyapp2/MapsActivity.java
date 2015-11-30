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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private Geofence fence;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Circle circle;
    private static final StoreLocation[] LOCATIONS = new StoreLocation[] {
            new StoreLocation(new LatLng(51.523231, -0.040399), new String("Queens' Building")),
            new StoreLocation(new LatLng(51.52446209938, -0.0392165780067444), new String("Jewish Cemetry")),
<<<<<<< HEAD
            new StoreLocation(new LatLng(51.391151, -0.287265), new String("Testing Geofences")),
=======
>>>>>>> 9991f004348af5254b9cf848200fd2d25560fa05

    };

    ArrayList<Geofence> mGeofenceList = new ArrayList<Geofence>();
    private static final int GEOFENCERADIUS =50; //50 meters
    private PendingIntent mCurrentIntent;


<<<<<<< HEAD
=======
    //51.527820, -0.021114
>>>>>>> 9991f004348af5254b9cf848200fd2d25560fa05
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

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        //createGeofences();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map
     * is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
<<<<<<< HEAD
     * we just add a marker in London, England.
=======
     * we just add a marker near Sydney, Australia.
>>>>>>> 9991f004348af5254b9cf848200fd2d25560fa05
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in London and move the camera
        LatLng london = new LatLng(51.523, -0.0402);
        circle = mMap.addCircle(new CircleOptions()
                .center(london)
                .radius(10)
                .strokeColor(Color.WHITE)
                .fillColor(Color.BLUE));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(london));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));

        //TODO have to implement settings
        //set markers
        for (int ix = 0; ix < LOCATIONS.length; ix++) {
            mMap.addMarker(new MarkerOptions()
                    .position(LOCATIONS[ix].getPosition()));
        }
    }
    public void createGeofences () {

        for (int i = 0; i < LOCATIONS.length; i++) {
<<<<<<< HEAD
            Log.i("Geofence","Geofence created");
=======
            Log.i("COOL","££££££££");
>>>>>>> 9991f004348af5254b9cf848200fd2d25560fa05
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


        createGeofences();
<<<<<<< HEAD
        Log.i("TOOL", "Geofence list size:"+ mGeofenceList.size());
=======
        Log.i("TOOL", mGeofenceList.size() + "");
>>>>>>> 9991f004348af5254b9cf848200fd2d25560fa05
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
<<<<<<< HEAD
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
=======
        mLocationRequest.setInterval(7000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
>>>>>>> 9991f004348af5254b9cf848200fd2d25560fa05
        return mLocationRequest;
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, createLocationRequest(), this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateMap();
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,getGeofencingRequest(),getGeofencePendingIntent()).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                //Log.i("APPPP", "It works");
            }
        });
<<<<<<< HEAD
        Log.i("Location", "Location updated");
=======
        Log.i("WORKs", "YAEH");
>>>>>>> 9991f004348af5254b9cf848200fd2d25560fa05
    }
    private void updateMap()
    {

        LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        circle.setCenter(current);
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
}
