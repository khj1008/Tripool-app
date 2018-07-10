package com.gachon.kimhyju.tripool.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendfindActivity extends Activity implements View.OnClickListener{
    private NetworkService networkService;
    LinearLayout findfriend_layout;
    RelativeLayout confirmfriend_layout;
    EditText findfriend_email;
    ImageView findfriend_image;
    TextView findfriend_nickname;
    Button findFriend_ok;
    Button findFriend_cancel;
    Boolean flag;
    String friend_email;
    User friend;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendfind);

        requestMe();

        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();

        findfriend_layout=findViewById(R.id.findfriend_layout);
        findfriend_layout.setVisibility(View.VISIBLE);
        confirmfriend_layout=findViewById(R.id.confirmfriend_layout);
        confirmfriend_layout.setVisibility(View.INVISIBLE);
        findfriend_email=findViewById(R.id.findfriend_email);
        findfriend_image=findViewById(R.id.findfriend_image);
        findfriend_nickname=findViewById(R.id.findfriend_nickname);
        findFriend_ok=findViewById(R.id.findFriend_ok);
        findFriend_cancel=findViewById(R.id.findFriend_cancel);
        findFriend_ok.setOnClickListener(this);
        findFriend_cancel.setOnClickListener(this);
        flag=false;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.findFriend_ok:
                if(flag==false){

                    find_Friend();
                }else{
                    request_Friend();
                    finish();
                }
                break;
            case R.id.findFriend_cancel:
                finish();
                break;
        }
    }

    public void find_Friend(){
        friend_email=findfriend_email.getText().toString();
        findUser(friend_email);
    }

    public void findUser(String email){
        Call<User> finduser=networkService.find_User(email);
        finduser.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> user, Response<User> response){
                if(response.isSuccessful()){
                    if(response==null){
                        flag=false;
                        //Toast.makeText(getApplicationContext(),"Email을 잘못 입력하셨습니다.",Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        friend = response.body();
                        findfriend_layout.setVisibility(View.INVISIBLE);
                        confirmfriend_layout.setVisibility(View.VISIBLE);
                        findfriend_nickname.setText(friend.getNickname());
                        MultiTransformation mul=new MultiTransformation(new CircleCrop(),new CenterCrop());
                        Glide.with(getApplicationContext()).load(friend.getThumbnail_image()).apply(RequestOptions.bitmapTransform(mul)).into(findfriend_image);
                        findFriend_ok.setText("친구 요청");
                        flag=true;
                    }
                }else{
                    int statusCode=response.code();
                    Log.d("MyTag(onResponse)","응답코드 : "+statusCode);
                }
            }
            @Override
            public void onFailure(Call<User> trip, Throwable t){
                Log.d("MyTag(onFailure)","응답코드 : "+t.getMessage());
                Toast.makeText(getApplicationContext(),"Email을 잘못 입력하셨습니다.", Toast.LENGTH_LONG).show();
            }
        });
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


    public void request_Friend(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // FMC 메시지 생성 start

                    JSONObject notification = new JSONObject();
                    notification.put("title","친구요청");
                    notification.put("body",nickName+"님으로부터 친구요청이 왔습니다!");
                    notification.put("click_action","FRIEND_REQUEST");


                    JSONObject data=new JSONObject();
                    data.put("type","1");
                    data.put("user_id",user_id);
                    data.put("nickName",nickName);
                    data.put("email",email);
                    data.put("thumbnail_image",thumbnail_image);



                    JSONObject requestData=new JSONObject();
                    requestData.put("to",friend.getToken());

                    requestData.put("notification",notification);
                    requestData.put("data",data);
                    Log.e("MyTag",friend.getToken());



                    // FMC 메시지 생성 end

                    URL Url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                    conn.setDoOutput(true);
                    conn.addRequestProperty("Authorization", "key=" + getString(R.string.server_key));
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-type", "application/json");
                    conn.setRequestMethod("POST");
                    conn.connect();

                    OutputStream os = conn.getOutputStream();
                    os.write(requestData.toString().getBytes("UTF-8"));
                    os.close();
                    conn.getResponseCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
