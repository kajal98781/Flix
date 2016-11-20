package com.kmdev.flix.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.kmdev.flix.R;

/**
 * Created by Kajal on 10/30/2016.
 */
public class VersionDialogPrefrence extends DialogPreference {
    public VersionDialogPrefrence(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle(R.string.title_version);
        builder.setMessage(R.string.pref_title_new_message_notifications);
        super.onPrepareDialogBuilder(builder);
    }
}
