package com.abdallaadelessa.kioskmodedemo.domain.blocker;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.abdallaadelessa.kioskmodedemo.MyApplication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by abdullah on 3/6/17.
 */

public class WhiteListManager {
    private static final String KEY_WHITELISTED_APPS = "KEY_WHITELISTED_APPS";
    private static final String DELIMITER = ",";

    public static void updateWhiteListApp(Set<String> packageNames) {
        SharedPreferences.Editor editor = MyApplication.getInstance().getSharedPreferences(KEY_WHITELISTED_APPS, MODE_PRIVATE).edit();
        editor.putString(KEY_WHITELISTED_APPS, TextUtils.join(DELIMITER, packageNames));
        editor.apply();
    }

    @NonNull
    public static Set<String> getWhiteListedPackagesNames() {
        return getList();
    }

    @NonNull
    public static String[] getPackagesNamesForLockTask() {
        Set<String> whiteListedPackagesNamesAsList = getWhiteListedPackagesNames();
        whiteListedPackagesNamesAsList.add(MyApplication.getInstance().getPackageName());
        return whiteListedPackagesNamesAsList.toArray(new String[whiteListedPackagesNamesAsList.size()]);
    }

    //===========>

    private static Set<String> getList() {
        Set<String> packageNames;
        String names = MyApplication.getInstance().getSharedPreferences(KEY_WHITELISTED_APPS, MODE_PRIVATE).getString(KEY_WHITELISTED_APPS, null);
        if (names == null) {
            packageNames = new HashSet<>();
        } else {
            packageNames = new HashSet<>(Arrays.asList(TextUtils.split(names, DELIMITER)));
        }
        return packageNames;
    }
}
