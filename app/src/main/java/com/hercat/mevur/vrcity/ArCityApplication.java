package com.hercat.mevur.vrcity;

import android.app.Application;
import android.util.Log;

import com.baidu.ar.bean.DuMixARConfig;
import com.baidu.ar.util.Res;
import com.baidu.mapapi.SDKInitializer;

public class ArCityApplication extends Application {
    private final String TAG = "VR APPLICATION";
    @Override
    public void onCreate() {
        super.onCreate();
        Res.addResource(this);
        DuMixARConfig.setAppId("13098");
        DuMixARConfig.setAPIKey("d4beb3f396afb8fdff6597cbe7069c75");
        Log.i(TAG, "onCreate: baidu map sdk is initialized");
        SDKInitializer.initialize(getApplicationContext());
    }
}
