package com.abdallaadelessa.kioskmodedemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.abdallaadelessa.kioskmodedemo.R;
import com.abdallaadelessa.kioskmodedemo.domain.blocker.LockManager;
import com.abdallaadelessa.kioskmodedemo.ui.listapps.ListLockedAppsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class LockActivity extends AppCompatActivity {
    @BindView(R.id.swLockMode)
    Switch swLockMode;
    @BindView(R.id.vgContent)
    FrameLayout vgContent;
    @BindView(R.id.tvState)
    TextView tvState;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private LockManager lockManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        ButterKnife.bind(this);
        requestKeepScreenOn();
        lockManager = new LockManager(this);
        if (lockManager.isAppInLockTaskMode(this)) {
            swLockMode.setChecked(true);
            showContent();
        } else {
            showMessage(getString(R.string.not_activated));
        }
        //==========>
        swLockMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchLockMode(isChecked);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposeAll();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            requestImmersiveMode();
        }
    }

    @Override
    public void finish() {
        if (lockManager.isAppInLockTaskMode(this)) return;
        super.finish();
    }

    //======================> UI

    private void requestKeepScreenOn() {
        //Keep screen highlight always on. This is not required for kiosk mode, but makes things more nice
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void requestImmersiveMode() {
        //Enable full-screen mode. This is not required for kiosk mode, but makes things more nice
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void showHint(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessage(String message) {
        vgContent.setVisibility(View.GONE);
        tvState.setVisibility(View.VISIBLE);
        if (message != null) {
            tvState.setText(message);
        }
    }

    private void showContent() {
        vgContent.setVisibility(View.VISIBLE);
        tvState.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.vgContent, ListLockedAppsFragment.newInstance(), "TAG").commitAllowingStateLoss();
    }

    //======================> Lock Mode

    public void refreshLockTaskPackages() {
        lockManager.refreshLockTaskPackages();
    }

    private void switchLockMode(boolean isOn) {
        disposeAll();
        if (isOn) {
            compositeDisposable.add(lockManager.requestStartLockTask(this).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    switch (integer) {
                        case LockManager.STATUS_CODE_WORKING:
                            showHint(getString(R.string.text_kiosk_mode_wokring));
                            showContent();
                            break;
                        case LockManager.STATUS_CODE_NO_PERMISSION:
                            showHint(getString(R.string.text_no_permission_text));
                            showContent();
                            break;
                        case LockManager.STATUS_CODE_NOT_DEVICE_ADMIN:
                            showHint(getString(R.string.text_not_a_device_admin_text));
                            showContent();
                            break;
                        case LockManager.STATUS_CODE_NOT_WORKING:
                        default:
                            showMessage(getString(R.string.kiosk_not_working));
                            break;
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                    showMessage(getString(R.string.kiosk_not_working));
                }
            }));
        } else {
            compositeDisposable.add(lockManager.requestStopLockTask(this).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    showMessage(getString(R.string.not_activated));
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    showMessage(getString(R.string.not_activated));
                }
            }));
        }
    }

    private void disposeAll() {
        compositeDisposable.dispose();
        compositeDisposable.clear();
    }

    //======================>

}
