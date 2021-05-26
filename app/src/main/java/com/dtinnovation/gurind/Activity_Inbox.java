package com.dtinnovation.gurind;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dtinnovation.gurind.adapters.CustomListAdapter;
import com.dtinnovation.gurind.models.MessageInfo;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Activity_Inbox extends Fragment {

    Flashbar flashbar;
    private Vibrator vibrator;
    private ProgressDialog progressDialog;
    View view;
    private Animation smallToBig, slideUpLate, shake;
    private ArrayList<MessageInfo> messageInfos;
    private CustomListAdapter customListAdapter;
    private ListView customListView;
    private ArrayList<Integer> id;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_inbox, container, false);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        smallToBig = AnimationUtils.loadAnimation(view.getContext(), R.anim.smalltobig);
        slideUpLate = AnimationUtils.loadAnimation(view.getContext(), R.anim.btta2);
        shake = AnimationUtils.loadAnimation(view.getContext(), R.anim.anim_shake_edit_text);
        id = new ArrayList<>();

        customListView= view.findViewById(R.id.custom_list_view);
        messageInfos = new ArrayList<>();
        customListAdapter = new CustomListAdapter(messageInfos, getContext());
        customListView.setAdapter(customListAdapter);
        getDatas(getContext());
        customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ConnectivityManager conMgr =  (ConnectivityManager) Objects.requireNonNull(getActivity().getSystemService(Context.CONNECTIVITY_SERVICE));
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

                if (netInfo == null){
                    vibrator.vibrate(20);
                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide1("Please enable your internet connection.!");
                    }
                    flashbar.show();

                }else {

                    RequestQueue queue = Volley.newRequestQueue(getContext());
                    String url = "http://220.247.238.246/gurind/mobile_API/inbox_messages_body.php";

                    try {

                        JSONObject jsonLoginCredentials = new JSONObject();
                        jsonLoginCredentials.put("customer_id", Activity_SignIn.Customer_id);
                        jsonLoginCredentials.put("message_id", id.get(i));

                        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, jsonLoginCredentials, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    Log.e("check", response.toString());

                                    if (response.getInt("status") == 200) {

                                        new LovelyStandardDialog(getContext(), LovelyStandardDialog.ButtonLayout.VERTICAL)
                                                .setTopColorRes(R.color.splashBackground)
                                                .setButtonsColorRes(R.color.colorPrimary)
                                                .setIcon(R.drawable.splashlogo)
                                                .setTitle(response.getString("MESSAGE_HEADING"))
                                                .setMessage(response.getString("MESSAGE_BODY"))
                                                .setPositiveButton("Dismiss", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        messageInfos.clear();
                                                        id.clear();
                                                        customListAdapter.notifyDataSetChanged();
                                                        getDatas(getContext());
                                                    }
                                                })
                                                .show();

                                        progressDialog.dismiss();
                                    }
                                } catch (JSONException ex) {
                                    progressDialog.dismiss();
                                    Log.e("Error", ex.getMessage());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Log.e("Error", error.getMessage());
                            }
                        });
                        jsonObject.setShouldCache(false);
                        jsonObject.setRetryPolicy(new RetryPolicy() {
                            @Override
                            public int getCurrentTimeout() {
                                return 50000;
                            }

                            @Override
                            public int getCurrentRetryCount() {
                                return 50000;
                            }

                            @Override
                            public void retry(VolleyError error) throws VolleyError {
                                progressDialog.dismiss();
                                Log.e("Error", error.getMessage());
                            }
                        });
                        queue.add(jsonObject);


                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("Exception Error", Objects.requireNonNull(e.getMessage()));
                    }


                }
            }
        });

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setMessage("Fetching order data.!");
        progressDialog.setIcon(R.drawable.splashlogo);
        progressDialog.setCanceledOnTouchOutside(false);




        return view;
    }

    private void getDatas(Context context){

        try {

            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "http://220.247.238.246/gurind/mobile_API/inbox_messages.php";

            JSONObject jsonLoginCredentials = new JSONObject();
            jsonLoginCredentials.put("customer_id", Activity_SignIn.Customer_id);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, jsonLoginCredentials, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try{
                        Log.e("response", response.toString());

                        if(response.getInt("status") == 200){

                            JSONArray jsonArray = response.getJSONArray("data");

                            for(int i=0; i<jsonArray.length(); i++){

                                JSONObject data = jsonArray.getJSONObject(i);

                                messageInfos.add(new MessageInfo(data.getString("id"),data.getString("subject"),data.getString("narration"), data.getString("MESSAGE_DATE_AND_TIME"), data.getString("read_status")));
                                id.add(Integer.parseInt(data.getString("id")));
                                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), getResources().getIdentifier("layout_animation_from_left","anim",getContext().getPackageName()));
                                customListView.setLayoutAnimation(animation);
                                customListAdapter.notifyDataSetChanged();
                                customListView.scheduleLayoutAnimation();
                            }

                            progressDialog.dismiss();
                        }
                        if(response.getInt("status") == 500){
                            if(flashbar == null){
                                flashbar = enterExitAnimationSlide(response.getString("message"));
                            }
                            flashbar.show();
                            progressDialog.dismiss();
                        }

                    }catch(JSONException ex){
                        progressDialog.dismiss();
                        Log.e("Error", ex.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                }
            });
            jsonObject.setShouldCache(false);
            jsonObject.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                    progressDialog.dismiss();
                    Log.e("Error", error.getMessage());
                }
            });
            queue.add(jsonObject);

        }catch(Exception e){
            progressDialog.dismiss();
            Log.e("Exception Error", Objects.requireNonNull(e.getMessage()));
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Inbox");
    }

    private Flashbar enterExitAnimationSlide(String msg){
        return new Flashbar.Builder(getActivity())
                .gravity(Flashbar.Gravity.BOTTOM)
                .duration(3000)
                .message(msg)
                .messageColor(ContextCompat.getColor(getContext(), R.color.splashBackground))
                .backgroundColor(ContextCompat.getColor(getContext(), R.color.flashBackground))
                .showIcon()
                .iconColorFilterRes(R.color.flashIcon)
                .icon(R.drawable.ic_cross)
                .enterAnimation(FlashAnim.with(getContext())
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(getContext())
                        .animateBar()
                        .duration(2800)
                        .slideFromLeft()
                        .overshoot())
                .build();
    }

    private Flashbar enterExitAnimationSlide1(String msg){
        return new Flashbar.Builder(getActivity())
                .gravity(Flashbar.Gravity.BOTTOM)
                .duration(3000)
                .message(msg)
                .messageColor(ContextCompat.getColor(getContext(), R.color.splashBackground))
                .backgroundColor(ContextCompat.getColor(getContext(), R.color.flashBackground))
                .showIcon()
                .iconColorFilterRes(R.color.flashCaution)
                .icon(R.drawable.ic_caution)
                .enterAnimation(FlashAnim.with(getContext())
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(getContext())
                        .animateBar()
                        .duration(2800)
                        .slideFromLeft()
                        .overshoot())
                .build();
    }

}
