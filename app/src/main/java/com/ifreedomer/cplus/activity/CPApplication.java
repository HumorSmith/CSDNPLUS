package com.ifreedomer.cplus.activity;

import android.app.Application;

public class CPApplication extends Application {
    public static CPApplication INSTANCE = null;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
