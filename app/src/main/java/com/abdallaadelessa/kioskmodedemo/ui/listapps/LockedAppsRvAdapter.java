package com.abdallaadelessa.kioskmodedemo.ui.listapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdallaadelessa.kioskmodedemo.R;
import com.abdallaadelessa.kioskmodedemo.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abdullah on 3/6/17.
 */

public class LockedAppsRvAdapter extends RecyclerView.Adapter<LockedAppsRvAdapter.AppInfoViewHolder> {
    private List<AppInfo> appInfoList;

    public LockedAppsRvAdapter() {
        appInfoList = new ArrayList<>();
    }

    public LockedAppsRvAdapter(List<AppInfo> appInfoList) {
        this.appInfoList = appInfoList;
    }

    @Override
    public AppInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_locked_app_row, parent, false));
    }

    @Override
    public void onBindViewHolder(AppInfoViewHolder holder, int position) {
        AppInfo appInfo = appInfoList.get(position);
        holder.ivIcon.setImageDrawable(appInfo.getIcon());
        holder.tvName.setText(appInfo.getAppname());
        holder.itemView.setTag(appInfo);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Context context = v.getContext();
                    AppInfo appInfo = (AppInfo) v.getTag();
                    PackageManager pm = context.getPackageManager();
                    Intent launchIntent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
                    context.startActivity(launchIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appInfoList != null ? appInfoList.size() : 0;
    }

    public static class AppInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView)
        ImageView ivIcon;
        @BindView(R.id.textView)
        TextView tvName;

        public AppInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
