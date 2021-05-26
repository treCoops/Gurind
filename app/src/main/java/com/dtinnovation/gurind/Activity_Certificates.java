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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dtinnovation.gurind.adapters.GalleryAdapter;
import com.dtinnovation.gurind.models.Image;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Activity_Certificates extends Fragment {

    Flashbar flashbar;
    private Vibrator vibrator;
    private ProgressDialog progressDialog;
    View view;
    private ArrayList<Image> images;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_certificates, container, false);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

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
                        flashbar = enterExitAnimationSlide1("Please enable your internet connection.!");
                    }
                    flashbar.show();

                }else {
                    recyclerView = view.findViewById(R.id.recycler_view);

                    images = new ArrayList<>();
                    mAdapter = new GalleryAdapter(getContext(), images);

                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);

                    recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getContext(), recyclerView, new GalleryAdapter.ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("images", images);
                            bundle.putInt("position", position);

                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                            newFragment.setArguments(bundle);
                            newFragment.show(ft, "slideshow");
                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));

                    fetchImages();

                }




        return view;
    }

    private void fetchImages() {

        try {
            progressDialog.show();

            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "http://220.247.238.246/gurind/mobile_API/certificate_images.php";

            JSONObject jsonLoginCredentials = new JSONObject();
            jsonLoginCredentials.put("request", 1);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, jsonLoginCredentials, new Response.Listener<JSONObject>() {
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

                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);


                                Image image = new Image();
                                image.setName(data.getString("image_heading"));

                                image.setSmall(data.getString("image_path"));
                                image.setMedium(data.getString("image_path"));
                                image.setLarge(data.getString("image_path"));
                                image.setTimestamp("sdsdffdf");

                                images.add(image);
                                progressDialog.dismiss();

                            }

                            mAdapter.notifyDataSetChanged();

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



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Certifications");
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
