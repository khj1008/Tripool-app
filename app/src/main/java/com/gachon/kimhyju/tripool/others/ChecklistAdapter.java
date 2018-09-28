package com.gachon.kimhyju.tripool.others;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gachon.kimhyju.tripool.object.Checklist;

import java.util.ArrayList;

public class ChecklistAdapter extends BaseAdapter {
    ArrayList<Checklist> checklist=new ArrayList<Checklist>();
    Context context;
    public ChecklistAdapter(Context context){
        this.context=context;
    }

    public void clear(){
        checklist.clear();
    }

    @Override
    public int getCount(){
        return checklist.size();
    }

    public void addItem(Checklist checklistitem){
        checklist.add(checklistitem);
    }

    @Override
    public Object getItem(int position){
        return checklist.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        ChecklistView view=new ChecklistView(context);
        Checklist cl=checklist.get(position);
        view.setGoodsName(cl.getItem_name());
        Log.e("checklistName",cl.getItem_name());
        return view;
    }
}
