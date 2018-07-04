package com.gachon.kimhyju.tripool.others;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.gachon.kimhyju.tripool.R;

public class FriendlistView extends LinearLayout{
    MultiTransformation mul;
    ImageView friendlist_image;
    TextView friendlist_name;
    Context mcontext;

    public FriendlistView(Context context){
        super(context);
        init(context);

    }

    public FriendlistView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.friend_item,this,true);
        friendlist_name = (TextView)findViewById(R.id.friendlist_name);
        friendlist_image = (ImageView)findViewById(R.id.friendlist_image);
        mcontext=context;
        mul=new MultiTransformation(new CircleCrop(),new CenterCrop());
    }

    public void setName(String name){
        friendlist_name.setText(name);
    }
    public void setImage(String url){
        Glide.with(mcontext).load(url).apply(RequestOptions.bitmapTransform(mul)).into(friendlist_image);;
    }
}
