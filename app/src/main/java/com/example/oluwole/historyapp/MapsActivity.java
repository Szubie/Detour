package com.example.oluwole.historyapp;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.usage.NetworkStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;


import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;

import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GPSDialog.GPSDialogListener{

    private GoogleMap mMap;
    public static GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Location mLastLocation;
    public static ArrayList<StoreLocation> Locationlist= new ArrayList<StoreLocation>();



    ArrayList<Geofence> mGeofenceList = new ArrayList<Geofence>();
    private static final int GEOFENCERADIUS =50; //50 meters
    private static final String LOCATION_KEY = "Location";
    private PendingIntent mGeofencePendingIntent;
    private boolean FLAG_FIRST_CYCLE = true;// the flag indicates if the app has start from an idle state, if it's true I must load
    //the data from the server based on the user location, set the camera zoom to be staedy at 14.0 and create geofences it's done all in the
    //updateUserPosition() method, after the flag is set to false.
    private Locale LANGUAGE= Locale.ENGLISH;
    private boolean isGeoFenceEnabled=false;//the flag if set true the geofences will be created, it's set
    //true if the user goes away for a houndred meters from it's starting point and false after it has created them
    private CameraUpdate init_zoom=null;// default map's zoom
    private long user_id=0;
    private boolean mDataLoaded=false;
    private double SEARCH_RADIUS=1;

    public static boolean isGpsEnabled=false;//variable for the GPS state
    public static boolean isNetworkEnabled=false;
    public static boolean mMapsApi_connected=false;//flag for knowing if the googleMapsApi are connected
    public static FragmentManager GpsFragmentManager;
    public static String COUNTRY="";
    public static String CITY="";
    public static final String PASSING="Loc";




    //51.527820, -0.021114
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Firebase.setAndroidContext(this);
        init_zoom=CameraUpdateFactory.zoomTo(14);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        //to be able to click the button setMarkerbutton the ArrayList LocationList must first be fetch
        ImageButton setMarkerbutton = (ImageButton) findViewById(R.id.imageButton);
        setMarkerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO solve if CITY=null;

                if (!isNetworkEnabled)
                    NetworkEnabled();
                else {
                    if (!isGpsEnabled)
                        GPSEnabled();
                    else {
                        if (mDataLoaded) {
                            DbAdapter adapter=new DbAdapter(getApplicationContext(),CITY);
                            adapter.open();
                            adapter.CheckAndReplaceTable();
                            startAddLocationsActivity();
                        }
                        else
                            PrintToast("Please wait...");
                    }
                }

            }
        });
        // starts ExlporeLocation activity
        Button explore_button = (Button) findViewById(R.id.button);
        explore_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startExploreListActivity();
            }
        });

    }//END ONCREATE method

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps_activity, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView =(SearchView) MenuItemCompat.getActionView(searchItem);

        // Define the listener
        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when action item collapses

                updateMap(false,null);

                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        };
        // Assign the listener to that action item
        MenuItemCompat.setOnActionExpandListener(searchItem, expandListener);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println(query);
                searchView.clearFocus();
                ArrayList<StoreLocation> arrayList = new ArrayList<StoreLocation>();
                //do query on the Locationlist ArrayList
                String tmp = "";
                if (query.charAt(0) == '#') {
                    for (int i = 1; i < query.length(); i++) {
                        tmp += query.charAt(i);
                    }

                    Locationlist.trimToSize();
                    for (int j = 0; j < Locationlist.size(); j++) {
                        if (ScanTags(Locationlist.get(j).getTags(), tmp)) {
                            arrayList.add(Locationlist.get(j));
                        }

                    }
                } else {
                    Locationlist.trimToSize();
                    for (int j = 0; j < Locationlist.size(); j++) {
                        if (Locationlist.get(j).getTitle().equals(query))
                            arrayList.add(Locationlist.get(j));
                    }
                }
                updateMap(true, arrayList);
                arrayList.clear();
                return false;
            }

            private boolean ScanTags(String tags, String filter) {
                int i = 0;
                String tmp = "";
                boolean result = false;
                while (tags.charAt(i) == '#') {
                    i++;
                    if (i == tags.length() - 1)
                        break;
                    while (tags.charAt(i) != '#') {
                        tmp += tags.charAt(i);
                        i++;
                        if (tags.charAt(i) == '.')
                            break;
                    }

                    if (tmp.equals(filter)) {
                        result = true;
                        break;
                    } else {
                        tmp = "";
                    }

                }
                return result;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Define the listener

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

        }
        if (id==R.id.action_movecamera){
            if (mCurrentLocation!=null)
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));
        }

        return super.onOptionsItemSelected(item);
    }

    private void NetworkEnabled(){

        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isNetworkEnabled = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        //TODO should create a dialog since it's more visible
        //ask the user to activate data connection
        if (!isNetworkEnabled)
            PrintToast("Couldn't connetect to the network, please activate data connection.");
    }

    private void GPSEnabled(){
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGpsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (!isGpsEnabled) {
            GpsFragmentManager=getFragmentManager();
            DialogFragment gpSstatuschange = new GPSDialog();
            gpSstatuschange.show(GpsFragmentManager, "GPSDialog");
        }
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
                        sleep(5000);  //Delay of 5 seconds
                    } catch (Exception e) {

                    } finally {
                        mGoogleApiClient.connect();
                    }
                }
            }.start();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        PrintToast("Please activate the location service");
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button, start GPS broadcast receiver

    }

    //this method is used  when the button Explore Locations is clicked
    private void startExploreListActivity(){
        Intent intent = new Intent(getApplicationContext(), ExploreLocations.class);
        intent.putParcelableArrayListExtra(PASSING, Locationlist);
        //onSaveInstanceState(savedInstanceState);
        startActivity(intent);
    }

    //starts the AddLocationsActivity for results
    private void startAddLocationsActivity(){
        final String PASSLIST = "PASSLIST";
        Intent intent = new Intent(getApplicationContext(), AddLocationsActivity.class);
        intent.putParcelableArrayListExtra(PASSLIST, Locationlist);
        final int REQUEST=9001;
        startActivityForResult(intent, REQUEST);
    }

    //Add locations to the database
    private void setLocationValue(Firebase master, Intent data) {
        String location_name = data.getStringExtra("location_name");
        String location_description = data.getStringExtra("location_description");
        String location_tags = data.getStringExtra("location_tags");

        ArrayList<CacheLocations> tmpList=new ArrayList<>(1);
        CacheLocations cacheLocations = new CacheLocations(location_name,new GeoLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        tmpList.add(cacheLocations);
        //futher control in the online database, to check if the location name was already stored
        master.child(location_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    startAddLocationsActivity();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i("CHECK LOC_NAME:","There are prolems"+firebaseError.getMessage());
            }
        });

        GeoFire geoFire = new GeoFire(master);
        geoFire.setLocation(location_name, new GeoLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, FirebaseError error) {
                if (error != null) {
                    PrintToast("There was a problem when savaing the point: " + error.toString());
                    //TODO I must send the intent data back to the startAddLocationsActivity, so that the user doesn't have to write it again
                    startAddLocationsActivity();
                } else {
                    PrintToast("The location has been saved!");
                }
            }
        });
        //getPArent gets the string Path that is higher
        master.getParent().child("Descriptions").child(location_name).setValue(location_description);

        ArrayList<String> TagsArrayList = new ArrayList<String>();
        HashTagScan(location_tags, TagsArrayList);
        for (int i=0;i<TagsArrayList.size();i++)
            master.getParent().child("TagsByPlace").child(location_name).child(TagsArrayList.get(i)).setValue(true);
        TagsArrayList.clear();
        master.getParent().child("Favourites").child(location_name).child("count").setValue((long) 0);
        addAttributes(tmpList);
    }
    //must return number of tags contained in the string
    //the string that's scanned must be like this, i.e: #rain#winter#xmas -->rainwinterxmas
    //to be saved on the online database
    private void HashTagScan(String s,ArrayList list){
        String tmp="";
        int i=0;

        while (s.charAt(i)=='#') {
            i++;
            if (i==s.length()-1)
                break;
            while (s.charAt(i) != '#') {
                tmp+=s.charAt(i);
                i++;
                if (s.charAt(i)==' ')
                    break;
            }

            list.add(tmp);
            tmp="";
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Firebase master = GetPathLocations("Places");
        //it's called when the user has wrote the location's name, description and has clicked the deploy button
        if (requestCode == 9001) {
            if(resultCode == Activity.RESULT_OK) {
                if (isNetworkEnabled)
                    setLocationValue(master, data);
                else{
                    PrintToast("Network connection is absent at the moment, please try in another moment.");
                    //TODO the intent data i should send it back to the startAddLocationsActivity
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                //quite difficult to reach
            }
        }
        //it's called when the user goes back from the GPS setting option activity
        else if (requestCode==1){
            if (isNetworkEnabled)
                GPSEnabled();
            else
                NetworkEnabled();
        }

    }//onActivityResult

    private void updateGroupPosition(){
        GetGroupPath("0/Positions/"+user_id).setValue(mCurrentLocation);
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



    /*
    Updates the objects inside the map(geofences)
     */
    private void updateMap(boolean fromQuery,ArrayList querylist){
        // this instruction clears the Map object from the other object, it's needed in orther to display
        //the right current geofences without having the previous ones still on screen
        //CircleOption options = new CircleOption();
        //mOptions.setOption(mOptions.getOption().center(circle.getCenter()));
        //mOptions.setOption(mOptions.getOption().radius(circle.getRadius()));
        mMap.clear();
        //circle=mMap.addCircle(mOptions.radius(circle.getRadius())
        //        .center(circle.getCenter()));//i need to add again the user circle object on screen

        if (fromQuery){
            for (int ix = 0; ix < querylist.size(); ix++) {
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                        .snippet(((StoreLocation) querylist.get(ix)).getTags())
                        .title(((StoreLocation) querylist.get(ix)).getTitle())
                        .position(((StoreLocation) querylist.get(ix)).getPosition()));
            }
        }
        else {
            //set markers based on the return objects of the geoquery
            for (int ix = 0; ix < Locationlist.size(); ix++) {
                if (Locationlist.get(ix).getSeen()){
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_color))
                            .snippet(Locationlist.get(ix).getTags())
                            .title(Locationlist.get(ix).getTitle())
                            .position(Locationlist.get(ix).getPosition()));
                }
                else {
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_gray))
                            .snippet(Locationlist.get(ix).getTags())
                            .title(Locationlist.get(ix).getTitle())
                            .position(Locationlist.get(ix).getPosition()));
                }
            }
        }
    }

    /*
    The method onMapReady is called only once at the start and it load
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;//get map object in a global variable
        mMap.setMyLocationEnabled(true);
        LatLng london = new LatLng(51.523, -0.0402);//default location
        CameraUpdate position= CameraUpdateFactory.newLatLng(london);// camera points the default location


        mMap.moveCamera(position);
        mMap.moveCamera(init_zoom);

        //callback method when the user clicks in the small in the info window.
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                PrintToast(marker.getTitle());
                //we can use this to start a new activity or fragment, to display daital about the locations
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                return false;
            }
        });


    }

    private void ListaddLocation(StoreLocation newStoreLocation,ArrayList list){
        list.trimToSize();
        //I must know if newStoreLocation is in the local dataBase
        //yes: i must overwrite the local data with the newStoreLocation ones then create a newer newStoreLocations
        //no:newStoreLocation goes in the local database
        StoreLocation finalStorelocation=checkLocalDb(newStoreLocation);
        if (finalStorelocation!=null){
            if (!list.contains(finalStorelocation)) {
                list.add(finalStorelocation);

            }
            else {
                //The storelocation obj was already in the list but I must
                //controll if the favourites value is equal between the 2 objects
                list.set(list.indexOf(finalStorelocation), finalStorelocation);
            }
        }
        else {
            if (!list.contains(newStoreLocation)) {
                list.add(newStoreLocation);
            }
            else {
                //The storelocation obj was already in the list but I must
                //controll if the favourites value is equal between the 2 objects
                list.set(list.indexOf(newStoreLocation), newStoreLocation);
            }
        }

    }

    public void createGeofences () {
        if (!Locationlist.isEmpty()) {
            for (int i = 0; i < Locationlist.size(); i++) {
                if (!Locationlist.get(i).getSeen()) {
                    mGeofenceList.add(new Geofence.Builder()
                            // Set the request ID of the geofence. This is a string to identify this
                            // geofence.
                            .setRequestId(String.valueOf(i))

                            .setCircularRegion(
                                    Locationlist.get(i).getPosition().latitude,
                                    Locationlist.get(i).getPosition().longitude,
                                    GEOFENCERADIUS
                            )
                            .setExpirationDuration(5000)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                            .build());
                }

            }
        }
    }

    public String getMyLocationAddress(int i) {
    //int i =0 is for the country
    //int i=1 is for the city
        Geocoder geocoder= new Geocoder(this, LANGUAGE);


            //Place your latitude and longitude
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude(),1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addresses != null) {

                Address fetchedAddress = addresses.get(0);
                String[] strAddress = new String[2];


                strAddress[0]=fetchedAddress.getCountryName();
                strAddress[1]=fetchedAddress.getLocality();

                return strAddress[i];

            }

            else{
                //TODO find a solution when we can't find an address
                PrintToast("Could not get address!");

                getMyLocationAddress(i);
                //we'll give London and UK as a default value or ask the user for input


            }

        return null;
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.i("Connected", "Location services connected.");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //TODO check Network state, prompt the user to turn on data connection,
        if (mLastLocation!=null&&isNetworkEnabled) {
            //AddressResultReceiver mResultReceiver = null;
            //Intent intent = new Intent(this, FetchAddressIntentService.class);
            //intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
            //intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mLastLocation);
            //startService(intent);

            if (COUNTRY.equals("")){

                COUNTRY = getMyLocationAddress(0);
                CITY = getMyLocationAddress(1);
            }


            mMapsApi_connected=true;
            startLocationUpdates();
        }
        else{
            PrintToast("Small issues with the gps or network, please wait.");
            //TODO check isnetworkenabled and isgpsenabled before calling methods
            GPSEnabled();
        }

    }
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.

            Log.i("DATA:", resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY));

            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
                Log.i("SUcces","found address");
            }

        }
    }
    @Override
    public void onConnectionSuspended(int i) {
        mMapsApi_connected=false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mMapsApi_connected=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onStart(){
        registerGPSreceiver();
        registerNetworkBroadcastReceiver();
        NetworkEnabled();
        if (isNetworkEnabled)
            GPSEnabled();

        super.onStart();
    }
    @Override
    protected void onStop() {
        unregisterGPSreceiver();
        unregisterNetworkBroadcastReceiver();
        if (mMapsApi_connected) {
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
                mGoogleApiClient.disconnect();
            }
            mMapsApi_connected = false;
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void registerNetworkBroadcastReceiver() {
        PackageManager pm = getPackageManager();
        ComponentName compName =
                new ComponentName(getApplicationContext(),
                        NetworkBroadcastReceiver.class);
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void unregisterNetworkBroadcastReceiver() {
        PackageManager pm = getPackageManager();
        ComponentName compName =
                new ComponentName(getApplicationContext(),
                        NetworkBroadcastReceiver.class);
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

        //These methods register and unregister the GPS Broadcast receiver, that knows when the gps is on or off
    private void unregisterGPSreceiver(){
        PackageManager pm = getPackageManager();
        ComponentName compName =
                new ComponentName(getApplicationContext(),
                        GPSreceiver.class);
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
    private void registerGPSreceiver(){
        PackageManager pm = getPackageManager();
        ComponentName compName =
                new ComponentName(getApplicationContext(),
                        GPSreceiver.class);
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, createLocationRequest(), this);
    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (mMapsApi_connected) {
            if (mCurrentLocation==null){
                mCurrentLocation=location;
            }
            LatLng old = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
            LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
            double distance =DistanceTo(old,current)*1000;
            Log.i("DISTANCE", "" + distance);
            Log.i("City", CITY+" "+ COUNTRY);
            if (distance<30){
                mCurrentLocation = location;
            }
            updateUserPosition();// it updates the user's circle inside the map

        }//check if there are problem with the gps

        else {
            if (isNetworkEnabled) {
                if (!isGpsEnabled)
                    GPSEnabled();
            }
            else
                NetworkEnabled();
        }

    }
    /*
    This method will update the position of the user on the map, the value 'current' rapresents the
    current position of the user, the ruturn
     */
    private void updateUserPosition()
    {
        LatLng current = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        double distance = DistanceTo(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()), current) * 1000;
        Log.i("DISTANCE", "Distance in m: " + distance);
        Log.i("ZOOM", " " + mMap.getCameraPosition().zoom);

        //TODO check network
        if (isNetworkEnabled) {
            final Firebase master = GetPathLocations("Places");
            if (FLAG_FIRST_CYCLE)//since it's the first cycle, fetch all the data near the user;
            {
                mDataLoaded = false;
                mMap.moveCamera(init_zoom);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(current));
                mDataLoaded = false;
                GeoFire geoFire = new GeoFire(master);
                GetDataByLocation(geoFire, new LatLng(current.latitude, current.longitude), SEARCH_RADIUS);
            } else if (distance > 150) {
                //if the user moved more than a houndred meters, get new data from the database and change mLastLocation's value with the current's value
                isGeoFenceEnabled = true;
                mLastLocation.setLatitude(current.latitude);
                mLastLocation.setLongitude(current.longitude);
                mDataLoaded = false;
                GeoFire geoFire = new GeoFire(master);
                GetDataByLocation(geoFire, new LatLng(current.latitude, current.longitude), 1);
            }
        }
        else
            NetworkEnabled();
    }

    private GeofencingRequest getGeofencingRequest () {

            GeofencingRequest.Builder builder = new GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(mGeofenceList);
            return builder.build();
    }

    private PendingIntent getGeofencePendingIntent () {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(this, GeofenceReceiver.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().

        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    private void GetDataByLocation(final GeoFire geoFire,LatLng latLng,double radius){
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            ArrayList<CacheLocations> CacheLocations = new ArrayList<CacheLocations>();

            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                //Local
                CacheLocations = GeofencesCacheList(key, location, CacheLocations);


            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));

            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
                addAttributes(CacheLocations);
            }

            @Override
            public void onGeoQueryError(FirebaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });

    }
    private void PrintToast(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
    //Will give back the path of the city element: Descriptions,Places,Tags and version
    private Firebase GetPathLocations(String string){
        final String ROOT="https://scorching-inferno-6511.firebaseio.com/Detour";
        Firebase path=new Firebase(ROOT);
        path.child("Country").child(COUNTRY).child("City").child(CITY).child(string).keepSynced(true);
        return path.child("Country").child(COUNTRY).child("City").child(CITY).child(string);
    }
    private Firebase GetGroupPath(String string){
        final String ROOT="https://scorching-inferno-6511.firebaseio.com/Detour/User Groups";
        Firebase path=new Firebase(ROOT);
        return path.child(string);
    }

    //THIS  METHODS: DistanceTo and degreesToRadians are to calculate the distance between 2 points
    private double DistanceTo(LatLng old,LatLng recent){
        int radius = 6371; // Earth's radius in kilometers
        double latDelta = degreesToRadians(old.latitude - recent.latitude);
        double lonDelta = degreesToRadians(old.longitude- recent.longitude);

        double a = (Math.sin(latDelta / 2) * Math.sin(latDelta / 2)) +
                (Math.cos(degreesToRadians(old.latitude)) * Math.cos(degreesToRadians(recent.latitude)) *
                        Math.sin(lonDelta / 2) * Math.sin(lonDelta / 2));

        double c = (double) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));

        return radius * c; //Distance in km
    }

    private double degreesToRadians(double degrees){

        return (degrees * Math.PI / 180);
    }

    //controlls if the value objects were already in the list, if it's in the list then don't modify the list
    private ArrayList GeofencesCacheList(String key,GeoLocation location,ArrayList list){
        list.trimToSize();
        if (list.contains(new CacheLocations(key,location))){
            return list;
        }
        else
        //no need for an else statment
        list.add(new CacheLocations(key,location));

        return list;
    }

    private void addAttributes(final ArrayList list){

        GetPathLocations("").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //creating the object StoreLocation and store them in the ArrayList,
                String description;

                ArrayList<String> Tags = new ArrayList<String>();

                Iterator<CacheLocations> attribute = list.iterator();
                while (attribute.hasNext()) {

                    final CacheLocations locations = attribute.next();
                    description = (String) snapshot.child("Descriptions").child(locations.getKey()).getValue();


                    long fav = (long) snapshot.child("Favourites").child(locations.getKey()).child("count").getValue();


                    for (DataSnapshot dataSnapshot : snapshot.child("TagsByPlace").child(locations.getKey()).getChildren()) {
                        if (dataSnapshot.getKey() != null)
                            Tags.add((String) dataSnapshot.getKey());
                        else
                            Tags.add("");
                    }
                    //snapshot.child("TagsByPlace").child(locations.getKey()).getChildren();


                    System.out.println(description);
                    System.out.println(Tags.get(0));
                    Iterator<String> TagBuilder = Tags.iterator();
                    String finalTag = "";

                    while (TagBuilder.hasNext()) {
                        if ((Tags.size() > 1)) {
                            finalTag += "#" + TagBuilder.next() ;
                            TagBuilder.remove();
                        } else {
                            finalTag += "#" + TagBuilder.next() + ".";
                        }
                    }
                    //float rate=total/count;
                    final StoreLocation newStoreLocation = new StoreLocation(new LatLng(locations.getLocation().latitude, locations.getLocation().longitude), locations.getKey(), description, finalTag, fav, false, false);
                    //it controll's if there have been changes in the storeLocation object(i.e: the Favourite number, seen, HasFavourite)
                    //
                    //
                    ListaddLocation(newStoreLocation, Locationlist);
                    Tags.clear();
                    //updates the DetailActivity Fav value

                }

                if (isGeoFenceEnabled || FLAG_FIRST_CYCLE) {
                    createGeofences();
                    mGeofencePendingIntent = getGeofencePendingIntent();
                    if (!mGeofenceList.isEmpty()) {
                        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(), mGeofencePendingIntent).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                Log.i("APPPP", "" + status.getStatusMessage());

                            }
                        });

                    }
                    FLAG_FIRST_CYCLE = false;
                    isGeoFenceEnabled = false;
                }


                updateMap(false,null);
                mDataLoaded=true;
        }

        @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }//END addATTRIBUTES METHOD


    private StoreLocation checkLocalDb(StoreLocation newStorelocation){
        StoreLocation niceStoreLocation = null;
        DbAdapter dbHelper = new DbAdapter(this,CITY);
        dbHelper.open();
        SQLiteCursor cursor = dbHelper.fetchContactsByFilter(newStorelocation.getTitle());// query by name

        if (!cursor.moveToFirst()){
            //CREATE the Record
            dbHelper.createContact( newStorelocation.getTitle(),BoolToInt(newStorelocation.getSeen()), BoolToInt(newStorelocation.getHasFavourite()));
        }
        else {
            //overwrites the object fields in the local database
            //create storeLocation object
            //and adds it to the local storeLocation ArrayList
            cursor.moveToFirst();
            dbHelper.updateContact(newStorelocation.getTitle(), cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_SEEN)), cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_HASFAVOURITE)));
            niceStoreLocation=new StoreLocation(
                    new LatLng(newStorelocation.getPosition().latitude,newStorelocation.getPosition().longitude),
                    newStorelocation.getTitle(),
                    newStorelocation.getDescription(),
                    newStorelocation.getTags(),
                    newStorelocation.getFavourites(),
                    IntToBool(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_SEEN))),
                    IntToBool(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_HASFAVOURITE)))
            );
            cursor.close();
        }

        cursor.close();
        dbHelper.close();
        return niceStoreLocation;
    }

    private boolean IntToBool(int i){
        if (i==0)
            return false;
        else
            return true;
    }

    private int BoolToInt(boolean b){
        if (b)
            return 1;
        else
            return 0;
    }

    //simple object
    private class CacheLocations{
        private String key;
        private GeoLocation location;

        public CacheLocations(String key,GeoLocation location){
            this.key=key;
            this.location=location;
        }

        public String getKey(){
            return key;
        }

        public GeoLocation getLocation(){
            return location;
        }

        @Override
        public boolean equals(Object object) {

            if (object != null && object instanceof CacheLocations) {
                CacheLocations o = (CacheLocations) object;
                if (key == null) {
                    return (o.key==null);
                }
                else {
                    return key.equals(o.key);
                }

            }

            return false;
        }
    }
}

