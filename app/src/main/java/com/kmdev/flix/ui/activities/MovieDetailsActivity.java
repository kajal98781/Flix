package com.kmdev.flix.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
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
import com.kmdev.flix.RestClient.RestClient;
import com.kmdev.flix.models.DataBaseMovieDetails;
import com.kmdev.flix.models.ResponseMovieDetails;
import com.kmdev.flix.models.ResponseMovieReview;
import com.kmdev.flix.models.ResponseMovieVideo;
import com.kmdev.flix.ui.adapters.ReviewMovieAdapter;
import com.kmdev.flix.ui.adapters.VideoMovieAdapter;
import com.kmdev.flix.utils.Constants;
import com.kmdev.flix.utils.DataBaseHelper;
import com.kmdev.flix.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MovieDetailsActivity extends BaseAppCompatActivity implements ApiHitListener, View.OnClickListener {
    ResponseMovieDetails mResponseMovieDetails;
    private RestClient mRestClient;
    private Toolbar mToolbar;
    private ImageView mImageMovieBack;
    private TextView mTvDescription, mTvMovieTitle, mTvReleaseDate, mTvReviews, mTvVideos;
    private RecyclerView mRecyclerViewReview, mRecyclerViewVideos, getmRecyclerViewRecommends;
    private ReviewMovieAdapter mReviewAdapter;
    private VideoMovieAdapter mVideoMovieAdapter;
    private List<ResponseMovieReview.ReviewBean> mReviewBeanList;
    private List<ResponseMovieVideo.VideoBean> mVideoBeanList;
    private String mImageUrl;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private ProgressBar mProgressBar, mProgressBarVideo, mProgressBarReview, mProgressBarDetail;
    private TextView mTvLoadingVideo, mTvLoadReview, mTvLoadDetails;
    private FloatingActionButton mFabFavourite;
    private boolean mIsFavorite;
    private DataBaseHelper mDatabase;
    private String mMovieId;
    private String mTitle;
    private ResponseMovieReview mResponseMovieReview;
    private ResponseMovieVideo mResponseMovieVideo;
    private boolean mIsLoadingReview = false;
    private boolean mIsLoadingTrailers = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        bindViewsById();
        init(savedInstanceState);


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void bindViewsById() {
        mRestClient = new RestClient(MovieDetailsActivity.this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mImageMovieBack = (ImageView) findViewById(R.id.imageMovieBack);
        mTvDescription = (TextView) findViewById(R.id.tv_description);
        mRecyclerViewReview = (RecyclerView) findViewById(R.id.recycler_review);
        mRecyclerViewVideos = (RecyclerView) findViewById(R.id.recycler_videos);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mTvMovieTitle = (TextView) findViewById(R.id.tv_title);
        mTvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mTvReviews = (TextView) findViewById(R.id.tv_reviews);
        mTvVideos = (TextView) findViewById(R.id.tv_videos);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBarVideo = (ProgressBar) findViewById(R.id.progress_bar_video);
        mProgressBarReview = (ProgressBar) findViewById(R.id.progress_bar_review);
        mTvLoadReview = (TextView) findViewById(R.id.tv_loading_review);
        mTvLoadingVideo = (TextView) findViewById(R.id.tv_loading_video);
        mFabFavourite = (FloatingActionButton) findViewById(R.id.fab_favorite);
    }

    private void init(Bundle savedInstanceState) {
        mReviewBeanList = new ArrayList<>();
        mDatabase = new DataBaseHelper(this);
        mVideoBeanList = new ArrayList<>();
        mImageMovieBack.setOnClickListener(this);
        mFabFavourite.setOnClickListener(this);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //hide  title from toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //get parcelable data
        String movieDetails = getIntent().getStringExtra(Constants.TYPE_MOVIE_DETAILS);
        boolean isComeFromFavourites = getIntent().getBooleanExtra(Constants.TYPE_IS_FAVOURITE, false);

        //set review adapter
        mRecyclerViewReview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mReviewAdapter = new ReviewMovieAdapter(mReviewBeanList);
        mRecyclerViewReview.setAdapter(mReviewAdapter);
        //set video adapter
        mRecyclerViewVideos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mVideoMovieAdapter = new VideoMovieAdapter(mVideoBeanList);
        mRecyclerViewVideos.setAdapter(mVideoMovieAdapter);

        if (movieDetails != null && savedInstanceState == null) {
            mResponseMovieDetails = new Gson().fromJson(movieDetails, ResponseMovieDetails.class);
            if (mResponseMovieDetails != null) {
                mMovieId = String.valueOf(mResponseMovieDetails.getId());
                checkMovieExistIntofavourites();
                mTvDescription.setText(mResponseMovieDetails.getOverview());
                mTitle = mResponseMovieDetails.getTitle();
                //   mToolbar.setTitle(mResponseMovieDetails.getTitle());
                mImageUrl = mResponseMovieDetails.getPoster_path();
                Picasso.with(this)
                        .load(ApiUrls.IMAGE_PATH_HIGH + mResponseMovieDetails.getBackdrop_path())
                        .placeholder(R.mipmap.ic_launcher)   // optional
                        .error(R.mipmap.ic_launcher)
                        .into(mImageMovieBack);
                mTvMovieTitle.setText(mResponseMovieDetails.getOriginal_title());
                mTvReleaseDate.setText(mResponseMovieDetails.getRelease_date());

            }

        }
        //review click listener
        ItemClickSupport.addTo(mRecyclerViewReview)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        String mAuthor = mReviewBeanList.get(position).getAuthor();
                        String mContent = mReviewBeanList.get(position).getContent();
                        Utils.displayReviewDetails(MovieDetailsActivity.this, mAuthor, mContent);
                    }
                });
        //video click listener
        ItemClickSupport.addTo(mRecyclerViewVideos)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        String urlVideo = ApiUrls.VIDEO_PATH_YOUTUBE + mVideoBeanList.get(position).getKey();
                        Intent ytplay = new Intent(Intent.ACTION_VIEW, Uri.parse(urlVideo));
                        startActivity(ytplay);

                    }
                });


        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    Utils.applyFontForToolbarTitle(MovieDetailsActivity.this);

                    getSupportActionBar().setTitle(mTitle);
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    isShow = false;
                }
            }
        });
        //get reviews from database
        if (isComeFromFavourites) {
            DataBaseMovieDetails dataBaseMovieDetails = mDatabase.getMovieDetails(Integer.parseInt(mMovieId));
            ResponseMovieReview responseMovieReview = dataBaseMovieDetails.getResponseMovieReview();
            if (responseMovieReview != null) {
                List<ResponseMovieReview.ReviewBean> reviewBeanList = responseMovieReview.getReviewBean();
                if (reviewBeanList.size() > 0) {
                    mReviewBeanList.clear();
                    mReviewBeanList.addAll(reviewBeanList);
                    if (mReviewAdapter != null) {
                        mReviewAdapter.notifyDataSetChanged();
                    }
                } else {
                    mTvReviews.setVisibility(View.VISIBLE);
                    mRecyclerViewReview.setVisibility(View.GONE);
                }
            }

            //get videos from database
            ResponseMovieVideo responseMovieVideo = dataBaseMovieDetails.getResponseMovieVideo();
            if (responseMovieVideo != null) {
                List<ResponseMovieVideo.VideoBean> videoBeanList = responseMovieVideo.getVideoBean();
                if (videoBeanList.size() > 0) {
                    mVideoBeanList.clear();
                    mVideoBeanList.addAll(videoBeanList);
                    if (mVideoMovieAdapter != null) {
                        mVideoMovieAdapter.notifyDataSetChanged();
                    }
                } else {
                    mTvVideos.setVisibility(View.VISIBLE);
                    mRecyclerViewVideos.setVisibility(View.GONE);
                }
            }
        } else {
            if (!TextUtils.isEmpty(mMovieId)) {
                mIsLoadingReview = true;
                mIsLoadingTrailers = true;
                mProgressBarReview.setVisibility(View.VISIBLE);
                mTvLoadReview.setVisibility(View.VISIBLE);
                mRestClient.callback(this).getReviews(mMovieId);
                mProgressBarVideo.setVisibility(View.VISIBLE);
                mTvLoadingVideo.setVisibility(View.VISIBLE);
                mRestClient.callback(this).getVideos(mMovieId);
            }
        }


    }


    private void checkMovieExistIntofavourites() {
        boolean checkFav = mDatabase.checkISDataExist(mMovieId);
        if (checkFav) {
            mFabFavourite.setImageResource(R.drawable.ic_favorite_black_24dp);
            mIsFavorite = false;
        } else {
            mFabFavourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            mIsFavorite = true;
        }

    }

    @Override
    public void onSuccessResponse(int apiId, Object response) {
        if (apiId == ApiIds.ID_MOVIE_REVIEW) {
            mIsLoadingReview = false;
            mProgressBarReview.setVisibility(View.GONE);
            mTvLoadReview.setVisibility(View.GONE);
            mResponseMovieReview = (ResponseMovieReview) response;
            if (mResponseMovieReview != null) {
                List<ResponseMovieReview.ReviewBean> reviewBeen = mResponseMovieReview.getReviewBean();
                if (reviewBeen.size() > 0) {
                    mReviewBeanList.clear();
                    mReviewBeanList.addAll(reviewBeen);
                    if (mReviewAdapter != null) {
                        mReviewAdapter.notifyDataSetChanged();
                    }
                } else {
                    mTvReviews.setVisibility(View.VISIBLE);
                    mRecyclerViewReview.setVisibility(View.GONE);
                }
            }
        } else if (apiId == ApiIds.ID_MOVIE_VIDEO) {
            mIsLoadingTrailers = false;
            mProgressBarVideo.setVisibility(View.GONE);
            mTvLoadingVideo.setVisibility(View.GONE);
            mResponseMovieVideo = (ResponseMovieVideo) response;
            if (mResponseMovieVideo != null) {
                List<ResponseMovieVideo.VideoBean> videoBean = mResponseMovieVideo.getVideoBean();
                if (videoBean.size() > 0) {
                    mVideoBeanList.clear();
                    mVideoBeanList.addAll(videoBean);
                    if (mVideoBeanList != null) {
                        mVideoMovieAdapter.notifyDataSetChanged();
                    }
                } else {
                    mTvVideos.setVisibility(View.VISIBLE);
                    mRecyclerViewVideos.setVisibility(View.GONE);
                }
            }
        }


    }

    @Override
    public void onFailResponse(int apiId, String error) {
        dismissLoadingDialog();
        displayErrorDialog(getResources().getString(R.string.error), error);
    }

    @Override
    public void networkNotAvailable() {
        dismissLoadingDialog();
        displayErrorDialog(R.string.error, R.string.internet_connection);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_favorite:
                if (!mIsLoadingReview && !mIsLoadingTrailers) {
                    if (mIsFavorite) {
                        addToFavourites();
                        mFabFavourite.setImageResource(R.drawable.ic_favorite_black_24dp);
                        mIsFavorite = false;
                        displayShortToast(R.string.add_to_favourites);
                    } else {
                        mFabFavourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        displayShortToast(R.string.remove_to_favourites);
                        removeFromFavourites();
                        mIsFavorite = true;
                    }
                } else {
                    displayShortToast(R.string.wait_data_loaded);
                }
                break;
            case R.id.imageMovieBack:
                callImageFullScreen();
                break;

        }
    }


    private void shareMovieDetails() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<b><center>" + mTitle + "</center></b") + "\n\n" +
                Html.fromHtml("<b><center>" + mTvDescription.getText().toString() + "</center></b"));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_share:

                shareMovieDetails();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.movie_detail_menu, menu);
        return true;
    }

    private void addToFavourites() {

        DataBaseMovieDetails dataBaseMovieDetails = new DataBaseMovieDetails();
        dataBaseMovieDetails.setResponseMovieDetails(mResponseMovieDetails);
        dataBaseMovieDetails.setResponseMovieReview(mResponseMovieReview);
        dataBaseMovieDetails.setResponseMovieVideo(mResponseMovieVideo);
        dataBaseMovieDetails.setMovieId(mResponseMovieDetails.getId());
        mDatabase.addMovies(dataBaseMovieDetails);


        //  Toast.makeText(MovieDetailsActivity.this, "save data", Toast.LENGTH_SHORT).show();

    }

    private void removeFromFavourites() {
        mDatabase.deleteMovie(mMovieId);

    }

    private void callImageFullScreen() {
        Intent intent = new Intent(MovieDetailsActivity.this, MovieImageFullScreenActivity.class);
        intent.putExtra(Constants.FULL_IMAGE_URL, mImageUrl);
/*
        intent.putExtra(Constants.MOVIE_TITLE,mTvMovieTitle.getText().toString() );
        intent.putExtra(Constants.MOVIE_RELEASE_DATE, mTvReleaseDate.getText().toString());
*/
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(HomeActivity.class);
    }
}
