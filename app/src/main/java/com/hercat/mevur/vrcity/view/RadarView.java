package com.hercat.mevur.vrcity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class RadarView extends View {

    private int maxRadius;
    private int width;
    private int height;

    private float centralX;
    private float centralY;

    // default is 15°
    private int rangeOfRadar = 15;

    private Paint backgroundPainter;

    private Paint circlePainter;

    private Paint dotPainter;

    private Paint fanPainter;

    private RectF rect;

    private Canvas mCanvas;

    private float orientation;




    private int backgroundColor = Color.argb(128, 51, 102, 153);
    private int circleColor = Color.rgb(238, 238, 238);
    private int dotColor = Color.rgb(0, 255, 255);
    private int fanColor = Color.argb(100, 204, 255, 255);


    private EndlessHorizontalScrollViewAdapter adapter;


    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //init background painter
        backgroundPainter = new Paint();
        backgroundPainter.setStyle(Paint.Style.FILL);
        backgroundPainter.setAntiAlias(true);
        backgroundPainter.setColor(backgroundColor);

        //init circle painter
        circlePainter = new Paint();
        circlePainter.setStyle(Paint.Style.STROKE);
        circlePainter.setColor(circleColor);
        circlePainter.setAntiAlias(true);

        //init dot painter
        dotPainter = new Paint();
        dotPainter.setStyle(Paint.Style.FILL);
        dotPainter.setColor(dotColor);
        dotPainter.setAntiAlias(true);

        //init fan painter
        fanPainter = new Paint();
        fanPainter.setStyle(Paint.Style.FILL);
        fanPainter.setColor(fanColor);
        fanPainter.setAntiAlias(true);

        rect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        centralX = width / 2.0f;
        centralY = height / 2.0f;
        //reverse 20 pixels as padding
        maxRadius = Math.min(width, height) / 2 - 10;


        System.out.println(width + " " + height + " " + maxRadius);
        System.out.println(centralX + " " + centralY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        startDraw();
    }

    private void startDraw() {
        // draw background
        mCanvas.drawCircle(centralX, centralY, maxRadius, backgroundPainter);

        // draw circle
        int span = maxRadius / 5;
        for (int i = 0; i < 5 ; i++) {
            float radius = maxRadius - span * i;
            mCanvas.drawCircle(centralX, centralY, radius, circlePainter);
        }

        // draw fan
        rect.set((width - maxRadius * 2) / 2 , (height - maxRadius * 2) / 2,
                maxRadius * 2, maxRadius * 2);
        float start = 0;
        if (orientation < 15) {
            float rest = 15 - orientation;
            start = 360 - rest;
        } else {
            start = orientation - 15;
        }
        mCanvas.drawArc(rect,
                orientation - 15,
                30,
                true, fanPainter);
    }

    public void setOrientation(float orientation) {
        this.orientation = orientation;
        invalidate();
    }
}
