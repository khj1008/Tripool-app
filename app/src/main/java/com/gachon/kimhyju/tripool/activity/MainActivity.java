package com.gachon.kimhyju.tripool.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.fragment.page_Caculate;
import com.gachon.kimhyju.tripool.fragment.page_Home;
import com.gachon.kimhyju.tripool.fragment.page_Map;
import com.gachon.kimhyju.tripool.fragment.page_Memo;
import com.gachon.kimhyju.tripool.fragment.page_Menu;
import com.gachon.kimhyju.tripool.object.User;
import com.gachon.kimhyju.tripool.others.ApplicationController;
import com.gachon.kimhyju.tripool.others.NetworkService;
import com.gachon.kimhyju.tripool.others.PageAdapter;
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

public class MainActivity extends AppCompatActivity implements page_Home.OnFragmentInteractionListener, page_Menu.OnFragmentInteractionListener, page_Memo.OnFragmentInteractionListener,
        page_Map.OnFragmentInteractionListener, page_Caculate.OnFragmentInteractionListener {
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private NetworkService networkService;
    int user_id;
    String nickName;
    String profile_image;
    String thumbnail_image;
    Gender gender;
    String email;
    String token;
    String action;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //서버에서 데이터를 받아오기
        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();

        //친구요청 알림 클릭시 넘어오는 intent처리
        Intent intent=getIntent();
        action=intent.getStringExtra("action");
        if (action.equals("request_friend")) {
            Intent friendAccept=new Intent(getApplicationContext(),FriendacceptActivity.class);
            friendAccept.putExtra("friend_id",user_id);
            friendAccept.putExtra("nickName",nickName);
            friendAccept.putExtra("email",email);
            friendAccept.putExtra("thumbnail_image",thumbnail_image);
            startActivity(friendAccept);
        }


        PageAdapter pageAdapter=new PageAdapter(getSupportFragmentManager(),this);
        ViewPager viewPager=(ViewPager)findViewById(R.id.view_pager);
        viewPager.setAdapter(pageAdapter);
        TabLayout mTab=(TabLayout)findViewById(R.id.tabs);
        mTab.setupWithViewPager(viewPager);
        requestMe();
        updateToken(user_id,token);
        int[] icons={
                R.drawable.ic_home,
                R.drawable.ic_coin,
                R.drawable.ic_map,
                R.drawable.ic_memo,
                R.drawable.ic_menu
        };
        for(int i=0; i<mTab.getTabCount(); i++){
            mTab.getTabAt(i).setIcon(icons[i]);
        }
                
    }



    @Override
    public void onBackPressed(){
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
            finish();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onDestroy(){
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
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

    @Override
    protected void onNewIntent(Intent intent){
        Log.d("MyMS","onNewIntent() called");
        super.onNewIntent(intent);
        page_Home fm = new page_Home();
        String from=intent.getStringExtra("from");
        String contents=intent.getStringExtra("contents");
        Toast.makeText(getApplicationContext(),contents,Toast.LENGTH_LONG).show();
    }

    public void updateToken(int user_id, String token){
        User user=new User();
        user.setUser_id(user_id);
        user.setToken(token);
        Call<User> updatetoken=networkService.update_token(user);
        updatetoken.enqueue(new Callback<User>(){
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
