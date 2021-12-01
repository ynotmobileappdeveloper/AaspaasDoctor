package com.ynot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.ynot.aaspaasdoctor.Model.TimeSlot;
import com.ynot.aaspaasdoctor.R;
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

public class NextCheckup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
String id; ACProgressFlower progress;
TextView name,gende,age;
    EditText date,time,symptem;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
     Spinner place;
     Button save;
  List<TimeSlot> morningtimeSlotList=new ArrayList<>();
    List<TimeSlot> noontimeSlotList=new ArrayList<>();

    List<TimeSlot> eveningtimeSlotList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_checkup);
        date=findViewById(R.id.datenext);
        time=findViewById(R.id.nexttime);
        save=findViewById(R.id.save);
        symptem=findViewById(R.id.symptoms);
        progress = new ACProgressFlower.Builder(NextCheckup.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Next Checkup");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getStringExtra("id");
        name=findViewById(R.id.test);
        gende=findViewById(R.id.gender);
        age=findViewById(R.id.age);
      //  place=findViewById(R.id.spinner);

        get_details();


//        place.setOnItemSelectedListener(this);
//        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,places);
//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    //    place.setAdapter(aa);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                datePickerDialog = new DatePickerDialog(NextCheckup.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                date.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);
                                get_time_sloats(date.getText().toString());

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();


            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NextCheckup.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if(selectedHour>12){
                            selectedHour=selectedHour-12;
                        }
                        time.setText(selectedHour + ":" + selectedMinute);

                    }
                }, hour, minute, false );//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

save.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(time.getText().toString().length()>0 && date.getText().toString().length()>0 &&symptem.getText().toString().length()>0)
        {savenextdate();}
    }
});




    }

    private void get_time_sloats(final String dateee) {
        //GETTIMESLOAT

        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GETTIMESLOAT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("timesloats",response.toString());
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                JSONArray jsonArray=ob.getJSONArray("mrng_slot");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject ob2=jsonArray.getJSONObject(i);
                                   morningtimeSlotList.add(new TimeSlot(ob2.getString("slot_id"),ob2.getString("time")));

                                }
                                JSONArray jsonArray2=ob.getJSONArray("noon_slot");
                                for(int i=0;i<jsonArray2.length();i++){
                                    JSONObject ob3=jsonArray2.getJSONObject(i);
                                    noontimeSlotList.add(new TimeSlot(ob3.getString("slot_id"),ob3.getString("time")));

                                }
                                JSONArray jsonArray3=ob.getJSONArray("evening_slot");
                                for(int i=0;i<jsonArray3.length();i++){
                                    JSONObject ob4=jsonArray3.getJSONObject(i);
                                    eveningtimeSlotList.add(new TimeSlot(ob4.getString("slot_id"),ob4.getString("time")));

                                }





//                                {"status":true,"mrng_slot":[{"slot_id":"40","time":"10:00 am"},{"slot_id":"42","time":"10:30 pm"},{"slot_id":"45","time":"11:00 pm"},
//                                    {"slot_id":"50","time":"11:30 pm"},{"slot_id":"59","time":"11:30 pm"},{"slot_id":"68","time":"11:30 pm"}],
//                                    "noon_slot":[{"slot_id":"51","time":"12:00 pm"},{"slot_id":"52","time":"12:30 pm"},{"slot_id":"53","time":"01:00 pm"},{"slot_id":"60","time":"12:00 pm"},
//                                    {"slot_id":"61","time":"12:30 pm"},{"slot_id":"62","time":"01:00 pm"},{"slot_id":"69","time":"12:00 pm"},{"slot_id":"70","time":"12:30 pm"},
//                                    {"slot_id":"71","time":"01:00 pm"}],
//                                    "evening_slot":[{"slot_id":"54","time":"05:00 pm"},{"slot_id":"55","time":"05:30 pm"},{"slot_id":"56","time":"06:00 pm"},
//                                    {"slot_id":"57","time":"06:30 pm"},{"slot_id":"58","time":"07:00 pm"},{"slot_id":"63","time":"05:00 pm"},{"slot_id":"64","time":"05:30 pm"},
//                                    {"slot_id":"65","time":"06:00 pm"},{"slot_id":"66","time":"06:30 pm"},{"slot_id":"67","time":"07:00 pm"},{"slot_id":"72","time":"05:00 pm"},
//                                    {"slot_id":"73","time":"05:30 pm"},{"slot_id":"74","time":"06:00 pm"},{"slot_id":"75","time":"06:30 pm"},{"slot_id":"76","time":"07:00 pm"}]}


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
                params.put("doc_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("booking_date", dateee);
                Log.e("timesloats",params.toString());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void savenextdate() {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.NEXTCHECKUPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(NextCheckup.this, ""+ob.getBoolean("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(NextCheckup.this, ""+ob.getBoolean("message"), Toast.LENGTH_SHORT).show();

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
                params.put("symptom", symptem.getText().toString());
             //   params.put("place", place.getSelectedItem().toString());
                params.put("date", date.getText().toString());
                params.put("sloat_id", time.getText().toString());
//                user_id,op_id,symptom,place,date,time
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}