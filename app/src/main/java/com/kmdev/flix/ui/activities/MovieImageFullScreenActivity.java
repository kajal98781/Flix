package com.kmdev.flix.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.brsoftech.core_utils.base.BaseAppCompatActivity;
import com.kmdev.flix.R;
import com.kmdev.flix.ui.RestClient.ApiUrls;
import com.kmdev.flix.ui.utils.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by Kajal on 10/16/2016.
 */
public class MovieImageFullScreenActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private String mImageUrl;
    private ImageView mImageFullScreenView, mImageViewCross;
    // private TextView mTvTitle, mTvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        bindViewsById();
        getBundleData();

        init();


    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        mImageUrl = bundle.getString(Constants.FULL_IMAGE_URL);
    }
      /*  mTitle = bundle.getString(Constants.MOVIE_TITLE);
        mDate = bundle.getString(Constants.MOVIE_RELEASE_DATE);
    }*/

    private void bindViewsById() {
        mImageFullScreenView = (ImageView) findViewById(R.id.imageFullView);
        mImageViewCross = (ImageView) findViewById(R.id.img_cross);
/*
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvDate = (TextView) findViewById(R.id.tv_date);
*/
        mImageViewCross.setOnClickListener(this);
    }

    private void init() {
        if (!TextUtils.isEmpty(mImageUrl)) {

            Picasso.with(this)
                    .load(ApiUrls.IMAGE_PATH_ULTRA + mImageUrl)
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(mImageFullScreenView);
        }
       /* if(!TextUtils.isEmpty(mTitle))
        {
            mTvTitle.setText(mTitle);
        }
        if(!TextUtils.isEmpty(mDate))
        {
            mTvDate.setText(mDate);
        }*/
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_cross:
                super.onBackPressed();
                break;
        }
    }
}
