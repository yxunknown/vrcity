package com.hercat.mevur.vrcity.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class EndlessHorizontalScrollView extends HorizontalScrollView
        implements SensorEventListener {
    private Context context;
    //sensor manager & orientation sensor
    private SensorManager sensorManager;
    private Sensor orientationSensor;

    private float pixelsPerDegree;
    private LinearLayout container;

    //get the width & height of current screen
    private DisplayMetrics displayMetrics;

    private LinearLayout.LayoutParams itemLayoutParams;

    private EndlessHorizontalScrollViewAdapter mAdapter;

    private OrientationListener orientationListener;


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

    }

    public void setOrientationListener(OrientationListener orientationListener) {
        this.orientationListener = orientationListener;
    }

    /**
     * set view adapter for current adapter
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
                for (int i = 0; i < container.getChildCount(); i++) {
                    ((LinearLayout) container.getChildAt(i)).removeAllViews();
                }
                // add views
                int childViewsCount = mAdapter.getCount();
                for (int index = 0; index < childViewsCount; index++) {
                    View view = mAdapter.getView(index, null,
                            EndlessHorizontalScrollView.this);
                    double direction = mAdapter.getDirection(index);
                    if (null != view) {
                        addView(direction, view, index);
                    }
                }
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                // have no idea
            }
        });
    }

    //<editor-fold desc="life cycle of scroll view">
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (null == displayMetrics) {
            // get current screen display metrics
            displayMetrics = new DisplayMetrics();
            getDisplay().getMetrics(displayMetrics);
            // get width & height at pixels of current screen
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            // construct layout params of each child view of container
            itemLayoutParams = new LinearLayout.LayoutParams(width / 2, height);

            // every screen can display 30 degree range of data
            // each item can display 15 degree range of data
            // therefore, for 360 degree, need 360 / 15 = 24 items to display all data
        }
        // fill scroll view automatically
        if (0 == getChildCount()) {
            // current scroll view has no child
            // add a linear layout to scroll view as container
            LinearLayout linearLayout = getLinearLayout();
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            addView(linearLayout);
        } else if (!(getChildAt(0) instanceof LinearLayout)) {
            // change child view to linear layout as container
            removeViewAt(0);
            LinearLayout linearLayout = getLinearLayout();
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            addView(linearLayout);
        }
        container = (LinearLayout) getChildAt(0);
        if (26 != container.getChildCount()) {
            // remove all view then to add 26 child views
            container.removeAllViews();
            for (int i = 0; i < 26; i++) {
                LinearLayout child = getLinearLayout();
                child.setOrientation(LinearLayout.VERTICAL);
                child.setLayoutParams(itemLayoutParams);
                child.setGravity(Gravity.CENTER);
                container.addView(child);
            }
        }
        if (null != mAdapter) {
            int childViewsCount = mAdapter.getCount();
            for (int index = 0; index < childViewsCount; index++) {
                View view = mAdapter.getView(index, null, this);
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
     * @param direction a angel at degree unit between 0 to 360
     * @param view the view will add on screen
     * @param position the index of view in view adapter
     */
    private void addView(double direction, View view, int position) {
        int direc = (int) direction;
        int index = direc % 15 == 0 ? direc / 15 : (direc / 15) + 1;
        ((LinearLayout) container.getChildAt(index)).addView(view);
        if (index == 1) {
            ((LinearLayout) container.getChildAt(25)).addView(
                    mAdapter.getView(position, null, this));
        }
        if (index == 24) {
            ((LinearLayout) container.getChildAt(0)).addView(
                    mAdapter.getView(position, null, this));
        }
    }

    /**
     * override to disable this scroll view scrolling by user touch event input
     * @param ev ev
     * @return a boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //disable touch to scroll
        return false;
    }

    /**
     * generate a new linear layout
     * @return a new LinearLayout instance
     */
    @NonNull
    private LinearLayout getLinearLayout() {
        return new LinearLayout(context);
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
     * @param orientation current orientation
     */
    public void updateOrientation(double orientation) {
        pixelsPerDegree = (computeHorizontalScrollRange() - 1080) / 360.0f;
        int scrollTO = (int) (orientation * pixelsPerDegree);
        smoothScrollTo(scrollTO, 0);
    }
}
