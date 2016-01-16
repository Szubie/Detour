package com.example.oluwole.historyapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by Oluwole on 22/11/2015.
 */
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjy on 23/11/2015.
 */
public class GeofenceReceiver extends IntentService {
    private OnSeenListener mOnSeen;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param GeofenceReceiver Used to name the worker thread, important only for debugging.
     */

    String mSingleGeofenceString;
    public GeofenceReceiver(String GeofenceReceiver) {
        super("GeofenceReceiver");
    }
    public GeofenceReceiver(){
        super("");
    }

    public interface OnSeenListener{
        public void onSeen(boolean s);
    }



    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
/*        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceUtils.GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;     */
        //mlistlocation=intent.getParcelableArrayListExtra(MapsActivity.PASSING);

        //mlistlocation.addAll((ArrayList) intent.getParcelableArrayListExtra(MapsActivity.PASSING));

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails,triggeringGeofences);
            Log.i("works", geofenceTransitionDetails);
        }

    }

    private void sendNotification(String notificationDetails,List triggeredList) {
        // Create an explicit content Intent that starts the ExploreActivity if there are many geofences active
        // else it opens the detail activity if there's one geofence.
        Intent notificationIntent;
        if (triggeredList.size()>1) {
            notificationIntent = new Intent(getApplicationContext(), ExploreLocations.class);
            notificationIntent.putExtra(MapsActivity.PASSING,MapsActivity.Locationlist);
        }
        else {
            notificationIntent = new Intent(getApplicationContext(), DetailsActivity.class);
            StoreLocation s=new StoreLocation(null,mSingleGeofenceString,null,null,0,false,false);
            notificationIntent.putExtra("DetailsActivity", MapsActivity.Locationlist.get(MapsActivity.Locationlist.indexOf(s)));
        }
        //notificationIntent = new Intent(getApplicationContext(), MapsActivity.class);
        long vibrate[]= {1000,3000};
        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        if (triggeredList.size()>1)
            // Add the ExploreActivity Activity to the task stack as the parent.
            stackBuilder.addParentStack(ExploreLocations.class);
        else
            stackBuilder.addParentStack(DetailsActivity.class);
        //stackBuilder.addParentStack(MapsActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.

                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText("An interesting location is near you!")
                .setVibrate(vibrate)
                .setPriority(2)
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        final ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(MapsActivity.Locationlist.get(Integer.parseInt(geofence.getRequestId())).getTitle());
        }


        DbAdapter dbHelper = new DbAdapter(getApplicationContext(), MapsActivity.CITY);
        dbHelper.open();

        for (int geoIndex=0;geoIndex<triggeringGeofencesIdsList.size();geoIndex++) {
            SQLiteCursor cursor = dbHelper.fetchContactsByFilter((String)triggeringGeofencesIdsList.get(geoIndex));
            cursor.moveToFirst();
            //geoIndex=cursor.getPosition();
            dbHelper.updateContact(cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_TITLE)), 1, cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_HASFAVOURITE)));
            cursor.close();
        }

        dbHelper.close();

        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);
        //all this section should be in a new thread;
        mSingleGeofenceString=(String)triggeringGeofencesIdsList.get(0);
        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);

        }
        return null;  //DANGER
    }

}

