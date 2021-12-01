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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button login;
    TextView signup, forgotpwd;
    EditText mob, pass;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please wait...");
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        mob = findViewById(R.id.mob);
        pass = findViewById(R.id.pass);
        forgotpwd = findViewById(R.id.forgot);
        forgotpwd.setOnClickListener(this);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                if (mob.getText().toString().length() != 10) {
                    mob.setError("Enter a valid mobile number !!");
                    mob.requestFocus();
                    return;
                }
                if (pass.getText().toString().isEmpty()) {
                    pass.setError("Please enter a password !!");
                    pass.requestFocus();
                    return;
                }
                check_login();
                break;
            case R.id.signup:
                startActivity(new Intent(getApplicationContext(), Registration.class));

                break;
            case R.id.forgot:
                if (mob.getText().toString().length() != 10) {
                    mob.setError("Enter a valid mobile number !!");
                    mob.requestFocus();
                    return;
                }

                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                intent.putExtra("ph", mob.getText().toString());
                startActivity(intent);

        }
    }

    private void check_login() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("resp_login", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                User user = new User(ob.getString("user_id"), ob.getString("username"), mob.getText().toString(), ob.getString("email"), ob.getString("qualification"));
                                SharedPrefManager.getInstatnce(getApplicationContext()).userLogin(user);
                                Toast.makeText(LoginActivity.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("mob", mob.getText().toString());
                params.put("pass", pass.getText().toString());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}