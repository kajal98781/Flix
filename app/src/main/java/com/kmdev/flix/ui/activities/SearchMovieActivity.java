package com.kmdev.flix.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.brsoftech.core_utils.base.BaseAppCompatActivity;
import com.brsoftech.core_utils.utils.ItemClickSupport;
import com.google.gson.Gson;
import com.kmdev.flix.R;
import com.kmdev.flix.RestClient.ApiHitListener;
import com.kmdev.flix.RestClient.ApiIds;
import com.kmdev.flix.RestClient.ConnectionDetector;
import com.kmdev.flix.RestClient.RestClient;
import com.kmdev.flix.models.ResponseMovieDetails;
import com.kmdev.flix.models.ResponseSearchMovie;
import com.kmdev.flix.ui.adapters.SearchMovieAdapter;
import com.kmdev.flix.utils.Constants;
import com.kmdev.flix.utils.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchMovieActivity extends BaseAppCompatActivity implements ApiHitListener {
    private RestClient mRestClient;
    private SearchMovieAdapter mSearchMovieAdapter;
    private RecyclerView mRecyclerSearch;
    private String mQuery;
    private EditText mEtSearch;
    private List<ResponseSearchMovie.ResultsSearchBean> mSearchBeanList;
    private Toolbar mToolBar;
    private TextView mTvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        bindViewsById();
        init();
    }


    private void bindViewsById() {
        mRecyclerSearch = (RecyclerView) findViewById(R.id.recycler_search);
        mEtSearch = (EditText) findViewById(R.id.et_search);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mTvError = (TextView) findViewById(R.id.tv_error_show);


    }

    private void init() {
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //hide  title from toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(4);

        mRestClient = new RestClient(this);
        mSearchBeanList = new ArrayList<>();
        mRecyclerSearch.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mSearchMovieAdapter = new SearchMovieAdapter(mSearchBeanList);
        mRecyclerSearch.setAdapter(mSearchMovieAdapter);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.spacing);
        mRecyclerSearch.addItemDecoration(itemDecoration);
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchQuery = mEtSearch.getText().toString();
                if (!TextUtils.isEmpty(searchQuery)) {
                    callSearchMovie(searchQuery);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSearchBeanList.clear();
                            mSearchMovieAdapter.notifyDataSetChanged();

                        }
                    }, 300);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
              /*  if(TextUtils.isEmpty(s))
                {
                    mSearchBeanList.clear();
                    mSearchMovieAdapter.notifyDataSetChanged();
                }*/

            }
        });
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchQuery = mEtSearch.getText().toString();
                    if (!TextUtils.isEmpty(searchQuery)) {
                        displayLoadingDialog(false);
                        callSearchMovie(searchQuery);
                    } else {

                    }

                    return true;
                }
                return false;
            }
        });
        ItemClickSupport.addTo(mRecyclerSearch).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                callMovieDetails(position);

            }
        });

        //mToolBar.
    }

    private void callSearchMovie(String searchQuery) {
        // displayLoadingDialog(false);
        mRestClient.callback(this).searchMovie(searchQuery);

    }

    private void callMovieDetails(int position) {
        if (ConnectionDetector.isNetworkAvailable(SearchMovieActivity.this)) {
            displayLoadingDialog(true);
            mRestClient.callback(this).getMovieDetails(String.valueOf(mSearchBeanList.get(position).getId()));
        } else {
            displayShortToast(R.string.internet_connection);
        }

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
    public void onSuccessResponse(int apiId, Object response) {
        dismissLoadingDialog();
        mTvError.setVisibility(View.GONE);

        if (apiId == ApiIds.ID_SEARCH_MOVIE) {
            ResponseSearchMovie resultsSearchBean = (ResponseSearchMovie) response;
            if (resultsSearchBean != null) {
                List<ResponseSearchMovie.ResultsSearchBean> searchBeanList = resultsSearchBean.getResultsSearchList();
                if (searchBeanList.size() > 0) {
                    mSearchBeanList.clear();
                    mSearchBeanList.addAll(searchBeanList);
                    if (mSearchMovieAdapter != null) {
                        mSearchMovieAdapter.notifyDataSetChanged();
                    }


                }
            }
        } else if (apiId == ApiIds.ID_MOVIE_DETAILS) {
            ResponseMovieDetails responseMovieDetails = (ResponseMovieDetails) response;
            String res = new Gson().toJson(responseMovieDetails);
            if (responseMovieDetails != null) {

                Intent movieDetailIntent = new Intent(this, MovieDetailsActivity.class);
                movieDetailIntent.putExtra(Constants.TYPE_MOVIE_DETAILS, res);
                startActivity(movieDetailIntent);
                finish();
            }
        }


    }

    @Override
    public void onFailResponse(int apiId, String error) {
        mTvError.setText(R.string.unable_load_movies);
        mTvError.setVisibility(View.VISIBLE);
    }

    @Override
    public void networkNotAvailable() {
        mTvError.setVisibility(View.VISIBLE);


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
