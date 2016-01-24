package com.example.oluwole.historyapp;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
//import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TableLayout;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


/**
 * Created by Oluwole on 03/12/2015.
 */
public class ExploreLocations extends AppCompatActivity {

    private static String[] location_name;
    private static LatLng[] location_coordinate;
    private static ArrayList<StoreLocation> LOCATIONS=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_list);
        //Intent intent = getIntent();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Quirky Facts");


        //final TabHost tabLayout = (TabHost) findViewById(R.id.tab_layout);

        //tabLayout.addTab(tabLayout.newTabSpec("All Location").setIndicator("Tab1").setContent(new Intent(this,FullLocationList.class)));
        //tabLayout.addTab(tabLayout.newTabSpec("Nearest").setIndicator("Tab2"));
        //tabLayout.addTab(tabLayout.newTabSpec("Popular").setIndicator("Tab3"));


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final CustomPagerAdapter pagerAdapter = new CustomPagerAdapter
                (getSupportFragmentManager(), 1);

        viewPager.setAdapter(pagerAdapter);
        // Give the TabLayout the ViewPager
        //TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        //tabLayout.setupWithViewPager(viewPager);

/*
        //just creating a new instace of the array, based on the number of the locations on the map
        LOCATIONS=intent.getParcelableArrayListExtra(MapsActivity.PASSING);
        location_name=new String[LOCATIONS.size()];

        for (int i=0; i<LOCATIONS.size();i++){
            location_name[i]=LOCATIONS.get(i).getTitle();
        }



        ListView listView = (ListView) findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,location_name);
        listView.setAdapter(adapter);

        final String DETAILPASS="DetailsActivity";
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(getApplicationContext(),DetailsActivity.class);
                intent1.putExtra(DETAILPASS,LOCATIONS.get(position));
                startActivity(intent1);
                finish();
            }
        });*/

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps_activity, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView =(SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println(query);
                searchView.clearFocus();
                //do query on the locationList.

                return false;
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
