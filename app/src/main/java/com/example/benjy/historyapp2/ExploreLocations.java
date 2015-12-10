package com.example.benjy.historyapp2;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Oluwole on 03/12/2015.
 */
public class ExploreLocations extends Activity {
    private static final String PASSING="Loc";
    private static StoreLocation[] LOCATIONS;
    private static String[] location_name;
    private static LatLng[] location_coordinate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_list);
        Intent intent = getIntent();
        //just creating a new instace of the array, based on the number of the locations on the map
        LOCATIONS = new StoreLocation[intent.getIntExtra(MapsActivity.LENGTH,10)];
        location_coordinate=new LatLng[LOCATIONS.length];
        location_name=new String[LOCATIONS.length];

        for (int i=0; i<LOCATIONS.length;i++){
            LOCATIONS[i]=intent.getParcelableExtra(MapsActivity.PASSING+i);
            location_name[i]=LOCATIONS[i].getTitle();
            location_coordinate[i]=LOCATIONS[i].getPosition();
        }



        ListView listView = (ListView) findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,location_name);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

}
