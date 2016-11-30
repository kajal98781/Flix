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
import com.kmdev.flix.RestClient.ApiHitListener;
import com.kmdev.flix.RestClient.ApiIds;
import com.kmdev.flix.RestClient.RestClient;
import com.kmdev.flix.models.ResponseMovieDetails;
import com.kmdev.flix.models.ResponseTopRated;
import com.kmdev.flix.ui.activities.MovieDetailsActivity;
import com.kmdev.flix.ui.adapters.TopRatedMovieAdapter;
import com.kmdev.flix.utils.Constants;
import com.kmdev.flix.utils.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kajal on 10/2/2016.
 */
public class TopRatedMovieFragment extends BaseSupportFragment implements ApiHitListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mTopRatedRecyclerView;
    private List<ResponseTopRated.TopRatedBean> mTopRatedBeanList = new ArrayList<>();
    private TopRatedMovieAdapter mTopRatedMovieAdapter;
    private RestClient mRestClient;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private TextView mTvLoading, mTvShowError;
    private int mPastVisibleItems, mVisibleItemCount, mTotalItemCount;
    private int mCurrentPage = 1;
    private boolean mIsLoadingNewItems = false;


    public static TopRatedMovieFragment newInstance() {
        Bundle args = new Bundle();
        TopRatedMovieFragment fragment = new TopRatedMovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =
                inflater.inflate(R.layout.fragment_top_rated_movie, container, false);
        bindViewsById(view);
        init();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRestClient = new RestClient(getActivity());
        mProgressBar.setVisibility(View.VISIBLE);
        mTvLoading.setVisibility(View.VISIBLE);
        mRestClient.callback(this).getTopRatedMovies(mCurrentPage);

    }

    private void callTopRatedMovies() {
      /*  mProgressBar.setVisibility(View.VISIBLE);
        mTvLoading.setVisibility(View.VISIBLE);*/
        if (!mIsLoadingNewItems) {
            mIsLoadingNewItems = true;
            mRestClient.callback(this).getTopRatedMovies(mCurrentPage);
        }


    }

    private void bindViewsById(View view) {
        mTopRatedRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_to_rated);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mTvLoading = (TextView) view.findViewById(R.id.tv_loading);
        mTvShowError = (TextView) view.findViewById(R.id.tv_show_error);
    }

    private void init() {
        //set toprated adapter
        mTopRatedRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mTopRatedMovieAdapter = new TopRatedMovieAdapter(mTopRatedBeanList);
        mTopRatedRecyclerView.setAdapter(mTopRatedMovieAdapter);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.spacing);
        mTopRatedRecyclerView.addItemDecoration(itemDecoration);

        //set top rated click  listener
        ItemClickSupport.addTo(mTopRatedRecyclerView)
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
//on scroll load more items in android
        mTopRatedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager linearLayoutManager = (StaggeredGridLayoutManager) mTopRatedRecyclerView.getLayoutManager();
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
                    callTopRatedMovies();
                    Log.d("tag", "LOAD NEXT ITEM");

                }


            }

        });
    }

    @Override
    public void onSuccessResponse(int apiId, Object response) {
        dismissLoadingDialog();
        mProgressBar.setVisibility(View.GONE);
        mTvLoading.setVisibility(View.GONE);
        if (apiId == ApiIds.ID_TOP_RATED_MOVIES) {
            mSwipeRefreshLayout.setRefreshing(false);
            ResponseTopRated responseTopRated = (ResponseTopRated) response;
            if (responseTopRated != null) {
                List<ResponseTopRated.TopRatedBean> topRatedBeen = responseTopRated.getResults();
                if (topRatedBeen != null && topRatedBeen.size() > 0) {
                    mTopRatedBeanList.clear();
                    mTopRatedBeanList.addAll(topRatedBeen);
                    if (mTopRatedBeanList != null) {
                        mTopRatedMovieAdapter.notifyDataSetChanged();
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

    private void callMovieDetails(int position) {
        displayLoadingDialog(true);
        mRestClient.callback(this).getMovieDetails(String.valueOf(mTopRatedBeanList.get(position).getId()));

    }


    @Override
    public void onFailResponse(int apiId, String error) {
        mProgressBar.setVisibility(View.GONE);
        mTvLoading.setVisibility(View.GONE);
        mTvShowError.setVisibility(View.VISIBLE);
        mTvShowError.setText(R.string.unable_load_movies);
    }

    @Override
    public void networkNotAvailable() {
        mProgressBar.setVisibility(View.GONE);
        mTvLoading.setVisibility(View.GONE);
        // displayErrorDialog(R.string.error,R.string.internet_connection);
        mTvShowError.setVisibility(View.VISIBLE);

    }

    @Override
    public void onRefresh() {
        callTopRatedMovies();

    }
}
