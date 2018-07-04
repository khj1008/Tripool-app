package com.gachon.kimhyju.tripool.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.activity.FriendlistActivity;
import com.gachon.kimhyju.tripool.activity.TriplistActivity;
import com.gachon.kimhyju.tripool.object.User;
import com.gachon.kimhyju.tripool.others.ApplicationController;
import com.gachon.kimhyju.tripool.others.NetworkService;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link page_Menu.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link page_Menu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class page_Menu extends Fragment implements View.OnClickListener {
    Context mcontext;
    int user_id;
    private NetworkService networkService;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button button;
    ImageButton triplistButton;
    ImageButton friendlistButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public page_Menu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment page_Settings.
     */
    // TODO: Rename and change types and number of parameters
    public static page_Menu newInstance() {
        page_Menu fragment = new page_Menu();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();
        user_id=getArguments().getInt("user_id");
        requestMe();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_page__menu,container,false);
        button=(Button)view.findViewById(R.id.button);
        triplistButton=view.findViewById(R.id.triplistButton);
        triplistButton.setOnClickListener(this);
        friendlistButton=view.findViewById(R.id.friendlistButton);
        friendlistButton.setOnClickListener(this);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.button:
                onClickUnlink();
                break;
            case R.id.triplistButton:
                Intent tripintent=new Intent(mcontext,TriplistActivity.class);
                tripintent.putExtra("user_id",user_id);
                startActivity(tripintent);
                break;
            case R.id.friendlistButton:
                Intent friendintent=new Intent(mcontext,FriendlistActivity.class);
                friendintent.putExtra("user_id",user_id);
                startActivity(friendintent);
                break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext=context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void onClickUnlink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(mcontext)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                            deleteUser(user_id);
                                    }
                                });
                                dialog.dismiss();
                                //getActivity().finish();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

    }

    public void deleteUser(int user_id){
        Call<User> insertuser=networkService.delete_User(user_id);
        insertuser.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> user, Response<User> response){
                if(response.isSuccessful()){
                    Toast.makeText(mcontext.getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
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

            }
        });
    }




}
