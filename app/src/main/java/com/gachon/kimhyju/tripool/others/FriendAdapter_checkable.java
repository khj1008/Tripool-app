package com.gachon.kimhyju.tripool.others;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.object.User;

import java.util.ArrayList;

public class FriendAdapter_checkable extends BaseAdapter {
    MultiTransformation mul;
    ArrayList<User> user=new ArrayList<User>();
    public FriendAdapter_checkable(){

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
        final int pos = position;
        final Context context = viewGroup.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friend_item_checkable, viewGroup, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView friend_image = (ImageView) convertView.findViewById(R.id.friendlist_image_check) ;
        TextView friend_name = (TextView) convertView.findViewById(R.id.friendlist_name_check) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        User u = user.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        mul=new MultiTransformation(new CircleCrop(),new CenterCrop());
        Glide.with(context).load(u.getThumbnail_image()).apply(RequestOptions.bitmapTransform(mul)).into(friend_image);
        friend_name.setText(u.getNickname());
        return convertView;
    }


}
