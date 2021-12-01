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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.aaspaasdoctor.Adapter.BookingAdapter;
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

public class Booking extends AppCompatActivity {
    RecyclerView book_rec;
    ArrayList<BookingModel> model = new ArrayList<>();
    ACProgressFlower progress;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Booking");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progress = new ACProgressFlower.Builder(Booking.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        book_rec = findViewById(R.id.book_rec);
        book_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        from=getIntent().getStringExtra("from");
if(getIntent().getStringExtra("status").equals("recent")){
    getRecentBooking();
}
else {
    get_booking(getIntent().getStringExtra("status"));

}
    }

    private void getRecentBooking() {
       // progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.BOOKINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                      //  progress.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {

                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
//                                for (int i = 0; i < array.length(); i++) {
//                                    JSONObject obb = array.getJSONObject(i);
//                                    model.add(new BookingModel(obb.getString("op_id"), obb.getString("name"), obb.getString("gender"), obb.getString("age"), obb.getString("date"), obb.getString("time"), obb.getString("op_type"), obb.getString("op_status")));
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);



                                    model.add(new BookingModel(obb.getString("op_id"), obb.getString("name"), obb.getString("gender"), obb.getString("age"), obb.getString("date"), obb.getString("time"), obb.getString("op_type"), obb.getString("op_status"),obb.getString("symptom")));
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

                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  progress.dismiss();

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

    private void getTodaysBooking(final String s) {
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.TOTAL_BOOKING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("booking_resp", response);
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {

                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new BookingModel(obb.getString("book_id"), obb.getString("name"), obb.getString("gender"), obb.getString("age"), obb.getString("date"), obb.getString("time"), obb.getString("op_type"), obb.getString("op_status"),obb.getString("symptom")));
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
                params.put("status", s);
                Log.e("params", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void get_booking(final String status) {
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.TOTAL_BOOKING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("booking_resp", response);
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {

                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new BookingModel(obb.getString("book_id"), obb.getString("name"), obb.getString("gender"), obb.getString("age"), obb.getString("date"), obb.getString("time"), obb.getString("op_type"), obb.getString("op_status"),obb.getString("symptom")));
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
                params.put("status", status);
                Log.e("params", String.valueOf(params));
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