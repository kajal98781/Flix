package com.kmdev.flix.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brsoftech.core_utils.utils.ItemClickSupport;
import com.google.gson.Gson;
import com.kmdev.flix.R;
import com.kmdev.flix.RestClient.ApiUrls;
import com.kmdev.flix.models.ResponsePeople;
import com.kmdev.flix.models.ResponsePeopleDetails;
import com.kmdev.flix.ui.adapters.PeopleAdapter;
import com.kmdev.flix.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class PeopleDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    private TextView mTvTitleToolbar, mTvBiography, mTvLoadingKnown, mTvTitle, mTvPopularity;
    private ResponsePeopleDetails mResponsePeopleDetails;
    private ResponsePeople mResponsePeople;
    private ProgressBar mProgressBarKnown;
    private RecyclerView mRecyclerKnownFor;
    private PeopleAdapter mPeopleAdapter;
    private ImageView mImageBackPic;
    private String mImageUrl, mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_details);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        bindViewsByID();
        init();
    }

    private void bindViewsByID() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mTvTitleToolbar = (TextView) findViewById(R.id.tv_title_toolbar);
        mTvBiography = (TextView) findViewById(R.id.tv_biography);
        mTvLoadingKnown = (TextView) findViewById(R.id.tv_loading_known);
        mRecyclerKnownFor = (RecyclerView) findViewById(R.id.recycler_known);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvPopularity = (TextView) findViewById(R.id.tv_popularity);
        mImageBackPic = (ImageView) findViewById(R.id.imageMovieBack);
        mImageBackPic.setOnClickListener(this);

    }

    private void init() {
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //hide  title from toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //get parcelable data
        String peopleDetails = getIntent().getStringExtra(Constants.TYPE_PEOPLE_DETAILS);
        String knownFor = getIntent().getStringExtra(Constants.TYPE_KNOWN_FOR);
        if (!TextUtils.isEmpty(peopleDetails)) {
            mResponsePeopleDetails = new Gson().fromJson(peopleDetails, ResponsePeopleDetails.class);
            mTvBiography.setText(mResponsePeopleDetails.getBiography());
            mTvTitle.setText(mResponsePeopleDetails.getName());
            mTvPopularity.setText(mResponsePeopleDetails.getPopularityInt() + "%");
            Picasso.with(getApplicationContext())
                    .load(ApiUrls.IMAGE_PATH_VERY_HIGH + mResponsePeopleDetails.getProfile_path())
                    .into(mImageBackPic);
            mImageUrl = mResponsePeopleDetails.getProfile_path();
            mTitle = mResponsePeopleDetails.getName();
        }
        if (!TextUtils.isEmpty(knownFor)) {
            mResponsePeople = new Gson().fromJson(knownFor, ResponsePeople.class);
            List<ResponsePeople.ResultsBean> mResponsePeopleResults = mResponsePeople.getResults();
            mPeopleAdapter = new PeopleAdapter(mResponsePeopleResults, true);
            mRecyclerKnownFor.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, true));
            mRecyclerKnownFor.setAdapter(mPeopleAdapter);

        }
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    showToolbarContents();
                    isShow = true;
                } else if (isShow) {
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    isShow = false;
                    hideToolbarContents();
                }
            }
        });
        ItemClickSupport.addTo(mRecyclerKnownFor)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                    }
                });
    }

    private void showToolbarContents() {
        mTvTitleToolbar.setVisibility(View.VISIBLE);
        mTvTitleToolbar.setText(mResponsePeopleDetails.getName());


    }

    private void hideToolbarContents() {
        mTvTitleToolbar.setText(mResponsePeopleDetails.getName());
        mTvTitleToolbar.setVisibility(View.GONE);


    }

    private void callImageFullScreen() {
        Intent intent = new Intent(PeopleDetailsActivity.this, MovieImageFullScreenActivity.class);
        intent.putExtra(Constants.FULL_IMAGE_URL, mImageUrl);
        intent.putExtra(Constants.MOVIE_TITLE, mTitle);

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageMovieBack:
                callImageFullScreen();
                break;
        }
    }
}
