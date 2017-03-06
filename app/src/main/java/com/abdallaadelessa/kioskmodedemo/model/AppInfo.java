package com.abdallaadelessa.kioskmodedemo.model;

import android.graphics.drawable.Drawable;

/**
 * Created by abdullah on 3/6/17.
 */
public class AppInfo {
    private String appname;
    private String packageName;
    private String versionName;
    private int versionCode;
    private Drawable icon;

    public AppInfo() {
        appname = "";
        packageName = "";
        versionName = "";
        versionCode = 0;
    }

    public AppInfo(String appname, String packageName, String versionName, int versionCode, Drawable icon) {
        this.appname = appname;
        this.packageName = packageName;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.icon = icon;
    }

    public String getAppname() {
        return appname;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public Drawable getIcon() {
        return icon;
    }
}
