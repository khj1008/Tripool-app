package com.gachon.kimhyju.tripool.others;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gachon.kimhyju.tripool.R;

public class TriplistView extends LinearLayout {
    TextView triplist_subject;
    TextView triplist_country;
    TextView triplist_period;

    public TriplistView(Context context){
        super(context);
        init(context);
    }

    public TriplistView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.trip_item,this,true);
        triplist_subject = (TextView)findViewById(R.id.triplist_subject);
        triplist_country=(TextView)findViewById(R.id.triplist_country);
        triplist_period=(TextView)findViewById(R.id.triplist_period);
    }

    public void setSubject(String subject){
        triplist_subject.setText(subject);
    }

    public void setCountry(String country){
        triplist_country.setText(country);
    }

    public void setPeriod(String period){
        triplist_period.setText(period);
    }

}
