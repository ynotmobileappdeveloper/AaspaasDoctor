package com.ynot.aaspaasdoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.aaspaasdoctor.Adapter.TodayBookingAdapter;
import com.ynot.aaspaasdoctor.Model.BookingModel;
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

public class Booking2 extends AppCompatActivity {
    RecyclerView book_rec;
    ArrayList<BookingModel> model = new ArrayList<>();
    ACProgressFlower progress;
    String from;
    Toolbar toolbar;
    LinearLayout daysss;
    TodayBookingAdapter todayBookingAdapter;
    Button today, week, month;
    LinearLayout nodata;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking2);
        daysss = findViewById(R.id.days);
        toolbar = findViewById(R.id.toolbar);
        today = findViewById(R.id.btntody);
        nodata = findViewById(R.id.nodata);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        week = findViewById(R.id.btnweek);
        month = findViewById(R.id.btnmonth);
        // setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setTitle("Booking");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progress = new ACProgressFlower.Builder(Booking2.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        book_rec = findViewById(R.id.book_rec);
        book_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        book_rec.setAdapter(todayBookingAdapter);
        from = getIntent().getStringExtra("from");
        if (getIntent().getStringExtra("status").equals("recent")) {
            getRecentBooking();
            daysss.setVisibility(View.GONE);

        } else {


            if (getIntent().getStringExtra("status").equals("today")) {
                daysss.setVisibility(View.VISIBLE);
                get_doctor_bookings("today");
            } else {
                daysss.setVisibility(View.GONE);
                getBooking(getIntent().getStringExtra("status"));
            }
        }

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_doctor_bookings("today");
                //  todayBookingAdapter.notifyDataSetChanged();
                week.setBackgroundResource(R.drawable.login_btnwhite);
                week.setTextColor(Color.BLACK);
                month.setBackgroundResource(R.drawable.login_btnwhite);
                month.setTextColor(Color.BLACK);
                today.setBackgroundResource(R.drawable.login_btn);
                today.setTextColor(Color.WHITE);
            }
        });
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_doctor_bookings("monthly");
                //  todayBookingAdapter.notifyDataSetChanged();
                week.setBackgroundResource(R.drawable.login_btnwhite);
                week.setTextColor(Color.BLACK);
                month.setBackgroundResource(R.drawable.login_btn);
                month.setTextColor(Color.WHITE);
                today.setBackgroundResource(R.drawable.login_btnwhite);
                today.setTextColor(Color.BLACK);


            }
        });
        week.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                get_doctor_bookings("weekly");
                week.setBackgroundResource(R.drawable.login_btn);
                week.setTextColor(Color.WHITE);
                month.setBackgroundResource(R.drawable.login_btnwhite);
                month.setTextColor(Color.BLACK);
                today.setBackgroundResource(R.drawable.login_btnwhite);
                today.setTextColor(Color.BLACK);

                //todayBookingAdapter.notifyDataSetChanged();
            }
        });
    }

    private void get_doctor_bookings(final String today) {
        //progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_DOCTOR_BOOKING,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (model.size() > 0) {
                            model.clear();
                            nodata.setVisibility(View.VISIBLE);
                        }
                        Log.e("booking_resp", response);
                        // progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new BookingModel(obb.getString("book_id"), obb.getString("name"), obb.getString("gender"), obb.getString("age"), obb.getString("date"), obb.getString("time"), obb.getString("op_type"), obb.getString("op_status"), obb.getString("symptom")));
                                }
                                todayBookingAdapter = new TodayBookingAdapter(getApplicationContext(), model, new TodayBookingAdapter.ItemClick() {
                                    @Override
                                    public void Click(BookingModel model) {
                                        if (model.getStatus().equals("0")) {
                                            Intent i = new Intent(getApplicationContext(), BookingDetails.class);
                                            i.putExtra("id", model.getId());
                                            startActivity(i);
                                        } else if (model.getStatus().equals("1")) {
                                            Intent i = new Intent(getApplicationContext(), PatientDetails.class);
                                            i.putExtra("id", model.getId());
                                            startActivity(i);
                                        }
                                    }
                                });
                                book_rec.setAdapter(todayBookingAdapter);


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
                // progress.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("status", today);
                Log.e("params", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    private void getRecentBooking() {
        // progress.show();
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.BOOKINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  progress.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            progressDialog.dismiss();
                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
//                                for (int i = 0; i < array.length(); i++) {
//                                    JSONObject obb = array.getJSONObject(i);
//                                    model.add(new BookingModel(obb.getString("op_id"), obb.getString("name"), obb.getString("gender"), obb.getString("age"), obb.getString("date"), obb.getString("time"), obb.getString("op_type"), obb.getString("op_status")));
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);


                                    model.add(new BookingModel(obb.getString("op_id"), obb.getString("name"), obb.getString("gender"), obb.getString("age"), obb.getString("date"), obb.getString("time"), obb.getString("op_type"), obb.getString("op_status"), obb.getString("symptom")));
                                }
                                book_rec.setAdapter(new TodayBookingAdapter(getApplicationContext(), model, new TodayBookingAdapter.ItemClick() {
                                    @Override
                                    public void Click(BookingModel model) {
                                        if (model.getStatus().equals("0")) {
                                            Intent i = new Intent(getApplicationContext(), BookingDetails.class);
                                            i.putExtra("id", model.getId());
                                            startActivity(i);
                                        } else if (model.getStatus().equals("1")) {
                                            Intent i = new Intent(getApplicationContext(), PatientDetails.class);
                                            i.putExtra("id", model.getId());
                                            startActivity(i);
                                        }
                                    }
                                }));


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
                //  progress.dismiss();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void getBooking(final String day) {
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.TOTAL_BOOKING,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (model.size() > 0) {
                            model.clear();
                        }
                        Log.e("booking_resp", response);
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new BookingModel(obb.getString("book_id"), obb.getString("name"), obb.getString("gender"), obb.getString("age"), obb.getString("date"), obb.getString("time"), obb.getString("op_type"), obb.getString("op_status"), obb.getString("symptom")));
                                }
                                if (day.equals("today")) {
                                    daysss.setVisibility(View.VISIBLE);
                                }
                                todayBookingAdapter = new TodayBookingAdapter(getApplicationContext(), model, new TodayBookingAdapter.ItemClick() {
                                    @Override
                                    public void Click(BookingModel model) {
                                        if (model.getStatus().equals("0")) {
                                            Intent i = new Intent(getApplicationContext(), BookingDetails.class);
                                            i.putExtra("id", model.getId());
                                            startActivity(i);
                                        } else if (model.getStatus().equals("1")) {
                                            Intent i = new Intent(getApplicationContext(), PatientDetails.class);
                                            i.putExtra("id", model.getId());
                                            startActivity(i);
                                        }
                                    }
                                });
                                book_rec.setAdapter(todayBookingAdapter);


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
                progress.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("status", day);
                Log.e("params", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}