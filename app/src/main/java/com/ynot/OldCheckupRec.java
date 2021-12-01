package com.ynot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.ynot.aaspaasdoctor.Adapter.MediRecAdapter2;
import com.ynot.aaspaasdoctor.Model.Medipriscription;
import com.ynot.aaspaasdoctor.R;
import com.ynot.aaspaasdoctor.Webservice.SharedPrefManager;
import com.ynot.aaspaasdoctor.Webservice.URLs;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class OldCheckupRec extends AppCompatActivity {
    String id, op_id = "";
    ACProgressFlower progress;
    TextView name, gende, age, symptem, docname, condate, sympt;
    ImageView pdf;
    LinearLayout nodata, layout;
    List<Medipriscription> medipriscriptionList = new ArrayList<>();
    MediRecAdapter2 mediRecAdapter;
    RecyclerView discription;
    LinearLayout symptom_head, head, frame;
    ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_checkup_rec);
        symptem = findViewById(R.id.symttt);
        nodata = findViewById(R.id.nodata);
        frame = findViewById(R.id.frame);
        head = findViewById(R.id.head);
        symptom_head = findViewById(R.id.symptom_head);
        image = findViewById(R.id.image);
        layout = findViewById(R.id.layout);
        progress = new ACProgressFlower.Builder(OldCheckupRec.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Old Checkup Records");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getStringExtra("id");
        op_id = getIntent().getStringExtra("op_id");
        name = findViewById(R.id.test);
        gende = findViewById(R.id.gender);
        age = findViewById(R.id.age);

        //docname,condate,sympt,discription,pdf
        docname = findViewById(R.id.doctor);
        condate = findViewById(R.id.dateofconsult);
        sympt = findViewById(R.id.symtommm);
        discription = findViewById(R.id.discription);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        discription.setLayoutManager(linearLayoutManager);

        //pdf=findViewById(R.id.pdf);


        get_details();
        getOldCheckupRecord();

    }

    private void getOldCheckupRecord() {
        //progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GETOLDCHECKUPRECORDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("oldchkupresponse", response);

                        // progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                frame.setVisibility(View.VISIBLE);
                                medipriscriptionList.add(new Medipriscription("Medicine", "Qty", "Days", ""));
                                docname.setText(ob.getString("doc_name"));
                                condate.setText(ob.getString("consult_date"));
                                sympt.setText(ob.getString("symptom"));

                                if (!ob.getString("symptom").isEmpty()) {
                                    head.setVisibility(View.VISIBLE);
                                    Log.e("second", "second");
                                    symptom_head.setVisibility(View.GONE);
                                } else {
                                    head.setVisibility(View.GONE);
                                }
                                if (ob.has("image")) {
                                    if (!ob.getString("image").isEmpty()) {
                                        head.setVisibility(View.GONE);
                                        Glide.with(getApplicationContext()).load(ob.getString("image")).into(image);
                                        image.setVisibility(View.VISIBLE);
                                    } else {
                                        image.setVisibility(View.GONE);
                                    }
                                }

                                JSONArray jsonArray = ob.getJSONArray("data");
                                if (jsonArray.length() > 0) {

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject ob2 = jsonArray.getJSONObject(i);
                                        medipriscriptionList.add(new Medipriscription(ob2.getString("medicine_name"), ob2.getString("qty"), ob2.getString("days"), ""));
                                    }
                                    mediRecAdapter = new MediRecAdapter2(getApplicationContext(), medipriscriptionList);
                                    discription.setAdapter(mediRecAdapter);
                                }

                            } else {
                                nodata.setVisibility(View.VISIBLE);
                                frame.setVisibility(View.GONE);
                                sympt.setVisibility(View.GONE);
                                symptom_head.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());

                //      progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("op_id", op_id);
                params.put("id", id);
                Log.e("oldchkup", params.toString());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    private void get_details() {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.BOOKING_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        Log.e("details", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                layout.setVisibility(View.VISIBLE);
                                name.setText(ob.getString("name"));
                                gende.setText(ob.getString("gender"));
                                age.setText(ob.getString("age"));
                                symptem.setText(ob.getString("symptom"));

                                if (!ob.getString("symptom").isEmpty()) {
                                    Log.e("first", "first");
                                    symptom_head.setVisibility(View.VISIBLE);
                                } else {
                                    symptom_head.setVisibility(View.GONE);
                                }
                                //  date.setText(ob.getString("date"));
//                                time.setText(ob.getString("time"));
//                                location.setText(ob.getString("op_type"));
                            } else {
//                                layout.setVisibility(View.GONE);
                                layout.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("op_id", op_id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }
}