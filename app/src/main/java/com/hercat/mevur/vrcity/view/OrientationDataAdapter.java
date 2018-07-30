package com.hercat.mevur.vrcity.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class OrientationDataAdapter extends BaseAdapter {

    @Override
    public abstract int getItemViewType(int position);

    @Override
    public abstract int getViewTypeCount();

    public abstract int count();

    public abstract View getView(int position, View ConvertView, ViewGroup parent);

    public abstract double getOrientation(int position);

    public abstract double getDistance(int position);


}
