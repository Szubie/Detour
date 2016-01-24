package com.example.oluwole.historyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Oluwole on 20/01/2016.
 */
public class FullLocationList extends Fragment {
    @Nullable
    private static String[] location_name= new String[MapsActivity.Locationlist.size()];
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.complete_location_list, container, false);

        for (int i=0; i<location_name.length;i++){
            location_name[i]=MapsActivity.Locationlist.get(i).getTitle();
        }

        ListView listView = (ListView) rootView.findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,location_name);
        listView.setAdapter(adapter);

        final String DETAILPASS="DetailsActivity";
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(getActivity(),DetailsActivity.class);
                intent1.putExtra(DETAILPASS,MapsActivity.Locationlist.get(position));
                startActivity(intent1);
            }
        });
        
        return rootView;
    }
}
