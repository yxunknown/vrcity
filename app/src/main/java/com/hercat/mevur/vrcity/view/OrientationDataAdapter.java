package com.hercat.mevur.vrcity.view;

import android.view.View;
import android.view.ViewGroup;

public abstract class OrientationDataAdapter {
    public abstract int count();

    public abstract int getViewType();

    public abstract int typeCount();

    public abstract View getView(int position, View ConvertView, ViewGroup parent);

    public abstract double getOrientation(int position);

    public abstract double getDistance(int position);


}
