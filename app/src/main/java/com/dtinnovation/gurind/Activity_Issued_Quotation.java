package com.dtinnovation.gurind;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;


public class Activity_Issued_Quotation extends Fragment {

    private Flashbar flashbar;
    private ProgressDialog progressDialog;
    private Vibrator vibrate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_issued_quotation, container, false);
        flashbar = null;
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setMessage("Fetching data for the table.!");
        progressDialog.setIcon(R.drawable.splashlogo);
        progressDialog.setCanceledOnTouchOutside(false);
        final TableLayout tableLayout = view.findViewById(R.id.tableLayout);


        vibrate = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        ConnectivityManager conMgr =  (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null){
            vibrate.vibrate(20);
            if(flashbar == null){
                flashbar = enterExitAnimationSlide("Please enable your internet connection.!");
            }
            flashbar.show();

        }else{
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
                String url = "http://220.247.238.246/gurind/mobile_API/issued_quotations.php";

                JSONObject Customer = new JSONObject();
                Customer.put("customer_id", Activity_SignIn.Customer_id);

                final JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, Customer, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            if(response.getInt("status") == 500) {
                                if (flashbar == null) {
                                    flashbar = enterExitAnimationSlide1(response.getString("message"));
                                }
                                flashbar.show();
                                progressDialog.dismiss();
                                vibrate.vibrate(20);
                            }

                            if(response.getInt("status") == 200){
                                JSONArray jsonArray = response.getJSONArray("data");

                                for(int i=0; i<jsonArray.length(); i++){
                                    JSONObject data = jsonArray.getJSONObject(i);

                                    if(i % 2 == 0){

                                        TableRow tr = new TableRow(view.getContext());
                                        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                                        TextView t1 = new TextView(view.getContext());
                                        t1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t1.setText(data.getString("QUOTATION_NO"));
                                        t1.setTextColor(Color.parseColor("#000000"));
                                        if(data.getString("STATUS").equals("PENDING")){
                                            t1.setBackground(view.getResources().getDrawable(R.drawable.table_body3));
                                        }else{
                                            t1.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                        }
                                        t1.setTextSize(15);
                                        t1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t1.setPadding(15,3, 15, 3);
                                        tr.addView(t1);

                                        TextView t2 = new TextView(view.getContext());
                                        t2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t2.setText(data.getString("PROJECT_REFERENCE"));
                                        t2.setTextColor(Color.parseColor("#000000"));
                                        if(data.getString("STATUS").equals("PENDING")){
                                            t2.setBackground(view.getResources().getDrawable(R.drawable.table_body3));
                                        }else{
                                            t2.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                        }
                                        t2.setTextSize(15);
                                        t2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t2.setPadding(15,3, 15, 3);
                                        tr.addView(t2);

                                        TextView t3 = new TextView(view.getContext());
                                        t3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t3.setText(data.getString("TOTAL_QUOTATION_QTY"));
                                        t3.setTextColor(Color.parseColor("#000000"));
                                        if(data.getString("STATUS").equals("PENDING")){
                                            t3.setBackground(view.getResources().getDrawable(R.drawable.table_body3));
                                        }else{
                                            t3.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                        }
                                        t3.setTextSize(15);
                                        t3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t3.setPadding(15,3, 15, 3);
                                        tr.addView(t3);

                                        TextView t4 = new TextView(view.getContext());
                                        t4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t4.setText(data.getString("TOTAL_SQM"));
                                        t4.setTextColor(Color.parseColor("#000000"));
                                        if(data.getString("STATUS").equals("PENDING")){
                                            t4.setBackground(view.getResources().getDrawable(R.drawable.table_body3));
                                        }else{
                                            t4.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                        }
                                        t4.setTextSize(15);
                                        t4.setGravity(Gravity.RIGHT);
                                        t4.setPadding(15,3, 15, 3);
                                        tr.addView(t4);

                                        TextView t5 = new TextView(view.getContext());
                                        t5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t5.setText(data.getString("TOTAL_VALUE"));
                                        t5.setTextColor(Color.parseColor("#000000"));
                                        if(data.getString("STATUS").equals("PENDING")){
                                            t5.setBackground(view.getResources().getDrawable(R.drawable.table_body3));
                                        }else{
                                            t5.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                        }
                                        t5.setTextSize(15);
                                        t5.setGravity(Gravity.RIGHT);
                                        t5.setPadding(15,3, 15, 3);
                                        tr.addView(t5);

                                        TextView t6 = new TextView(view.getContext());
                                        t6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t6.setText(data.getString("STATUS"));
                                        t6.setTextColor(Color.parseColor("#000000"));
                                        if(data.getString("STATUS").equals("PENDING")){
                                            t6.setBackground(view.getResources().getDrawable(R.drawable.table_body3));
                                        }else{
                                            t6.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                        }
                                        t6.setTextSize(15);
                                        t6.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t6.setPadding(15,3, 15, 3);
                                        tr.addView(t6);

                                        tableLayout.addView(tr);

                                    }else{

                                        TableRow tr = new TableRow(view.getContext());
                                        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                                        TextView t1 = new TextView(view.getContext());
                                        t1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t1.setText(data.getString("QUOTATION_NO"));
                                        t1.setTextColor(Color.parseColor("#000000"));
                                        if(data.getString("STATUS").equals("PENDING")){
                                            t1.setBackground(view.getResources().getDrawable(R.drawable.table_body3));
                                        }else{
                                            t1.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                        }
                                        t1.setTextSize(15);
                                        t1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t1.setPadding(15,3, 15, 3);
                                        tr.addView(t1);

                                        TextView t2 = new TextView(view.getContext());
                                        t2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t2.setText(data.getString("PROJECT_REFERENCE"));
                                        t2.setTextColor(Color.parseColor("#000000"));
                                        if(data.getString("STATUS").equals("PENDING")){
                                            t2.setBackground(view.getResources().getDrawable(R.drawable.table_body3));
                                        }else{
                                            t2.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                        }
                                        t2.setTextSize(15);
                                        t2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t2.setPadding(15,3, 15, 3);
                                        tr.addView(t2);

                                        TextView t3 = new TextView(view.getContext());
                                        t3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t3.setText(data.getString("TOTAL_QUOTATION_QTY"));
                                        t3.setTextColor(Color.parseColor("#000000"));
                                        if(data.getString("STATUS").equals("PENDING")){
                                            t3.setBackground(view.getResources().getDrawable(R.drawable.table_body3));
                                        }else{
                                            t3.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                        }
                                        t3.setTextSize(15);
                                        t3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t3.setPadding(15,3, 15, 3);
                                        tr.addView(t3);

                                        TextView t4 = new TextView(view.getContext());
                                        t4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t4.setText(data.getString("TOTAL_SQM"));
                                        t4.setTextColor(Color.parseColor("#000000"));
                                        if(data.getString("STATUS").equals("PENDING")){
                                            t4.setBackground(view.getResources().getDrawable(R.drawable.table_body3));
                                        }else{
                                            t4.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                        }
                                        t4.setTextSize(15);
                                        t4.setGravity(Gravity.RIGHT);
                                        t4.setPadding(15,3, 15, 3);
                                        tr.addView(t4);

                                        TextView t5 = new TextView(view.getContext());
                                        t5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t5.setText(data.getString("TOTAL_VALUE"));
                                        t5.setTextColor(Color.parseColor("#000000"));
                                        if(data.getString("STATUS").equals("PENDING")){
                                            t5.setBackground(view.getResources().getDrawable(R.drawable.table_body3));
                                        }else{
                                            t5.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                        }
                                        t5.setTextSize(15);
                                        t5.setGravity(Gravity.RIGHT);
                                        t5.setPadding(15,3, 15, 3);
                                        tr.addView(t5);

                                        TextView t6 = new TextView(view.getContext());
                                        t6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t6.setText(data.getString("STATUS"));
                                        t6.setTextColor(Color.parseColor("#000000"));
                                        if(data.getString("STATUS").equals("PENDING")){
                                            t6.setBackground(view.getResources().getDrawable(R.drawable.table_body3));
                                        }else{
                                            t6.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                        }
                                        t6.setTextSize(15);
                                        t6.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t6.setPadding(15,3, 15, 3);
                                        tr.addView(t6);

                                        tableLayout.addView(tr);

                                    }
                                }
                                progressDialog.dismiss();
                            }

                        }catch(JSONException ex){
                            Log.e("Error", ex.getMessage());
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", error.toString());
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

            }catch(Exception e) {
                progressDialog.dismiss();
                Log.e("Exception Error", Objects.requireNonNull(e.getMessage()));
                if (flashbar == null) {
                    flashbar = enterExitAnimationSlide("There was a problem with your request. Please try again later.");
                }
                flashbar.show();
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Issued Quotations (Last 90 days)");
    }

    private Flashbar enterExitAnimationSlide(String msg){
        return new Flashbar.Builder(getActivity())
                .gravity(Flashbar.Gravity.BOTTOM)
                .duration(3000)
                .message(msg)
                .messageColor(ContextCompat.getColor(getActivity(), R.color.splashBackground))
                .backgroundColor(ContextCompat.getColor(getActivity(), R.color.flashBackground))
                .showIcon()
                .iconColorFilterRes(R.color.flashIcon)
                .icon(R.drawable.ic_cross)
                .enterAnimation(FlashAnim.with(getActivity())
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(getActivity())
                        .animateBar()
                        .duration(2800)
                        .slideFromLeft()
                        .overshoot())
                .build();
    }

    private Flashbar enterExitAnimationSlide1(String msg){
        return new Flashbar.Builder(getActivity())
                .gravity(Flashbar.Gravity.BOTTOM)
                .duration(5000)
                .title("Oops..")
                .message(msg)
                .messageColor(ContextCompat.getColor(getActivity(), R.color.splashBackground))
                .backgroundColor(ContextCompat.getColor(getActivity(), R.color.flashBackground))
                .showIcon()
                .iconColorFilterRes(R.color.flashCaution)
                .icon(R.drawable.ic_caution)
                .enterAnimation(FlashAnim.with(getActivity())
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(getActivity())
                        .animateBar()
                        .duration(4800)
                        .slideFromLeft()
                        .overshoot())
                .build();
    }
}
