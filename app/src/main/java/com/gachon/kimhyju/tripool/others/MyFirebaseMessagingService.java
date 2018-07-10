package com.gachon.kimhyju.tripool.others;

import android.content.Intent;
import android.util.Log;

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
       /*
        type=Integer.valueOf(remoteMessage.getData().get("type"));
        user_id=Integer.valueOf(remoteMessage.getData().get("user_id"));
        nickName=remoteMessage.getData().get("nickName");
        email=remoteMessage.getData().get("email");
        thumbnail_image=remoteMessage.getData().get("thumbnail_image");
        */
        Log.d(TAG,"from:"+from);
        Log.d(TAG,notification_title);
        Log.d(TAG,notification_body);

        //알림 클릭시 이벤트(백그라운드에서 액티비티 실행시 앱이 Resume되면서 수락 액티비티 실행 or 백그라운드에서 액티비티 미 실행시 수락 액티비티만 실행)
        String noti_click=remoteMessage.getNotification().getClickAction();

        if(noti_click.equals("FRIEND_REQUEST")) {
            Intent click_intent = new Intent(noti_click);
            click_intent.putExtra("friend_id", Integer.valueOf(remoteMessage.getData().get("user_id")));
            click_intent.putExtra("nickName", remoteMessage.getData().get("nickName"));
            click_intent.putExtra("email", remoteMessage.getData().get("email"));
            click_intent.putExtra("thumbnail_image", remoteMessage.getData().get("thumbnail_image"));
            startActivity(click_intent);
        }else if(noti_click.equals("TRIP_INVITE")){
            Intent click_intent = new Intent(noti_click);
            click_intent.putExtra("trip_subject", remoteMessage.getNotification().getTitle());
            click_intent.putExtra("trip_date", remoteMessage.getData().get("date"));
            click_intent.putExtra("trip_id", remoteMessage.getData().get("trip_id"));
            click_intent.putExtra("nickName", remoteMessage.getData().get("nickName"));
            click_intent.putExtra("thumbnail_image", remoteMessage.getData().get("thumbnail_image"));
            startActivity(click_intent);
        }



    }


}
