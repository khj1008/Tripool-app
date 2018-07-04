package com.gachon.kimhyju.tripool.others;

import android.content.Intent;
import android.util.Log;

import com.gachon.kimhyju.tripool.activity.FriendacceptActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG="MyMS";
    String nickName, email, thumbnail_image;
    int type, user_id;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){

        Log.d(TAG,"onMessageReceived() called");
        String from = remoteMessage.getFrom();
        String notification_title=remoteMessage.getNotification().getTitle();
        String notification_body=remoteMessage.getNotification().getBody();
        type=Integer.valueOf(remoteMessage.getData().get("type"));
        user_id=Integer.valueOf(remoteMessage.getData().get("user_id"));
        nickName=remoteMessage.getData().get("nickName");
        email=remoteMessage.getData().get("email");
        thumbnail_image=remoteMessage.getData().get("thumbnail_image");
        Log.d(TAG,"from:"+from);
        Log.d(TAG,notification_title);
        Log.d(TAG,notification_body);
        String noti_clcik=remoteMessage.getNotification().getClickAction();
        Intent click_intent=new Intent(noti_clcik);
        click_intent.putExtra("action","request_friend");
        click_intent.putExtra("friend_id",user_id);
        click_intent.putExtra("nickName",nickName);
        click_intent.putExtra("email",email);
        click_intent.putExtra("thumbnail_image",thumbnail_image);


        switch(type){
            case 1:
                Intent intent=new Intent(getApplicationContext(), FriendacceptActivity.class);
                intent.putExtra("friend_id",user_id);
                intent.putExtra("nickName",nickName);
                intent.putExtra("email",email);
                intent.putExtra("thumbnail_image",thumbnail_image);
                startActivity(intent);
        }

    }


}
