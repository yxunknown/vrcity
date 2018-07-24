package com.hercat.mevur.vrcity.view;

import android.widget.BaseAdapter;

public abstract class EndlessHorizontalScrollViewAdapter extends BaseAdapter {
    /**
     * get direction of view at position
     * direction should be at degree unit
     * and is an angular based on the North direction
     * @param position position of view
     * @return direction of view at position
     */
    public abstract double getDirection(int position);
}
