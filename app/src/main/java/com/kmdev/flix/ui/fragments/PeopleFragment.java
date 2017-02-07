package com.kmdev.flix.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.kmdev.flix.RestClient.ConnectionDetector;
import com.kmdev.flix.RestClient.RestClient;
import com.kmdev.flix.models.ResponsePeople;
import com.kmdev.flix.models.ResponsePeopleDetails;
import com.kmdev.flix.prefrences.AppPrefs;
import com.kmdev.flix.ui.activities.PeopleDetailsActivity;
import com.kmdev.flix.ui.activities.SearchMovieActivity;
import com.kmdev.flix.ui.adapters.PeopleAdapter;
import com.kmdev.flix.utils.Constants;
import com.kmdev.flix.utils.ItemOffsetDecoration;
import com.kmdev.flix.utils.SelectedMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kajal on 1/22/2017.
 */
public class PeopleFragment extends BaseSupportFragment implements ApiHitListener {
    private RestClient mRestClient;
    private RecyclerView mRecyclerViewPeople;
    private PeopleAdapter mPeopleAdapter;
    private TextView mTvNoInernet;
    private int mCurrentPage = 1;
    private List<ResponsePeople.ResultsBean> mPopularPeopleList;
    private ProgressBar mProgressBar;
    private TextView mTvLoading, mTvErrorShow;
    private List<ResponsePeople.ResultsBean.KnownForBean> mKnowPeopleList;
    private SelectedMenu mCurrentState = SelectedMenu.IDLE;
    private boolean mIsClearDataSet = false;
    private ResponsePeople mResponsePeople;

    public static PeopleFragment newInstance() {

        Bundle args = new Bundle();

        PeopleFragment fragment = new PeopleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmnet_people, container, false);
        bindViewsById(view);
        init();
        callToGetPopularPeople();
        return view;
    }


    private void bindViewsById(View view) {
        mRecyclerViewPeople = (RecyclerView) view.findViewById(R.id.recycler_people);
        mTvNoInernet = (TextView) view.findViewById(R.id.tv_no_internet_available);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        //mProgressBar.getIndeterminateDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        mTvLoading = (TextView) view.findViewById(R.id.tv_loading);
        mTvErrorShow = (TextView) view.findViewById(R.id.tv_error_show);

    }

    private void init() {
        mPopularPeopleList = new ArrayList<>();
        mKnowPeopleList = new ArrayList<>();
        mRestClient = new RestClient(getActivity());
        mRecyclerViewPeople.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mPeopleAdapter = new PeopleAdapter(mPopularPeopleList, false);
        mRecyclerViewPeople.setAdapter(mPeopleAdapter);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.spacing);
        mRecyclerViewPeople.addItemDecoration(itemDecoration);

        ItemClickSupport.addTo(mRecyclerViewPeople)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        callPeopleDetails(position);
                    }
                });

    }

    private void callPeopleDetails(int position) {
        if (ConnectionDetector.isNetworkAvailable(getActivity())) {
            displayLoadingDialog(true);
            mRestClient.callback(this).getPeopleDetails(String.valueOf(mPopularPeopleList.get(position).getId()));
        } else {
            displayShortToast(R.string.internet_connection);
        }
    }

    private void callToGetPopularPeople() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTvLoading.setVisibility(View.VISIBLE);
        mRestClient.callback(this).getPopularPeople(mCurrentPage);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.people_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_popular:
                if (mCurrentState != SelectedMenu.POPULAR_PEOPLE) {
                    item.setChecked(true);
                    displayLoadingDialog(false);
                    mIsClearDataSet = true;
                    mCurrentPage = 1;
                    AppPrefs.setStringKeyvaluePrefs(getActivity(), AppPrefs.KEY_MENU_VALUE, getString(R.string.popular));
                    mRestClient.callback(this).getPopularPeople(mCurrentPage);
                    mCurrentState = SelectedMenu.POPULAR_PEOPLE;
                }
                break;
            case R.id.action_latest:
                if (mCurrentState != SelectedMenu.LATEST_PEOPLE) {
                    item.setChecked(true);
                    displayLoadingDialog(false);
                    mIsClearDataSet = true;
                    mCurrentPage = 1;
                    AppPrefs.setStringKeyvaluePrefs(getActivity(), AppPrefs.KEY_MENU_VALUE, getString(R.string.latest));
                    mRestClient.callback(this).getLatestPeople(mCurrentPage);
                    mCurrentState = SelectedMenu.LATEST_PEOPLE;
                }
                break;
            case R.id.action_search:
                Intent movieIntent = new Intent(getActivity(), SearchMovieActivity.class);
                movieIntent.putExtra(ItemListFragment.ARG_TYPE, ItemListFragment.ARG_PEOPLE);
                startActivity(movieIntent);

                break;
        }
        return true;
    }

    @Override
    public void onSuccessResponse(int apiId, Object response) {
        mProgressBar.setVisibility(View.GONE);
        mTvLoading.setVisibility(View.GONE);
//        mTvErrorShow.setVisibility(View.GONE);

        if (apiId == ApiIds.ID_POPULAR_PEOPLE) {
            mResponsePeople = (ResponsePeople) response;
            if (mResponsePeople != null) {
                List<ResponsePeople.ResultsBean> popularPeople = mResponsePeople.getResults();
                for (int i = 0; i < popularPeople.size(); i++) {
                    mKnowPeopleList = popularPeople.get(i).getKnown_for();
                }
                if (popularPeople != null && popularPeople.size() > 0) {
                    mPopularPeopleList.clear();
                    //   mIsClearDataSet = false;

                    mPopularPeopleList.addAll(popularPeople);
                    if (mPeopleAdapter != null) {
                        mPeopleAdapter.notifyDataSetChanged();
                        // mIsLoadingNewItems = false;

                    }
                }
            }

        } else if (apiId == ApiIds.ID_PEOPLE_DETAILS) {
            ResponsePeopleDetails responsePeopleDetails = (ResponsePeopleDetails) response;
            String res = new Gson().toJson(responsePeopleDetails);
            String resKnown = new Gson().toJson(mResponsePeople);
            if (responsePeopleDetails != null) {

                Intent peopleDetailIntent = new Intent(getActivity(), PeopleDetailsActivity.class);
                peopleDetailIntent.putExtra(Constants.TYPE_PEOPLE_DETAILS, res);
                peopleDetailIntent.putExtra(Constants.TYPE_KNOWN_FOR, resKnown);
                startActivity(peopleDetailIntent);
            }

        }

    }

    @Override
    public void onFailResponse(int apiId, String error) {
        dismissLoadingDialog();
        displayErrorDialog(R.string.error, R.string.internet_connection);

    }

    @Override
    public void networkNotAvailable() {
        mTvNoInernet.setVisibility(View.VISIBLE);

    }
}
