package com.ynot.aaspaasdoctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.ynot.aaspaasdoctor.Model.SpinnerModel;
import com.ynot.aaspaasdoctor.Webservice.SharedPrefManager;
import com.ynot.aaspaasdoctor.Webservice.URLs;
import com.ynot.aaspaasdoctor.Webservice.User;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class Profile extends AppCompatActivity {

    TextInputEditText name, qualification, exp, email, hosp_add,mob;
    Button save;

    ACProgressFlower progress;
    Spinner department;
    ArrayList<SpinnerModel> model = new ArrayList<>();
    List<String> depart = new ArrayList<>();
    String dep_id = "";
    int po=-1;
    Button create;
    ProgressDialog progressDialog;
    String  imgurl;
    Uri profile_uri;
    BottomSheetDialog dialog;
    ImageView gallery, camera, close,profile;
    String filePath = "", subcat_id = "", brand_id = "";
    ImageView profile_nodata, driving_nodata, vehicle_nodata, book_nodata;
    Bitmap profile_bitmap, licence_bitmap, vehicle_bitmap, book_bitmap;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    public static final int PROFILE_CAMERA = 1;
    public static final int PROFILE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profile=findViewById(R.id.profile);
        progress = new ACProgressFlower.Builder(Profile.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        name = findViewById(R.id.test);
        qualification = findViewById(R.id.quali);
        department = findViewById(R.id.department);
        exp = findViewById(R.id.exp);
        mob = findViewById(R.id.mob);
        email = findViewById(R.id.email);
        hosp_add = findViewById(R.id.hosp_add);
        save = findViewById(R.id.save);
    View modalbottomsheet = getLayoutInflater().inflate(R.layout.bottom_sheet_sample, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(modalbottomsheet);
        dialog.setTitle("Choose item");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        camera = dialog.findViewById(R.id.camera);
        close = dialog.findViewById(R.id.close);
        gallery = dialog.findViewById(R.id.gallery);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(PROFILE_CAMERA, PROFILE_GALLERY);
            }
        });
        get_profile();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    add_profile();
                }
            }
        });
        if(po>-1){
        department.setSelection(po);
        }

        department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                dep_id = model.get(i).getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void checkPermission(final int profileCamera, final int profileGallery) {

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(Profile.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(Profile.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(Profile.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            dialog.show();
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, profileCamera);*/
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {

                        }
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(Profile.this,
                                    "com.ynot.aaspaasdoctor.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, profileCamera);


                        }
                    }
                    dialog.dismiss();
                }
            });
            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent logoIntent = new Intent();
                    logoIntent.setType("image/*");
                    logoIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(logoIntent, "Select Picture"), profileGallery);
                    dialog.dismiss();
                }
            });


        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        filePath = image.getAbsolutePath();
        return image;
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    private void get_department(final String dep_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_DEPARTMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {

                                JSONArray array = ob.getJSONArray("departments");
                                model = new ArrayList<>();
                                depart = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new SpinnerModel(obb.getString("id"), obb.getString("department")));
                                    depart.add(obb.getString("department"));

                                                                    }
                                for (int j = 0; j < model.size(); j++) {
                                    Log.e("checkig",model.get(j).getId()+"=="+dep_id);

                                    if (Integer.valueOf(model.get(j).getId())==Integer.valueOf(dep_id)) {
                                        department.setSelection(j);
                                        po=j;
                                        Log.e("work","working");

                                    }
                                }


                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Profile.this, android.R.layout.simple_dropdown_item_1line, depart);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                department.setAdapter(adapter);
                                department.setSelection(po);

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
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    private void get_profile() {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("profile", response);
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                name.setText(ob.getString("name"));
                                qualification.setText(ob.getString("qualification"));
                                exp.setText(ob.getString("experience"));
                                mob.setText(ob.getString("mob"));
                                email.setText(ob.getString("email"));
                                hosp_add.setText(ob.getString("address"));

                                if(ob.getString("image").length()>3) {
                                    Glide.with(getApplicationContext()).load(ob.getString("image")).into(profile);
                                }
                                else {
                                    profile.setImageResource(R.drawable.doctoriconn);
                                }

                                imgurl=ob.getString("image");
                                get_department(ob.getString("department_id"));

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
                Log.e("in", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void add_profile() {
      //  progressDialog.show();
        Toast.makeText(this, "Updating Profile Please Wait.....", Toast.LENGTH_SHORT).show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLs.EDIT_PROFILE,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.e("resp", new String(response.data));

                        // Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(new String(response.data));
                            if (ob.getBoolean("status")) {
                                Toast.makeText(Profile.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(Profile.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("name", name.getText().toString());
                params.put("qualification", qualification.getText().toString());
                params.put("department", department.getSelectedItem().toString());
                params.put("department_id", dep_id);
                params.put("experience", exp.getText().toString());
                params.put("mob", mob.getText().toString());
                params.put("email", email.getText().toString());
                params.put("address", hosp_add.getText().toString());

                if (profile_bitmap == null) {
                    if(imgurl!=null){
                    params.put("image", imgurl);}
                    else {
                        params.put("image",imgurl);
                    }
                }
                Log.e("editprofile", String.valueOf(params));
                return params;
            }
            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                if (profile_bitmap != null) {
                    params.put("image", new VolleyMultipartRequest.DataPart(imagename + ".jpeg", getFileDataFromDrawable(profile_bitmap)));
                }
                Log.e("data", String.valueOf(params));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);


    }

    private boolean validation() {
//        if (profile_bitmap == null && imgurl.length()==0) {
//            Toast.makeText(Profile.this, "Please choose Profile image", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (name.getText().toString().isEmpty()) {
            name.setError("Please fill this field !!");
            name.requestFocus();
            return false;
        }
        if (qualification.getText().toString().isEmpty()) {
            qualification.setError("Please fill this field !!");
            qualification.requestFocus();
            return false;
        }
        if (model.size() > 0) {
            if (dep_id.isEmpty()) {
                TextView error = (TextView) department.getSelectedView();
                error.setError("");
                error.setTextColor(Color.RED);
                error.setText("Choose Department !");
                return false;
            }
        } else {
            Toast.makeText(Profile.this, "Choose Department !", Toast.LENGTH_LONG).show();
            return false;
        }
        if (exp.getText().toString().isEmpty()) {
            exp.setError("Please fill this field !!");
            exp.requestFocus();
            return false;
        }
        if (mob.getText().toString().length() != 10) {
            mob.setError("Please enter a valid mobile number !!");
            mob.requestFocus();
            return false;
        }
        if (!isValidEmail(email.getText().toString())) {
            email.setError("Please enter a valid Email-ID !!");
            email.requestFocus();
            return false;
        }
        if (hosp_add.getText().toString().isEmpty()) {
            hosp_add.setError("Please fill this field !!");
            hosp_add.requestFocus();
            return false;
        }
        return true;

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

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_GALLERY) {

            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                profile_uri = imageUri;
                try {
                    profile_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profile_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //filePath = getPath(profile_uri);
                if (filePath != null) {

                } else {
                    Toast.makeText(this, "No Image Selected !!", Toast.LENGTH_SHORT).show();
                }
                profile.setImageBitmap(profile_bitmap);
            }

        }
        if (requestCode == PROFILE_CAMERA) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            profile_bitmap = BitmapFactory.decodeFile(filePath, options);
            profile.setImageBitmap(profile_bitmap);
        }
    }
}