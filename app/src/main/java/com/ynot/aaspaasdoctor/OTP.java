package com.ynot.aaspaasdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.aaspaasdoctor.Webservice.SharedPrefManager;
import com.ynot.aaspaasdoctor.Webservice.URLs;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OTP extends AppCompatActivity {
    String mob = "", password = "", otp = "";
    TextView mobile, resend;
    EditText enterotp;
    Button verify;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
        progressDialog = new ProgressDialog(getApplicationContext());
        mobile = findViewById(R.id.mob);
        resend = findViewById(R.id.resend);
        enterotp = findViewById(R.id.otp);
        verify = findViewById(R.id.verify);
        resend = findViewById(R.id.resend);
        if (getIntent().hasExtra("otp")) {
            otp = getIntent().getStringExtra("otp");
        }
        if (getIntent().hasExtra("ph")) {
            mob = getIntent().getStringExtra("ph");
            mobile.setText(mob);
        }
        if (getIntent().hasExtra("pass")) {
            password = getIntent().getStringExtra("pass");
        }
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enterotp.getText().toString().equals(otp)) {
                    Change_password();
                } else {
                    Toast.makeText(OTP.this, "Invalid otp", Toast.LENGTH_SHORT).show();
                }
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendotp();
            }
        });

    }

    private void resendotp() {
//        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.RESENDOTP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();
                        Log.e("resendotp", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(OTP.this, ob.getString("message"), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(OTP.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mob", mob);
                //    params.put("status","2");
                Log.e("resendotp", params.toString());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void Change_password() {
        // progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CHANGEPASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  progressDialog.dismiss();
                        Log.e("change_password", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(OTP.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                // SharedPrefManager.getInstatnce(getApplicationContext()).logout();
                                finish();
                            } else {
                                Toast.makeText(OTP.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // progressDialog.dismiss();
                Log.e("error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mob", mob);
                params.put("user_otp", enterotp.getText().toString());
                params.put("otp", otp);
                params.put("pass", password);

                //    params.put("status","2");
                Log.e("change_password", params.toString());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }
}