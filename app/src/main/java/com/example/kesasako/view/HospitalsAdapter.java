package com.example.kesasako.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kesasako.hospitalsearch.R;

import java.util.List;

/**
 * Created by kesasako on 2015/01/11.
 */
public class HospitalsAdapter extends BaseAdapter {

    private Context context;
    private List<HospitalDetail> list;
    private LayoutInflater layoutInflater = null;

    public HospitalsAdapter(Context context, List<HospitalDetail> list) {
        super();
        this.context = context;
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HospitalDetail hospitalDetail = (HospitalDetail) getItem(position);
        convertView = layoutInflater.inflate(R.layout.list_item, null);
        TextView tv = (TextView) convertView.findViewById(R.id.list_item);
        tv.setText(hospitalDetail.getName());
        return convertView;
    }
}