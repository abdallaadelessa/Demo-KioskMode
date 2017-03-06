package com.abdallaadelessa.kioskmodedemo.ui.listapps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abdallaadelessa.kioskmodedemo.R;
import com.abdallaadelessa.kioskmodedemo.ui.editapps.EditLockedAppsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by abdullah on 3/6/17.
 */
public abstract class BaseListAppsFragment extends Fragment {
    protected static final String TAG = "EditLockedAppsFragment";
    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;
    @BindView(R.id.tvMessage)
    protected TextView tvMessage;
    @BindView(R.id.rvList)
    protected RecyclerView rvList;
    @BindView(R.id.btnUpdate)
    protected ImageButton btnUpdate;
    private CompositeDisposable compositeDisposable;

    //======================>

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_locked_apps, container, false);
        ButterKnife.bind(this, view);
        initUI();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposeAll();
    }

    //======================>

    protected void initUI() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.vgContent, EditLockedAppsFragment.newInstance(), TAG)
                        .addToBackStack(TAG).commitAllowingStateLoss();
            }
        });
        compositeDisposable = new CompositeDisposable();
        loadData();
    }

    protected void showContent(boolean show) {
        progressBar.setVisibility(show ? View.GONE : View.VISIBLE);
        tvMessage.setVisibility(show ? View.GONE : View.VISIBLE);
        rvList.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    protected void showMessage(String message) {
        progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
        rvList.setVisibility(View.GONE);
        tvMessage.setText(message);
    }

    protected void addDisposable(Disposable d) {
        compositeDisposable.add(d);
    }

    protected void disposeAll() {
        compositeDisposable.dispose();
        compositeDisposable.clear();
    }

    //======================>

    protected abstract void loadData();

    //======================>
}
