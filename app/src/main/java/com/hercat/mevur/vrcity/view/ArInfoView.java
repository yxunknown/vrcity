package com.hercat.mevur.vrcity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class ArInfoView extends ViewGroup {

    private Context mContext;

    public ArInfoView(Context context) {
        this(context, null);
    }

    public ArInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //set transparent background
        setBackgroundColor(Color.TRANSPARENT);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
