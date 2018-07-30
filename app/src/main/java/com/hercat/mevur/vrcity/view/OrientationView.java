package com.hercat.mevur.vrcity.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrientationView extends RelativeLayout implements SensorEventListener {

    private RecyclerBin mRecyler;
    private OrientationDataAdapter mAdapter;
    private Context mContext;

    private SensorManager sensorManager;
    private Sensor orientationSensor;

    private OrientationListener mOrientationListener;

    private float currentOrientation;

    private DisplayMetrics displayMetrics;

    private int baseline;
    private float pixelsPerDegree;

    private int width;
    private int height;


    public OrientationView(Context context) {
        this(context, null);
    }

    public OrientationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrientationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {

        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (null != orientationSensor) {
            sensorManager.registerListener(this, orientationSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }

        mRecyler = new RecyclerBin();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (null == displayMetrics) {
            // get current screen display metrics
            displayMetrics = new DisplayMetrics();
            getDisplay().getMetrics(displayMetrics);
            // get width & height at pixels of current screen
            this.width = displayMetrics.widthPixels;
            this.height = displayMetrics.heightPixels;
            this.pixelsPerDegree = this.width / 30.0f;
            this.baseline = this.width / 2;
        }
        if (null != mAdapter) {
            int childCount = mAdapter.count();
            float start = currentOrientation < 20 ?
                    360 - 20 + currentOrientation : currentOrientation - 20;
            float end = start + 40 >= 360 ? start + 40 - 360 : start + 40;
            removeAllViews();
            for (int i = 0; i < childCount; i++) {
                double orientation = mAdapter.getOrientation(i);
                if (orientation >= start && orientation <= end) {
                    View v = mAdapter.getView(i, null, this);
                    v.setLayoutParams(getItemRelativeLayoutParams(i));
                    addView(v);
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setmOrientationListener(OrientationListener mOrientationListener) {
        this.mOrientationListener = mOrientationListener;
    }

    public void setmAdapter(@NonNull OrientationDataAdapter mAdapter) {
        this.mAdapter = mAdapter;
        this.mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                // refresh data
                requestLayout();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
            }
        });
        mRecyler.setTypeCount(this.mAdapter.getViewTypeCount());

    }

    public RelativeLayout.LayoutParams getItemRelativeLayoutParams(int position) {
        double orientation = mAdapter.getOrientation(position);
        double distance = mAdapter.getDistance(position);

        RelativeLayout.LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        double delta = orientation - currentOrientation;
        int left = (int) (baseline + delta * pixelsPerDegree);
        int top = (int) (1000 - distance / 1000);
        layoutParams.setMargins(left, top, 0, 0);
        return layoutParams;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Sensor.TYPE_ORIENTATION == event.sensor.getType()) {
            updateOrientation(event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void updateOrientation(float orientation) {
        this.currentOrientation = orientation;
        if (null != this.mOrientationListener) {
            mOrientationListener.onOrientationChange(orientation);
        }
    }

    private class RecyclerBin {
        private List<View>[] cacheBuckets;
        private int mTypeCount;
        private Map<Integer, Integer> types;
        private int[] countOfEachTypeView;


        public void setTypeCount(int typeCount) {
            if (typeCount < 1) {
                throw new IllegalStateException("count of view type should not be less than 1.");
            } else {
                countOfEachTypeView = new int[typeCount];
                // ignore the warning
                cacheBuckets = new List[mTypeCount];

                // init cacheBuckets % countOfEachTypeView
                for (int index = 0; index < mTypeCount; index++) {
                    cacheBuckets[index] = new ArrayList<>();
                    // there is no view in the caches initial
                    countOfEachTypeView[index] = 0;
                }
            }
        }

        /**
         * get a view from cache, need to specify the type and position of view
         *
         * @param type     type of view
         * @param position position of view
         * @return a view or null
         */
        @Nullable
        public View getFromCaches(int type, int position) {
            int whichBucket = getTypeIndex(type);
            if (whichBucket < 0 || whichBucket >= mTypeCount
                    || position < 0 || position > countOfEachTypeView[whichBucket]) {
                return null;
            } else {
                return cacheBuckets[whichBucket].get(position);

            }
        }

        public int getTypeIndex(int type) {
            return types.get(type);
        }

        public void addType(int type) {
            this.types.put(type, this.types.size());
        }

        public int isViewInCaches(int type, View view) {
            int which = getTypeIndex(type);
            if (type < 0 || type > mTypeCount) {
                return -1;
            } else {
                return cacheBuckets[which].indexOf(view);
            }
        }

        public void addViewIntoCaches(int type, View view) {
            int whichBucket = getTypeIndex(type);
            cacheBuckets[whichBucket].add(view);
            countOfEachTypeView[whichBucket]++;
        }

        public void update() {
            for (int bucket = 0; bucket < mTypeCount; bucket++) {
                for (int index = countOfEachTypeView[bucket];
                     index <= cacheBuckets[bucket].size(); index++) {
                    View v = cacheBuckets[bucket].get(index);
                    v = null;
                }
            }
        }

        public void recycle(int type) {
            int whichBucket = getTypeIndex(type);
            countOfEachTypeView[whichBucket]--;
        }
    }
}
