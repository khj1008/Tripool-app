package com.gachon.kimhyju.tripool.others;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gachon.kimhyju.tripool.object.User;

import java.util.ArrayList;

public class FriendAdapter_checkable extends BaseAdapter {
    ArrayList<User> user=new ArrayList<User>();
    Context context;
    public FriendAdapter_checkable(Context context){
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
        //final int pos=position;
        //final Context context=viewGroup.getContext();
        //LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //convertView=inflater.inflate(R.layout.friend_item_checkable, viewGroup, false);
        //ImageView imageView=convertView.findViewById(R.id.friendlist_image_check);
        //TextView textView=convertView.findViewById(R.id.friendlist_name_check);
        //User u=user.get(position);
        //mul=new MultiTransformation(new CircleCrop(),new CenterCrop());
        //Glide.with(convertView).load(u.getThumbnail_image()).apply(RequestOptions.bitmapTransform(mul)).into(imageView);;
        //textView.setText(u.getNickname());

        FriendlistView_checkable view=new FriendlistView_checkable(context);
        User u=user.get(position);
        view.setName(u.getNickname());
        view.setImage(u.getThumbnail_image());
        return convertView;
    }


}
