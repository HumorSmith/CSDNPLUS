package com.ifreedomer.cplus.util;


import android.widget.TextView;

import com.ifreedomer.cplus.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ToolbarUtil {

    public static void setTitleBar(AppCompatActivity context, Toolbar toolbar, String title) {
        context.setSupportActionBar(toolbar);
        if (context.getSupportActionBar() != null) {
            TextView titleTv = toolbar.findViewById(R.id.titleTv);
            if (titleTv != null) {
                titleTv.setText(title);
            }
            toolbar.setTitle("");
        }
    }
}
