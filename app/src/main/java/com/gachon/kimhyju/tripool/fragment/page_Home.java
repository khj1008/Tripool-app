package com.gachon.kimhyju.tripool.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gachon.kimhyju.tripool.R;
import com.gachon.kimhyju.tripool.object.Checklist;
import com.gachon.kimhyju.tripool.object.Trip;
import com.gachon.kimhyju.tripool.others.ApplicationController;
import com.gachon.kimhyju.tripool.others.ChecklistAdapter;
import com.gachon.kimhyju.tripool.others.NetworkService;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import java.net.URISyntaxException;
import java.util.List;

import io.socket.client.IO;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link page_Home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link page_Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class page_Home extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Context context;
    ListView checklist_view;
    private NetworkService networkService;
    ChecklistAdapter checklistAdapter;
    Trip maintrip;
    String maintrip_Id;
    int user_id;

    private io.socket.client.Socket socket;
    {
        try{
            socket = IO.socket("http://210.102.181.158:62005");
        }catch (URISyntaxException ue){
            ue.printStackTrace();
        }
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public page_Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment page_Home.
     */
    // TODO: Rename and change types and number of parameters
    public static page_Home newInstance() {
        page_Home fragment = new page_Home();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            maintrip_Id = getArguments().getString("trip_id");
           user_id = getArguments().getInt("user_id");
        }
        ApplicationController application=ApplicationController.getInstance();
        application.buildNetworkService("210.102.181.158",62005);
        networkService= ApplicationController.getInstance().getNetworkService();
        socket.on("checklistUpdate",checklistUpdate);
        socket.connect();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_page__home,container,false);
        Log.e("user_id in page_Home",String.valueOf(user_id));
        checklistAdapter=new ChecklistAdapter(getContext());
        checklist_view=view.findViewById(R.id.checklist);
        requestMe();
        return view;
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
        this.context=context;
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

        //socket.disconnect();
        //socket.off("checklistUpdate", checklistUpdate);
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

    ///체크리스트에 변경이 발생했을때 업데이트
    private Emitter.Listener checklistUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("socket.connected","checklistUpdate");
            getChecklist(maintrip_Id);
        }
    };

    public void getChecklist(String trip_id){
        checklistAdapter.clear();
        Call<List<Checklist>> getchecklist=networkService.get_checklist(trip_id);
        getchecklist.enqueue(new Callback<List<Checklist>>(){
            @Override
            public void onResponse(Call<List<Checklist>> checklist, Response<List<Checklist>> response){
                if(response.isSuccessful()){
                    List<Checklist> checkList=response.body();
                    for(Checklist checklistitem : checkList){
                        checklistAdapter.addItem(checklistitem);
                    }
                    checklist_view.setAdapter(checklistAdapter);
                    checklistAdapter.notifyDataSetChanged();

                }else{
                    int statusCode=response.code();
                    Log.d("MyTag(onResponse)","응답코드 : "+statusCode);
                }

            }
            @Override
            public void onFailure(Call<List<Checklist>> checklist, Throwable t){
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
                get_maintrip(user_id);
            }
        });
    }

    public void get_maintrip(int user_id){
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
                    getChecklist(maintrip_Id);
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
    public void onResume(){
        super.onResume();
        get_maintrip(user_id);
    }

}





