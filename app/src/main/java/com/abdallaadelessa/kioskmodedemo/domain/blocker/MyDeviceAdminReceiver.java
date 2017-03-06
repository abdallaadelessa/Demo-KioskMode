package com.abdallaadelessa.kioskmodedemo.domain.blocker;

import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;

/**
 * Created by abdullah on 3/5/17.
 */

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context, MyDeviceAdminReceiver.class);
    }
}
