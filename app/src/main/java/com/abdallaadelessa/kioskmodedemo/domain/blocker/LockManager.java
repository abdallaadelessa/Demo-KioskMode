package com.abdallaadelessa.kioskmodedemo.domain.blocker;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.UserManager;
import android.provider.Settings;

import com.abdallaadelessa.kioskmodedemo.ui.LockActivity;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by abdullah on 3/6/17.
 */

public class LockManager {
    public static final int STATUS_CODE_WORKING = 1;
    public static final int STATUS_CODE_NOT_WORKING = 0;
    public static final int STATUS_CODE_NO_PERMISSION = -1;
    public static final int STATUS_CODE_NOT_DEVICE_ADMIN = -2;
    private final String mPackageName;
    private ComponentName mAdminComponentName;
    private DevicePolicyManager mDevicePolicyManager;
    //---->
    private static final String Battery_PLUGGED_ANY = Integer.toString(
            BatteryManager.BATTERY_PLUGGED_AC |
                    BatteryManager.BATTERY_PLUGGED_USB |
                    BatteryManager.BATTERY_PLUGGED_WIRELESS);
    private static final String DONT_STAY_ON = "0";

    //======================>

    public LockManager(Context context) {
        mAdminComponentName = MyDeviceAdminReceiver.getComponentName(context);
        mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mPackageName = context.getPackageName();
    }

    public boolean isAppInLockTaskMode(Context context) {
        ActivityManager activityManager;
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For SDK version 23 and above.
            return activityManager.getLockTaskModeState() != ActivityManager.LOCK_TASK_MODE_NONE;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // When SDK version >= 21. This API is deprecated in 23.
            return activityManager.isInLockTaskMode();
        }
        return false;
    }

    public Observable<Integer> requestStartLockTask(Activity activity) {
        final WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                boolean lockStarted = false;
                Activity currentActivity = activityWeakReference.get();
                if (mDevicePolicyManager.isDeviceOwnerApp(mPackageName)) {
                    refreshLockTaskPackages();
                    if (mDevicePolicyManager.isLockTaskPermitted(mPackageName)) {
                        if (currentActivity != null) {
                            startLock(currentActivity); //Working in full mode
                            e.onNext(STATUS_CODE_WORKING);
                            lockStarted = true;
                        }
                    } else {
                        if (currentActivity != null) {
                            startLock(currentActivity); //Working in emulation mode
                            e.onNext(STATUS_CODE_NO_PERMISSION);
                            lockStarted = true;
                        }
                    }
                } else {
                    if (currentActivity != null) {
                        startLock(currentActivity); //Working in emulation mode
                        e.onNext(STATUS_CODE_NOT_DEVICE_ADMIN);
                        lockStarted = true;
                    }
                }
                if (!lockStarted) {
                    e.onNext(STATUS_CODE_NOT_WORKING);
                }
                e.onComplete();
            }
        });
    }

    public void refreshLockTaskPackages() {
        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName, WhiteListManager.getPackagesNamesForLockTask());
    }

    public Observable<Boolean> requestStopLockTask(Activity activity) {
        final WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                try {
                    Activity currentActivity = activityWeakReference.get();
                    if (currentActivity != null) {
                        stopLock(currentActivity);
                    }
                    e.onNext(true);
                    e.onComplete();
                } catch (Exception error) {
                    e.onError(error);
                }
            }
        });
    }

    //======================>

    private void startLock(Activity activity) {
        activity.startLockTask();
        setDefaultPolicies(true);
    }

    private void stopLock(Activity activity) {
        setDefaultPolicies(false);
        activity.stopLockTask();
    }

    //======================>

    private void setDefaultPolicies(boolean active) {
        // set user restrictions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);
        }
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, active);
            mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, active);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // enable STAY_ON_WHILE_PLUGGED_IN
            enableStayOnWhilePluggedIn(active);
            // set System Update policy
            if (active) {
                mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                        SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
            } else {
                mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName, null);
            }
        }

        // set this Activity as a lock task package
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        if (active) {
            // set Cosu activity as home intent receiver so that it is started
            // on reboot
            mDevicePolicyManager.addPersistentPreferredActivity(
                    mAdminComponentName, intentFilter, new ComponentName(
                            mPackageName, LockActivity.class.getName()));
        } else {
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(
                    mAdminComponentName, mPackageName);
        }
    }

    private void setUserRestriction(String restriction, boolean disallow) {
        if (disallow) {
            mDevicePolicyManager.addUserRestriction(mAdminComponentName,
                    restriction);
        } else {
            mDevicePolicyManager.clearUserRestriction(mAdminComponentName,
                    restriction);
        }
    }

    private void enableStayOnWhilePluggedIn(boolean enabled) {
        if (enabled) {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    Battery_PLUGGED_ANY);
        } else {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN, DONT_STAY_ON);
        }
    }

    //======================>
}
