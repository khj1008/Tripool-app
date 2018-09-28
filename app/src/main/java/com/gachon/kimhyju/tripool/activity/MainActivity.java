package com.gachon.kimhyju.tripool.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.fragment.page_Caculate;
import com.gachon.kimhyju.tripool.fragment.page_Home;
import com.gachon.kimhyju.tripool.fragment.page_Map;
import com.gachon.kimhyju.tripool.fragment.page_Memo;
import com.gachon.kimhyju.tripool.fragment.page_Menu;
import com.gachon.kimhyju.tripool.object.Trip;
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

import java.net.URISyntaxException;

import io.socket.client.IO;
import me.majiajie.pagerbottomtabstrip.MaterialMode;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements page_Home.OnFragmentInteractionListener, page_Menu.OnFragmentInteractionListener, page_Memo.OnFragmentInteractionListener,
        page_Map.OnFragmentInteractionListener, page_Caculate.OnFragmentInteractionListener, View.OnClickListener, page_Map.onMarkerAddListener {
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private NetworkService networkService;
    private FloatingActionButton floatingAddButton;
    private io.socket.client.Socket socket;
    {
        try{
            socket = IO.socket("http://210.102.181.158:62005");
        }catch (URISyntaxException ue){
            ue.printStackTrace();
        }
    }

    int user_id;
    int selected_Page;
    double latitude;
    double longitude;
    String nickName;
    String profile_image;
    String thumbnail_image;
    Gender gender;
    String email;
    String token;
    Trip maintrip;
    String maintrip_Id;
    PageAdapter pageAdapter;



    NavigationController mNavigationController;
    int[] testColors = {0xFFF6511D, 0xFFFFB400, 0xFF00A6ED, 0xFF7FB800, 0xFF0D2C54};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //서버에서 데이터를 받아오기
        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();
        requestMe();
        updateToken(user_id,token);
        floatingAddButton=findViewById(R.id.floatingAddButton);
        floatingAddButton.setOnClickListener(this);

        //initUI();

        //네비게이션 탭 적용
        PageNavigationView pageBottomTabLayout =  findViewById(R.id.tab);
        pageAdapter=new PageAdapter(getSupportFragmentManager(),this);
        mNavigationController = pageBottomTabLayout.material()
                .addItem(R.drawable.home,"체크리스트",testColors[0])
                .addItem(R.drawable.coin, "정산내역",testColors[1])
                .addItem(R.drawable.map, "지도",testColors[2])
                .addItem(R.drawable.memo, "메모",testColors[3])
                .addItem(R.drawable.menu, "메뉴",testColors[4])
                .setDefaultColor(0x89FFFFFF)
                .setMode(MaterialMode.CHANGE_BACKGROUND_COLOR | MaterialMode.HIDE_TEXT)
                .build();
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(4);
        mNavigationController.setupWithViewPager(viewPager);
        mNavigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                selected_Page=index;
                Log.i("asd","selected: " + index + " old: " + old);
                if(index==4){
                    floatingAddButton.setVisibility(View.INVISIBLE);
                }else{
                    floatingAddButton.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onRepeat(int index) {
                Log.i("asd","onRepeat selected: " + index);
            }
        });



    }

    @Override
    public void onClick(View v){
        switch (selected_Page){
            case 0:
                Intent homeintent=new Intent(getApplicationContext(), AddChecklistActivity.class);
                homeintent.putExtra("user_Id",user_id);
                homeintent.putExtra("trip_Id",maintrip_Id);
                startActivity(homeintent);
                break;
            case 1:
                break;
            case 2:
                Intent mapintent=new Intent(getApplicationContext(),AddMarkerActivity.class);
                mapintent.putExtra("latitude",latitude);
                mapintent.putExtra("longitude",longitude);
                mapintent.putExtra("user_id",user_id);
                mapintent.putExtra("trip_id",maintrip_Id);
                mapintent.putExtra("user_nickName",nickName);
                startActivity(mapintent);
                break;
            case 3:
                break;
        }
    }


    //뒤로가기버튼을 연속으로 두번 눌렀을 때 앱이 종료되도록
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
                get_maintrip(user_id);
            }
        });
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





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_fab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.add_checklist:
                Toast.makeText(getApplicationContext(),"checklist clicked",Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        get_maintrip(user_id);
    }


    public void get_maintrip(int user_id){
        Call<Trip> getmaintrip=networkService.get_maintrip(user_id);
        getmaintrip.enqueue(new Callback<Trip>(){
            @Override
            public void onResponse(Call<Trip> trip, Response<Trip> response){
                if(response.isSuccessful()){
                    if(response==null){
                        Log.e("error","불러오지못함");
                        return;
                    }else {
                        maintrip = response.body();
                        maintrip_Id=maintrip.getTrip_id();
                    }
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

    @Override
    public void onMarkerAdd(double latitude, double longitude){

        Toast.makeText(getApplicationContext(),"latitude : "+latitude+"longitude : "+longitude,Toast.LENGTH_LONG).show();
    }




}

