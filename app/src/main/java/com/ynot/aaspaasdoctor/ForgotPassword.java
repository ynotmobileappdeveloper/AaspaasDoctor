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
import com.ynot.aaspaasdoctor.Webservice.User;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {
TextView mobno,resend;
EditText newpwd,confirm;
Button getotp;
ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mobno=findViewById(R.id.mob);
        progressDialog = new ProgressDialog(ForgotPassword.this);
        progressDialog.setMessage("Please wait...");
        resend=findViewById(R.id.resend);
        newpwd=findViewById(R.id.newpassword);
        confirm=findViewById(R.id.confirm);
        if(getIntent().hasExtra("ph")){
            mobno.setText(getIntent().getStringExtra("ph"));
        }
        getotp=findViewById(R.id.getotp);
        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newpwd.getText().toString().length()<6 ){
                    newpwd.setError("Password is too short");
                    return;
                }
                else if(!confirm.getText().toString().equals(newpwd.getText().toString())){
                    confirm.setError("Password Mismatch");
                }
                else{
                    get_otp();
                }
            }
        });
    }

    private void get_otp() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.OTPVERIFY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("get_otp", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(ForgotPassword.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                               Intent intent=new Intent(getApplicationContext(),OTP.class);
                               intent.putExtra("otp",ob.getString("otp"));
                               intent.putExtra("ph",mobno.getText().toString());
                               intent.putExtra("pass",newpwd.getText().toString());
                               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                finish();
                            } else {
                                Toast.makeText(ForgotPassword.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("errorrr",error.toString());

                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mob", mobno.getText().toString());
            //    params.put("status","2");
                Log.e("getotp",params.toString());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }
}