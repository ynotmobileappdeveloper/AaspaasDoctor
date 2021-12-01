package com.ynot.aaspaasdoctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

public class PatientDetailsEdit extends AppCompatActivity {
    TextView name, gender, age, location, date,symptom;
    String id = "";
    ACProgressFlower progress;
    LinearLayout layout,sym;
    Spinner timespinner;
    String t="";
    ImageView edit;
    int day, month, year;
    ArrayList<SpinnerModel> model = new ArrayList<>();
    List<String> time = new ArrayList<>();
    String select_time = "";
Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details_edit);
        save=findViewById(R.id.save);
        progress = new ACProgressFlower.Builder(PatientDetailsEdit.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Patient Details");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        name = findViewById(R.id.test);
        gender = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        location = findViewById(R.id.location);
        date = findViewById(R.id.date);
        timespinner = findViewById(R.id.time);
        layout = findViewById(R.id.layout);
sym=findViewById(R.id.symtmmm);
symptom=findViewById(R.id.symptom);
        id = getIntent().getStringExtra("id");
        get_details();
        String dd = date.getText().toString();
        String[] sep = dd.split("/");
        day = Integer.parseInt(sep[0]);
        month = Integer.parseInt(sep[1]);
        year = Integer.parseInt(sep[2]);

        get_edit_time(date.getText().toString(),t);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(PatientDetailsEdit.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                        /*      Your code   to get date and time    */
                        selectedmonth = selectedmonth + 1;
                        date.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                        get_edit_time(date.getText().toString(),t);
                    }
                }, year, month - 1, day);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();


            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.size() > 0) {
                    if (!(timespinner.getSelectedItemPosition()>=0)) {
                        TextView error = (TextView) timespinner.getSelectedView();
                        error.setError("");
                        error.setTextColor(Color.RED);
                        error.setText("Choose Time !");
                        return;
                    }
                } else {
                    Toast.makeText(PatientDetailsEdit.this, "Choose other Date for Time !", Toast.LENGTH_LONG).show();
                    return;
                }
                edit_op();
            }
        });


    }

    private void edit_op() {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.EDIT_OP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resedit", response);

                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(PatientDetailsEdit.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(PatientDetailsEdit.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("op_id", id);
                params.put("date", date.getText().toString());
                params.put("slot_id", select_time);
                params.put("time", timespinner.getSelectedItem().toString());
                Log.e("edieddataop", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void get_edit_time(String string, final String t) {
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
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(PatientDetailsEdit.this,android.R.layout.simple_dropdown_item_1line, time) {
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
                                timespinner.setAdapter(adapter);
                                for(int j=0;j<model.size();j++){
                                    if(model.get(j).getTime().equals(t)){
                                        timespinner.setSelection(j);
                                    }
                                }

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
                params.put("booking_date", date.getText().toString());
                Log.e("edit_params", String.valueOf(params));
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
                                t=ob.getString("time");
                                location.setText(ob.getString("op_type"));
                                symptom.setText(ob.getString("symptom"));
                                if(ob.getString("symptom").equals(null) ||ob.getString("symptom").equals("")){
                                    sym.setVisibility(View.GONE);
                                }
                                else {
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