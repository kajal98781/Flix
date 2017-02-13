package com.kmdev.flix.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brsoftech.core_utils.base.BaseAppCompatActivity;
import com.brsoftech.core_utils.utils.ItemClickSupport;
import com.google.gson.Gson;
import com.kmdev.flix.R;
import com.kmdev.flix.RestClient.ApiHitListener;
import com.kmdev.flix.RestClient.ApiIds;
import com.kmdev.flix.RestClient.ApiUrls;
import com.kmdev.flix.RestClient.ConnectionDetector;
import com.kmdev.flix.RestClient.RestClient;
import com.kmdev.flix.models.ResponseMovieDetails;
import com.kmdev.flix.models.ResponsePeople;
import com.kmdev.flix.models.ResponsePeopleDetails;
import com.kmdev.flix.models.ResponsePersonMovie;
import com.kmdev.flix.ui.adapters.PersonMovieCreditAdapter;
import com.kmdev.flix.ui.fragments.ItemListFragment;
import com.kmdev.flix.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class PeopleDetailsActivity extends BaseAppCompatActivity implements View.OnClickListener, ApiHitListener {
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    private TextView mTvTitleToolbar, mTvBiography, mTvLoadingKnown, mTvTitle, mTvPopularity;
    private ResponsePeopleDetails mResponsePeopleDetails;
    private ResponsePeople mResponsePeople;
    private ProgressBar mProgressBarKnown;
    private RecyclerView mRecyclerKnownFor;
    // private PeopleAdapter mPeopleAdapter;
    private ImageView mImageBackPic;
    private String mImageUrl, mTitle;
    private RestClient mRestClient;
    private List<ResponsePersonMovie.CastBean> mCastBeanList;
    private PersonMovieCreditAdapter mPersonMovieCreditAdapter;

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
        mProgressBarKnown = (ProgressBar) findViewById(R.id.progress_bar_known);
        mTvLoadingKnown = (TextView) findViewById(R.id.tv_loading_known);
        mImageBackPic.setOnClickListener(this);

    }

    private void init() {
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //hide  title from toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mRestClient = new RestClient(this);
        mCastBeanList = new ArrayList<>();
        //get parcelable data
        String peopleDetails = getIntent().getStringExtra(Constants.TYPE_PEOPLE_DETAILS);
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
            if (ConnectionDetector.isNetworkAvailable(getApplicationContext())) {
                mProgressBarKnown.setVisibility(View.VISIBLE);
                mTvLoadingKnown.setVisibility(View.VISIBLE);
                mRestClient.callback(this)
                        .getPeopleMovieCredits(mResponsePeopleDetails.getId());

            } else {

            }
        }
        mPersonMovieCreditAdapter = new PersonMovieCreditAdapter(mCastBeanList);
        mRecyclerKnownFor.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, true));
        mRecyclerKnownFor.setAdapter(mPersonMovieCreditAdapter);
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
                        callMovieDetails(position);

                    }
                });
    }

    private void callMovieDetails(int position) {
        if (ConnectionDetector.isNetworkAvailable(getApplicationContext())) {
            displayLoadingDialog(true);
            mRestClient.callback(this).getMovieDetails(String.valueOf(mCastBeanList.get(position).getId()));
        } else {
            displayShortToast(R.string.internet_connection);
        }

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

    @Override
    public void onSuccessResponse(int apiId, Object response) {
        dismissLoadingDialog();
        mProgressBarKnown.setVisibility(View.GONE);
        mTvLoadingKnown.setVisibility(View.GONE);
        if (apiId == ApiIds.ID_PEOPLE_MOVIE_CREDITS) {
            ResponsePersonMovie responsePersonMovie = (ResponsePersonMovie) response;
            mRecyclerKnownFor.setVisibility(View.VISIBLE);
            if (responsePersonMovie != null) {
                List<ResponsePersonMovie.CastBean> castBeanList = responsePersonMovie.getCast();
                mCastBeanList.clear();
                mCastBeanList.addAll(castBeanList);
                if (mPersonMovieCreditAdapter != null) {
                    mPersonMovieCreditAdapter.notifyDataSetChanged();
                }
             /*   mPersonMovieCreditAdapter = new PersonMovieCreditAdapter(mCastBeanList);
                mRecyclerKnownFor.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, true));
                mRecyclerKnownFor.setAdapter(mPersonMovieCreditAdapter);
*/
            }
        } else if (apiId == ApiIds.ID_MOVIE_DETAILS) {
            ResponseMovieDetails responseMovieDetails = (ResponseMovieDetails) response;
            String res = new Gson().toJson(responseMovieDetails);
            if (responseMovieDetails != null) {

                Intent movieDetailIntent = new Intent(getApplicationContext(), MovieDetailsActivity.class);
                movieDetailIntent.putExtra(ItemListFragment.ARG_TYPE, ItemListFragment.ARG_MOVIES);
                movieDetailIntent.putExtra(Constants.TYPE_MOVIE_DETAILS, res);
                startActivity(movieDetailIntent);
            }
        }
    }

    @Override
    public void onFailResponse(int apiId, String error) {
        mProgressBarKnown.setVisibility(View.GONE);
        mTvLoadingKnown.setVisibility(View.GONE);

    }

    @Override
    public void networkNotAvailable() {
        mTvLoadingKnown.setText(R.string.internet_connection);

    }
}
