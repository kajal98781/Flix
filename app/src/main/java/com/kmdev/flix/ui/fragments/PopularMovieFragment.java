package com.kmdev.flix.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brsoftech.core_utils.base.BaseSupportFragment;
import com.brsoftech.core_utils.utils.ItemClickSupport;
import com.google.gson.Gson;
import com.kmdev.flix.R;
import com.kmdev.flix.models.ResponseMovieDetails;
import com.kmdev.flix.models.ResponsePopularMovie;
import com.kmdev.flix.ui.RestClient.ApiHitListener;
import com.kmdev.flix.ui.RestClient.ApiIds;
import com.kmdev.flix.ui.RestClient.RestClient;
import com.kmdev.flix.ui.activities.MovieDetailsActivity;
import com.kmdev.flix.ui.adapters.PopularMovieAdapter;
import com.kmdev.flix.ui.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kajal on 10/2/2016.
 */
public class PopularMovieFragment extends BaseSupportFragment implements ApiHitListener, SwipeRefreshLayout.OnRefreshListener {
    private RestClient mRestClient;
    private RecyclerView mRecyclerPopularMovie;
    private PopularMovieAdapter mPopularMovieAdapter;
    private List<ResponsePopularMovie.PopularMovie> mPopularMovieList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private TextView mTvLoading, mTvErrorShow;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private int mCurrentPage = 1;
    private int mPastVisibleItems, mVisibleItemCount, mTotalItemCount;
    private boolean mIsLoadingNewItems = false;


    public static PopularMovieFragment newInstance() {
        Bundle args = new Bundle();
        PopularMovieFragment fragment = new PopularMovieFragment();
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
    }

    private void init() {
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerPopularMovie.setLayoutManager(mStaggeredGridLayoutManager);
        mPopularMovieAdapter = new PopularMovieAdapter(mPopularMovieList);
        mRecyclerPopularMovie.setAdapter(mPopularMovieAdapter);
        ItemClickSupport
                .addTo(mRecyclerPopularMovie)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        callMovieDetails(position);

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
                    callPopularMovie();
                    Log.d("tag", "LOAD NEXT ITEM");

                }


            }

        });


    }

    private void callPopularMovie() {
        if (mCurrentPage == 1) {
            mIsLoadingNewItems = false;
        } else {

            if (!mIsLoadingNewItems) {
                mIsLoadingNewItems = true;
                mRestClient.callback(this).getPopularMovies(mCurrentPage);
            }
        }


     /*   int index = mPopularMovieAdapter.getItemCount() - 1;
        for (int i = index; i < index + 10; i++) {
            mPopularMovieAdapter.add(new Item("Kajal_" + i));
        }*/

    }

    private void callMovieDetails(int position) {
        displayLoadingDialog(true);
        mRestClient.callback(this).getMovieDetails(String.valueOf(mPopularMovieList.get(position).getId()));


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRestClient = new RestClient(getActivity());
        // displayLoadingDialog(true);
        mProgressBar.setVisibility(View.VISIBLE);
        mTvLoading.setVisibility(View.VISIBLE);
        mRestClient.callback(this).getPopularMovies(mCurrentPage);
    }

    @Override
    public void onSuccessResponse(int apiId, Object response) {
        dismissLoadingDialog();
        mProgressBar.setVisibility(View.GONE);
        mTvLoading.setVisibility(View.GONE);
        if (apiId == ApiIds.ID_POPULAR_MOVIES) {
            mSwipeRefreshLayout.setRefreshing(false);
            ResponsePopularMovie responsePopularMovie = (ResponsePopularMovie) response;
            if (responsePopularMovie != null) {
                List<ResponsePopularMovie.PopularMovie> popularMovie = responsePopularMovie.getPopularMovie();
                if (popularMovie != null && popularMovie.size() > 0) {
                    //   mPopularMovieList.clear();
                    mPopularMovieList.addAll(popularMovie);
                    if (mPopularMovieAdapter != null) {
                        mPopularMovieAdapter.notifyDataSetChanged();
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
        mTvLoading.setVisibility(View.GONE);
        if (getActivity() != null) {
            mTvErrorShow.setText(R.string.unable_load_movies);
            mTvErrorShow.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void networkNotAvailable() {
        mProgressBar.setVisibility(View.GONE);
        mTvLoading.setVisibility(View.GONE);
        // displayErrorDialog(R.string.error, R.string.internet_connection);
        mTvErrorShow.setVisibility(View.VISIBLE);

    }

    @Override
    public void onRefresh() {
        callPopularMovie();
    }
}
