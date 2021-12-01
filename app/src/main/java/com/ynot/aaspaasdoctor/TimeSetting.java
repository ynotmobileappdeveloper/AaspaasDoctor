package com.ynot.aaspaasdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.ynot.aaspaasdoctor.Adapter.DayAdapter;
import com.ynot.aaspaasdoctor.Model.Day;
import com.ynot.aaspaasdoctor.Webservice.SharedPrefManager;
import com.ynot.aaspaasdoctor.Webservice.URLs;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class TimeSetting extends AppCompatActivity {

    RecyclerView rec;
    ArrayList<Day> model = new ArrayList<>();
    ArrayList<Day> select_days = new ArrayList<>();
    DayAdapter adapter;
    Button save;
    String type = "";
    ACProgressFlower progress;
    int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_setting);
        progress = new ACProgressFlower.Builder(TimeSetting.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type = getIntent().getStringExtra("type");

        rec = findViewById(R.id.rec);
        save = findViewById(R.id.save);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        model = new ArrayList<>();
        model.add(new Day("Monday", "", ""));
        model.add(new Day("Tuesday", "", ""));
        model.add(new Day("Wednesday", "", ""));
        model.add(new Day("Thursday", "", ""));
        model.add(new Day("Friday", "", ""));
        model.add(new Day("Saturday", "", ""));
        model.add(new Day("Sunday", "", ""));

        adapter = new DayAdapter(TimeSetting.this, model, new DayAdapter.Select() {
            @Override
            public void SelectedDays(ArrayList<Day> model) {
                select_days = model;
            }
        });
        rec.setAdapter(adapter);

        getOptime();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String model = gson.toJson(select_days);
                Log.e("select", model);
                status = 0;
                for (int i = 0; i < select_days.size(); i++) {
                    if (!select_days.get(i).getStartime().equals("") && !select_days.get(i).getEndtime().equals("")) {
                        status = 1;
                    }
                    if (select_days.get(i).getStartime().length() > 2 && select_days.get(i).getEndtime().length() > 2) {
                        status = 2;
                    }


                }


                if (status == 1) {
                    Toast.makeText(TimeSetting.this, "Submitting Empty list", Toast.LENGTH_SHORT).show();
                    add_op_time(model);
                } else {
                    add_op_time(model);
                }
            }
        });

    }

    private void getOptime() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GETOPTIME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progress.dismiss();
                        Log.e("getoptimeresponse", response);

                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                JSONArray jsonArray = ob.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject ob2 = jsonArray.getJSONObject(i);
                                    if (ob2.getString("day").equals(model.get(i).getDay())) {
                                        model.get(i).setStartime(ob2.getString("start_time"));
                                        model.get(i).setEndtime(ob2.getString("end_time"));
                                    }
                                    //model.add(new Day(ob2.getString("day"),,));

                                }
                                adapter = new DayAdapter(TimeSetting.this, model, new DayAdapter.Select() {
                                    @Override
                                    public void SelectedDays(ArrayList<Day> model) {
                                        select_days = model;
                                    }
                                });
                                rec.setAdapter(adapter);


                            } else {
                                // Toast.makeText(TimeSetting.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("type", type);
                Log.e("getoptime(", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void add_op_time(final String model) {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.ADD_OP_TIME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(TimeSetting.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Consultation.class));
                                finish();
                            } else {
                                Toast.makeText(TimeSetting.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("data", model);
                params.put("type", type);
                Log.e("inp", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}