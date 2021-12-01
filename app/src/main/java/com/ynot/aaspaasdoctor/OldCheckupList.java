package com.ynot.aaspaasdoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.LabRecord;
import com.ynot.OldCheckupRec;
import com.ynot.aaspaasdoctor.Adapter.LabRecAdapter;
import com.ynot.aaspaasdoctor.Adapter.OldCheckupAdapter;
import com.ynot.aaspaasdoctor.Model.LabRecModel;
import com.ynot.aaspaasdoctor.Model.OldCheckupModel;
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

public class OldCheckupList extends AppCompatActivity {

    OldCheckupAdapter adapter;
    ArrayList<OldCheckupModel> model = new ArrayList<>();
    RecyclerView rec;
    ACProgressFlower progress;
    TextView name, gende, age, symptem;
    String id = "";
    LinearLayout sym_head, layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_checkup_list);
        rec = findViewById(R.id.rec);
        name = findViewById(R.id.test);
        gende = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        symptem = findViewById(R.id.symttt);
        layout = findViewById(R.id.layout);
        sym_head = findViewById(R.id.sym_head);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rec.setNestedScrollingEnabled(false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Old Checkup Lists");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        progress = new ACProgressFlower.Builder(OldCheckupList.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        id = getIntent().getStringExtra("id");

        getOldRecords(id);
        //get_details(id);


    }

    private void getOldRecords(final String id) {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_OLD_RECORDS_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        Log.e("etlabrec", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                JSONArray jsonArray = ob.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    model.add(new OldCheckupModel(jsonObject.getString("id"), jsonObject.getString("date"), jsonObject.getString("name"), jsonObject.getString("gender"), jsonObject.getString("age")));

                                }
                                adapter = new OldCheckupAdapter(getApplicationContext(), model, new OldCheckupAdapter.Click() {
                                    @Override
                                    public void Itemclick(OldCheckupModel list) {
                                        Intent intent = new Intent(getApplicationContext(), OldCheckupRec.class);
                                        intent.putExtra("id", list.getId());
                                        intent.putExtra("op_id", id);
                                        startActivity(intent);
                                    }
                                });
                                rec.setAdapter(adapter);


                            } else {
//
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

    private void get_details(final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.BOOKING_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                layout.setVisibility(View.VISIBLE);
                                name.setText(ob.getString("name"));
                                gende.setText(ob.getString("gender"));
                                age.setText(ob.getString("age"));
                                symptem.setText(ob.getString("symptom"));
                               /* if (!ob.getString("symptom").isEmpty()) {
                                    sym_head.setVisibility(View.VISIBLE);
                                } else {
                                    sym_head.setVisibility(View.GONE);
                                }*/
                            } else {
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
                params.put("op_id", id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }
}