package com.kmdev.flix.ui.activities;

import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brsoftech.core_utils.base.BaseAppCompatActivity;
import com.kmdev.flix.R;
import com.kmdev.flix.RestClient.ApiUrls;
import com.kmdev.flix.utils.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by Kajal on 10/16/2016.
 */
public class MovieImageFullScreenActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private String mImageUrl, mTitle;
    private ImageView mImageFullScreenView, mImageViewCross, mImageViewDownload;
    private TextView mTvTitle, mTvDate;
    private DownloadManager mDownloadManager;
    private long mDownloadReference;

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
        mTitle = bundle.getString(Constants.MOVIE_TITLE);
    }


    private void bindViewsById() {
        mImageFullScreenView = (ImageView) findViewById(R.id.imageFullView);
        mImageViewCross = (ImageView) findViewById(R.id.img_cross);
        mImageViewDownload = (ImageView) findViewById(R.id.img_download);
        mImageViewCross.setOnClickListener(this);
        mImageViewDownload.setOnClickListener(this);
    }

    private void init() {
        if (!TextUtils.isEmpty(mImageUrl)) {

            Picasso.with(this)
                    .load(ApiUrls.IMAGE_PATH_ULTRA + mImageUrl)
                    .into(mImageFullScreenView);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_cross:
                super.onBackPressed();
                break;
            case R.id.img_download:
                callToDownLoadAndSaveImage();
                break;
        }
    }

    private void callToDownLoadAndSaveImage() {
/*
// Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                Utils.downloadFile(MovieImageFullScreenActivity.this,
                        ApiUrls.IMAGE_PATH_ORIGINAL + mImageUrl,
                        mTitle,
                        "Download Image...");
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

*/

      /*  if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Utils.downloadFile(MovieImageFullScreenActivity.this,
                ApiUrls.IMAGE_PATH_ORIGINAL + mImageUrl,
                mTitle,
                "Download image..."
        );*/
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission Granted
            callToDownLoadAndSaveImage();
        } else {
            // Permission Denied
            Toast.makeText(MovieImageFullScreenActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
