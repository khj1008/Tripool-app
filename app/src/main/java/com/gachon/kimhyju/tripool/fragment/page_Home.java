package com.gachon.kimhyju.tripool.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gachon.kimhyju.tripool.R;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link page_Home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link page_Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class page_Home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EditText send_text;
    TextView receive_text;
    Button send_button;
    RequestQueue queue;
    Context context;
    String regId;

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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_page__home,container,false);
        send_text=view.findViewById(R.id.send_text);
        receive_text=view.findViewById(R.id.receive_text);
        send_button=view.findViewById(R.id.send_button);
        queue= Volley.newRequestQueue(context.getApplicationContext());
        getRegistrationId();
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(send_text.getText().toString());
            }
        });
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


    public void getRegistrationId(){
        regId= FirebaseInstanceId.getInstance().getToken();

    }

    public void send(String input){
        JSONObject requestData=new JSONObject();
        try{
            JSONObject notification = new JSONObject();
            notification.put("title","test");
            notification.put("body","test message");

            JSONObject data=new JSONObject();
            data.put("memo",input);

            JSONArray idArray=new JSONArray();
            idArray.put(0,regId);
            Log.e("MyMS",regId);

            requestData.put("notification",notification);
            requestData.put("data",data);
            requestData.put("priority","high");
            requestData.put("registration_ids",idArray);
        }catch (Exception e){
            e.printStackTrace();
        }

        sendData(requestData, new SendResponseListener() {
            @Override
            public void onRequestStarted() {
                Log.d("MyMS","onRequestCompleted() called");
            }

            @Override
            public void onRequestCompleted() {
                Log.d("MyMS","onRequestStarted() called");
            }

            @Override
            public void onRequestWithError(VolleyError error) {
                Log.d("MyMS","onRequestWithError() called");
            }
        });
    }

    public interface SendResponseListener{
        public void onRequestStarted();
        public void onRequestCompleted();
        public void onRequestWithError(VolleyError error);
    }
    public void sendData(JSONObject requestData, final SendResponseListener listener){
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequestCompleted();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onRequestWithError(error);
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<String, String>();
                headers.put("Authorization",getText(R.string.server_key).toString());
                return headers;
            }
            @Override
            public String getBodyContentType(){
                return "application/json";
            }
        };
        request.setShouldCache(false);
        listener.onRequestStarted();
        queue.add(request);


    }
}
