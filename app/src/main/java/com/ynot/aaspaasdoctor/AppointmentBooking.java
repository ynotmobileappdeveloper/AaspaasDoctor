package com.ynot.aaspaasdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.aaspaasdoctor.Adapter.EveningSlotAdpater;
import com.ynot.aaspaasdoctor.Adapter.MorningSlotAdpater;
import com.ynot.aaspaasdoctor.Adapter.NoonSlotAdpater;
import com.ynot.aaspaasdoctor.Model.TimeSlot;
import com.ynot.aaspaasdoctor.Webservice.SharedPrefManager;
import com.ynot.aaspaasdoctor.Webservice.URLs;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class AppointmentBooking extends AppCompatActivity {
    String id; ACProgressFlower progress;
    TextView name,gende,age;
    EditText date,time,symptem;
    RecyclerView morning_rec, noon_rec, evening_rec;
    ArrayList<TimeSlot> mornig_model = new ArrayList<>();
    ArrayList<TimeSlot> noon_model = new ArrayList<>();
    ArrayList<TimeSlot> evenig_model = new ArrayList<>();
    MorningSlotAdpater morningSlotAdpater;
    NoonSlotAdpater noonSlotAdpater;
    EveningSlotAdpater eveningSlotAdpater;
    CalendarView calender;
    String select_date = "", doc_id = "", select_slot = "";
    Button book;
    ACProgressFlower dialog;
    TextView morning_txt, noon_txt, evening_txt, no_slots;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_booking);
        date=findViewById(R.id.datenext);
        time=findViewById(R.id.nexttime);
        symptem=findViewById(R.id.symptoms);
        progress = new ACProgressFlower.Builder(AppointmentBooking.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        id = getIntent().getStringExtra("id");
        name=findViewById(R.id.test);
        gende=findViewById(R.id.gender);
        age=findViewById(R.id.age);
        //
        get_details();
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Next Checkup");
        doc_id = getIntent().getStringExtra("id");
        Date d = new Date();
        CharSequence s = DateFormat.format("d-MM-yyyy ", d.getTime());
        select_date = String.valueOf(s);
        Log.e("select_date", select_date);

        morning_rec = findViewById(R.id.morning);
        calender = findViewById(R.id.calender);
        noon_rec = findViewById(R.id.noon);
        book = findViewById(R.id.book);
        evening_rec = findViewById(R.id.evening);
        morning_txt = findViewById(R.id.morning_txt);
        noon_txt = findViewById(R.id.noon_txt);
        evening_txt = findViewById(R.id.evening_txt);
        layout = findViewById(R.id.layout);
        no_slots = findViewById(R.id.no_slots);


        morning_rec.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        noon_rec.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        evening_rec.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

        get_time_slots(select_date);

        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String y = String.valueOf(year);
                String m = String.valueOf(month + 1);
                String d = String.valueOf(dayOfMonth);
                select_date = d + "-" + m + "-" + y;
                get_time_slots(select_date);
            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(symptem.getText().toString().length()>0){

                booking();
                }
                else {
                    symptem.requestFocus();
                    symptem.setError("please fill symptoms ");
                }
            }
        });


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
    private void booking() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.NEXTCHECKUPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("addresp", response);

                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(AppointmentBooking.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(AppointmentBooking.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.toString());
                dialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("op_id", id);
                params.put("symptom", symptem.getText().toString());
                params.put("booking_date", select_date);
                params.put("slot_id", select_slot);
                Log.e("add_doctor_booking",params.toString());
                return params;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    private void get_time_slots(final String select_date) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GETTIMESLOAT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                no_slots.setVisibility(View.GONE);
                                book.setVisibility(View.VISIBLE);
                                layout.setVisibility(View.VISIBLE);
                                JSONArray mrng = ob.getJSONArray("mrng_slot");
                                mornig_model = new ArrayList<>();
                                for (int i = 0; i < mrng.length(); i++) {
                                    JSONObject ob_mrng = mrng.getJSONObject(i);
                                    mornig_model.add(new TimeSlot(ob_mrng.getString("slot_id"), ob_mrng.getString("time")));
                                }
                                if (mornig_model.size() > 0) {
                                    morning_txt.setVisibility(View.VISIBLE);
                                } else {
                                    morning_txt.setVisibility(View.GONE);
                                }
                                morningSlotAdpater = new MorningSlotAdpater(getApplicationContext(), mornig_model, new MorningSlotAdpater.Select() {
                                    @Override
                                    public void Click(String status, TimeSlot model) {
                                        if (status.equals("yes")) {
                                            noonSlotAdpater.disable();
                                            eveningSlotAdpater.disable();
                                            eveningSlotAdpater.notifyDataSetChanged();
                                            noonSlotAdpater.notifyDataSetChanged();
                                            select_slot = model.getSlot_id();
                                            Log.e("select", select_slot);
                                        }

                                    }
                                });
                                morning_rec.setAdapter(morningSlotAdpater);


                                JSONArray noon = ob.getJSONArray("noon_slot");
                                noon_model = new ArrayList<>();
                                for (int i = 0; i < noon.length(); i++) {
                                    JSONObject ob_noon = noon.getJSONObject(i);
                                    noon_model.add(new TimeSlot(ob_noon.getString("slot_id"), ob_noon.getString("time")));
                                }
                                if (noon_model.size() > 0) {
                                    noon_txt.setVisibility(View.VISIBLE);
                                } else {
                                    noon_txt.setVisibility(View.GONE);
                                }
                                noonSlotAdpater = new NoonSlotAdpater(getApplicationContext(), noon_model, new NoonSlotAdpater.Select() {
                                    @Override
                                    public void Click(String status, TimeSlot model) {
                                        if (status.equals("yes")) {
                                            morningSlotAdpater.disable();
                                            eveningSlotAdpater.disable();
                                            eveningSlotAdpater.notifyDataSetChanged();
                                            morningSlotAdpater.notifyDataSetChanged();
                                            select_slot = model.getSlot_id();
                                            Log.e("select", select_slot);
                                        }
                                    }
                                });
                                noon_rec.setAdapter(noonSlotAdpater);

                                JSONArray eve = ob.getJSONArray("evening_slot");
                                evenig_model = new ArrayList<>();
                                for (int i = 0; i < eve.length(); i++) {
                                    JSONObject ob_eve = eve.getJSONObject(i);
                                    evenig_model.add(new TimeSlot(ob_eve.getString("slot_id"), ob_eve.getString("time")));
                                }
                                if (evenig_model.size() > 0) {
                                    evening_txt.setVisibility(View.VISIBLE);
                                } else {
                                    evening_txt.setVisibility(View.GONE);
                                }
                                eveningSlotAdpater = new EveningSlotAdpater(getApplicationContext(), evenig_model, new EveningSlotAdpater.Select() {
                                    @Override
                                    public void Click(String status, TimeSlot model) {
                                        if (status.equals("yes")) {
                                            morningSlotAdpater.disable();
                                            noonSlotAdpater.disable();
                                            noonSlotAdpater.notifyDataSetChanged();
                                            morningSlotAdpater.notifyDataSetChanged();
                                            select_slot = model.getSlot_id();
                                            Log.e("select", select_slot);

                                        }
                                    }
                                });
                                evening_rec.setAdapter(eveningSlotAdpater);
                            } else {
                                no_slots.setVisibility(View.VISIBLE);
                                book.setVisibility(View.GONE);
                                layout.setVisibility(View.GONE);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("doc_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("booking_date", select_date);
                Log.e("slot_params", String.valueOf(params));
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
        }
        return super.onOptionsItemSelected(item);
    }


}