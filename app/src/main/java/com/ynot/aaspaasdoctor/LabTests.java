package com.ynot.aaspaasdoctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.ynot.aaspaasdoctor.Model.Labtest;
import com.ynot.aaspaasdoctor.Model.TestModel;
import com.ynot.aaspaasdoctor.Webservice.SharedPrefManager;
import com.ynot.aaspaasdoctor.Webservice.URLs;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class LabTests extends AppCompatActivity {
    String id;
    TextView name, gende, age;
    ACProgressFlower progress;
    FloatingActionButton add;
    Button save;
    EditText discription, symptom;
    List<Labtest> labTestsList = new ArrayList<>();
    List<String> test_name = new ArrayList<>();
    ArrayList<TestModel> model = new ArrayList<>();
    String test_id;
    Spinner test_spinner;
    TextView date_pick;
    ArrayAdapter<String> adapter;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_tests);
        Toolbar toolbar = findViewById(R.id.toolbar);
        save = findViewById(R.id.save);
        add = findViewById(R.id.add);
        discription = findViewById(R.id.discri);
        test_spinner = findViewById(R.id.test_spinner);
        date_pick = findViewById(R.id.date_pick);
        //    sym=findViewById(R.id.sym);
        symptom = findViewById(R.id.symtem);
        progress = new ACProgressFlower.Builder(LabTests.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        toolbar.setTitle(R.string.pris);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getStringExtra("id");
        Log.i("id", id);
        name = findViewById(R.id.test);
        gende = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        get_details();
        get_tests();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                labTestsList.add(new Labtest(test_id, date_pick.getText().toString(), discription.getText().toString()));
                discription.getText().clear();
                date_pick.setText("");
                //  adapter.notifyDataSetChanged();
                test_id = "";
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(test_id)) {
                    TextView error = (TextView) test_spinner.getSelectedView();
                    error.setError("");
                    error.setTextColor(Color.RED);
                    error.setText("Choose Test !");
                    return;
                }
                if (date_pick.getText().toString().isEmpty()) {
                    Toast.makeText(LabTests.this, "Please choose date !!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (discription.getText().toString().isEmpty()) {
                    discription.setError("Please fill this field !");
                    discription.requestFocus();
                }
                savedata();

            }
        });

        test_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(android.R.id.text1);
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                    test_id = "";
                } else {
                    textView.setTextColor(Color.BLACK);
                    test_id = model.get(position - 1).getId();
                    Log.e("id", test_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        date_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MM/yyyy"; // your format
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                        date_pick.setText(sdf.format(myCalendar.getTime()));
                    }

                };
                new DatePickerDialog(LabTests.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


    }

    private void get_tests() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_ALL_TESTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {

                                JSONArray array = ob.getJSONArray("data");
                                test_name = new ArrayList<>();
                                test_name.add("Choose Tests..");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    test_name.add(obb.getString("name"));
                                    model.add(new TestModel(obb.getString("id"), obb.getString("name")));
                                    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, test_name) {
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
                                    test_spinner.setAdapter(adapter);
                                }


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Nullable
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

    private void savedata() {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SAVELABTESTLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {

                                Toast.makeText(LabTests.this, "" + ob.getString("message"), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(LabTests.this, "" + ob.getString("message"), Toast.LENGTH_SHORT).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("op_id", id);
                params.put("date", date_pick.getText().toString());
                params.put("note", discription.getText().toString());
                params.put("test_id", test_id);
                Log.e("input_params", params.toString());

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
}