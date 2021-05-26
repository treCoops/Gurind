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

import androidx.annotation.NonNull;
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
import com.highsoft.highcharts.Common.HIChartsClasses.HIChart;
import com.highsoft.highcharts.Common.HIChartsClasses.HIColumn;
import com.highsoft.highcharts.Common.HIChartsClasses.HIOptions;
import com.highsoft.highcharts.Common.HIChartsClasses.HIPlotOptions;
import com.highsoft.highcharts.Common.HIChartsClasses.HISeries;
import com.highsoft.highcharts.Common.HIChartsClasses.HISubtitle;
import com.highsoft.highcharts.Common.HIChartsClasses.HITitle;
import com.highsoft.highcharts.Common.HIChartsClasses.HITooltip;
import com.highsoft.highcharts.Common.HIChartsClasses.HIXAxis;
import com.highsoft.highcharts.Common.HIChartsClasses.HIYAxis;
import com.highsoft.highcharts.Core.HIChartView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;



public class Monthly_Sales_Value extends Fragment {

    HIChartView hiChartView;
    private Flashbar flashbar;
    private ProgressDialog progressDialog;
    private Vibrator vibrate;
    ArrayList<Double> ColumnData = new ArrayList<>();
    ArrayList<HISeries> series = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_montly_sales_value, container, false);

        hiChartView = view.findViewById(R.id.MSValue);

        final HIOptions option = new HIOptions();

        flashbar = null;progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setMessage("Fetching data for the table.!");
        progressDialog.setIcon(R.drawable.splashlogo);
        progressDialog.setCanceledOnTouchOutside(false);

        vibrate = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        hiChartView.setWillNotDraw(true);

        final HITitle title_MSV = new HITitle();
        title_MSV.setText("Monthly Sales Value");

        final HIChart chart = new HIChart();
        chart.setType("column");
        option.setChart(chart);


        final HISubtitle subtitle_MSV = new HISubtitle();
        subtitle_MSV.setText("Year "+ Calendar.getInstance().get(Calendar.YEAR));

        final HITooltip tooltip = new HITooltip();
        tooltip.setHeaderFormat("<span style=\"font-size:15px\">{point.key}</span><table>");
        tooltip.setPointFormat("<tr><td style=\"color:{series.color};padding:0\">{series.name}: </td>" + "<td style=\"padding:0\"><b>{point.y}</b></td></tr>");
        tooltip.setFooterFormat("</table>");
        tooltip.setShared(true);
        tooltip.setUseHTML(true);

        final HIPlotOptions plotOptions = new HIPlotOptions();
        plotOptions.setColumn(new HIColumn());
        plotOptions.getColumn().setPointPadding(0.2);
        plotOptions.getColumn().setBorderWidth(0);


        final HIXAxis hixAxis = new HIXAxis();
        hixAxis.setCategories(new ArrayList<>(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" )));
        HITitle xTitle = new HITitle();
        xTitle.setText("Months");
        hixAxis.setTitle(xTitle);

        final HIYAxis hiyAxis = new HIYAxis();
        HITitle yTitle = new HITitle();
        yTitle.setText("Monthly Sales Value (LKR)");
        hiyAxis.setTitle(yTitle);

        final HIColumn MSDataColumn = new HIColumn();
        MSDataColumn.setName("Monthly Sales Value");
        MSDataColumn.setShowInLegend(false);

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
                String url = "http://220.247.238.246/gurind/mobile_API/monthly_sales_value.php";

                final JSONObject Customer = new JSONObject();
                Customer.put("customer_id", Activity_SignIn.Customer_id);

                final JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, Customer, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            if(response.getInt("status") == 500) {
                                if (flashbar == null) {
                                    flashbar = enterExitAnimationSlide(response.getString("message"));
                                }
                                flashbar.show();
                                progressDialog.dismiss();
                            }

                            if(response.getInt("status") == 200){

                                JSONArray jsonArray = response.getJSONArray("data");

                                for(int i=0; i<jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);

                                    ColumnData.add(data.getDouble("Jan"));
                                    ColumnData.add(data.getDouble("Feb"));
                                    ColumnData.add(data.getDouble("Mar"));
                                    ColumnData.add(data.getDouble("Apr"));
                                    ColumnData.add(data.getDouble("May"));
                                    ColumnData.add(data.getDouble("Jun"));
                                    ColumnData.add(data.getDouble("Jul"));
                                    ColumnData.add(data.getDouble("Aug"));
                                    ColumnData.add(data.getDouble("Sep"));
                                    ColumnData.add(data.getDouble("Oct"));
                                    ColumnData.add(data.getDouble("Nov"));
                                    ColumnData.add(data.getDouble("Dec"));

                                }

                                MSDataColumn.setData(ColumnData);

                                series.add(MSDataColumn);

                                option.setTitle(title_MSV);
                                option.setSubtitle(subtitle_MSV);
                                option.setXAxis(new ArrayList(){{add(hixAxis);}});
                                option.setYAxis(new ArrayList(){{add(hiyAxis);}});
                                option.setTooltip(tooltip);
                                option.setPlotOptions(plotOptions);
                                option.setSeries(series);
                                option.setChart(chart);
                                hiChartView.setWillNotDraw(false);
                                hiChartView.setOptions(option);
                                hiChartView.reload();

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
                        progressDialog.dismiss();
                        Log.e("Error", error.toString());
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

}
