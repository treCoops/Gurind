package com.dtinnovation.gurind;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.developer.kalert.KAlertDialog;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class Activity_Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Flashbar flashbar;
    private boolean doubleBackToExitPressedOnce = false;
    ArrayList<String> items=new ArrayList<>();
    SpinnerDialog spinnerDialog;
    TextView txtCusname;
    TextView inboxCount;
    private Vibrator vibrate;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setMessage("Fetching customer data.!");
        progressDialog.setIcon(R.drawable.splashlogo);
        progressDialog.setCanceledOnTouchOutside(false);
        initialSpinner();

        vibrate = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        inboxCount = (TextView) navigationView.getMenu().findItem(R.id.nav_notifications).getActionView();



        onNavigationItemSelected(navigationView.getMenu().getItem(3));
        navigationView.getMenu().getItem(3).setChecked(true);
        Activity_SignIn user = new Activity_SignIn();
        View headerView = navigationView.getHeaderView(0);
        txtCusname = headerView.findViewById(R.id.txt_customerName);

        ConnectivityManager conMgr =  (ConnectivityManager) Objects.requireNonNull(this.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null) {
            vibrate.vibrate(20);
            if (flashbar == null) {
                flashbar = enterExitAnimationSlide1("Please enable your internet connection.!");
            }
            flashbar.show();
        }

        if(Activity_SignIn.Account_type == 200){
            txtCusname.setText(user.Customer_name);
            initializeCountDrawer();
        }
        if(Activity_SignIn.Account_type == 205){
            txtCusname.setText("Please Select Customer");

            txtCusname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spinnerDialog.showSpinerDialog();
                }
            });

        }
    }

    public void initializeCountDrawer(){
        progressDialog.show();
        inboxCount.setGravity(Gravity.CENTER_VERTICAL);
        inboxCount.setTypeface(null, Typeface.BOLD);
        inboxCount.setTextColor(getResources().getColor(R.color.colorPrimary));

        try {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://220.247.238.246/gurind/mobile_API/inbox_messages_count.php";

            JSONObject jsonLoginCredentials = new JSONObject();
            jsonLoginCredentials.put("customer_id", Activity_SignIn.Customer_id);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, jsonLoginCredentials, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                    try{

                        if(response.getInt("status") == 200){
                            inboxCount.setText(response.getString("UNREAD_COUNT"));
                            progressDialog.dismiss();
                        }
                        if(response.getInt("status") == 500){
                            inboxCount.setText(response.getString("UNREAD_COUNT"));
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
                    onBackPressed();

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
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
                flashbar = null;
                if (doubleBackToExitPressedOnce) {
                    finishAffinity();
                    System.exit(0);
                    return;
                }

                this.doubleBackToExitPressedOnce = true;

                if (flashbar == null) {
                    flashbar = enterExitAnimationSlide("Please click BACK again to exit!");
                }
                flashbar.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
    }

    private Flashbar enterExitAnimationSlide(String msg){
        return new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.BOTTOM)
                .duration(1000)
                .message(msg)
                .messageColor(ContextCompat.getColor(this, R.color.splashBackground))
                .backgroundColor(ContextCompat.getColor(this, R.color.flashBackground))
                .showIcon()
                .iconColorFilterRes(R.color.flashCaution)
                .icon(R.drawable.ic_caution)
                .enterAnimation(FlashAnim.with(this)
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(this)
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .build();
    }

    private Flashbar enterExitAnimationSlide1(String msg){
        return new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.BOTTOM)
                .duration(1000)
                .message(msg)
                .messageColor(ContextCompat.getColor(this, R.color.splashBackground))
                .backgroundColor(ContextCompat.getColor(this, R.color.flashBackground))
                .showIcon()
                .iconColorFilterRes(R.color.flashIcon)
                .icon(R.drawable.ic_cross)
                .enterAnimation(FlashAnim.with(this)
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(this)
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .build();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        initializeCountDrawer();

        switch (id) {

            case R.id.nav_dashboard:
                ft.replace(R.id.content_frame, new Activity_Dashboard());
                ft.commit();
                break;

            case R.id.nav_statusOfOrder:
                ft.replace(R.id.content_frame, new Activity_Status());
                ft.commit();
                break;

            case R.id.nav_notifications:
                ft.replace(R.id.content_frame, new Activity_Inbox());
                ft.commit();
                break;

            case R.id.nav_unsettleInvoices:
                ft.replace(R.id.content_frame, new Activity_Unsettle_Invoices());
                ft.commit();
                break;

            case R.id.nav_confirm_orders:
                ft.replace(R.id.content_frame, new Activity_Issued_Quotation());
                ft.commit();
                break;

            case R.id.nav_running_quotation:
                ft.replace(R.id.content_frame, new Activity_Running_Orders());
                ft.commit();
                break;

            case R.id.nav_recently_completed:
                ft.replace(R.id.content_frame, new Activity_Recently_Completed());
                ft.commit();
                break;

            case R.id.nav_last_few_payment:
                ft.replace(R.id.content_frame, new Activity_Last_Few_Payment());
                ft.commit();
                break;

            case R.id.nav_debtor_statement:
                ft.replace(R.id.content_frame, new Activity_Debtor_Statement());
                ft.commit();
                break;

            case R.id.nav_ready_not:
                ft.replace(R.id.content_frame, new Activity_Ready_Not());
                ft.commit();
                break;

            case R.id.nav_recent_delivery:
                ft.replace(R.id.content_frame, new Activity_Recent_Deliveries());
                ft.commit();
                break;

            case R.id.nav_holiday_calander:
                ft.replace(R.id.content_frame, new Activity_Calander());
                ft.commit();
                break;

            case R.id.nav_certificates:
                ft.replace(R.id.content_frame, new Activity_Certificates());
                ft.commit();
                break;

            case R.id.nav_company_profile:
                ft.replace(R.id.content_frame, new Activity_Company_Profile());
                ft.commit();
                break;

            case R.id.nav_change_password:
                ft.replace(R.id.content_frame, new Activity_Change_Password());
                ft.commit();
                break;

            case R.id.nav_logout:
                new KAlertDialog(this, KAlertDialog.CUSTOM_IMAGE_TYPE)
                        .cancelButtonColor(R.color.colorPrimary)
                        .confirmButtonColor(R.color.colorPrimary)
                        .setCancelText("CANCEL")
                        .setContentText("Do you want to logout?")
                        .setConfirmText("YES")
                        .setCustomImage(R.drawable.exclamation_mark)
                        .showCancelButton(true)
                        .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                startActivity(new Intent(Activity_Home.this, Activity_SignIn.class));
                                Animatoo.animateSplit(Activity_Home.this);
                                kAlertDialog.cancel();
                            }
                        })
                        .show();
                break;

            case R.id.manufacture:
                                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("http://www.dtinnovations.lk/"));
                startActivity(viewIntent);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initialSpinner(){

        progressDialog.show();

        ConnectivityManager conMgr =  (ConnectivityManager) Objects.requireNonNull(this.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null){
            vibrate.vibrate(20);
            if(flashbar == null){
                flashbar = enterExitAnimationSlide1("Please enable your internet connection.!");
            }
            flashbar.show();

        }else {

            try {

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = "http://220.247.238.246/gurind/mobile_API/all_customers.php";

                JSONObject jsonLoginCredentials = new JSONObject();
                jsonLoginCredentials.put("username", "aaa");

                JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, jsonLoginCredentials, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                items.add(data.getString("ID_AND_NAME"));
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
                        onBackPressed();

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

            Collections.sort(items);
            spinnerDialog = new SpinnerDialog(this, items, "Select Customer", "Close");// With No Animation
            spinnerDialog = new SpinnerDialog(this, items, "Select Customer", R.style.DialogAnimations_SmileWindow, "Close");// With 	Animation

            spinnerDialog.setCancellable(true); // for cancellable
            spinnerDialog.setShowKeyboard(false);// for open keyboard by default

            spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {
                    String parts[] = item.split(" ", 2);
                    Activity_SignIn.Customer_id = String.format(parts[0]);
                    initializeCountDrawer();
                    txtCusname.setText(item);
                }
            });
        }
    }

}
