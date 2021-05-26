package com.dtinnovation.gurind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import java.util.Objects;

public class Activity_ForgotPassword extends AppCompatActivity {

    ImageView logo;
    FrameLayout cardBackground;
    EditText txtEmail;
    LinearLayout line_email;
    Button btn_submit;
    TextView txtGetUsername,txtGetPassword;

    private Animation smallToBig, slideUpFast, slideUpLate, shake;

    private Vibrator vibrator;
    private Flashbar flashbar;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        logo = findViewById(R.id.logo);
        cardBackground = findViewById(R.id.cardBackground);
        txtEmail = findViewById(R.id.txtEmail);
        line_email = findViewById(R.id.line_email);
        btn_submit = findViewById(R.id.btn_submit);
        txtGetUsername = findViewById(R.id.txtGetUsername);
        txtGetPassword = findViewById(R.id.txtGetPassword);

        smallToBig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);
        slideUpFast = AnimationUtils.loadAnimation(this, R.anim.btta);
        slideUpLate = AnimationUtils.loadAnimation(this, R.anim.btta2);
        shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake_edit_text);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        cardBackground.startAnimation(smallToBig);
        logo.startAnimation(slideUpFast);
        txtEmail.startAnimation(slideUpLate);
        line_email.startAnimation(slideUpLate);
        btn_submit.startAnimation(slideUpLate);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setMessage("Searching for your account details.!");
        progressDialog.setIcon(R.drawable.splashlogo);
        progressDialog.setCanceledOnTouchOutside(false);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashbar = null;

                if (TextUtils.isEmpty(txtEmail.getText().toString().trim())) {
                    vibrator.vibrate(20);
                    txtEmail.startAnimation(shake);
                    line_email.startAnimation(shake);

                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("Please enter account created e-mail address.!");
                    }
                    flashbar.show();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString().trim()).matches()){
                    vibrator.vibrate(20);
                    txtEmail.startAnimation(shake);
                    line_email.startAnimation(shake);

                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("Please enter valid e-mail address.!");
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
                    forget(txtEmail.getText().toString().trim());
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Activity_ForgotPassword.this, Activity_SignIn.class));
        Animatoo.animateSlideLeft(Activity_ForgotPassword.this);
        finish();
    }

    public void forget(String mail){
        progressDialog.show();

        try {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://220.247.238.246/gurind/mobile_API/forgot_password.php";

            JSONObject jsonLoginCredentials = new JSONObject();
            jsonLoginCredentials.put("email", mail);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, jsonLoginCredentials, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{

                        if(response.getInt("status") == 500) {
                            progressDialog.dismiss();
                            vibrator.vibrate(20);
                            txtEmail.startAnimation(shake);
                            line_email.startAnimation(shake);
                            if (flashbar == null) {
                                flashbar = enterExitAnimationSlide(response.getString("message"));
                            }
                            flashbar.show();
                        }

                        if(response.getInt("status") == 501){
                            progressDialog.dismiss();
                            txtEmail.startAnimation(shake);
                            line_email.startAnimation(shake);
                            vibrator.vibrate(20);
                            if (flashbar == null) {
                                flashbar = enterExitAnimationSlide(response.getString("message"));
                            }
                            flashbar.show();
                        }

                        if(response.getInt("status") == 200){
                            progressDialog.dismiss();
                            Log.e("Response", response.toString());
                            txtGetUsername.setVisibility(View.VISIBLE);
                            txtGetPassword.setVisibility(View.VISIBLE);
                            txtGetUsername.setText("Username: "+response.getString("user_name"));
                            txtGetPassword.setText("Password: "+response.getString("plain_text"));
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
}
