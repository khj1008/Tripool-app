package com.gachon.kimhyju.tripool.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.object.Trip;
import com.gachon.kimhyju.tripool.object.User;
import com.gachon.kimhyju.tripool.others.ApplicationController;
import com.gachon.kimhyju.tripool.others.FriendAdapter_checkable;
import com.gachon.kimhyju.tripool.others.NetworkService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripcreateActivity extends Activity implements View.OnClickListener {
    String trip_id;
    String trip_subject;
    String flag;
    EditText trip_subject_edit;
    TextView trip_start_date;
    TextView trip_end_date;
    TextView create_trip_textview;
    Button trip_start_button;
    Button trip_end_button;
    Button trip_create_button;
    Button trip_cancel_button;
    ListView friendlist;
    Calendar today;
    Calendar start_date;
    Calendar end_date;
    DatePickerDialog startdate_picker_dialog;
    DatePickerDialog enddate_picker_dialog;
    DatePickerDialog.OnDateSetListener startdate_picker_listener;
    DatePickerDialog.OnDateSetListener enddate_picker_listener;
    FriendAdapter_checkable friendAdapter_checkable;
    int friend_id;
    String friend_name;
    String friend_profile_image;
    String friend_image;
    String friend_gender;
    String friend_email;
    String friend_token;

    SimpleDateFormat sdf;
    SimpleDateFormat sdf_id;

    String start_date_sql;
    String end_date_sql;
    String create_date_sql;

    private NetworkService networkService;

    int user_id;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tripcreate);

        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();
        trip_create_button=(Button)findViewById(R.id.trip_create_button);
        trip_cancel_button=(Button)findViewById(R.id.trip_cancel_button);
        trip_start_date=(TextView)findViewById(R.id.trip_start_text);
        trip_end_date=(TextView)findViewById(R.id.trip_end_text);
        create_trip_textview=findViewById(R.id.create_trip_textview);
        trip_start_button=(Button)findViewById(R.id.tripstart_button);
        trip_start_button.setOnClickListener(this);
        trip_end_button=(Button)findViewById(R.id.tripend_button);
        trip_end_button.setOnClickListener(this);
        trip_subject_edit=findViewById(R.id.trip_subject);
        friendlist=findViewById(R.id.tripcreate_friendlist);
        friendAdapter_checkable=new FriendAdapter_checkable();

        intent=getIntent();
        user_id=intent.getIntExtra("user_id",0);
        flag=intent.getStringExtra("flag");
        Log.e("Mytag",flag);
        if(flag.equals("modify")) {
            modify_set();
        }



        today=new GregorianCalendar();
        start_date=new GregorianCalendar();
        end_date=new GregorianCalendar();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf_id=new SimpleDateFormat("yyyyMMddHHmmss");


        startdate_picker_listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                start_date.set(year,month,date);
                trip_start_date.setText(year+"-"+(month+1)+"-"+date);
                start_date_sql = sdf.format(start_date.getTime());
            }
        };
        enddate_picker_listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                Log.e("tag",year+"-"+month);
                end_date.set(year,month,date);
                trip_end_date.setText(year+"-"+(month+1)+"-"+date);
                end_date_sql = sdf.format(end_date.getTime());




            }
        };
        startdate_picker_dialog=new DatePickerDialog(TripcreateActivity.this,startdate_picker_listener, today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH));
        enddate_picker_dialog=new DatePickerDialog(TripcreateActivity.this,enddate_picker_listener, today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH));


        create_date_sql=sdf.format(today.getTime());
        trip_id=sdf_id.format(today.getTime())+String.valueOf(user_id);

        trip_create_button.setOnClickListener(this);
        trip_cancel_button.setOnClickListener(this);
        trip_start_date.setText(sdf.format(today.getTime()));
        trip_end_date.setText(sdf.format(today.getTime()));

        getFriend(user_id);


    }

    @Override
    public void onClick(View v){

        switch(v.getId()){
            case R.id.tripstart_button:
                startdate_picker_dialog.show();
                break;
            case R.id.tripend_button:
                enddate_picker_dialog.show();
                break;
            case R.id.trip_create_button:
                if(flag.equals("modify")) {
                    trip_id=intent.getStringExtra("trip_id");
                    trip_subject = trip_subject_edit.getText().toString();
                    modifyTrip(trip_id,trip_subject,start_date_sql,end_date_sql);
                    finish();
                }else {
                    trip_subject = trip_subject_edit.getText().toString();
                    trip_id = sdf_id.format(today.getTime()) + String.valueOf(user_id);
                    createTrip(trip_id, trip_subject, start_date_sql, end_date_sql, create_date_sql, user_id);
                    finish();
                }
            case R.id.trip_cancel_button:
                finish();
                break;
        }
    }


    public void createTrip(String trip_id, String subject,String start_date, String end_date, String create_date, int user_id){
        Trip trip=new Trip();
        trip.setTrip_id(trip_id);
        trip.setStart_date(start_date);
        trip.setEnd_date(end_date);
        trip.setSubject(subject);
        trip.setCreate_date(create_date);
        trip.setCreator_id(user_id);
        Call<Trip> createtrip=networkService.create_trip(trip);
        createtrip.enqueue(new Callback<Trip>(){
            @Override
            public void onResponse(Call<Trip> trip, Response<Trip> response){
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                }else{
                    int statusCode=response.code();
                    Log.d("MyTag(onResponse)","응답코드 : "+statusCode);
                }
            }
            @Override
            public void onFailure(Call<Trip> trip, Throwable t){
                Log.d("MyTag(onFailure)","응답코드 : "+t.getMessage());
            }
        });
    }

    public void modifyTrip(String trip_id, String subject,String start_date, String end_date){
        Trip trip=new Trip();
        trip.setTrip_id(trip_id);
        trip.setStart_date(start_date);
        trip.setEnd_date(end_date);
        trip.setSubject(subject);
        Call<Trip> modifytrip=networkService.modify_trip(trip);
        modifytrip.enqueue(new Callback<Trip>(){
            @Override
            public void onResponse(Call<Trip> trip, Response<Trip> response){
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                }else{
                    int statusCode=response.code();
                    Log.d("MyTag(onResponse)","응답코드 : "+statusCode);
                }
            }
            @Override
            public void onFailure(Call<Trip> trip, Throwable t){
                Log.d("MyTag(onFailure)","응답코드 : "+t.getMessage());
            }
        });
    }



    public void modify_set(){
            trip_create_button.setText("수 정");
            create_trip_textview.setText("여행 수정");
            trip_subject_edit.setText(intent.getStringExtra("subject"));
            trip_start_date.setText(intent.getStringExtra("start_date"));
            trip_end_date.setText(intent.getStringExtra("end_date"));
            start_date_sql=intent.getStringExtra("start_date");
            end_date_sql=intent.getStringExtra("end_date");
    }


    public void getFriend(int user_id){
        friendAdapter_checkable.clear();
        Call<List<User>> getfriend=networkService.find_friend(user_id);
        getfriend.enqueue(new Callback<List<User>>(){
            @Override
            public void onResponse(Call<List<User>> user, Response<List<User>> response){
                if(response.isSuccessful()){
                    List<User> friendList=response.body();
                    for(User frienditem : friendList){
                        friend_name=frienditem.getNickname();
                        friend_image=frienditem.getThumbnail_image();
                        friend_email=frienditem.getEmail();
                        friend_gender=frienditem.getGender();
                        friend_profile_image=frienditem.getProfile_image();
                        friend_token=frienditem.getToken();
                        friend_id=frienditem.getUser_id();
                        friendAdapter_checkable.addItem(frienditem);
                    }
                    friendAdapter_checkable.notifyDataSetChanged();
                    friendlist.setAdapter(friendAdapter_checkable);

                }else{
                    int statusCode=response.code();
                    Log.d("MyTag(onResponse)","응답코드 : "+statusCode);
                }
            }
            @Override
            public void onFailure(Call<List<User>> user, Throwable t){
                Log.d("MyTag(onFailure)","응답코드 : "+t.getMessage());
            }
        });
    }
}
