package com.dtinnovation.gurind;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dtinnovation.gurind.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class Activity_Status extends Fragment {

    Flashbar flashbar;
    ArrayList<String> orders=new ArrayList<>();
    SpinnerDialog spinnerDialog;
    private Vibrator vibrate;
    TextView txtPrjOrder, txtStatusHeading1, txtStatusHeading;
    private ProgressDialog progressDialog;
    View view;
    TableLayout tableLayoutForHead1, tableLayout1, tableLayout2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_status, container, false);
        vibrate = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        txtPrjOrder = view.findViewById(R.id.txtPrjOrder);
        tableLayoutForHead1 = view.findViewById(R.id.tableLayoutForHead1);
        tableLayout1 = view.findViewById(R.id.tableLayout1);
        tableLayout2 = view.findViewById(R.id.tableLayout2);
        txtStatusHeading1 = view.findViewById(R.id.txtStatusHeading1);
        txtStatusHeading = view.findViewById(R.id.txtStatusHeading);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setMessage("Fetching order data.!");
        progressDialog.setIcon(R.drawable.splashlogo);
        progressDialog.setCanceledOnTouchOutside(false);
        initialSpinner();

        txtPrjOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerDialog.showSpinerDialog();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Status Of My Order");
    }

    private void initialSpinner(){
        ConnectivityManager conMgr =  (ConnectivityManager) Objects.requireNonNull(view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null){
            vibrate.vibrate(20);
            if(flashbar == null){
                flashbar = enterExitAnimationSlide("Please enable your internet connection.!");
            }
            flashbar.show();

        }else {

            try {
                progressDialog.show();
                if(Activity_SignIn.Customer_id.equals("0")){
                    progressDialog.dismiss();
                    vibrate.vibrate(20);
                    if (flashbar == null) {
                        flashbar = enterExitAnimationSlide("Select a customer.");
                    }
                    flashbar.show();
                }

                RequestQueue queue = Volley.newRequestQueue(view.getContext());
                String url = "http://220.247.238.246/gurind/mobile_API/all_orders.php";

                JSONObject jsonLoginCredentials = new JSONObject();
                jsonLoginCredentials.put("customer_id", Activity_SignIn.Customer_id);

                JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, jsonLoginCredentials, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                orders.add(data.getString("quotation_no")+" --- "+data.getString("project_reference"));
                            }
                            progressDialog.dismiss();
                        } catch (JSONException ex) {
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
                    }
                });
                queue.add(jsonObject);
            } catch (JSONException ex) {
                progressDialog.dismiss();
                Log.e("Error", ex.getMessage());
            }

            Collections.sort(orders);
            spinnerDialog = new SpinnerDialog(getActivity(), orders, "Select Order No Or Project Reference", "Close");// With No Animation
            spinnerDialog = new SpinnerDialog(getActivity(), orders, "Select Order No Or Project Reference", R.style.DialogAnimations_SmileWindow, "Close");// With 	Animation

            spinnerDialog.setCancellable(true); // for cancellable
            spinnerDialog.setShowKeyboard(false);// for open keyboard by default

            spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {
                    txtPrjOrder.setText(item);
                    String parts[] = item.split(" --- ", 2);

                    ConnectivityManager conMgr =  (ConnectivityManager) Objects.requireNonNull(view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE));
                    NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

                    if (netInfo == null){
                        vibrate.vibrate(20);
                        if(flashbar == null){
                            flashbar = enterExitAnimationSlide("Please enable your internet connection.!");
                        }
                        flashbar.show();

                    }else {

                        try {
                            progressDialog.show();
                            if (Activity_SignIn.Customer_id.equals("0")) {
                                progressDialog.dismiss();
                                vibrate.vibrate(20);
                                if (flashbar == null) {
                                    flashbar = enterExitAnimationSlide("Select a customer.");
                                }
                                flashbar.show();
                            }

                            RequestQueue queue = Volley.newRequestQueue(view.getContext());
                            String url = "http://220.247.238.246/gurind/mobile_API/status_my_order.php";

                            JSONObject jsonLoginCredentials = new JSONObject();
                            jsonLoginCredentials.put("quotation_no", parts[0]);

                            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, jsonLoginCredentials, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    tableLayoutForHead1.removeAllViews();
                                    tableLayout1.removeAllViews();
                                    tableLayout2.removeAllViews();
                                    txtStatusHeading.setVisibility(View.INVISIBLE);
                                    txtStatusHeading1.setVisibility(View.INVISIBLE);

                                    try {

                                        TableRow tr = new TableRow(view.getContext());
                                        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                                        TextView t1 = new TextView(view.getContext());
                                        t1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t1.setText("Work Order No");
                                        t1.setTextColor(Color.parseColor("#000000"));
                                        t1.setTextSize(18);
                                        t1.setTypeface(null, Typeface.BOLD);
                                        t1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        t1.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                        t1.setPadding(10,3, 15, 3);
                                        tr.addView(t1);

                                        TextView t2 = new TextView(view.getContext());
                                        t2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t2.setText(": "+response.getString("WORK_ORDER_NO"));
                                        t2.setTypeface(null, Typeface.BOLD);
                                        t2.setTextColor(Color.parseColor("#000000"));
                                        t2.setTextSize(18);
                                        t2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        t2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                        t2.setPadding(15,3, 15, 3);
                                        tr.addView(t2);

                                        TableRow tr1 = new TableRow(view.getContext());
                                        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                                        TextView t3 = new TextView(view.getContext());
                                        t3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t3.setText("Work Order Date");
                                        t3.setTextColor(Color.parseColor("#000000"));
                                        t3.setTextSize(18);
                                        t3.setTypeface(null, Typeface.BOLD);
                                        t3.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        t3.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                        t3.setPadding(10,3, 15, 3);
                                        tr1.addView(t3);

                                        TextView t4 = new TextView(view.getContext());
                                        t4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t4.setText(": "+response.getString("WORK_ORDER_DATE"));
                                        t4.setTextColor(Color.parseColor("#000000"));
                                        t4.setTextSize(18);
                                        t4.setTypeface(null, Typeface.BOLD);
                                        t4.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        t4.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                        t4.setPadding(15,3, 15, 3);
                                        tr1.addView(t4);

                                        TableRow tr2 = new TableRow(view.getContext());
                                        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                                        TextView t5 = new TextView(view.getContext());
                                        t5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t5.setText("Work Order Quantity");
                                        t5.setTextColor(Color.parseColor("#000000"));
                                        t5.setTextSize(18);
                                        t5.setTypeface(null, Typeface.BOLD);
                                        t5.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        t5.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                        t5.setPadding(10,3, 15, 3);
                                        tr2.addView(t5);

                                        TextView t6 = new TextView(view.getContext());
                                        t6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t6.setText(": "+response.getString("ORDER_QUANTITY"));
                                        t6.setTextColor(Color.parseColor("#000000"));
                                        t6.setTextSize(18);
                                        t6.setTypeface(null, Typeface.BOLD);
                                        t6.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        t6.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                        t6.setPadding(15,3, 15, 3);
                                        tr2.addView(t6);

                                        tableLayoutForHead1.addView(tr);
                                        tableLayoutForHead1.addView(tr1);
                                        tableLayoutForHead1.addView(tr2);

                                        if(response.getInt("status2") == 200){

                                            txtStatusHeading.setVisibility(View.VISIBLE);

                                            TableRow s2r = new TableRow(view.getContext());

                                            TextView s2rT1 = new TextView(view.getContext());
                                            s2rT1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                            s2rT1.setTextColor(Color.parseColor("#ffffff"));
                                            s2rT1.setBackground(view.getResources().getDrawable(R.drawable.table_heading));
                                            s2rT1.setTextSize(16);
                                            s2rT1.setText("Thickness");
                                            s2rT1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                            s2rT1.setPadding(18,3, 18, 3);
                                            s2r.addView(s2rT1);

                                            TextView s2rT2 = new TextView(view.getContext());
                                            s2rT2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                            s2rT2.setTextColor(Color.parseColor("#ffffff"));
                                            s2rT2.setBackground(view.getResources().getDrawable(R.drawable.table_heading));
                                            s2rT2.setTextSize(16);
                                            s2rT2.setText("Ready Cum.Qty");
                                            s2rT2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                            s2rT2.setPadding(18,3, 18, 3);
                                            s2r.addView(s2rT2);

                                            TextView s2rT3 = new TextView(view.getContext());
                                            s2rT3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                            s2rT3.setTextColor(Color.parseColor("#ffffff"));
                                            s2rT3.setBackground(view.getResources().getDrawable(R.drawable.table_heading));
                                            s2rT3.setTextSize(16);
                                            s2rT3.setText("Ready Date");
                                            s2rT3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                            s2rT3.setPadding(18,3, 18, 3);
                                            s2r.addView(s2rT3);

                                            tableLayout1.addView(s2r);

                                            JSONArray data = response.getJSONArray("data2");
                                            int a=0;

                                            for (int i = 0; i < data.length(); i++) {
                                                JSONObject data0 = data.getJSONObject(i);

                                                if(i % 2 == 0){

                                                    if(data0.getInt("QUANTITY") > 0) {

                                                        a++;

                                                        TableRow sl1r = new TableRow(view.getContext());

                                                        TextView sl1rT1 = new TextView(view.getContext());
                                                        sl1rT1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                        sl1rT1.setTextColor(Color.parseColor("#000000"));
                                                        sl1rT1.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                                        sl1rT1.setTextSize(16);
                                                        sl1rT1.setText(data0.getString("TYPE"));
                                                        sl1rT1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                        sl1rT1.setPadding(18, 3, 18, 3);
                                                        sl1r.addView(sl1rT1);

                                                        TextView sl1rT2 = new TextView(view.getContext());
                                                        sl1rT2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                        sl1rT2.setTextColor(Color.parseColor("#000000"));
                                                        sl1rT2.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                                        sl1rT2.setTextSize(16);
                                                        sl1rT2.setText(data0.getString("QUANTITY"));
                                                        sl1rT2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                        sl1rT2.setPadding(18, 3, 18, 3);
                                                        sl1r.addView(sl1rT2);

                                                        TextView sl1rT3 = new TextView(view.getContext());
                                                        sl1rT3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                        sl1rT3.setTextColor(Color.parseColor("#000000"));
                                                        sl1rT3.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                                        sl1rT3.setTextSize(16);
                                                        sl1rT3.setText(data0.getString("READY_DATE"));
                                                        sl1rT3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                        sl1rT3.setPadding(18, 3, 18, 3);
                                                        sl1r.addView(sl1rT3);

                                                        tableLayout1.addView(sl1r);
                                                    }

                                                }else{

                                                    if(data0.getInt("QUANTITY") > 0) {

                                                        a++;

                                                        TableRow sl1r = new TableRow(view.getContext());

                                                        TextView sl1rT1 = new TextView(view.getContext());
                                                        sl1rT1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                        sl1rT1.setTextColor(Color.parseColor("#000000"));
                                                        sl1rT1.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                                        sl1rT1.setTextSize(16);
                                                        sl1rT1.setText(data0.getString("TYPE"));
                                                        sl1rT1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                        sl1rT1.setPadding(18, 3, 18, 3);
                                                        sl1r.addView(sl1rT1);

                                                        TextView sl1rT2 = new TextView(view.getContext());
                                                        sl1rT2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                        sl1rT2.setTextColor(Color.parseColor("#000000"));
                                                        sl1rT2.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                                        sl1rT2.setTextSize(16);
                                                        sl1rT2.setText(data0.getString("QUANTITY"));
                                                        sl1rT2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                        sl1rT2.setPadding(18, 3, 18, 3);
                                                        sl1r.addView(sl1rT2);

                                                        TextView sl1rT3 = new TextView(view.getContext());
                                                        sl1rT3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                        sl1rT3.setTextColor(Color.parseColor("#000000"));
                                                        sl1rT3.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                                        sl1rT3.setTextSize(16);
                                                        sl1rT3.setText(data0.getString("READY_DATE"));
                                                        sl1rT3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                        sl1rT3.setPadding(18, 3, 18, 3);
                                                        sl1r.addView(sl1rT3);

                                                        tableLayout1.addView(sl1r);
                                                    }

                                                }

                                            }

                                            if(a==0){
                                                tableLayout1.removeAllViews();
                                                txtStatusHeading.setVisibility(View.INVISIBLE);
                                            }
                                        }


                                        if(response.getInt("status3") == 200) {

                                            txtStatusHeading1.setVisibility(View.VISIBLE);

                                            TableRow s3r = new TableRow(view.getContext());

                                            TextView s3rT1 = new TextView(view.getContext());
                                            s3rT1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                            s3rT1.setTextColor(Color.parseColor("#ffffff"));
                                            s3rT1.setBackground(view.getResources().getDrawable(R.drawable.table_heading));
                                            s3rT1.setTextSize(16);
                                            s3rT1.setText("Disp No");
                                            s3rT1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                            s3rT1.setPadding(18,3, 18, 3);
                                            s3r.addView(s3rT1);

                                            TextView s3rT2 = new TextView(view.getContext());
                                            s3rT2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                            s3rT2.setTextColor(Color.parseColor("#ffffff"));
                                            s3rT2.setBackground(view.getResources().getDrawable(R.drawable.table_heading));
                                            s3rT2.setTextSize(16);
                                            s3rT2.setText("Thickness");
                                            s3rT2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                            s3rT2.setPadding(18,3, 18, 3);
                                            s3r.addView(s3rT2);

                                            TextView s2rT3 = new TextView(view.getContext());
                                            s2rT3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                            s2rT3.setTextColor(Color.parseColor("#ffffff"));
                                            s2rT3.setBackground(view.getResources().getDrawable(R.drawable.table_heading));
                                            s2rT3.setTextSize(16);
                                            s2rT3.setText("Disp Cum Qty");
                                            s2rT3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                            s2rT3.setPadding(18,3, 18, 3);
                                            s3r.addView(s2rT3);

                                            TextView s2rT4 = new TextView(view.getContext());
                                            s2rT4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                            s2rT4.setTextColor(Color.parseColor("#ffffff"));
                                            s2rT4.setBackground(view.getResources().getDrawable(R.drawable.table_heading));
                                            s2rT4.setTextSize(16);
                                            s2rT4.setText("Lorry No/Driver");
                                            s2rT4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                            s2rT4.setPadding(18,3, 18, 3);
                                            s3r.addView(s2rT4);

                                            tableLayout2.addView(s3r);

                                            JSONArray data2 = response.getJSONArray("data3");

                                            for (int i = 0; i < data2.length(); i++) {
                                                JSONObject data00 = data2.getJSONObject(i);

                                                if(i % 2 == 0){

                                                    TableRow sl1r = new TableRow(view.getContext());

                                                    TextView sl1rT1 = new TextView(view.getContext());
                                                    sl1rT1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                    sl1rT1.setTextColor(Color.parseColor("#000000"));
                                                    sl1rT1.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                                    sl1rT1.setTextSize(16);
                                                    sl1rT1.setText(data00.getString("disp"));
                                                    sl1rT1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                    sl1rT1.setPadding(18,3, 18, 3);
                                                    sl1r.addView(sl1rT1);

                                                    TextView sl1rT2 = new TextView(view.getContext());
                                                    sl1rT2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                    sl1rT2.setTextColor(Color.parseColor("#000000"));
                                                    sl1rT2.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                                    sl1rT2.setTextSize(16);
                                                    sl1rT2.setText(data00.getString("item_description"));
                                                    sl1rT2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                    sl1rT2.setPadding(18,3, 18, 3);
                                                    sl1r.addView(sl1rT2);

                                                    TextView sl1rT3 = new TextView(view.getContext());
                                                    sl1rT3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                    sl1rT3.setTextColor(Color.parseColor("#000000"));
                                                    sl1rT3.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                                    sl1rT3.setTextSize(16);
                                                    sl1rT3.setText(data00.getString("TOTAL_DISPATCHED"));
                                                    sl1rT3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                    sl1rT3.setPadding(18,3, 18, 3);
                                                    sl1r.addView(sl1rT3);

                                                    TextView sl1rT4 = new TextView(view.getContext());
                                                    sl1rT4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                    sl1rT4.setTextColor(Color.parseColor("#000000"));
                                                    sl1rT4.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                                    sl1rT4.setTextSize(16);
                                                    sl1rT4.setText(data00.getString("driver_name")+"/"+data00.getString("vehi_number"));
                                                    sl1rT4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                    sl1rT4.setPadding(18,3, 18, 3);
                                                    sl1r.addView(sl1rT4);

                                                    tableLayout2.addView(sl1r);

                                                }else{

                                                    TableRow sl1r = new TableRow(view.getContext());

                                                    TextView sl1rT1 = new TextView(view.getContext());
                                                    sl1rT1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                    sl1rT1.setTextColor(Color.parseColor("#000000"));
                                                    sl1rT1.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                                    sl1rT1.setTextSize(16);
                                                    sl1rT1.setText(data00.getString("disp"));
                                                    sl1rT1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                    sl1rT1.setPadding(18,3, 18, 3);
                                                    sl1r.addView(sl1rT1);

                                                    TextView sl1rT2 = new TextView(view.getContext());
                                                    sl1rT2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                    sl1rT2.setTextColor(Color.parseColor("#000000"));
                                                    sl1rT2.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                                    sl1rT2.setTextSize(16);
                                                    sl1rT2.setText(data00.getString("item_description"));
                                                    sl1rT2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                    sl1rT2.setPadding(18,3, 18, 3);
                                                    sl1r.addView(sl1rT2);

                                                    TextView sl1rT3 = new TextView(view.getContext());
                                                    sl1rT3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                    sl1rT3.setTextColor(Color.parseColor("#000000"));
                                                    sl1rT3.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                                    sl1rT3.setTextSize(16);
                                                    sl1rT3.setText(data00.getString("TOTAL_DISPATCHED"));
                                                    sl1rT3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                    sl1rT3.setPadding(18,3, 18, 3);
                                                    sl1r.addView(sl1rT3);

                                                    TextView sl1rT4 = new TextView(view.getContext());
                                                    sl1rT4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                                    sl1rT4.setTextColor(Color.parseColor("#000000"));
                                                    sl1rT4.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                                    sl1rT4.setTextSize(16);
                                                    sl1rT4.setText(data00.getString("driver_name")+"/"+data00.getString("vehi_number"));
                                                    sl1rT4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                    sl1rT4.setPadding(18,3, 18, 3);
                                                    sl1r.addView(sl1rT4);

                                                    tableLayout2.addView(sl1r);

                                                }
                                            }
                                        }

                                        progressDialog.dismiss();
                                    } catch (JSONException ex) {
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
                                }
                            });
                            queue.add(jsonObject);
                        } catch (JSONException ex) {
                            progressDialog.dismiss();
                            Log.e("Error", ex.getMessage());
                        }
                    }
                }
            });
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

}
