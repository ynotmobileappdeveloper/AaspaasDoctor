package com.ynot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.aaspaasdoctor.Adapter.LabRecAdapter;
import com.ynot.aaspaasdoctor.LabDetailPage;
import com.ynot.aaspaasdoctor.Model.LabRecModel;
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

public class LabRecord extends AppCompatActivity {
    String id, pdflink = "http://www.africau.edu/images/default/sample.pdf";
    ACProgressFlower progress;
    TextView name, gende, age, symptem;
    ImageView pdf;
    LinearLayout nodata;
    LabRecAdapter labRecAdapter;
    List<LabRecModel> labRecModelList = new ArrayList<>();
    RecyclerView labrec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_record);
        symptem = findViewById(R.id.symttt);
        progress = new ACProgressFlower.Builder(LabRecord.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Lab Records");
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        id = getIntent().getStringExtra("id");
        name = findViewById(R.id.test);
        gende = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        labrec = findViewById(R.id.labrec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        labrec.setLayoutManager(linearLayoutManager);
        nodata = findViewById(R.id.nodata);
        get_details();
        getLabrecords();

    }

//    private void getLabrecords() {
//        labRecAdapter=new LabRecAdapter(getApplicationContext());
//        labrec.setAdapter(labRecAdapter);
//    }

    private void getLabrecords() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GETLABRECORDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("etlabrec", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                JSONArray jsonArray = ob.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    labRecModelList.add(new LabRecModel(jsonObject.getString("id"), jsonObject.getString("date"), jsonObject.getString("name"), jsonObject.getString("gender"), jsonObject.getString("age"), jsonObject.getString("test")));

                                }
                                labRecAdapter = new LabRecAdapter(getApplicationContext(), labRecModelList, new LabRecAdapter.Click() {
                                    @Override
                                    public void Itemclick(LabRecModel list) {
                                        Intent intent = new Intent(getApplicationContext(), LabDetailPage.class);
                                        intent.putExtra("id", list.getId());
                                        startActivity(intent);
                                    }
                                });
                                labrec.setAdapter(labRecAdapter);


                            } else {
//                                layout.setVisibility(View.GONE);
                                if (labRecModelList.size() == 0) {
                                    nodata.setVisibility(View.VISIBLE);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("op_id", id);
                Log.e("params", params.toString());
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