package com.gachon.kimhyju.tripool.others;

import android.app.Activity;

import com.kakao.auth.KakaoSDK;

public class GlobalApplication extends ApplicationController {
    private static volatile GlobalApplication instance = null;
    private static volatile Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static GlobalApplication getGlobalApplicationContext(){
        return instance;
    }

    public static void setCurrentActivity(Activity currentActivity){
        GlobalApplication.currentActivity = currentActivity;
    }

    public static Activity getCurrentActivity(){
        return currentActivity;
    }



}

