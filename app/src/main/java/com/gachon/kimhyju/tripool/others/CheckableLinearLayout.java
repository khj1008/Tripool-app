package com.gachon.kimhyju.tripool.others;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.gachon.kimhyju.tripool.R;

public class CheckableLinearLayout extends LinearLayout implements Checkable {
    public CheckableLinearLayout(Context context){
        super(context);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs){
        super(context, attrs);
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

}
