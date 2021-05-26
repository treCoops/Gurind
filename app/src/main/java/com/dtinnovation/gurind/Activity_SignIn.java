package com.dtinnovation.gurind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Objects;

public class Activity_SignIn extends AppCompatActivity {

    public static String Customer_name;
    public static String Customer_id;
    public static String Username;
    public static int Account_type;

    private ImageView imageBack;
    private ImageView btnSignIn;
    private ImageView logo;
    private TextView btnForgotPassword;

    private EditText txtUsername;
    private LinearLayout line_username;

    private EditText txtPassword;
    private LinearLayout line_password;

    private Animation smallToBig, slideUpFast, slideUpLate, shake;

    private Vibrator vibrator;
    private Flashbar flashbar;

    private ProgressDialog progressDialog;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        imageBack = findViewById(R.id.imageBack);
        logo = findViewById(R.id.logo);
        txtUsername = findViewById(R.id.txtUsername);
        line_username = findViewById(R.id.line_username);
        txtPassword = findViewById(R.id.txtPassword);
        line_password = findViewById(R.id.line_password);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        smallToBig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);
        slideUpFast = AnimationUtils.loadAnimation(this, R.anim.btta);
        slideUpLate = AnimationUtils.loadAnimation(this, R.anim.btta2);
        shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake_edit_text);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        imageBack.startAnimation(smallToBig);
        logo.startAnimation(slideUpFast);

        txtUsername.startAnimation(slideUpLate);
        line_username.startAnimation(slideUpLate);
        txtPassword.startAnimation(slideUpLate);
        line_password.startAnimation(slideUpLate);
        btnSignIn.startAnimation(slideUpLate);
        btnForgotPassword.startAnimation(slideUpLate);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setMessage("Signing to with your credentials.!");
        progressDialog.setIcon(R.drawable.splashlogo);
        progressDialog.setCanceledOnTouchOutside(false);

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Activity_SignIn.this, Activity_ForgotPassword.class));
                Animatoo.animateInAndOut(Activity_SignIn.this);
                finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flashbar = null;

                if (TextUtils.isEmpty(txtUsername.getText().toString().trim())) {
                    vibrator.vibrate(20);
                    txtUsername.startAnimation(shake);
                    line_username.startAnimation(shake);

                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("Please enter your username.!");
                    }
                    flashbar.show();
                    return;
                }


                if(TextUtils.isEmpty(txtPassword.getText().toString().trim())){
                    vibrator.vibrate(20);
                    txtPassword.startAnimation(shake);
                    line_password.startAnimation(shake);

                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("Please enter your password.!");
                    }
                    flashbar.show();

                    return;
                }

                if(txtPassword.getText().toString().trim().length()<8){
                    vibrator.vibrate(20);
                    txtPassword.startAnimation(shake);
                    line_password.startAnimation(shake);

                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("Password must have more than 8 characters.!");
                    }
                    flashbar.show();

                    return;
                }

                ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

                if (netInfo == null){
                    vibrator.vibrate(20);
                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("Please enable your internet connection.!");
                    }
                    flashbar.show();

                    return;
                }else{
                    login(txtUsername.getText().toString().trim(), txtPassword.getText().toString().trim());
                }


            }
        });
    }

    private Flashbar enterExitAnimationSlide(String msg){
        return new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.BOTTOM)
                .duration(2000)
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
                        .duration(1800)
                        .slideFromLeft()
                        .overshoot())
                .build();
    }

    private void login(String email, final String password){

        progressDialog.show();

        try {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://220.247.238.246/gurind/mobile_API/mobile_user_login.php";

            JSONObject jsonLoginCredentials = new JSONObject();
            jsonLoginCredentials.put("username", email);
            jsonLoginCredentials.put("password", getSha1Hex(password));

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, jsonLoginCredentials, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{

                        if(response.getInt("status") == 500) {
                            progressDialog.dismiss();
                            vibrator.vibrate(20);
                            txtUsername.startAnimation(shake);
                            line_username.startAnimation(shake);
                            txtPassword.startAnimation(shake);
                            line_password.startAnimation(shake);
                            if (flashbar == null) {
                                flashbar = enterExitAnimationSlide(response.getString("message"));
                            }
                            flashbar.show();
                        }

                        if(response.getInt("status") == 501){
                            progressDialog.dismiss();
                            txtUsername.startAnimation(shake);
                            line_username.startAnimation(shake);
                            txtPassword.startAnimation(shake);
                            line_password.startAnimation(shake);
                            vibrator.vibrate(20);
                            if (flashbar == null) {
                                flashbar = enterExitAnimationSlide(response.getString("message"));
                            }
                            flashbar.show();
                        }

                        if(response.getInt("status") == 200){
                            progressDialog.dismiss();

                            Customer_id = response.getString("customerid");
                            Customer_name = response.getString("customername");
                            Username = response.getString("username");
                            Account_type = 200;

                            startActivity(new Intent(Activity_SignIn.this, Activity_Home.class));
                            Animatoo.animateInAndOut(Activity_SignIn.this);
                            finish();
                        }

                        if(response.getInt("status") == 205){
                            progressDialog.dismiss();
                            Customer_id = "0";
                            Customer_name = response.getString("customername");
                            Username = response.getString("username");
                            Account_type = 205;

                            startActivity(new Intent(Activity_SignIn.this, Activity_Home.class));
                            Animatoo.animateInAndOut(Activity_SignIn.this);
                            finish();
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
        flashbar = null;
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        if (flashbar == null) {
            flashbar = enterExitAnimationSlide1("Please click BACK again to exit!");
        }
        flashbar.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private Flashbar enterExitAnimationSlide1(String msg){
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

    public static String getSha1Hex(String clearString)
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(clearString.getBytes("UTF-8"));
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes)
            {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        }
        catch (Exception ignored)
        {
            ignored.printStackTrace();
            return null;
        }
    }

}
