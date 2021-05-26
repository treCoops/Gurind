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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
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
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class Activity_Calander extends Fragment implements CalendarPickerController {

    Flashbar flashbar;
    private Vibrator vibrator;
    private ProgressDialog progressDialog;

    private AgendaCalendarView agenda_calendar_view;
    Calendar minDate, maxDate;
    View view;
    CalendarPickerController calendarPickerController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_calander, container, false);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        agenda_calendar_view = view.findViewById(R.id.agenda_calendar_view);

        minDate = Calendar.getInstance();
        maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.MONTH, 2);

        List<CalendarEvent> eventList = new ArrayList<>();

        calendarPickerController = this;

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setMessage("Fetching order data.!");
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
            mockList(eventList);


        }

        agenda_calendar_view.init(eventList, minDate, maxDate, Locale.getDefault(), this);




        return view;
    }





    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Calender");
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

                            agenda_calendar_view.init(eventList, minDate, maxDate, Locale.getDefault(), calendarPickerController);

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

    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {

    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }
}
