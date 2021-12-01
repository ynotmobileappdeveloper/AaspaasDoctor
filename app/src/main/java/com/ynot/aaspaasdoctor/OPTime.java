package com.ynot.aaspaasdoctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.aaspaasdoctor.Model.SpinnerModel;
import com.ynot.aaspaasdoctor.Webservice.SharedPrefManager;
import com.ynot.aaspaasdoctor.Webservice.URLs;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class OPTime extends AppCompatActivity {

    RelativeLayout cancel;
    Button save;
    TimePickerDialog timePickerDialog;
    EditText time_txt, date, op;
    Calendar calendar;
    int currentHour;
    int currentMinute;
    int day, month, year;
    String op_id = "";
    ACProgressFlower progress;
    Spinner spinner_time;
    ArrayList<SpinnerModel> model = new ArrayList<>();
    List<String> time = new ArrayList<>();
    String select_time = "";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_p_time);
        progress = new ACProgressFlower.Builder(OPTime.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit OP Time");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);
        spinner_time = findViewById(R.id.spinner_time);
        date = findViewById(R.id.date);
        op = findViewById(R.id.op);

        op.setText(getIntent().getStringExtra("op"));
        date.setText(getIntent().getStringExtra("date"));
        // time_txt.setText(getIntent().getStringExtra("time"));
        op_id = getIntent().getStringExtra("id");


        String dd = date.getText().toString();
        String[] sep = dd.split("/");
        day = Integer.parseInt(sep[0]);
        month = Integer.parseInt(sep[1]);
        year = Integer.parseInt(sep[2]);

        get_edit_time(date.getText().toString());


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.size() > 0) {
                    if (select_time.isEmpty()) {
                        TextView error = (TextView) spinner_time.getSelectedView();
                        error.setError("");
                        error.setTextColor(Color.RED);
                        error.setText("Choose Time !");
                        return;
                    }
                } else {
                    Toast.makeText(OPTime.this, "Choose other Date for Time !", Toast.LENGTH_LONG).show();
                    return;
                }
                edit_op();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm_booking("2");
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(OPTime.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                        /*      Your code   to get date and time    */
                        selectedmonth = selectedmonth + 1;
                        date.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                        get_edit_time(date.getText().toString());
                    }
                }, year, month - 1, day);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();

            }
        });
        spinner_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = view.findViewById(android.R.id.text1);
                if (i == 0) {
                    textView.setTextColor(Color.GRAY);
                    select_time = "";
                } else {
                    textView.setTextColor(Color.BLACK);
                    select_time = model.get(i - 1).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void get_edit_time(final String date) {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.EDIT_TIME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("edit_resp", response);
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {

                                JSONArray array = ob.getJSONArray("timeslots");
                                model = new ArrayList<>();
                                time = new ArrayList<>();
                                time.add("Choose Time");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new SpinnerModel(obb.getString("slot_id"), obb.getString("time")));
                                    time.add(obb.getString("time"));
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(OPTime.this, android.R.layout.simple_dropdown_item_1line, time) {
                                    @Override
                                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                        View view = super.getDropDownView(position, convertView, parent);
                                        TextView tv = (TextView) view;
                                        if (position == 0) {
                                            tv.setTextColor(Color.GRAY);
                                        } else {
                                            tv.setTextColor(Color.BLACK);
                                        }
                                        return view;
                                    }
                                };
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner_time.setAdapter(adapter);

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
                params.put("booking_date", date);
                Log.e("edit_params", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void edit_op() {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.EDIT_OP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(OPTime.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(OPTime.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", String.valueOf(error));
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("op_id", op_id);
                params.put("date", date.getText().toString());
                params.put("slot_id", select_time);
                params.put("time", spinner_time.getSelectedItem().toString());
                Log.e("edieddataop", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void confirm_booking(final String s) {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CONFIRM_OP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(OPTime.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(OPTime.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("status", s);
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