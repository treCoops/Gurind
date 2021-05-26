package com.dtinnovation.gurind;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Activity_Change_Password extends Fragment {

    Flashbar flashbar;
    private Vibrator vibrator;
    private ProgressDialog progressDialog;
    FrameLayout cardBackground;
    EditText txtCurrentPassword, txtNewPassword, txtConfirmPassword;
    LinearLayout line_currentPassword, line_newPassword, line_confirmPassword;
    Button btn_submit;
    View view;
    private Animation smallToBig, slideUpLate, shake;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_change_password, container, false);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        cardBackground = view.findViewById(R.id.cardBackground);
        txtCurrentPassword = view.findViewById(R.id.txtCurrentPassword);
        txtNewPassword = view.findViewById(R.id.txtNewPassword);
        txtConfirmPassword = view.findViewById(R.id.txtConfirmPassword);
        line_currentPassword = view.findViewById(R.id.line_currentPassword);
        line_newPassword = view.findViewById(R.id.line_newPassword);
        line_confirmPassword = view.findViewById(R.id.line_confirmPassword);
        btn_submit = view.findViewById(R.id.btn_submit);

        smallToBig = AnimationUtils.loadAnimation(view.getContext(), R.anim.smalltobig);
        slideUpLate = AnimationUtils.loadAnimation(view.getContext(), R.anim.btta2);
        shake = AnimationUtils.loadAnimation(view.getContext(), R.anim.anim_shake_edit_text);

        cardBackground.startAnimation(smallToBig);

        txtConfirmPassword.startAnimation(slideUpLate);
        txtCurrentPassword.startAnimation(slideUpLate);
        txtNewPassword.startAnimation(slideUpLate);
        line_newPassword.startAnimation(slideUpLate);
        line_confirmPassword.startAnimation(slideUpLate);
        line_currentPassword.startAnimation(slideUpLate);
        btn_submit.startAnimation(slideUpLate);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setMessage("Fetching order data.!");
        progressDialog.setIcon(R.drawable.splashlogo);
        progressDialog.setCanceledOnTouchOutside(false);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashbar = null;

                if (TextUtils.isEmpty(txtCurrentPassword.getText().toString().trim())) {
                    vibrator.vibrate(20);
                    txtCurrentPassword.startAnimation(shake);
                    line_currentPassword.startAnimation(shake);

                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("Please enter your current password.!");
                    }
                    flashbar.show();
                    return;
                }

                if(txtCurrentPassword.getText().toString().trim().length()<8){
                    vibrator.vibrate(20);
                    txtCurrentPassword.startAnimation(shake);
                    line_currentPassword.startAnimation(shake);

                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("Password must have more than 8 characters.!");
                    }
                    flashbar.show();

                    return;
                }

                if (TextUtils.isEmpty(txtNewPassword.getText().toString().trim())) {
                    vibrator.vibrate(20);
                    txtNewPassword.startAnimation(shake);
                    line_newPassword.startAnimation(shake);

                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("Please enter your new password.!");
                    }
                    flashbar.show();
                    return;
                }

                if(txtNewPassword.getText().toString().trim().length()<8){
                    vibrator.vibrate(20);
                    txtNewPassword.startAnimation(shake);
                    line_newPassword.startAnimation(shake);

                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("Password must have more than 8 characters.!");
                    }
                    flashbar.show();

                    return;
                }

                if (TextUtils.isEmpty(txtConfirmPassword.getText().toString().trim())) {
                    vibrator.vibrate(20);
                    txtConfirmPassword.startAnimation(shake);
                    line_confirmPassword.startAnimation(shake);

                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("Please enter your confirmation password.!");
                    }
                    flashbar.show();
                    return;
                }

                if(txtConfirmPassword.getText().toString().trim().length()<8){
                    vibrator.vibrate(20);
                    txtConfirmPassword.startAnimation(shake);
                    line_confirmPassword.startAnimation(shake);

                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("Password must have more than 8 characters.!");
                    }
                    flashbar.show();

                    return;
                }


                if(!txtConfirmPassword.getText().toString().trim().equals(txtNewPassword.getText().toString().trim())){
                    vibrator.vibrate(20);
                    txtConfirmPassword.startAnimation(shake);
                    line_confirmPassword.startAnimation(shake);

                    txtNewPassword.startAnimation(shake);
                    line_newPassword.startAnimation(shake);

                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("New password and confirmation password is not matching.!");
                    }
                    flashbar.show();
                    return;
                }


                ConnectivityManager conMgr =  (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

                if (netInfo == null){
                    vibrator.vibrate(20);
                    if(flashbar == null){
                        flashbar = enterExitAnimationSlide("Please enable your internet connection.!");
                    }
                    flashbar.show();

                }else {
                    changePassword(Activity_SignIn.Username, txtNewPassword.getText().toString().trim(), txtCurrentPassword.getText().toString().trim());
                }
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Change Password");
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
                .iconColorFilterRes(R.color.success)
                .icon(R.drawable.ic_check_black_24dp)
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

    public void changePassword(String username, String newPassword, String currentPassword){
        progressDialog.show();

        try {

            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "http://220.247.238.246/gurind/mobile_API/mobile_reset_password.php";

            JSONObject jsonLoginCredentials = new JSONObject();
            jsonLoginCredentials.put("username", username);
            jsonLoginCredentials.put("nPassword", newPassword);
            jsonLoginCredentials.put("cPassword", currentPassword);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, jsonLoginCredentials, new Response.Listener<JSONObject>() {
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

                        if(response.getInt("status") == 201){
                            progressDialog.dismiss();
                            txtCurrentPassword.startAnimation(shake);
                            line_currentPassword.startAnimation(shake);
                            vibrator.vibrate(20);
                            if (flashbar == null) {
                                flashbar = enterExitAnimationSlide(response.getString("message"));
                            }
                            flashbar.show();
                        }

                        if(response.getInt("status") == 200){
                            progressDialog.dismiss();
                            if (flashbar == null) {
                                flashbar = enterExitAnimationSlide1(response.getString("message"));
                            }
                            flashbar.show();
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

}
