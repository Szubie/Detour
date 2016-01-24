package com.example.oluwole.historyapp;

/**
 * Created by Oluwole on 20/01/2016.
 */
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class CustomPagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    private String tabTitles[] = new String[] { "All Locations", "Tab2", "Tab3" };

    public CustomPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment tab1 = new FullLocationList();
                return tab1;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}