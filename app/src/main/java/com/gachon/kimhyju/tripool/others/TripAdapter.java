package com.gachon.kimhyju.tripool.others;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gachon.kimhyju.tripool.object.Trip;

import java.util.ArrayList;

public class TripAdapter extends BaseAdapter {
    ArrayList<Trip> trip=new ArrayList<Trip>();
    Context context;
    public TripAdapter(Context context){
        this.context=context;
    }

    public void clear(){
        trip.clear();
    }

    @Override
    public int getCount(){
        return trip.size();
    }

    public void addItem(Trip tripitem){
        trip.add(tripitem);
    }

    @Override
    public Object getItem(int position){
        return trip.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        TriplistView view=new TriplistView(context);
        Trip t=trip.get(position);
        view.setSubject(t.getSubject());
        view.setPeriod(t.getStart_date()+" ~ "+t.getEnd_date());
        return view;
    }
}
