package com.ynot.aaspaasdoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.ynot.aaspaasdoctor.Adapter.MediRecAdapter;
import com.ynot.aaspaasdoctor.Model.MedicineModel;
import com.ynot.aaspaasdoctor.Model.Medipriscription;
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

public class Medicines extends AppCompatActivity {
    String id;
    TextView gende, age, user_name;
    AutoCompleteTextView name;
    EditText symptom, days, qty;
    ACProgressFlower progress;
    CardView medicine, lab;
    LinearLayout sym;
    String medi_id = "";
    RecyclerView medicinerec;
    Button save, save_btn;
    FloatingActionButton add;
    List<Medipriscription> medipriscriptionList = new ArrayList<>();
    MediRecAdapter mediRecAdapter;
    Dialog dialog;
    List<String> medicines = new ArrayList<>();
    ArrayList<MedicineModel> medi_model = new ArrayList<>();
    ArrayAdapter<String> adapter;
    int count = 0;
    LinearLayout header;
    String save_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicines);
        dialog = new Dialog(Medicines.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        medicinerec = findViewById(R.id.medicinerecyler);
        sym = findViewById(R.id.sym);
        save = findViewById(R.id.save);
        add = findViewById(R.id.add);
        header = findViewById(R.id.header);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        medicinerec.setLayoutManager(linearLayoutManager);
        medicinerec.setNestedScrollingEnabled(false);


        symptom = findViewById(R.id.symptoms);
        progress = new ACProgressFlower.Builder(Medicines.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        toolbar.setTitle("Medicine list");
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        id = getIntent().getStringExtra("id");
        user_name = findViewById(R.id.test);
        gende = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        get_details();
        dialog = new Dialog(Medicines.this);
        dialog.setContentView(R.layout.dialog_type);
        dialog.setCanceledOnTouchOutside(true);
        save_btn = dialog.findViewById(R.id.save_btn);
        name = (AutoCompleteTextView) dialog.findViewById(R.id.name);
        days = dialog.findViewById(R.id.days);
        qty = dialog.findViewById(R.id.qty);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                header.setVisibility(View.VISIBLE);
                dialog.show();
                name.setText("");
                days.setText("");
                qty.setText("");
                name.requestFocus();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String model = gson.toJson(medipriscriptionList);
                Log.e("select", model);
                if (medipriscriptionList.size() > 0) {
                    savedata(model);
                } else {
                    Toast.makeText(Medicines.this, "Please Add Medicine !!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        get_products();

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty()) {
                    name.setError("Please fill this field !!");
                    return;
                }
                if (qty.getText().toString().isEmpty()) {
                    qty.setError("Please fill this field !!");
                    return;
                }
                if (days.getText().toString().isEmpty()) {
                    days.setError("Please fill this field !!");
                    return;
                }
                if (medi_id.isEmpty()) {
                    name.setError("Please choose from list");
                    Toast.makeText(Medicines.this, "Choose medicine from POPUP", Toast.LENGTH_LONG).show();
                    return;
                }

                dialog.dismiss();
                count++;

                medipriscriptionList.add(new Medipriscription(name.getText().toString(), qty.getText().toString(), days.getText().toString(), medi_id));
                mediRecAdapter = new MediRecAdapter(getApplicationContext(), medipriscriptionList, new MediRecAdapter.Click() {
                    @Override
                    public void delete(int position) {
                        medipriscriptionList.remove(position);
                        mediRecAdapter.notifyItemRemoved(position);
                        mediRecAdapter.notifyItemRangeChanged(position, medipriscriptionList.size());

                        if (medipriscriptionList.size() > 0) {
                            header.setVisibility(View.VISIBLE);
                        } else {
                            count = 0;
                            header.setVisibility(View.VISIBLE);
                        }
                    }
                });
                medicinerec.setAdapter(mediRecAdapter);
            }


        });

        name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                medi_id = medi_model.get(position).getId();
            }
        });


    }

    private void savedata(final String model) {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SAVEMEDICINELIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {

                                Toast.makeText(Medicines.this, "" + ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(Medicines.this, "" + ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("symptoms", symptom.getText().toString());
                params.put("medi_list", model);
                Log.e("medilistpost", params.toString());
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
                        Log.e("resp",response);
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {

                                user_name.setText(ob.getString("name"));
                                gende.setText(ob.getString("gender"));
                                age.setText(ob.getString("age"));
                                //  symptem.setText(ob.getString("symptom"));

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

    private void get_products() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.MEDICAL_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                JSONArray array = ob.getJSONArray("products");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    medicines.add(obb.getString("name"));
                                    medi_model.add(new MedicineModel(obb.getString("p_id"), obb.getString("name")));

                                }
                                adapter = new ArrayAdapter<String>(Medicines.this, android.R.layout.simple_spinner_dropdown_item, medicines);
                                name.setAdapter(adapter);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

}