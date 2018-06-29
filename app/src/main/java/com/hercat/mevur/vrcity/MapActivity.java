package com.hercat.mevur.vrcity;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.ar.ARFragment;
import com.baidu.ar.constants.ARConfigKey;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends FragmentActivity {

    private ARFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        if (null != findViewById(R.id.ar_container)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Bundle data = new Bundle();
            JSONObject obj = new JSONObject();
            try {
                obj.put(ARConfigKey.AR_KEY, "10198910");
                obj.put(ARConfigKey.AR_TYPE, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            data.putString(ARConfigKey.AR_VALUE, obj.toString());
            ARFragment arFragment = new ARFragment();
            arFragment.setArguments(data);
            fragmentTransaction.replace(R.id.ar_container, arFragment);
            fragmentTransaction.commitNowAllowingStateLoss();
        }

    }

}
