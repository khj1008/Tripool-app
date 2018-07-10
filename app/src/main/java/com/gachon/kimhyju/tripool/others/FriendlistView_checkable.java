package com.gachon.kimhyju.tripool.others;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.gachon.kimhyju.tripool.R;

public class FriendlistView_checkable extends LinearLayout implements Checkable {

    MultiTransformation mul;
    ImageView friendlist_image_check;
    TextView friendlist_name_check;
    Context mcontext;

    public FriendlistView_checkable(Context context){
        super(context);
        init(context);

    }

    public FriendlistView_checkable(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    @Override
    public boolean isChecked(){
        CheckBox cb=findViewById(R.id.friend_checkbox);
        return cb.isChecked();
    }

    @Override
    public void toggle() {
        CheckBox cb = findViewById(R.id.friend_checkbox) ;
        setChecked(cb.isChecked() ? false : true) ;
    }

    @Override
    public void setChecked(boolean checked) {
        CheckBox cb = findViewById(R.id.friend_checkbox) ;
        if (cb.isChecked() != checked) {
            cb.setChecked(checked) ;
        }

        // CheckBox 가 아닌 View의 상태 변경.
    }

    public void init(Context context){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.friend_item,this,true);
        friendlist_name_check = (TextView)findViewById(R.id.friendlist_name_check);
        friendlist_image_check = (ImageView)findViewById(R.id.friendlist_image_check);
        mcontext=context;
        mul=new MultiTransformation(new CircleCrop(),new CenterCrop());
    }

    public void setName(String name){
        friendlist_name_check.setText(name);
    }
    public void setImage(String url){
        Glide.with(mcontext).load(url).apply(RequestOptions.bitmapTransform(mul)).into(friendlist_image_check);;
    }
}
