package com.gachon.kimhyju.tripool.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.object.Checklist;
import com.gachon.kimhyju.tripool.others.ApplicationController;
import com.gachon.kimhyju.tripool.others.NetworkService;

import java.net.URISyntaxException;

import io.socket.client.IO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddChecklistActivity extends Activity implements View.OnClickListener{
    private NetworkService networkService;

    Button Addchecklist_Ok;
    Button Addchecklist_Cancel;
    EditText Addchekclist_Text;
    String checklist_Name;
    String trip_Id;
    int user_Id;

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
        setContentView(R.layout.activity_addchecklist);

        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();

        Intent intent=getIntent();
        trip_Id=intent.getStringExtra("trip_Id");
        user_Id=intent.getIntExtra("user_Id",0);

        Addchecklist_Ok=findViewById(R.id.Addchecklist_Ok);
        Addchecklist_Cancel=findViewById(R.id.Addchecklist_Cancel);
        Addchekclist_Text=findViewById(R.id.Addchecklist_Text);
        Addchecklist_Ok.setOnClickListener(this);
        Addchecklist_Cancel.setOnClickListener(this);
        socket.connect();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.Addchecklist_Ok:
                checklist_Name=Addchekclist_Text.getText().toString();
                addChecklist(checklist_Name,trip_Id,user_Id);
                Log.e("Checklist",checklist_Name+","+trip_Id+","+user_Id);
                finish();
                break;
            case R.id.Addchecklist_Cancel:
                finish();
                break;
        }
    }

    public void addChecklist(String checklist_Name, String trip_id, int user_id){
        Checklist checklist=new Checklist(checklist_Name,trip_id,user_id);
        Call<Checklist> addchecklist=networkService.add_checklist(checklist);
        addchecklist.enqueue(new Callback<Checklist>(){
            @Override
            public void onResponse(Call<Checklist> checklist, Response<Checklist> response){
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                }else{
                    int statusCode=response.code();
                    Log.d("MyTag(onResponse)","응답코드 : "+statusCode);
                }
            }
            @Override
            public void onFailure(Call<Checklist> checklist, Throwable t){
                Log.d("MyTag(onFailure)","응답코드 : "+t.getMessage());
            }
        });
        socket.emit("checklistUpdate");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        socket.disconnect();
    }
}
