package com.dtinnovation.gurind;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Activity_Debtor_Statement extends Fragment {

    private Flashbar flashbar;
    private ProgressDialog progressDialog;
    private Vibrator vibrate;
    TableLayout tableLayoutForHead;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_debtor_statement, container, false);

        final TextView txtHeading = view.findViewById(R.id.txtHeading);
        tableLayoutForHead = view.findViewById(R.id.tableLayoutForHead);
        flashbar = null;progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setMessage("Fetching data for the table.!");
        progressDialog.setIcon(R.drawable.splashlogo);
        progressDialog.setCanceledOnTouchOutside(false);
        final TableLayout tableLayout = view.findViewById(R.id.tableLayout_DS);


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
                String url = "http://220.247.238.246/gurind/mobile_API/debtors_statement.php";

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

                                txtHeading.setText("Debtors Statement as of "+ response.getString("today_string"));
                                Log.e("Date", response.getString("today_string"));

                                TableRow trh = new TableRow(view.getContext());
                                trh.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                                TextView t1h = new TextView(view.getContext());
                                t1h.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t1h.setText("Below 30 days :");
                                t1h.setTextSize(19);
                                t1h.setTypeface(null, Typeface.BOLD);
                                t1h.setTextColor(getResources().getColor(R.color.colorPrimary));
                                t1h.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                t1h.setPadding(15,3, 15, 3);
                                trh.addView(t1h);

                                TextView t2h = new TextView(view.getContext());
                                t2h.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t2h.setText("LKR "+convertTOCurrency(response.getString("below_30")));
                                t2h.setTextSize(19);
                                t2h.setTypeface(null, Typeface.BOLD);
                                t2h.setTextColor(getResources().getColor(R.color.colorPrimary));
                                t2h.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                t2h.setPadding(15,3, 15, 3);
                                trh.addView(t2h);

                                TableRow tr1h = new TableRow(view.getContext());
                                trh.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                                TextView t3h = new TextView(view.getContext());
                                t3h.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t3h.setText("31 - 60 days :");
                                t3h.setTextSize(19);
                                t3h.setTypeface(null, Typeface.BOLD);
                                t3h.setTextColor(getResources().getColor(R.color.colorPrimary));
                                t3h.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                t3h.setPadding(15,3, 15, 3);
                                tr1h.addView(t3h);

                                TextView t4h = new TextView(view.getContext());
                                t4h.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t4h.setText("LKR "+convertTOCurrency(response.getString("between_31_and_60")));
                                t4h.setTextSize(19);
                                t4h.setTypeface(null, Typeface.BOLD);
                                t4h.setTextColor(getResources().getColor(R.color.colorPrimary));
                                t4h.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                t4h.setPadding(15,3, 15, 3);
                                tr1h.addView(t4h);

                                TableRow tr2h = new TableRow(view.getContext());
                                trh.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                                TextView t5h = new TextView(view.getContext());
                                t5h.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t5h.setText("61 - 90 days :");
                                t5h.setTextSize(19);
                                t5h.setTypeface(null, Typeface.BOLD);
                                t5h.setTextColor(getResources().getColor(R.color.colorPrimary));
                                t5h.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                t5h.setPadding(15,3, 15, 3);
                                tr2h.addView(t5h);

                                TextView t6h = new TextView(view.getContext());
                                t6h.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t6h.setText("LKR "+convertTOCurrency(response.getString("between_61_and_90")));
                                t6h.setTextSize(19);
                                t6h.setTypeface(null, Typeface.BOLD);
                                t6h.setTextColor(getResources().getColor(R.color.colorPrimary));
                                t6h.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                t6h.setPadding(15,3, 15, 3);
                                tr2h.addView(t6h);

                                TableRow tr3h = new TableRow(view.getContext());
                                trh.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                                TextView t7h = new TextView(view.getContext());
                                t7h.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t7h.setText("91 - 120 days :");
                                t7h.setTextSize(19);
                                t7h.setTypeface(null, Typeface.BOLD);
                                t7h.setTextColor(getResources().getColor(R.color.colorPrimary));
                                t7h.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                t7h.setPadding(15,3, 15, 3);
                                tr3h.addView(t7h);

                                TextView t8h = new TextView(view.getContext());
                                t8h.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t8h.setText("LKR "+convertTOCurrency(response.getString("between_91_and_120")));
                                t8h.setTextSize(19);
                                t8h.setTypeface(null, Typeface.BOLD);
                                t8h.setTextColor(getResources().getColor(R.color.colorPrimary));
                                t8h.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                t8h.setPadding(15,3, 15, 3);
                                tr3h.addView(t8h);

                                TableRow tr4h = new TableRow(view.getContext());
                                trh.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                                TextView t9h = new TextView(view.getContext());
                                t9h.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t9h.setText("Above 120 days :");
                                t9h.setTextSize(19);
                                t9h.setTypeface(null, Typeface.BOLD);
                                t9h.setTextColor(getResources().getColor(R.color.colorPrimary));
                                t9h.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                t9h.setPadding(15,3, 15, 3);
                                tr4h.addView(t9h);

                                TextView t0h = new TextView(view.getContext());
                                t0h.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t0h.setText("LKR "+convertTOCurrency(response.getString("more_than120")));
                                t0h.setTextSize(19);
                                t0h.setTypeface(null, Typeface.BOLD);
                                t0h.setTextColor(getResources().getColor(R.color.colorPrimary));
                                t0h.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                t0h.setPadding(15,3, 15, 3);
                                tr4h.addView(t0h);

                                tableLayoutForHead.addView(trh);
                                tableLayoutForHead.addView(tr1h);
                                tableLayoutForHead.addView(tr2h);
                                tableLayoutForHead.addView(tr3h);
                                tableLayoutForHead.addView(tr4h);

                                Double docVal = 0.00;
                                Double docBal = 0.00;

                                JSONArray jsonArray = response.getJSONArray("data");

                                for(int i=0; i<jsonArray.length(); i++){
                                    JSONObject data = jsonArray.getJSONObject(i);

                                    if(i % 2 == 0){

                                        TableRow tr = new TableRow(view.getContext());
                                        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                                        TextView t1 = new TextView(view.getContext());
                                        t1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t1.setText(data.getString("DOC_TYPE"));
                                        t1.setTextColor(Color.parseColor("#000000"));
                                        t1.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                        t1.setTextSize(15);
                                        t1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t1.setPadding(15,3, 15, 3);
                                        tr.addView(t1);

                                        TextView t2 = new TextView(view.getContext());
                                        t2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t2.setText(data.getString("DOC_NO"));
                                        t2.setTextColor(Color.parseColor("#000000"));
                                        t2.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                        t2.setTextSize(15);
                                        t2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t2.setPadding(15,3, 15, 3);
                                        tr.addView(t2);

                                        TextView t3 = new TextView(view.getContext());
                                        t3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t3.setText(data.getString("DOC_DATE"));
                                        t3.setTextColor(Color.parseColor("#000000"));
                                        t3.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                        t3.setTextSize(15);
                                        t3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t3.setPadding(15,3, 15, 3);
                                        tr.addView(t3);

                                        TextView t4 = new TextView(view.getContext());
                                        t4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t4.setText(data.getString("CUR_CODE"));
                                        t4.setTextColor(Color.parseColor("#000000"));
                                        t4.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                        t4.setTextSize(15);
                                        t4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t4.setPadding(15,3, 15, 3);
                                        tr.addView(t4);

                                        TextView t5 = new TextView(view.getContext());
                                        t5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t5.setText(convertTOCurrency(data.getString("DOC_VALUE")));
                                        t5.setTextColor(Color.parseColor("#000000"));
                                        t5.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                        t5.setTextSize(15);
                                        t5.setGravity(Gravity.RIGHT);
                                        t5.setPadding(15,3, 15, 3);
                                        tr.addView(t5);

                                        TextView t6 = new TextView(view.getContext());
                                        t6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t6.setText(convertTOCurrency(data.getString("DOC_BALANCE")));
                                        t6.setTextColor(Color.parseColor("#000000"));
                                        t6.setBackground(view.getResources().getDrawable(R.drawable.table_body1));
                                        t6.setTextSize(15);
                                        t6.setGravity(Gravity.RIGHT);
                                        t6.setPadding(15,3, 15, 3);
                                        tr.addView(t6);

                                        tableLayout.addView(tr);

                                    }else{

                                        TableRow tr = new TableRow(view.getContext());
                                        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                                        TextView t1 = new TextView(view.getContext());
                                        t1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t1.setText(data.getString("DOC_TYPE"));
                                        t1.setTextColor(Color.parseColor("#000000"));
                                        t1.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                        t1.setTextSize(15);
                                        t1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t1.setPadding(15,3, 15, 3);
                                        tr.addView(t1);

                                        TextView t2 = new TextView(view.getContext());
                                        t2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t2.setText(data.getString("DOC_NO"));
                                        t2.setTextColor(Color.parseColor("#000000"));
                                        t2.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                        t2.setTextSize(15);
                                        t2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t2.setPadding(15,3, 15, 3);
                                        tr.addView(t2);

                                        TextView t3 = new TextView(view.getContext());
                                        t3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t3.setText(data.getString("DOC_DATE"));
                                        t3.setTextColor(Color.parseColor("#000000"));
                                        t3.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                        t3.setTextSize(15);
                                        t3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t3.setPadding(15,3, 15, 3);
                                        tr.addView(t3);

                                        TextView t4 = new TextView(view.getContext());
                                        t4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t4.setText(data.getString("CUR_CODE"));
                                        t4.setTextColor(Color.parseColor("#000000"));
                                        t4.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                        t4.setTextSize(15);
                                        t4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        t4.setPadding(15,3, 15, 3);
                                        tr.addView(t4);

                                        TextView t5 = new TextView(view.getContext());
                                        t5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t5.setText(convertTOCurrency(data.getString("DOC_VALUE")));
                                        t5.setTextColor(Color.parseColor("#000000"));
                                        t5.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                        t5.setTextSize(15);
                                        t5.setGravity(Gravity.RIGHT);
                                        t5.setPadding(15,3, 15, 3);
                                        tr.addView(t5);

                                        TextView t6 = new TextView(view.getContext());
                                        t6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                        t6.setText(convertTOCurrency(data.getString("DOC_BALANCE")));
                                        t6.setTextColor(Color.parseColor("#000000"));
                                        t6.setBackground(view.getResources().getDrawable(R.drawable.table_body2));
                                        t6.setTextSize(15);
                                        t6.setGravity(Gravity.RIGHT);
                                        t6.setPadding(15,3, 15, 3);
                                        tr.addView(t6);

                                        tableLayout.addView(tr);

                                    }

                                    docVal = docVal + Double.parseDouble(data.getString("DOC_VALUE"));
                                    docBal = docBal + Double.parseDouble(data.getString("DOC_BALANCE"));

                                }

                                TableRow tr = new TableRow(view.getContext());

                                TextView t1 = new TextView(view.getContext());
                                t1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t1.setTextColor(Color.parseColor("#ffffff"));
                                t1.setBackground(view.getResources().getDrawable(R.drawable.table_total));
                                t1.setTextSize(16);
                                t1.setText("Total");
                                t1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                t1.setPadding(15,3, 15, 3);
                                tr.addView(t1);

                                TextView t2 = new TextView(view.getContext());
                                t2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t2.setBackground(view.getResources().getDrawable(R.drawable.table_total));
                                t2.setPadding(15,3, 15, 3);
                                tr.addView(t2);

                                TextView t3 = new TextView(view.getContext());
                                t3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                t3.setBackground(view.getResources().getDrawable(R.drawable.table_total));
                                t3.setPadding(15,3, 15, 3);
                                tr.addView(t3);

                                TextView t4 = new TextView(view.getContext());
                                t4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                t4.setBackground(view.getResources().getDrawable(R.drawable.table_total));
                                t4.setPadding(15,3, 15, 3);
                                tr.addView(t4);

                                TextView t5 = new TextView(view.getContext());
                                t5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t5.setText(convertTOCurrency(String.valueOf(docVal)));
                                t5.setTextColor(Color.parseColor("#ffffff"));
                                t5.setBackground(view.getResources().getDrawable(R.drawable.table_heading));
                                t5.setTextSize(16);
                                t5.setGravity(Gravity.RIGHT);
                                t5.setPadding(15,3, 15, 3);
                                tr.addView(t5);

                                TextView t6 = new TextView(view.getContext());
                                t6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t6.setText(convertTOCurrency(String.valueOf(docBal)));
                                t6.setTextColor(Color.parseColor("#ffffff"));
                                t6.setBackground(view.getResources().getDrawable(R.drawable.table_heading));
                                t6.setTextSize(16);
                                t6.setGravity(Gravity.RIGHT);
                                t6.setPadding(15,3, 15, 3);
                                tr.addView(t6);

                                tableLayout.addView(tr);

                                TableRow tr5h = new TableRow(view.getContext());
                                trh.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

                                TextView t11h = new TextView(view.getContext());
                                t11h.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t11h.setText("Total Outstanding");
                                t11h.setTextSize(19);
                                t11h.setTypeface(null, Typeface.BOLD);
                                t11h.setTextColor(getResources().getColor(R.color.tableBody3));
                                t11h.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                t11h.setPadding(15,3, 15, 3);
                                tr5h.addView(t11h);

                                TextView t12h = new TextView(view.getContext());
                                t12h.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                                t12h.setText(": LKR "+convertTOCurrency(String.valueOf(docBal)));
                                t12h.setTextSize(19);
                                t12h.setTypeface(null, Typeface.BOLD);
                                t12h.setTextColor(getResources().getColor(R.color.tableBody3));
                                t12h.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                t12h.setPadding(15,3, 15, 3);
                                tr5h.addView(t12h);

                                tableLayoutForHead.addView(tr5h);

                                progressDialog.dismiss();
                            }

                        }catch(JSONException ex){
                            Log.e("Error", ex.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", error.toString());
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
        getActivity().setTitle("Debtor Statement");
    }

    private String convertTOCurrency(String value){
        Double val = Double.parseDouble(value);
        return String.format("%,.2f",val);
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
