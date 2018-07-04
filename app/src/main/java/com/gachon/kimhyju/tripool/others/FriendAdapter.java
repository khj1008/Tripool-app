package com.gachon.kimhyju.tripool.others;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gachon.kimhyju.tripool.object.User;

import java.util.ArrayList;

public class FriendAdapter extends BaseAdapter {

    ArrayList<User> user=new ArrayList<User>();
    Context context;
    public FriendAdapter(Context context){
        this.context=context;
    }

    public void clear(){
        user.clear();
    }

    @Override
    public int getCount(){
        return user.size();
    }

    public void addItem(User useritem){
        user.add(useritem);
    }

    @Override
    public Object getItem(int position){
        return user.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        FriendlistView view=new FriendlistView(context);
        User u=user.get(position);
        view.setName(u.getNickname());
        view.setImage(u.getThumbnail_image());
        return view;
    }
}
