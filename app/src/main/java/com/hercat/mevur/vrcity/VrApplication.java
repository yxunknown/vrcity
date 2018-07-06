package com.hercat.mevur.vrcity;

import android.app.Application;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;

public class VrApplication extends Application {
    private final String TAG = "VR APPLICATION";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: baidu map sdk is initialized");

    }
}
