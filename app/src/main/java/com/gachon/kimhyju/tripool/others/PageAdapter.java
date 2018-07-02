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
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Gender;
import com.kakao.util.helper.log.Logger;

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


    @Override
    public CharSequence getPageTitle(int position){
        return null;
        /*SpannableStringBuilder sb;
        ImageSpan span;
        switch(position){
            case 0:
                myDrawable = context.getResources().getDrawable(R.drawable.ic_home);
                sb = new SpannableStringBuilder("  Home"); // space added before text for convenience
                myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
                span = new ImageSpan(myDrawable);
                sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return sb;
            case 1:
                return "여행목록";
            case 2:
                return "친구목록";
            case 3:
                return "알림목록";
            case 4:
                return "환경설정";
            default:
                return null;
        }*/

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
}
