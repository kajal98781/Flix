package com.kmdev.flix.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brsoftech.core_utils.base.BaseSupportFragment;
import com.brsoftech.core_utils.utils.ItemClickSupport;
import com.google.gson.Gson;
import com.kmdev.flix.R;
import com.kmdev.flix.RestClient.ApiHitListener;
import com.kmdev.flix.RestClient.ApiIds;
import com.kmdev.flix.RestClient.ConnectionDetector;
import com.kmdev.flix.RestClient.RestClient;
import com.kmdev.flix.models.ResponseMovieDetails;
import com.kmdev.flix.models.ResponsePopularMovie;
import com.kmdev.flix.ui.activities.MovieDetailsActivity;
import com.kmdev.flix.ui.adapters.HomeMoviesAdapter;
import com.kmdev.flix.utils.Constants;
import com.kmdev.flix.utils.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kajal on 10/2/2016.
 */
public class HomeMovieFragment extends BaseSupportFragment implements ApiHitListener, SwipeRefreshLayout.OnRefreshListener, HomeMoviesAdapter.OnRetryListener {
    public static final String ARG_POPULAR = "popular";
    public static final String ARG_TOP_RATED = "top_rated";
    private static final String ARG_TYPE = "type";
    private RestClient mRestClient;
    private RecyclerView mRecyclerPopularMovie;
    private HomeMoviesAdapter mHomeMoviesAdapter;
    private List<ResponsePopularMovie.PopularMovie> mPopularMovieList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private TextView mTvLoading, mTvErrorShow;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private int mCurrentPage = 1;
    private int mPastVisibleItems, mVisibleItemCount, mTotalItemCount;
    private boolean mIsLoadingNewItems = false;
    private FrameLayout mFrameNetworkError;


    public static HomeMovieFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        HomeMovieFragment fragment = new HomeMovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular_movie, container, false);
        bindViewsById(view);
        init();
        return view;
    }

    private void bindViewsById(View view) {
        mRecyclerPopularMovie = (RecyclerView) view.findViewById(R.id.recyclerview_popular);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        //mProgressBar.getIndeterminateDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        mTvLoading = (TextView) view.findViewById(R.id.tv_loading);
        mTvErrorShow = (TextView) view.findViewById(R.id.tv_error_show);
        // mFrameNetworkError= (FrameLayout) view.findViewById(R.id.frame_network_error);
    }

    private void init() {
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerPopularMovie.setLayoutManager(mStaggeredGridLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.spacing);
        mRecyclerPopularMovie.addItemDecoration(itemDecoration);
        mHomeMoviesAdapter = new HomeMoviesAdapter(mPopularMovieList, this);
        mRecyclerPopularMovie.setAdapter(mHomeMoviesAdapter);
        ItemClickSupport
                .addTo(mRecyclerPopularMovie)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        if (position != mHomeMoviesAdapter.getItemCount() - 1) {
                            callMovieDetails(position);
                        }

                    }
                });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.black,
                R.color.colorPrimary,
                R.color.colorAccent);


        mRecyclerPopularMovie.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //  mTvErrorShow.setVisibility(View.GONE);
                onScrollLoadMovies();


            }

        });


    }

    private void onScrollLoadMovies() {
        StaggeredGridLayoutManager linearLayoutManager = (StaggeredGridLayoutManager) mRecyclerPopularMovie.getLayoutManager();
        // check if loading view (last item on our list) is visible
        mVisibleItemCount = linearLayoutManager.getChildCount();
        mTotalItemCount = linearLayoutManager.getItemCount();
        int[] firstVisibleItems = null;
        firstVisibleItems = linearLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
        if (firstVisibleItems != null && firstVisibleItems.length > 0) {
            mPastVisibleItems = firstVisibleItems[0];
        }

        if ((mVisibleItemCount + mPastVisibleItems) >= mTotalItemCount) {
            mCurrentPage++;
            callMovies();

            Log.d("tag", "LOAD NEXT ITEM");

        }
    }

    private void callMovies() {
        if (ConnectionDetector.isNetworkAvailable(getActivity())) {
            if (mCurrentPage == 1) {
                mIsLoadingNewItems = false;
            } else {
                if (!mIsLoadingNewItems) {
                    mIsLoadingNewItems = true;
                    getMovies();
                }
            }
        }


    }


    private void callMovieDetails(int position) {
        if (ConnectionDetector.isNetworkAvailable(getActivity())) {
            displayLoadingDialog(true);
            mRestClient.callback(this).getMovieDetails(String.valueOf(mPopularMovieList.get(position).getId()));
        } else {
            displayShortToast(R.string.internet_connection);
        }

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRestClient = new RestClient(getActivity());
        // displayLoadingDialog(true);
        mProgressBar.setVisibility(View.VISIBLE);
        mTvLoading.setVisibility(View.VISIBLE);
        getMovies();
    }

    private void getMovies() {
        if (TextUtils.equals(getArguments().getString(ARG_TYPE), ARG_POPULAR)) {
            mRestClient.callback(this).getPopularMovies(mCurrentPage);

        } else if (TextUtils.equals(getArguments().getString(ARG_TYPE), ARG_TOP_RATED))

        {
            mRestClient.callback(this).getTopRatedMovies(mCurrentPage);

        }
    }

    @Override
    public void onSuccessResponse(int apiId, Object response) {
        dismissLoadingDialog();
        mProgressBar.setVisibility(View.GONE);
        mTvLoading.setVisibility(View.GONE);
        mTvErrorShow.setVisibility(View.GONE);
        if (apiId == ApiIds.ID_POPULAR_MOVIES || apiId == ApiIds.ID_TOP_RATED_MOVIES) {
            mSwipeRefreshLayout.setRefreshing(false);
            ResponsePopularMovie responsePopularMovie = (ResponsePopularMovie) response;
            if (responsePopularMovie != null) {
                List<ResponsePopularMovie.PopularMovie> popularMovie = responsePopularMovie.getPopularMovie();
                if (popularMovie != null && popularMovie.size() > 0) {
                    //   mPopularMovieList.clear();
                    mPopularMovieList.addAll(popularMovie);
                    if (mHomeMoviesAdapter != null) {
                        mHomeMoviesAdapter.notifyDataSetChanged();
                        mIsLoadingNewItems = false;

                    }
                }
            }

        } else if (apiId == ApiIds.ID_MOVIE_DETAILS) {
            ResponseMovieDetails responseMovieDetails = (ResponseMovieDetails) response;
            String res = new Gson().toJson(responseMovieDetails);
            if (responseMovieDetails != null) {

                Intent movieDetailIntent = new Intent(getActivity(), MovieDetailsActivity.class);
                movieDetailIntent.putExtra(Constants.TYPE_MOVIE_DETAILS, res);
                startActivity(movieDetailIntent);
            }
        }


    }

    @Override
    public void onFailResponse(int apiId, String error) {
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);

        mTvLoading.setVisibility(View.GONE);
        if (mHomeMoviesAdapter != null) {
            if (getActivity() != null && mHomeMoviesAdapter.getItemCount() == 0) {
                mTvErrorShow.setText(R.string.unable_load_movies);
                mTvErrorShow.setVisibility(View.VISIBLE);
            }
        } else {
            displayShortToast(R.string.unable_load_movies);
        }

    }

    @Override
    public void networkNotAvailable() {

        mProgressBar.setVisibility(View.GONE);
        mTvLoading.setVisibility(View.GONE);
        mTvErrorShow.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(false);


    }

    @Override
    public void onRefresh() {
        if (ConnectionDetector.isNetworkAvailable(getActivity())) {
            callMovies();
        } else {
            displayShortToast(R.string.internet_connection);
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onRetry() {
        onScrollLoadMovies();

    }
}
