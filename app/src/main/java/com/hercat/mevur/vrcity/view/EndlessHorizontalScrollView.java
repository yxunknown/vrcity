package com.hercat.mevur.vrcity.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class EndlessHorizontalScrollView extends HorizontalScrollView
        implements SensorEventListener {
    private Context context;
    //sensor manager & orientation sensor
    private SensorManager sensorManager;
    private Sensor orientationSensor;

    private float pixelsPerDegree;
    private RelativeLayout container;

    //get the width & height of current screen
    private DisplayMetrics displayMetrics;

    private EndlessHorizontalScrollViewAdapter mAdapter;

    private OrientationListener orientationListener;
    private List<View> caches;

    private double orientation;



    public EndlessHorizontalScrollView(Context context) {
        super(context, null);
        init(context);
    }

    public EndlessHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context);
    }

    public EndlessHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        //init sensor
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        //get accelerometer sensor and magnetic sensor
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //
        // although SensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) is deprecated,
        // but the data of SensorManager.getOrientation(rotationMetrics, orientationValues),
        // orientationValues can not get a stable value.
        //

        if (null != orientationSensor) {
            //register sensor change event listener
            sensorManager.registerListener(this, orientationSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }

        caches = new ArrayList<>();

    }

    public void setOrientationListener(OrientationListener orientationListener) {
        this.orientationListener = orientationListener;
    }

    /**
     * set view adapter for current adapter
     *
     * @param adapter adapter
     */
    public void setAdapter(@NonNull EndlessHorizontalScrollViewAdapter adapter) {
        this.mAdapter = adapter;
        this.mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                // to change data
                // remove views
                container.removeAllViews();
                int childViewsCount = mAdapter.getCount();
                for (int index = 0; index < childViewsCount; index++) {
                    View view = obtainView(index);
                    double direction = mAdapter.getDirection(index);
                    addView(direction, view, index);
                }

            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                // have no idea
            }
        });
        System.out.println("EndlessHorizontalScrollView.setAdapter");
    }

    //<editor-fold desc="life cycle of scroll view">
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        System.out.println("EndlessHorizontalScrollView.onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (null == displayMetrics) {
            // get current screen display metrics
            displayMetrics = new DisplayMetrics();
            getDisplay().getMetrics(displayMetrics);
            // get width & height at pixels of current screen

            // every screen can display 30 degree range of data
            // each item can display 15 degree range of data
            // therefore, for 360 degree, need 360 / 15 = 24 items to display all data
        }
        // fill scroll view automatically
        if (0 == getChildCount()) {
            // current scroll view has no child
            // add a linear layout to scroll view as container
            RelativeLayout relativeLayout = getReleativeLayout();
            addView(relativeLayout);
        } else if (!(getChildAt(0) instanceof RelativeLayout)) {
            // change child view to linear layout as container
            removeViewAt(0);
            RelativeLayout relativeLayout = getReleativeLayout();
            addView(relativeLayout);
        }
        container = (RelativeLayout) getChildAt(0);
        container.removeAllViews();
        if (null != mAdapter && 0 == container.getChildCount()) {
            int childViewsCount = mAdapter.getCount();
            for (int index = 0; index < childViewsCount; index++) {
                View view = obtainView(index);
                double direction = mAdapter.getDirection(index);
                if (null != view) {
                    addView(direction, view, index);
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //there is where the view die
        sensorManager.unregisterListener(this);
    }
    //</editor-fold>

    /**
     * get child view container from child view containers
     * NOTED:
     * A whole 360 degree is divide to 24 areas
     * each area can display 15 degree range of data
     * hence, this method can select an area to display a view based on direction
     *
     * @param direction a angel at degree unit between 0 to 360
     * @param view      the view will add on screen
     * @param position  the index of view in view adapter
     */
    private void addView(double direction, View view, int position) {
        view.forceLayout();
        System.out.println(position);
        double distance = mAdapter.getDistance(position);
        int top = (int) distance;
        direction = Math.abs(direction);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                displayMetrics.widthPixels / 2,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        if (direction <= 15) {
            lp.setMargins(0, top, 0, 0);
        } else if (direction >= 345) {
            lp.setMargins(displayMetrics.widthPixels * 12, top, 0, 0);
        } else {
            lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            float ppn = displayMetrics.widthPixels * 12 / 360.0f;
            int marginStart = (int) (ppn * direction);
            marginStart += displayMetrics.widthPixels / 2;
            lp.setMargins(marginStart, top, 0, 0);
        }
        view.setLayoutParams(lp);
        container.addView(view);

        if (direction <= 15) {
            lp = new RelativeLayout.LayoutParams(
                    displayMetrics.widthPixels / 2,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            int left = (int) (displayMetrics.widthPixels * 12.5);
            lp.setMargins(left, top, 0, 0);
            View v = mAdapter.getView(position, null, this);
            v.setLayoutParams(lp);
            container.addView(v);
        }
        if (direction >= 345) {
            lp = new RelativeLayout.LayoutParams(
                    displayMetrics.widthPixels / 2,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, top, 0, 0);
            View v = mAdapter.getView(position, null, this);
            v.setLayoutParams(lp);
            container.addView(v);
        }
    }

    /**
     * override to disable this scroll view scrolling by user touch event input
     *
     * @param ev ev
     * @return a boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //disable touch to scroll
        return false;
    }

    private View obtainView(int position) {

        return mAdapter.getView(position, null, this);
    }

    /**
     * generate a new RelativeLayout
     *
     * @return a new RelativeLayout instance
     */
    @NonNull
    private RelativeLayout getReleativeLayout() {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                displayMetrics.widthPixels * 13,
                displayMetrics.heightPixels
        );
        relativeLayout.setLayoutParams(layoutParams);
        return relativeLayout;
    }

    //<editor-fold desc="sensor event change listener">
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Sensor.TYPE_ORIENTATION == event.sensor.getType()) {
            updateOrientation(event.values[0]);
            if (null != this.orientationListener) {
                orientationListener.onOrientationChange(event.values[0]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //</editor-fold>

    /**
     * update current viewport based on orientation
     *
     * @param orientation current orientation
     */
    private void updateOrientation(double orientation) {
        this.orientation = orientation;
        pixelsPerDegree = (computeHorizontalScrollRange() - 1080) / 360.0f;
        int scrollTO = (int) (orientation * pixelsPerDegree);
        smoothScrollTo(scrollTO, 0);
    }

}
