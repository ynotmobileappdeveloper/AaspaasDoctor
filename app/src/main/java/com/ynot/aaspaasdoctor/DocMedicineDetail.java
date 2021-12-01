package com.ynot.aaspaasdoctor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.card.MaterialCardView;
import com.ynot.aaspaasdoctor.Adapter.DocmedicineDetailAdpater;
import com.ynot.aaspaasdoctor.Model.TypeModel;
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

public class DocMedicineDetail extends AppCompatActivity {


    DocmedicineDetailAdpater adapter;
    ArrayList<TypeModel> model = new ArrayList<>();
    String city_id = "", subcity_id = "", store_id = "", id = "", status;
    RecyclerView rec, stores_rec;
    TextView date, name, gender, age, symptoms;
    LinearLayout list_layout;
    ImageView image, nodata;
    MaterialCardView cardView;

    Button share_btn, send;
    Dialog share;
    Spinner city;
    ProgressDialog progressDialog;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_medicine_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        rec = findViewById(R.id.rec);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        name = findViewById(R.id.name);
        layout = findViewById(R.id.layout);
        date = findViewById(R.id.date);
        gender = findViewById(R.id.gender);
        symptoms = findViewById(R.id.tests);
        age = findViewById(R.id.age);
        cardView = findViewById(R.id.cardView);
        list_layout = findViewById(R.id.list_layout);
        image = findViewById(R.id.image);
        nodata = findViewById(R.id.nodata);

        id = getIntent().getStringExtra("id");

        get_details();
    }

    private void get_details() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_DOC_MEDICINE_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("data", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                layout.setVisibility(View.VISIBLE);
                                name.setText(ob.getString("name"));
                                date.setText(ob.getString("date"));
                                gender.setText(ob.getString("gender"));
                                age.setText(ob.getString("age"));
                                symptoms.setText(ob.getString("symptoms"));

                                JSONArray array = ob.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new TypeModel(obb.getString("id"), obb.getString("name"), obb.getString("qty"), obb.getString("days")));

                                }
                                adapter = new DocmedicineDetailAdpater(getApplicationContext(), model);
                                rec.setAdapter(adapter);

                                if (model.size() == 0) {
                                    cardView.setVisibility(View.VISIBLE);
                                    list_layout.setVisibility(View.GONE);
                                    Glide.with(DocMedicineDetail.this).load(ob.getString("image")).addListener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            nodata.setVisibility(View.VISIBLE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            nodata.setVisibility(View.GONE);
                                            return false;
                                        }
                                    }).into(image);

                                } else {
                                    cardView.setVisibility(View.GONE);
                                    list_layout.setVisibility(View.VISIBLE);
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
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("id", id);
                Log.e("input", String.valueOf(params));
                return params;
            }
        };
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