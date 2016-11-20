package com.kmdev.flix.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Kajal on 10/2/2016.
 */

public class HomeAdapter extends FragmentStatePagerAdapter {
    ArrayList<Fragment> fragments = new ArrayList<>();

    public HomeAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;


    }

    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);

    }


    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment f) {
        fragments.add(f);

    }
}