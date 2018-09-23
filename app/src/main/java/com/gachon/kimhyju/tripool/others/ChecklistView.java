package com.gachon.kimhyju.tripool.others;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gachon.kimhyju.tripool.R;

public class ChecklistView extends LinearLayout {
    ImageView check;
    TextView goodsName;
    public ChecklistView(Context context){
        super(context);
        init(context);
    }

    public ChecklistView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.goods_item,this,true);
        check=findViewById(R.id.goods_check);
        goodsName=findViewById(R.id.goods_name);
    }

    public void setCheck(int isChecked){
        if(isChecked==0)check.setColorFilter(R.color.gray);
        else if(isChecked==1)check.setColorFilter(Color.argb(255,255,187,53));
        else check.setColorFilter(Color.argb(255,204,2,2));
    }

    public void setGoodsName(String name){
        goodsName.setText(name);
    }
}
