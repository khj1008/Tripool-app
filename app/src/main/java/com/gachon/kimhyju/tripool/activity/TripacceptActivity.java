package com.gachon.kimhyju.tripool.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.object.Trip;
import com.gachon.kimhyju.tripool.others.ApplicationController;
import com.gachon.kimhyju.tripool.others.NetworkService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Gender;
import com.kakao.util.helper.log.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripacceptActivity extends Activity implements View.OnClickListener{
    private NetworkService networkService;
    int user_id;
    String nickName;
    String profile_image;
    String thumbnail_image;
    Gender gender;
    String email;
    String token;

    TextView trip_subject_textView;
    TextView trip_date_textView;
    ImageView friend_image_imageView;
    TextView friend_nickName_textView;
    Button trip_accept_join;
    Button trip_accept_reject;

    String trip_subject;
    String trip_date;
    String trip_id;
    String friend_nickName;
    String friend_thumbnail_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tripaccept);

        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();
        requestMe();

        trip_date_textView=findViewById(R.id.trip_accept_date);
        trip_subject_textView=findViewById(R.id.trip_accept_subject);
        friend_image_imageView=findViewById(R.id.trip_accept_image);
        friend_nickName_textView=findViewById(R.id.trip_accept_name);
        trip_accept_join=findViewById(R.id.trip_accept_join);
        trip_accept_reject=findViewById(R.id.trip_accept_reject);
        trip_accept_join.setOnClickListener(this);
        trip_accept_reject.setOnClickListener(this);

        Intent intent=getIntent();
        trip_subject=intent.getStringExtra("trip_subject");
        trip_date=intent.getStringExtra("trip_date");
        trip_id=intent.getStringExtra("trip_id");
        friend_nickName=intent.getStringExtra("nickName");
        friend_thumbnail_image=intent.getStringExtra("thumbnail_image");



    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.trip_accept_join:
                joinTrip(trip_id,user_id);
                finish();
                break;
            case R.id.trip_accept_reject:
                finish();
                break;
        }
    }

    private void requestMe(){
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("error", "Session Closed Error is " + errorResult.toString());
            }
            @Override
            public void onSuccess(MeV2Response result) {
                user_id=(int)result.getId();
                nickName=result.getNickname();
                profile_image=result.getProfileImagePath();
                thumbnail_image=result.getThumbnailImagePath();
                gender=result.getKakaoAccount().getGender();
                email=result.getKakaoAccount().getEmail();
                token= FirebaseInstanceId.getInstance().getToken();
            }
        });
    }

    public void joinTrip(String trip_id,int user_id){
        Call<Trip> jointrip=networkService.join_trip(trip_id,user_id);
        jointrip.enqueue(new Callback<Trip>(){
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
            public void onFailure(Call<Trip> user, Throwable t){
                Log.d("MyTag(onFailure)","응답코드 : "+t.getMessage());
            }
        });
    }
}
