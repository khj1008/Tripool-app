package com.gachon.kimhyju.tripool.others;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.gachon.kimhyju.tripool.fragment.page_Caculate;
import com.gachon.kimhyju.tripool.fragment.page_Home;
import com.gachon.kimhyju.tripool.fragment.page_Map;
import com.gachon.kimhyju.tripool.fragment.page_Memo;
import com.gachon.kimhyju.tripool.fragment.page_Menu;
import com.gachon.kimhyju.tripool.object.Trip;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Gender;
import com.kakao.util.helper.log.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PageAdapter extends FragmentPagerAdapter {
    private static int PAGE_NUMBER=5;
    Context context;
    Drawable myDrawable;
    String title;
    Bundle bundle;
    int user_id;
    String nickName;
    String profile_image;
    String thumbnail_image;
    Gender gender;
    String email;
    Trip maintrip;
    String maintrip_Id;
    private NetworkService networkService;
    public PageAdapter(FragmentManager fm, Context mcontext){
        super(fm);
        requestMe();
        bundle=new Bundle();
        bundle.putInt("user_id",user_id);
        bundle.putString("nickName",nickName);
        bundle.putString("profile_image",profile_image);
        bundle.putString("thumbnail_image",thumbnail_image);
        bundle.putString("gender",String.valueOf(gender));
        bundle.putString("email",email);
        context=mcontext;

        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();
    }



    @Override
    public Fragment getItem(int position){
        Fragment fm;
        switch(position){
            case 0:
                fm=page_Home.newInstance();
                fm.setArguments(bundle);
                return fm;
            case 1:
                return page_Caculate.newInstance();
            case 2:
                return page_Map.newInstance();
            case 3:
                return page_Memo.newInstance();
            case 4:
                fm=page_Menu.newInstance();
                fm.setArguments(bundle);
                return fm;
            default:
                return null;
        }
    }


    @Override
    public int getCount(){
        return PAGE_NUMBER;
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
            }
        });
    }

    public void get_maintrip(int user_id){
        maintrip=new Trip();
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
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }
}
