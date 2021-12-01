package com.ynot.aaspaasdoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.aaspaasdoctor.Adapter.ConsultAdapter;
import com.ynot.aaspaasdoctor.Model.ConsultationModel;
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

public class ViewCunsultation extends AppCompatActivity {
Toolbar toolbar;
RecyclerView recyclerView;
ConsultAdapter consultAdapter;
TextView nodata;
List<ConsultationModel> consultationModelList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cunsultation);
        nodata=findViewById(R.id.nodata);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        recyclerView=findViewById(R.id.consultrec);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
      //  getConsultation();
        
    }

    private void getConsultation() {
        if(consultationModelList.size()>0){
            consultationModelList.clear();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_CONSULTATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progress.dismiss();
                        Log.e("postconsult", response);

                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                JSONArray jsonArray=ob.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject ob2=jsonArray.getJSONObject(i);
                                    consultationModelList.add(new ConsultationModel(ob2.getString("id"),ob2.getString("latitiude"),ob2.getString("longititude"),ob2.getString("type"),ob2.getString("name"),ob2.getString("district"),ob2.getString("city"),ob2.getString("pincode")));
                                }
                                consultAdapter=new ConsultAdapter(getApplicationContext(),consultationModelList);
recyclerView.setAdapter(consultAdapter);


                            } else {
                                nodata.setVisibility(View.VISIBLE);
                              //  Toast.makeText(ViewCunsultation.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
//                params.put("latitiude", latitiude);
//                params.put("longititude",longititude);
//                params.put("type", ctype);
//                params.put("name", nameclinic.getText().toString());
//                params.put("district", district.getText().toString());
//                params.put("city", city.getText().toString());
//                params.put("pincode", pincode.getText().toString());
                Log.e("getconsult_input(", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
getConsultation();
    }
}