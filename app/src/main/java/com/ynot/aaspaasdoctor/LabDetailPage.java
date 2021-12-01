package com.ynot.aaspaasdoctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ynot.aaspaasdoctor.Adapter.TestAdapter;
import com.ynot.aaspaasdoctor.Model.SlotModel;
import com.ynot.aaspaasdoctor.Webservice.SharedPrefManager;
import com.ynot.aaspaasdoctor.Webservice.URLs;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LabDetailPage extends AppCompatActivity {


    TestAdapter adapter;
    TextView date, name, gender, age, tests, title, labtest;
    RecyclerView rec;
    ProgressDialog progressDialog;
    ArrayList<SlotModel> model = new ArrayList<>();
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_detail_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Test Details");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");

        rec = findViewById(R.id.rec);
        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
        gender = findViewById(R.id.gender);
        tests = findViewById(R.id.tests);
        age = findViewById(R.id.age);
        layout = findViewById(R.id.layout);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rec.setNestedScrollingEnabled(false);

        get_data(getIntent().getStringExtra("id"));

    }

    private void get_data(final String id) {
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.DOC_LABTEST_DETAILS
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject ob = new JSONObject(response);
                    Log.e("resp", response);
                    progressDialog.dismiss();

                    if (ob.getBoolean("status")) {
                        layout.setVisibility(View.VISIBLE);
                        name.setText(ob.getString("name"));
                        gender.setText(ob.getString("gender"));
                        age.setText(ob.getString("age"));
                        tests.setText(ob.getString("symptoms"));

                        JSONArray array = ob.getJSONArray("data");
                        model = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obb = array.getJSONObject(i);
                            model.add(new SlotModel("", obb.getString("lab_tests")));
                        }
                        adapter = new TestAdapter(getApplicationContext(), model);
                        rec.setAdapter(adapter);


                    } else {
                        layout.setVisibility(View.GONE);
                        Toast.makeText(LabDetailPage.this, "No data Found !!", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                Log.e("resp", String.valueOf(params));
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