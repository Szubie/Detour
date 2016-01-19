package com.example.oluwole.historyapp;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

/**
 * Created by Oluwole on 27/12/2015.
 */
public class DetailsActivity extends Activity {
    public static StoreLocation LOCATIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        Firebase.setAndroidContext(this);

        Intent intent = getIntent();
        TextView title = (TextView) findViewById(R.id.TitleTextView);
        TextView tags = (TextView) findViewById(R.id.TagTextView);
        TextView description = (TextView) findViewById(R.id.DescriptionTextView);
        final TextView rate = (TextView) findViewById(R.id.RatingTextView);
        ImageView imageView = (ImageView) findViewById(R.id.SeenView);
        final RadioButton favButton = (RadioButton) findViewById(R.id.FavButton);

        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);


        LOCATIONS=(StoreLocation)intent.getParcelableExtra("DetailsActivity");

        title.setText(LOCATIONS.getTitle());
        tags.setText(LOCATIONS.getTags());
        description.setText(LOCATIONS.getDescription());
        rate.setText("" + LOCATIONS.getFavourites());
        final Firebase master = GetPathLocations("Favourites");


        if (LOCATIONS.getSeen()){
            imageView.setImageResource(R.drawable.flag_color);
        }

        if (LOCATIONS.getHasFavourite()){
            favButton.setText("Favourite");
        }


        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favButton.getText().equals("Favourite")){
                    DbAdapter dbHelper = new DbAdapter(getApplicationContext(),MapsActivity.CITY);
                    dbHelper.open();
                    SQLiteCursor cursor = dbHelper.fetchContactsByFilter(LOCATIONS.getTitle());
                    cursor.moveToFirst();
                    dbHelper.updateContact(cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_TITLE)), cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_SEEN)), 1);
                    cursor.close();
                    dbHelper.close();
                    favButton.setText("Favourite");
                    if (MapsActivity.isNetworkEnabled)
                        master.child(LOCATIONS.getTitle()).child("count").setValue(LOCATIONS.getFavourites() + (long) 1);
                    else
                        PrintToast("No data connection found, couldn't complete the action.");
                    rate.setText("" + (LOCATIONS.getFavourites()+1));
                }
                else {
                    DbAdapter dbHelper = new DbAdapter(getApplicationContext(), MapsActivity.CITY);
                    dbHelper.open();
                    SQLiteCursor cursor = dbHelper.fetchContactsByFilter(LOCATIONS.getTitle());
                    cursor.moveToFirst();
                    dbHelper.updateContact(cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_TITLE)), cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_SEEN)), 0);
                    cursor.close();
                    dbHelper.close();
                    favButton.clearFocus();
                    favButton.setText("Not Favourite");
                    if (MapsActivity.isNetworkEnabled)
                        master.child(LOCATIONS.getTitle()).child("count").setValue(LOCATIONS.getFavourites() - (long) 1);
                    else
                        PrintToast("No data connection found, couldn't complete the action.");
                    rate.setText("" + (LOCATIONS.getFavourites()));
                }

            }
        });


    }
    //Will give back the path of the city element: Descriptions,Places,Tags and version
    private Firebase GetPathLocations(String string){
        final String ROOT="https://scorching-inferno-6511.firebaseio.com/Detour";
        Firebase path=new Firebase(ROOT);
        return path.child("Country").child(MapsActivity.COUNTRY).child("City").child(MapsActivity.CITY).child(string);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    private void PrintToast(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
