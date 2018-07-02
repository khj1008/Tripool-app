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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.object.User;
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

public class FriendacceptActivity extends Activity implements View.OnClickListener{
    private NetworkService networkService;
    int user_id;
    String nickName;
    String profile_image;
    String thumbnail_image;
    Gender gender;
    String email;
    String token;
    String friend_email;
    int friend_user_id;
    String friend_thumbnail_image;
    String friend_nickName;

    TextView acceptfriend_text;
    ImageView acceptfriend_image;
    Button acceptfriend_ok;
    Button acceptfriend_reject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendaccept);

        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();
        requestMe();

        acceptfriend_text=findViewById(R.id.acceptfriend_text);
        acceptfriend_image=findViewById(R.id.acceptfriend_image);
        acceptfriend_ok=findViewById(R.id.acceptFriend_ok);
        acceptfriend_reject=findViewById(R.id.acceptFriend_reject);

        Intent intent=getIntent();
        friend_user_id=intent.getIntExtra("friend_id",0);
        friend_email=intent.getStringExtra("email");
        friend_thumbnail_image=intent.getStringExtra("thumbnail_image");
        friend_nickName=intent.getStringExtra("nickName");


        MultiTransformation mul=new MultiTransformation(new CircleCrop(),new CenterCrop());
        Glide.with(getApplicationContext()).load(friend_thumbnail_image).apply(RequestOptions.bitmapTransform(mul)).into(acceptfriend_image);
        acceptfriend_text.setText(friend_nickName+"님이 친구요청을 보냈습니다!!");

        acceptfriend_ok.setOnClickListener(this);
        acceptfriend_reject.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.acceptFriend_ok:
                addFriend(user_id,friend_user_id);
                finish();
                break;
            case R.id.acceptFriend_reject:
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

    public void addFriend(int user_id,int friend_id){
        Call<User> addfriend=networkService.add_friend(user_id,friend_id);
        addfriend.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> user, Response<User> response){
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                }else{
                    int statusCode=response.code();
                    Log.d("MyTag(onResponse)","응답코드 : "+statusCode);
                }
            }
            @Override
            public void onFailure(Call<User> user, Throwable t){
                Log.d("MyTag(onFailure)","응답코드 : "+t.getMessage());
            }
        });
    }
}
