package com.gachon.kimhyju.tripool.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.others.ApplicationController;
import com.gachon.kimhyju.tripool.others.NetworkService;

import java.net.URISyntaxException;

import io.socket.client.IO;

public class AddMarkerActivity extends Activity implements View.OnClickListener{
    private NetworkService networkService;
    double latitude;
    double longitude;
    int user_id;
    String nickName;
    String trip_id;


    private io.socket.client.Socket socket;
    {
        try{
            socket = IO.socket("http://210.102.181.158:62005");
        }catch (URISyntaxException ue){
            ue.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_marker);

        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();

        Intent intent=getIntent();
        latitude=intent.getDoubleExtra("latitude",0);
        longitude=intent.getDoubleExtra("longitude",0);
        user_id=intent.getIntExtra("user_id",0);
        nickName=intent.getStringExtra("user_nickName");
        trip_id=intent.getStringExtra("trip_id");
    }

    @Override
    public void onClick(View v){

    }
}
