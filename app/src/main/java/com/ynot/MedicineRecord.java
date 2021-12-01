package com.ynot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.aaspaasdoctor.Adapter.DocMedicineAdapter;
import com.ynot.aaspaasdoctor.Adapter.MediRecAdapter2;
import com.ynot.aaspaasdoctor.DocMedicineDetail;
import com.ynot.aaspaasdoctor.Model.DocmedicineModel;
import com.ynot.aaspaasdoctor.Model.Medipriscription;
import com.ynot.aaspaasdoctor.R;
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

public class MedicineRecord extends AppCompatActivity {
    String id, pdflink = "http://www.africau.edu/images/default/sample.pdf";
    ACProgressFlower progress;
    TextView name, gende, age, symptem, docname, condate, sympt, tv, nodata_found;
    ArrayList<DocmedicineModel> model = new ArrayList<>();
    LinearLayout nodata;
    DocMedicineAdapter adapter;
    RecyclerView rec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_record);
        symptem = findViewById(R.id.symttt);
        tv = findViewById(R.id.textView16);
        nodata_found = findViewById(R.id.nodata_found);
        progress = new ACProgressFlower.Builder(MedicineRecord.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Medicine Records");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        id = getIntent().getStringExtra("id");

        rec = findViewById(R.id.rec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rec.setLayoutManager(linearLayoutManager);

        get_data();

    }

    private void get_data() {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_DOC_MEDICINE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                nodata_found.setVisibility(View.GONE);
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    DocmedicineModel data = new DocmedicineModel();
                                    data.setAge(obb.getString("age"));
                                    data.setId(obb.getString("id"));
                                    data.setName(obb.getString("name"));
                                    data.setDate(obb.getString("date"));
                                    data.setGender(obb.getString("gender"));
                                    data.setStatus(obb.getString("image"));
                                    data.setMedicine(obb.getString("medicines"));
                                    model.add(data);

                                }
                                adapter = new DocMedicineAdapter(getApplicationContext(), model, new DocMedicineAdapter.Click() {
                                    @Override
                                    public void ItemClick(DocmedicineModel list) {
                                        Intent i = new Intent(getApplicationContext(), DocMedicineDetail.class);
                                        i.putExtra("id", list.getId());
                                        startActivity(i);
                                    }
                                });
                                rec.setAdapter(adapter);
                            } else {
                                nodata_found.setVisibility(View.VISIBLE);
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
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("op_id", id);
                Log.e("input", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }
}