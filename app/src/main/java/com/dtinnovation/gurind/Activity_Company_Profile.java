package com.dtinnovation.gurind;


import android.annotation.SuppressLint;
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
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
import com.dtinnovation.gurind.models.Image;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;



public class Activity_Company_Profile extends Fragment{

    Flashbar flashbar;
    private Vibrator vibrator;
    private ProgressDialog progressDialog;


    View view;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_company_profile, container, false);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        final WebView pdfViewPager = view.findViewById(R.id.pdfViewPager);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setMessage("Fetching data.!");
        progressDialog.setIcon(R.drawable.splashlogo);
        progressDialog.setCanceledOnTouchOutside(false);


        ConnectivityManager conMgr =  (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null){
            vibrator.vibrate(20);
            if(flashbar == null){
                flashbar = enterExitAnimationSlide("Please enable your internet connection.!");
            }
            flashbar.show();

        }else {

            try {
                progressDialog.show();

                RequestQueue queue = Volley.newRequestQueue(getContext());
                String url = "http://220.247.238.246/gurind/mobile_API/company_prof.php";

//                JSONObject jsonLoginCredentials = new JSONObject();
//                jsonLoginCredentials.put("request", 0);

                JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getInt("status") == 500) {
                                progressDialog.dismiss();
                                vibrator.vibrate(20);
                                if (flashbar == null) {
                                    flashbar = enterExitAnimationSlide(response.getString("message"));
                                }
                                flashbar.show();
                                progressDialog.dismiss();
                            }

                            if (response.getInt("status") == 200) {

                                JSONArray data = response.getJSONArray("data");
                                JSONObject obj = data.getJSONObject(0);

                                String PDFURL = obj.getString("image_path");
                                pdfViewPager.requestFocus();
                                pdfViewPager.getSettings().setSupportZoom(true);
                                pdfViewPager.getSettings().setJavaScriptEnabled(true);
                                String URL = "https://docs.google.com/viewer?embedded=true&url="+PDFURL;
                                pdfViewPager.loadUrl(URL);
                                pdfViewPager.setWebViewClient(new WebViewClient(){
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        view.loadUrl(url);
                                        return true;
                                    }
                                });
                                pdfViewPager.setWebChromeClient(new WebChromeClient() {
                                    public void onProgressChanged(WebView view, int progress) {
                                        if (progress < 100) {
                                            progressDialog.show();
                                        }
                                        if (progress == 100) {
                                            progressDialog.dismiss();
                                        }
                                    }
                                });



                            }

                        } catch (JSONException ex) {

                            Log.e("Error", ex.getMessage());
                            progressDialog.dismiss();
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
                        Log.e("Error", error.getMessage());
                        progressDialog.dismiss();
                    }
                });
                queue.add(jsonObject);

            } catch (Exception e) {
                Log.e("Exception Error", Objects.requireNonNull(e.getMessage()));
                progressDialog.dismiss();
            }
        }
        return view;
    }





    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Company Profile");
    }

    private void mockList(final List<CalendarEvent> eventList) {

        final SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

        progressDialog.show();

        try {

            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "http://220.247.238.246/gurind/mobile_API/holidays.php";


            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{

                        if(response.getInt("status") == 500) {
                            progressDialog.dismiss();
                            vibrator.vibrate(20);
                            if (flashbar == null) {
                                flashbar = enterExitAnimationSlide(response.getString("message"));
                            }
                            flashbar.show();
                        }



                        if(response.getInt("status") == 200){
                            progressDialog.dismiss();

                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                Log.e("Date", data.getString("DATE_STRING"));

                                String dateInString = data.getString("DATE_STRING");

                                Date date = null;
                                try {
                                    date = sdf.parse(dateInString);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);

                                BaseCalendarEvent event1 = new BaseCalendarEvent(data.getString("narration"), "", "Gurind Accor (Pvt) Ltd",
                                        ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.eventColor), calendar, calendar, true);
                                eventList.add(event1);

                            }


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

    private Flashbar enterExitAnimationSlide(String msg){
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
