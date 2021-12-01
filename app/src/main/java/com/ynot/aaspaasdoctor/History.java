package com.ynot.aaspaasdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.LabRecord;
import com.ynot.MedicineRecord;
import com.ynot.OldCheckupRec;
import com.ynot.Surgeryrecord;
import com.ynot.aaspaasdoctor.Adapter.HistoryAdapter;
import com.ynot.aaspaasdoctor.Model.HistoryModel;
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

public class History extends AppCompatActivity {

    RecyclerView history_rec;
    CardView recentmedi,oldcheckup,labrec,medicinecre,surgicalrecord;
    ArrayList<HistoryModel> model = new ArrayList<>();
    ACProgressFlower progress;
    TextView nodata;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        id=getIntent().getStringExtra("id");
        recentmedi=findViewById(R.id.recentmedirecods);
        recentmedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent intent=new Intent(getApplicationContext(), MedicalRecord.class);
             intent.putExtra("id",id);
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             startActivity(intent);
            }
        });
        oldcheckup=findViewById(R.id.oldchkup);
        oldcheckup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), OldCheckupList.class);
                intent.putExtra("id",id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        labrec=findViewById(R.id.labrecord);
        labrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), LabRecord.class);
                intent.putExtra("id",id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        medicinecre=findViewById(R.id.medirecords);
        medicinecre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), MedicineRecord.class);
                intent.putExtra("id",id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        surgicalrecord=findViewById(R.id.surgeryrecords);
        surgicalrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), Surgeryrecord.class);
                intent.putExtra("id",id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });




        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Patient History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progress = new ACProgressFlower.Builder(History.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        history_rec = findViewById(R.id.history_rec);
        nodata = findViewById(R.id.nodata);
        history_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        get_history(getIntent().getStringExtra("id"));





    }

    private void get_history(final String id) {

        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.HISTORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("history_resp", response);
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                history_rec.setVisibility(View.VISIBLE);
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    if(id.equals(obb.getString("book_id"))){
                                    model.add(new HistoryModel(obb.getString("book_id"), obb.getString("name"), obb.getString("gender"), obb.getString("age"), obb.getString("date"), obb.getString("time"), obb.getString("op_type"), obb.getString("op_status")));
                                }
                                }
                                history_rec.setAdapter(new HistoryAdapter(getApplicationContext(), model));

                            } else {
                                nodata.setVisibility(View.VISIBLE);
                                history_rec.setVisibility(View.GONE);
                                //recentmedi,oldcheckup,labrec,medicinecre,surgicalrecord;
                                recentmedi.setVisibility(View.GONE);
                                oldcheckup.setVisibility(View.GONE);
                                labrec.setVisibility(View.GONE);
                                medicinecre.setVisibility(View.GONE);
                                surgicalrecord.setVisibility(View.GONE);


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
                params.put("book_id", id);
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