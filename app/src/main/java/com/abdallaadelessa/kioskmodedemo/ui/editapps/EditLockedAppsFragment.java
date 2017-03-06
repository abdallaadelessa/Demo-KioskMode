package com.abdallaadelessa.kioskmodedemo.ui.editapps;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.abdallaadelessa.kioskmodedemo.R;
import com.abdallaadelessa.kioskmodedemo.domain.apps.AppsInfoManager;
import com.abdallaadelessa.kioskmodedemo.model.AppInfo;
import com.abdallaadelessa.kioskmodedemo.domain.blocker.WhiteListManager;
import com.abdallaadelessa.kioskmodedemo.model.EditAppInfo;
import com.abdallaadelessa.kioskmodedemo.ui.LockActivity;
import com.abdallaadelessa.kioskmodedemo.ui.listapps.BaseListAppsFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Created by abdullah on 3/6/17.
 */

public class EditLockedAppsFragment extends BaseListAppsFragment {

    private EditLockedAppsRvAdapter adapter;

    public static EditLockedAppsFragment newInstance() {
        Bundle args = new Bundle();
        EditLockedAppsFragment fragment = new EditLockedAppsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //======================>

    @Override
    protected void initUI() {
        super.initUI();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUpdatedLockedApps();
            }
        });
    }

    @Override
    protected void loadData() {
        showContent(false);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        AppsInfoManager appsInfoManager = new AppsInfoManager();
        final Set<String> whiteListedPackagesNames = WhiteListManager.getWhiteListedPackagesNames();
        addDisposable(appsInfoManager.getInstalledApps(true).flatMap(new Function<ArrayList<AppInfo>, ObservableSource<AppInfo>>() {
            @Override
            public ObservableSource<AppInfo> apply(ArrayList<AppInfo> appInfos) throws Exception {
                return Observable.fromIterable(appInfos);
            }
        }).map(new Function<AppInfo, EditAppInfo>() {
            @Override
            public EditAppInfo apply(AppInfo appInfo) throws Exception {
                boolean isChecked = whiteListedPackagesNames.contains(appInfo.getPackageName());
                return new EditAppInfo(appInfo.getAppname(), appInfo.getPackageName(), appInfo.getVersionName(), appInfo.getVersionCode(), appInfo.getIcon(), isChecked);
            }
        }).toList().subscribe(new Consumer<List<EditAppInfo>>() {
            @Override
            public void accept(List<EditAppInfo> appInfos) throws Exception {
                if (appInfos != null && !appInfos.isEmpty()) {
                    showContent(true);
                    adapter = new EditLockedAppsRvAdapter(appInfos);
                    rvList.setAdapter(adapter);
                } else {
                    showMessage(getString(R.string.no_white_listed_apps));
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                showMessage(throwable.getMessage());
            }
        }));
    }

    private void saveUpdatedLockedApps() {
        if (adapter != null) {
            addDisposable(Observable.fromIterable(adapter.getEditAppInfos()).filter(new Predicate<EditAppInfo>() {
                @Override
                public boolean test(EditAppInfo editAppInfo) throws Exception {
                    return editAppInfo.isChecked();
                }
            }).map(new Function<EditAppInfo, String>() {
                @Override
                public String apply(EditAppInfo editAppInfo) throws Exception {
                    return editAppInfo.getPackageName();
                }
            }).toList().subscribe(new Consumer<List<String>>() {
                @Override
                public void accept(List<String> strings) throws Exception {
                    WhiteListManager.updateWhiteListApp(new HashSet<>(strings));
                    if (getActivity() != null && getActivity() instanceof LockActivity) {
                        ((LockActivity) getActivity()).refreshLockTaskPackages();
                        getActivity().onBackPressed();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                }
            }));
        }
    }

    //======================>

}
