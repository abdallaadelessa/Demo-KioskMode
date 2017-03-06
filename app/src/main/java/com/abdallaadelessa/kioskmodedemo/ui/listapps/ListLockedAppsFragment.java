package com.abdallaadelessa.kioskmodedemo.ui.listapps;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import com.abdallaadelessa.kioskmodedemo.R;
import com.abdallaadelessa.kioskmodedemo.domain.apps.AppsInfoManager;
import com.abdallaadelessa.kioskmodedemo.model.AppInfo;
import com.abdallaadelessa.kioskmodedemo.domain.blocker.WhiteListManager;

import java.util.List;
import java.util.Set;

import io.reactivex.functions.Consumer;

/**
 * Created by abdullah on 3/6/17.
 */

public class ListLockedAppsFragment extends BaseListAppsFragment {

    public static BaseListAppsFragment newInstance() {
        Bundle args = new Bundle();
        BaseListAppsFragment fragment = new ListLockedAppsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //======================>

    @Override
    protected void loadData() {
        showContent(false);
        rvList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        final Set<String> whiteListedPackagesNamesAsList = WhiteListManager.getWhiteListedPackagesNames();
        AppsInfoManager appsInfoManager = new AppsInfoManager();
        addDisposable(appsInfoManager.getAppInfosForList(whiteListedPackagesNamesAsList).subscribe(new Consumer<List<AppInfo>>() {
            @Override
            public void accept(List<AppInfo> appInfos) throws Exception {
                if (appInfos != null && !appInfos.isEmpty()) {
                    showContent(true);
                    rvList.setAdapter(new LockedAppsRvAdapter(appInfos));
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

    //======================>
}
