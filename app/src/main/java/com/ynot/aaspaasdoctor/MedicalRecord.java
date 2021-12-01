package com.ynot.aaspaasdoctor;

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

public class MedicalRecord extends AppCompatActivity {
    String id;//pdflink="http://www.africau.edu/images/default/sample.pdf";
    ACProgressFlower progress;
    TextView name, gende, age, symptem, docname, condate, sympt, textView16;
    LinearLayout nodata, layout;
    ImageView pdf;
    List<Medipriscription> medipriscriptionList = new ArrayList<>();
    MediRecAdapter2 mediRecAdapter;
    RecyclerView discription;
    ImageView image;
    LinearLayout symptom_head, head, frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicai_record);
        symptem = findViewById(R.id.symttt);
        nodata = findViewById(R.id.nodata);
        frame = findViewById(R.id.frame);
        head = findViewById(R.id.head);
        textView16 = findViewById(R.id.textView16);
        layout = findViewById(R.id.layout);
        image = findViewById(R.id.image);
        symptom_head = findViewById(R.id.symptom_head);
        progress = new ACProgressFlower.Builder(MedicalRecord.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Recent Medical Records");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getStringExtra("id");
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

        getRecentMedicalRecords();

        get_details();

    }

    private void getRecentMedicalRecords() {
        //  progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GETRECENTMEDICALRECORDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        //   progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                symptom_head.setVisibility(View.GONE);

//                                "doc_name":"Dr Arun","consult_date":"30-12-2020","symptom":"head ache",
//                                        "data":[{"id":"1","list_id":"1","medicine_name":"trymol","qty":"10","days":"5","created_at":"2020-12-30 13:31:34"},
//                                {"id":"2","list_id":"1","medicine_name":"paracetamol","qty":"10","days":"5","created_at":"2020-12-30 13:31:34"}]}
////                                //doc_name,consult_date,symptom,discription,recent_medi_pdf
                                docname.setText(ob.getString("doc_name"));
                                condate.setText(ob.getString("consult_date"));
                                sympt.setText(ob.getString("symptom"));
                                if (!ob.getString("symptom").isEmpty()) {
                                    head.setVisibility(View.VISIBLE);
                                } else {
                                    head.setVisibility(View.GONE);
                                }

                                if (ob.has("image")) {
                                    if (!ob.getString("image").isEmpty()) {

                                        Glide.with(getApplicationContext()).load(ob.getString("image")).into(image);
                                        image.setVisibility(View.VISIBLE);
                                    } else {
                                        image.setVisibility(View.GONE);
                                    }
                                }
                                JSONArray jsonArray = ob.getJSONArray("data");
                                Log.e("size", String.valueOf(jsonArray.length()));
                                if (jsonArray.length() > 0) {
                                    medipriscriptionList.add(new Medipriscription("Medicine", "Qty", "Days", ""));
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject ob2 = jsonArray.getJSONObject(i);
                                        medipriscriptionList.add(new Medipriscription(ob2.getString("medicine_name"), ob2.getString("qty"), ob2.getString("days"), ""));
                                    }
                                    mediRecAdapter = new MediRecAdapter2(getApplicationContext(), medipriscriptionList);
                                    discription.setAdapter(mediRecAdapter);
                                }


                            } else {
                                nodata.setVisibility(View.VISIBLE);
                                textView16.setVisibility(View.GONE);
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

                //    progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("op_id", id);
                Log.e("get_recent_medi_re", params.toString());

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
                        Log.e("details", response);
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                layout.setVisibility(View.VISIBLE);
                                name.setText(ob.getString("name"));
                                gende.setText(ob.getString("gender"));
                                age.setText(ob.getString("age"));
                                symptem.setText(ob.getString("symptom"));
                                if (!ob.getString("symptom").isEmpty()) {
                                    symptom_head.setVisibility(View.VISIBLE);
                                } else {
                                    symptom_head.setVisibility(View.GONE);
                                }
                                //  date.setText(ob.getString("date"));
//                                time.setText(ob.getString("time"));
//                                location.setText(ob.getString("op_type"));
                            } else {
                                layout.setVisibility(View.GONE);
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
                params.put("op_id", id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }
}