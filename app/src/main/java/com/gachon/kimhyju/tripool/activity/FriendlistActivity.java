package com.gachon.kimhyju.tripool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.object.User;
import com.gachon.kimhyju.tripool.others.ApplicationController;
import com.gachon.kimhyju.tripool.others.FriendAdapter;
import com.gachon.kimhyju.tripool.others.NetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendlistActivity extends AppCompatActivity {
    private NetworkService networkService;
    FriendAdapter friendAdapter;
    int user_id;
    int friend_id;
    String friend_name;
    String friend_profile_image;
    String friend_image;
    String friend_gender;
    String friend_email;
    String friend_token;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();

        listView=findViewById(R.id.friendlist_view);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addFriendButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FriendlistActivity.this,FriendfindActivity.class);
                startActivity(intent);
            }
        });

        Intent intent= getIntent();
        user_id=intent.getIntExtra("user_id",0);
        friendAdapter=new FriendAdapter(getApplicationContext());
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };


    public void getFriend(int user_id){
        friendAdapter.clear();
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
                        friendAdapter.addItem(frienditem);
                    }
                    friendAdapter.notifyDataSetChanged();
                    listView.setAdapter(friendAdapter);

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


    public void onResume(){
        super.onResume();
        getFriend(user_id);
    }
}
