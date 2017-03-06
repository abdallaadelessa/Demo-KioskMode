package com.abdallaadelessa.kioskmodedemo.domain.apps;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.abdallaadelessa.kioskmodedemo.MyApplication;
import com.abdallaadelessa.kioskmodedemo.model.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by abdullah on 3/6/17.
 */

public class AppsInfoManager {
    private ArrayList<AppInfo> getInstalledApps(Context context, boolean getSysPackages) {
        ArrayList<AppInfo> res = new ArrayList<>();
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            String appName = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
            String packageName = p.packageName;
            String versionName = p.versionName;
            int versionCode = p.versionCode;
            Drawable drawable = p.applicationInfo.loadIcon(context.getPackageManager());
            res.add(new AppInfo(appName, packageName, versionName, versionCode, drawable));
        }
        return res;
    }

    private ArrayList<AppInfo> getAppInfos(Set<String> packageNames) {
        Context context = MyApplication.getInstance();
        ArrayList<AppInfo> res = new ArrayList<>();
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (p.versionName == null || !packageNames.contains(p.packageName)) {
                continue;
            }
            String appName = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
            String packageName = p.packageName;
            String versionName = p.versionName;
            int versionCode = p.versionCode;
            Drawable drawable = p.applicationInfo.loadIcon(context.getPackageManager());
            res.add(new AppInfo(appName, packageName, versionName, versionCode, drawable));
        }
        return res;
    }

    //================>

    public Observable<ArrayList<AppInfo>> getAppInfosForList(final Set<String> packageNames) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<AppInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<AppInfo>> emitter) throws Exception {
                try {
                    ArrayList<AppInfo> res = getAppInfos(packageNames);
                    emitter.onNext(res);
                    emitter.onComplete();
                } catch (Throwable throwable) {
                    emitter.onError(throwable);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ArrayList<AppInfo>> getInstalledApps(final boolean getSysPackages) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<AppInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<AppInfo>> emitter) throws Exception {
                try {
                    ArrayList<AppInfo> installedApps = getInstalledApps(MyApplication.getInstance(), getSysPackages);
                    emitter.onNext(installedApps);
                    emitter.onComplete();
                } catch (Throwable throwable) {
                    emitter.onError(throwable);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    //================>
}
