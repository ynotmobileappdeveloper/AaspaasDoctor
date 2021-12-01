package com.ynot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.ynot.aaspaasdoctor.R;
import com.ynot.aaspaasdoctor.Webservice.SharedPrefManager;
import com.ynot.aaspaasdoctor.Webservice.URLs;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class Surgeryrecord extends AppCompatActivity {
    String id, pdflink = "http://www.africau.edu/images/default/sample.pdf";
    ACProgressFlower progress;
    TextView name, gende, age, symptem, docname, condate, sympt, discription;
    LinearLayout nodata;
    ImageView pdf;
    FrameLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surgeryrecord);
        frame = findViewById(R.id.fame);
        frame.setVisibility(View.GONE);
        symptem = findViewById(R.id.symttt);
        nodata = findViewById(R.id.nodata);
        progress = new ACProgressFlower.Builder(Surgeryrecord.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Surgery Records");
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
        pdf = findViewById(R.id.pdf);


        get_details();
        getSurgeryRecords();

    }

    private void getSurgeryRecords() {
        // progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GETSURGERYREPORT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        // progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                frame.setVisibility(View.VISIBLE);
                                //doc_name,operation_date,symptom,discription,pdf
                                docname.setText(ob.getString("doc_name"));
                                condate.setText(ob.getString("operation_date"));
                                sympt.setText(ob.getString("symptom"));
                                discription.setText(ob.getString("description"));
//                                pdflink = ob.getString("surgery_pdf");


                            } else {
                                nodata.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());

                //  progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("op_id", id);
                Log.e("param", params.toString());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    private void get_details() {
        //progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.BOOKING_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("details", response);
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {

                                name.setText(ob.getString("name"));
                                gende.setText(ob.getString("gender"));
                                age.setText(ob.getString("age"));
                                symptem.setText(ob.getString("symptom"));

                                //  date.setText(ob.getString("date"));
//                                time.setText(ob.getString("time"));
//                                location.setText(ob.getString("op_type"));
                            } else {
//                                layout.setVisibility(View.GONE);
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