package com.hercat.mevur.vrcity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentActivity extends AppCompatActivity {
    @BindView(R.id.ar_container)
    LinearLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ARInfoView arInfoView = new ARInfoView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        arInfoView.setLayoutParams(lp);
        arInfoView.setInfo("你好");
        frameLayout.addView(arInfoView);
        arInfoView.setInfo("世界");
        frameLayout.addView(arInfoView);

    }
}
