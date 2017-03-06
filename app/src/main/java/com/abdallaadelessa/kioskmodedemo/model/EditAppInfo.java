package com.abdallaadelessa.kioskmodedemo.model;

import android.graphics.drawable.Drawable;

/**
 * Created by abdullah on 3/6/17.
 */

public class EditAppInfo extends AppInfo {
    private boolean checked;

    public EditAppInfo(String appname, String packageName, String versionName, int versionCode, Drawable icon, boolean checked) {
        super(appname, packageName, versionName, versionCode, icon);
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
