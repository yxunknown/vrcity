package com.hercat.mevur.vrcity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ARInfoView extends FrameLayout {
    private Context context;
    private ProgressBar bar;
    private TextView textView;
    private String info;
    public ARInfoView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ARInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public ARInfoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(this.context).inflate(R.layout.ar_info_view, this, true);
        textView = findViewById(R.id.txt_info);
        bar = findViewById(R.id.bar_spin);
        bar.setIndeterminate(true);
        info = "";
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        textView = findViewById(R.id.txt_info);
        textView.setText(info);
        System.out.println("on finish inflate");
    }

    public void setInfo(String info) {
        this.info = info;
        textView.setText(info);
        System.out.println("info = [" + info + "]");
    }

}
