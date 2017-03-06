package com.abdallaadelessa.kioskmodedemo.ui.editapps;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdallaadelessa.kioskmodedemo.R;
import com.abdallaadelessa.kioskmodedemo.model.EditAppInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abdullah on 3/6/17.
 */

public class EditLockedAppsRvAdapter extends RecyclerView.Adapter<EditLockedAppsRvAdapter.AppInfoViewHolder> {
    private List<EditAppInfo> editAppInfos;

    public EditLockedAppsRvAdapter(List<EditAppInfo> editAppInfos) {
        this.editAppInfos = editAppInfos;
    }

    public List<EditAppInfo> getEditAppInfos() {
        return editAppInfos;
    }

    @Override
    public AppInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_edit_locked_app_row, parent, false));
    }

    @Override
    public void onBindViewHolder(AppInfoViewHolder holder, int position) {
        EditAppInfo editAppInfo = editAppInfos.get(position);
        holder.ivIcon.setImageDrawable(editAppInfo.getIcon());
        holder.tvName.setText(editAppInfo.getAppname());
        holder.cbLocked.setChecked(editAppInfo.isChecked());
        holder.cbLocked.setTag(position);
        holder.cbLocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                EditAppInfo editAppInfo = editAppInfos.get(position);
                editAppInfo.setChecked(!editAppInfo.isChecked());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return editAppInfos != null ? editAppInfos.size() : 0;
    }

    public static class AppInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cbLocked)
        CheckBox cbLocked;
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
