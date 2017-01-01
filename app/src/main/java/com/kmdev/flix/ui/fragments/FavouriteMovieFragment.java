package com.kmdev.flix.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brsoftech.core_utils.base.BaseSupportFragment;
import com.brsoftech.core_utils.utils.ItemClickSupport;
import com.google.gson.Gson;
import com.kmdev.flix.R;
import com.kmdev.flix.models.DataBaseEventUpdateModel;
import com.kmdev.flix.models.ResponseMovieDetails;
import com.kmdev.flix.ui.activities.MovieDetailsActivity;
import com.kmdev.flix.ui.adapters.FavouriteMovieAdapter;
import com.kmdev.flix.utils.Constants;
import com.kmdev.flix.utils.DataBaseHelper;
import com.kmdev.flix.utils.ItemOffsetDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kajal on 10/23/2016.
 */
public class FavouriteMovieFragment extends BaseSupportFragment implements View.OnClickListener {
    //  private SwipeRefreshLayout mSwipeRefreshLayout;
    private static final String TAG = "FavouriteMovieFragment";
    private RecyclerView mRecyclerViewFav;
    private List<ResponseMovieDetails> mMovieDetailsList;
    private FavouriteMovieAdapter mFavouriteMovieAdapter;
    private TextView mTvNoFavAvail, mTvNoInternet;
    private DataBaseHelper mDataBase;

    public static FavouriteMovieFragment newInstance() {

        Bundle args = new Bundle();

        FavouriteMovieFragment fragment = new FavouriteMovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_movie, container, false);
        bindViewsById(view);
        init();
        return view;
    }


    private void init() {
        mMovieDetailsList = new ArrayList<>();
        mFavouriteMovieAdapter = new FavouriteMovieAdapter(mMovieDetailsList);
        mDataBase = new DataBaseHelper(getActivity());

        //set favourite adapter to recycler
        mRecyclerViewFav.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerViewFav.setAdapter(mFavouriteMovieAdapter);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.spacing);
        mRecyclerViewFav.addItemDecoration(itemDecoration);
        callToGetFavouriteMovies();
        ItemClickSupport.
                addTo(mRecyclerViewFav).
                setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        callMovieDetails(position);
                    }
                });

    }

    public void callToGetFavouriteMovies() {

        //initialize database & get movies
        List<ResponseMovieDetails> responseMovieDetailsList = mDataBase.getAllMovies();
        if (responseMovieDetailsList.size() > 0) {
            mMovieDetailsList.clear();
            mTvNoFavAvail.setVisibility(View.GONE);
            mMovieDetailsList.addAll(responseMovieDetailsList);
            if (mFavouriteMovieAdapter != null) {
                mFavouriteMovieAdapter.notifyDataSetChanged();
            }
        } else {
            mMovieDetailsList.clear();
            if (mFavouriteMovieAdapter != null) {
                mFavouriteMovieAdapter.notifyDataSetChanged();
            }
            mTvNoFavAvail.setVisibility(View.VISIBLE);

        }
    }

    private void callMovieDetails(int position) {
        ResponseMovieDetails responseMovieDetails = mMovieDetailsList.get(position);
        displayLoadingDialog(false);
        String res = new Gson().toJson(responseMovieDetails);
        if (res != null) {
            dismissLoadingDialog();
            Intent movieDetailIntent = new Intent(getActivity(), MovieDetailsActivity.class);
            movieDetailIntent.putExtra(Constants.TYPE_MOVIE_DETAILS, res);
            movieDetailIntent.putExtra(Constants.TYPE_IS_FAVOURITE, true);
            startActivity(movieDetailIntent);
        }
    }


    private void bindViewsById(View view) {
        mRecyclerViewFav = (RecyclerView) view.findViewById(R.id.reccycler_fav);

        mTvNoFavAvail = (TextView) view.findViewById(R.id.tv_no_fav_available);
        mTvNoInternet = (TextView) view.findViewById(R.id.tv_no_internet_available);
    }


    @Override
    public void onClick(View v) {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDatabaseUpdateEvent(DataBaseEventUpdateModel dataBaseEventUpdateModel) {
        callToGetFavouriteMovies();
    }
}
