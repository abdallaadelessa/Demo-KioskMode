package com.abdallaadelessa.kioskmodedemo;

import android.app.Application;

/**
 * Created by abdullah on 3/6/17.
 */

public class MyApplication extends Application {
    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
