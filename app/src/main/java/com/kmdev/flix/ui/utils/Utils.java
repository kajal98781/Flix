package com.kmdev.flix.ui.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Kajal on 10/17/2016.
 */
public class Utils {
    private static AlertDialog mAlertDialogReview;

    public static void displayReviewDetails(Context context, String title, String content) {
        mAlertDialogReview = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(true)
                .setNegativeButton(com.brsoftech.core_utils.R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        mAlertDialogReview.show();
    }
}
