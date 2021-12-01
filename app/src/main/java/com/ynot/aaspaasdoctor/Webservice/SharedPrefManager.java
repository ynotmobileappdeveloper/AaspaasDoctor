package com.ynot.aaspaasdoctor.Webservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ynot.aaspaasdoctor.LoginActivity;

public class SharedPrefManager {


    private static final String SHARED_PREF_NAME = "volleylogin";
    private static final String KEY_ID = "keyid";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_QUALIFICATION = "qualification";


    private static SharedPrefManager mInstance;
    private static Context ctx;
    boolean status = false;

    private SharedPrefManager(Context context) {

        ctx = context;

    }

    public static synchronized SharedPrefManager getInstatnce(Context context) {

        if (mInstance == null) {

            mInstance = new SharedPrefManager(context);

        }
        return mInstance;

    }

    public void userLogin(User user) {


        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_QUALIFICATION, user.getQualfication());
        editor.apply();


    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    public User getUser() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_ID, null),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_PHONE, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_QUALIFICATION, null)
        );

    }

    public void logout() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(ctx, LoginActivity.class);
        i.putExtra("page_name", "shared");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ctx.startActivity(i);


    }

}
