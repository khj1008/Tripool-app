package com.gachon.kimhyju.tripool.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.object.Trip;
import com.gachon.kimhyju.tripool.others.ApplicationController;
import com.gachon.kimhyju.tripool.others.NetworkService;
import com.gachon.kimhyju.tripool.others.TripAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TriplistActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    int user_id, creator_id;
    String trip_id;
    String date_s, date_e;
    String trip_subject;
    Date date;
    Calendar cal;

    ListView listView;
    TripAdapter tripAdapter;
    private NetworkService networkService;
    SimpleDateFormat sdf1;
    SimpleDateFormat sdf2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triplist);

        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addTripButton);
        Intent intent= getIntent();
        user_id=intent.getIntExtra("user_id",0);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TriplistActivity.this,TripcreateActivity.class);
                intent.putExtra("user_id",user_id);
                intent.putExtra("flag","create");
                startActivity(intent);
            }
        });
        sdf1=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf2=new SimpleDateFormat("yyyy-MM-dd");
        cal=new GregorianCalendar();
        listView=findViewById(R.id.triplist_view);
        listView.setOnItemLongClickListener(this);
        tripAdapter=new TripAdapter(getApplicationContext());

    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };




    @Override
    public void onResume(){
        super.onResume();
        getTrip(user_id);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView,View view,int position,long id){
    Trip trip=(Trip)tripAdapter.getItem(position);
    trip_id=trip.getTrip_id();
    creator_id=trip.getCreator_id();
    trip_subject=trip.getSubject();
    date_s=trip.getStart_date();
    date_e=trip.getEnd_date();
        PopupMenu popup=new PopupMenu(TriplistActivity.this,view);
        getMenuInflater().inflate(R.menu.popupmenu,popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_modify:
                        if(user_id==creator_id) {
                            Intent intent = new Intent(TriplistActivity.this, TripcreateActivity.class);
                            intent.putExtra("flag", "modify");
                            intent.putExtra("trip_id",trip_id);
                            intent.putExtra("subject",trip_subject);
                            intent.putExtra("start_date",date_s);
                            intent.putExtra("end_date",date_e);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(),"여행 생성자만 여행을 수정할 수 있습니다.",Toast.LENGTH_LONG).show();
                        }

                        break;
                    case R.id.action_delete:
                        showDialog();
                        break;
                }
                return false;
            }
        });
        popup.show();

    return true;
    }


    public void deleteTrip(String trip_id,int user_id){
        Call<Trip> deletetrip=networkService.delete_trip(trip_id,user_id);
        deletetrip.enqueue(new Callback<Trip>(){
            @Override
            public void onResponse(Call<Trip> trip, Response<Trip> response){
                if(response.isSuccessful()){

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


    public void getTrip(int user_id){
        tripAdapter.clear();
        Call<List<Trip>> gettrip=networkService.get_trip(user_id);
        gettrip.enqueue(new Callback<List<Trip>>(){
            @Override
            public void onResponse(Call<List<Trip>> trip, Response<List<Trip>> response){
                if(response.isSuccessful()){
                    List<Trip> tripList=response.body();
                    for(Trip tripitem : tripList){
                        date_s=tripitem.getStart_date();
                        date_e=tripitem.getEnd_date();
                        try {
                            date=sdf1.parse(date_s);
                            cal.setTime(date);
                            cal.add(Calendar.DAY_OF_MONTH,1);
                            date=cal.getTime();
                            tripitem.setStart_date(sdf2.format(date));
                            date=sdf1.parse(date_e);
                            cal.setTime(date);
                            cal.add(Calendar.DAY_OF_MONTH,1);
                            date=cal.getTime();
                            tripitem.setEnd_date(sdf2.format(date));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        tripAdapter.addItem(tripitem);
                    }
                    tripAdapter.notifyDataSetChanged();
                    listView.setAdapter(tripAdapter);

                }else{
                    int statusCode=response.code();
                    Log.d("MyTag(onResponse)","응답코드 : "+statusCode);
                }
            }
            @Override
            public void onFailure(Call<List<Trip>> trip, Throwable t){
                Log.d("MyTag(onFailure)","응답코드 : "+t.getMessage());
            }
        });
    }

    public void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("여행 나가기");
        builder.setMessage("정말로 여행에서 나가시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteTrip(trip_id,user_id);
                getTrip(user_id);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

}
