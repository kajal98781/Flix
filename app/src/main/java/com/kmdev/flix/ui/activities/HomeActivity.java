package com.kmdev.flix.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brsoftech.core_utils.base.BaseAppCompatActivity;
import com.kmdev.flix.R;
import com.kmdev.flix.RestClient.ApiHitListener;
import com.kmdev.flix.ui.adapters.HomeAdapter;
import com.kmdev.flix.ui.fragments.FavouriteMovieFragment;
import com.kmdev.flix.ui.fragments.HomeMovieFragment;
import com.kmdev.flix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends BaseAppCompatActivity implements TabLayout.OnTabSelectedListener, ApiHitListener {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ArrayList<Fragment> mListFragments;
    private Toolbar mToolBar;
    private List<String> mTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        setTitle();
        tabSelection();

    }

    private void setTitle() {
        Utils.applyFontForToolbarTitle(HomeActivity.this);
        mToolBar.setTitle(R.string.app_name);

    }

    private void init() {
        mTitleList = new ArrayList<>();

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(1);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mListFragments = new ArrayList<Fragment>();
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            //    getSupportActionBar().setIcon(R.mipmap.ic_launcher);

            // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // getSupportActionBar().setTitle(getTitle());
        }


    }

    private void tabSelection() {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.popular));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.top_rated));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.favourite));

        mTitleList.add(getResources().getString(R.string.popular));
        mTitleList.add(getResources().getString(R.string.top_rated));
        mTitleList.add(getResources().getString(R.string.favourite));

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        HomeMovieFragment popularFragment = HomeMovieFragment.newInstance(HomeMovieFragment.ARG_POPULAR);
        HomeMovieFragment trendsFragment = HomeMovieFragment.newInstance(HomeMovieFragment.ARG_TOP_RATED);
        FavouriteMovieFragment favouriteMovieFragment = FavouriteMovieFragment.newInstance();

        mListFragments.add(popularFragment);
        mListFragments.add(trendsFragment);
        mListFragments.add(favouriteMovieFragment);
        final HomeAdapter adapter = new HomeAdapter
                (getSupportFragmentManager(), mListFragments, mTitleList);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Bold.ttf");
        ViewGroup vg = (ViewGroup) mTabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(tf);
                }
            }
        }
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mViewPager.setOffscreenPageLimit(2);

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);//Menu Resource, Menu
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_search) {
            startActivity(new Intent(HomeActivity.this, SearchMovieActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition(), true);

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    public void onSuccessResponse(int apiId, Object response) {

    }

    @Override
    public void onFailResponse(int apiId, String error) {
        displayErrorDialog(getResources().getString(R.string.error), error);


    }

    @Override
    public void networkNotAvailable() {

    }
}
