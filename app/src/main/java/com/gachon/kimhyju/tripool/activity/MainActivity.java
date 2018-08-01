package com.gachon.kimhyju.tripool.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements page_Home.OnFragmentInteractionListener, page_Menu.OnFragmentInteractionListener, page_Memo.OnFragmentInteractionListener,
        page_Map.OnFragmentInteractionListener, page_Caculate.OnFragmentInteractionListener {
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private NetworkService networkService;
    private com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton addButton;
    FloatingActionMenu actionMenu;
    int user_id;
    String nickName;
    String profile_image;
    String thumbnail_image;
    Gender gender;
    String email;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //서버에서 데이터를 받아오기
        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();
        requestMe();
        updateToken(user_id,token);
        final ImageView iconview=new ImageView(this);
        iconview.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_light));
        addButton=new FloatingActionButton.Builder(this).setContentView(iconview).build();
        SubActionButton.Builder subBuilder=new SubActionButton.Builder(this);
        ImageView listIcon=new ImageView(this);
        ImageView coinIcon=new ImageView(this);
        ImageView mapIcon=new ImageView(this);
        ImageView memoIcon=new ImageView(this);
        listIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_check));
        coinIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_coin));
        mapIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_map));
        memoIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_memo));
        actionMenu=new FloatingActionMenu.Builder(this)
                .addSubActionView(subBuilder.setContentView(listIcon).build())
                .addSubActionView(subBuilder.setContentView(coinIcon).build())
                .addSubActionView(subBuilder.setContentView(mapIcon).build())
                .addSubActionView(subBuilder.setContentView(memoIcon).build())
                .attachTo(addButton).build();
        actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                iconview.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(iconview, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                iconview.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(iconview, pvhR);
                animation.start();
            }
        });

        initUI();
                
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


    private void initUI(){
        PageAdapter pageAdapter=new PageAdapter(getSupportFragmentManager(),this);
        ViewPager viewPager=findViewById(R.id.view_pager);
        viewPager.setAdapter(pageAdapter);
        final String[] colors = getResources().getStringArray(R.array.vertical_ntb);
        final NavigationTabBar mTab=findViewById(R.id.tabs);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.home),
                        Color.parseColor(colors[0]))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.coin),
                        Color.parseColor(colors[1]))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.map),
                        Color.parseColor(colors[2]))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.memo),
                        Color.parseColor(colors[3]))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.menu),
                        Color.parseColor(colors[4]))
                        .build()
        );
        mTab.setModels(models);
        mTab.setViewPager(viewPager, 0);
        mTab.setBehaviorEnabled(true);
        mTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
            }

            //페이지로 넘어갔을 때 뱃지가 사라짐
            @Override
            public void onPageSelected(final int position) {
                mTab.getModels().get(position).hideBadge();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
            }
        });

        //기능마다 이벤트가 발생했을때 뱃지 표시
        /*
        mTab.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mTab.getModels().size(); i++) {
                    final NavigationTabBar.Model model = mTab.getModels().get(i);
                    mTab.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, i * 100);
                }
            }
        }, 500);
        */

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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

