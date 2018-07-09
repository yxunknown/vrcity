package com.hercat.mevur.vrcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.hercat.mevur.vrcity.R;
import com.hercat.mevur.vrcity.entity.PointInfo;

import java.util.List;
import java.util.Locale;

import anylife.scrolltextview.ScrollTextView;

public class InfoItemAdapter extends BaseAdapter {
    private Context context;
    private List<PointInfo> pointInfos;
    private LayoutInflater layoutInflater;

    public InfoItemAdapter(Context context, List<PointInfo> pointInfos) {
        this.context = context;
        this.pointInfos = pointInfos;
        this.layoutInflater = LayoutInflater.from(this.context);
    }
    @Override
    public int getCount() {
        return pointInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return pointInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return pointInfos.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PointInfo pointInfo = (PointInfo) getItem(position);
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.info_item, null);
            viewHolder = new ViewHolder();
            viewHolder.logo = convertView.findViewById(R.id.iv_logo);
            viewHolder.name = convertView.findViewById(R.id.tv_name);
            viewHolder.distance = convertView.findViewById(R.id.tv_distance);
            viewHolder.logo.setImageResource(R.drawable.logo_kfc);
            viewHolder.name.setText(pointInfo.getName());
            Double d = pointInfo.getDistance();
            viewHolder.distance.setText("距您" + d.intValue() + "米");
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.logo.setImageResource(R.drawable.logo_kfc);
            viewHolder.name.setText(pointInfo.getName());
            Double d = pointInfo.getDistance();
            viewHolder.distance.setText("距您" + d.intValue() + "米");
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView logo;
        ScrollTextView name;
        ScrollTextView distance;
    }
}
