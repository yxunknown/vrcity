package com.hercat.mevur.vrcity;

import android.app.Application;

import com.baidu.ar.bean.DuMixARConfig;
import com.baidu.ar.util.Res;

public class ArCityApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Res.addResource(this);
        DuMixARConfig.setAppId("13098");
        DuMixARConfig.setAPIKey("d4beb3f396afb8fdff6597cbe7069c75");
    }
}
