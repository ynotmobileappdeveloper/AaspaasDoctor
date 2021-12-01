package com.ynot.aaspaasdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.ynot.aaspaasdoctor.Webservice.SharedPrefManager;
import com.ynot.aaspaasdoctor.Webservice.URLs;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class PatientDetails extends AppCompatActivity {
    CardView history, nextCheckup, pre_and_lab;
    TextView name, gender, age, location, date, time, symptom;
    String id = "";
    ACProgressFlower progress;
    LinearLayout layout, sym;
    ImageView edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        progress = new ACProgressFlower.Builder(PatientDetails.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Patient Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sym = findViewById(R.id.symtommm);
        symptom = findViewById(R.id.symptom);
        edit = findViewById(R.id.edit);
        history = findViewById(R.id.recentmedirecods);
        nextCheckup = findViewById(R.id.next_checkup);
        pre_and_lab = findViewById(R.id.priscri);
        name = findViewById(R.id.test);
        gender = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        location = findViewById(R.id.location);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        layout = findViewById(R.id.layout);

        id = getIntent().getStringExtra("id");
        get_details();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id != null) {
                    Intent intent = new Intent(getApplicationContext(), PatientDetailsEdit.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }

            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), History.class);
                i.putExtra("id", id);
                startActivity(i);
            }
        });

        nextCheckup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AppointmentBooking.class);
                i.putExtra("id", id);
                startActivity(i);
            }
        });

        pre_and_lab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PriscriptionAndLab.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("id", id);
                startActivity(i);
            }
        });


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
                                layout.setVisibility(View.VISIBLE);
                                name.setText(ob.getString("name"));
                                gender.setText(ob.getString("gender"));
                                age.setText(ob.getString("age"));
                                date.setText(ob.getString("date"));
                                time.setText(ob.getString("time"));
                                location.setText(ob.getString("op_type"));
                                symptom.setText(ob.getString("symptom"));
                                if (ob.getString("symptom").equals(null) || ob.getString("symptom").equals("")) {
                                    sym.setVisibility(View.GONE);
                                } else {
                                    sym.setVisibility(View.VISIBLE);
                                }
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