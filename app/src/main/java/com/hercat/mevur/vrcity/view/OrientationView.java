package com.hercat.mevur.vrcity.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class OrientationView extends View {
    private RelativeLayout mContainer;
    private RecyclerBin mRecyler;
    private OrientationDataAdapter mAdapter;
    private Context mContext;

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
        mContainer = new RelativeLayout(mContext);
        // set the container to fill the screen
        RelativeLayout.LayoutParams containerLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.setLayoutParams(containerLayoutParams);


    }

    private class RecyclerBin {
        private List<View>[] cacheBuckets;
        private int mTypeCount;

        public void setTypeCount(int typeCount) {
            if (typeCount < 1) {
                throw new IllegalStateException("count of view type should not be less than 1.");
            } else {
                // ignore the warning
                cacheBuckets = new List[mTypeCount];
                for (int index = 0; index < mTypeCount; index++) {
                    cacheBuckets[index] = new ArrayList<>();
                }
            }
        }

        /**
         * get a view from cache, need to specify the type and position of view
         * @param type type of view
         * @param position position of view
         * @return a view or null
         */
        @Nullable
        public View getFromCaches(int type, int position) {
            if (type > mTypeCount) {
                return null;
            } else {
                List<View> bucket = cacheBuckets[type - 1];

            }
        }
    }


}
