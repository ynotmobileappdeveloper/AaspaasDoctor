package com.ynot.aaspaasdoctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registration extends AppCompatActivity implements View.OnClickListener {

    Button create;
    EditText name, qualification, mob, whatsapp, email, pass, cpass;
    ProgressDialog progressDialog;
Spinner department;
    Uri profile_uri;
    BottomSheetDialog dialog;
    ArrayList<SpinnerModel> model = new ArrayList<>();
    List<String> depart = new ArrayList<>();
    String dep_id = "";
    ImageView gallery, camera, close,profile;
    String filePath = "", subcat_id = "", brand_id = "";
    ImageView profile_nodata, driving_nodata, vehicle_nodata, book_nodata;
    Bitmap profile_bitmap, licence_bitmap, vehicle_bitmap, book_bitmap;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    public static final int PROFILE_CAMERA = 1;
    public static final int PROFILE_GALLERY = 2;
   // private Request<? extends Object> volleyMultipartRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        progressDialog = new ProgressDialog(Registration.this);
        progressDialog.setMessage("Please wait...");
        profile = findViewById(R.id.profile);

        create = findViewById(R.id.create);
        name = findViewById(R.id.test);
        qualification = findViewById(R.id.qualification);
        department = findViewById(R.id.department);
        mob = findViewById(R.id.mob);
        whatsapp = findViewById(R.id.whatsapp);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        cpass = findViewById(R.id.cpass);
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
        get_department(dep_id);
        department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                dep_id = model.get(i).getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        create.setOnClickListener(this);
    }

    private void checkPermission(final int profileCamera, final int profileGallery) {

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(Registration.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(Registration.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(Registration.this,
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
                            Uri photoURI = FileProvider.getUriForFile(Registration.this,
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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
                                depart.add("-Select Department-");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new SpinnerModel(obb.getString("id"), obb.getString("department")));
                                    depart.add(obb.getString("department"));

                                                                   }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Registration.this, android.R.layout.simple_dropdown_item_1line, depart);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                department.setAdapter(adapter);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create:
                if (validation()) {
                    if(department.getSelectedItemPosition()>0){
                        dep_id=model.get(department.getSelectedItemPosition()-1).getId();
                    }
                    registration();
                }
                break;
        }
    }

    private void registration() {
        progressDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLs.REGISTRATION,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.e("resp", new String(response.data));

                       // Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(new String(response.data));
                            if (ob.getBoolean("status")) {
                                User user = new User(ob.getString("user_id"), name.getText().toString(), mob.getText().toString(), email.getText().toString(), qualification.getText().toString());
                                SharedPrefManager.getInstatnce(getApplicationContext()).userLogin(user);
                                Toast.makeText(Registration.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(Registration.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("name", name.getText().toString());
                params.put("qualification", qualification.getText().toString());
                params.put("department_id", dep_id);
                params.put("mobile", mob.getText().toString());
                params.put("whatsapp", "+91 " + whatsapp.getText().toString());
                params.put("email", email.getText().toString());
                params.put("password", cpass.getText().toString());

                if (profile_bitmap == null) {
                    params.put("image", "");
                }
                Log.e("reg", String.valueOf(params));
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
//        if (profile_bitmap == null) {
//            Toast.makeText(Registration.this, "Please choose Profile image", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (name.getText().toString().isEmpty()) {
            name.setError("Please Fill this field !!");
            name.requestFocus();
            return false;
        }
        if (qualification.getText().toString().isEmpty()) {
            qualification.setError("Please Fill this field !!");
            qualification.requestFocus();
            return false;
        }
        if (department.getSelectedItemPosition()==0) {
            //department.setError("Please Fill this field !!");
            Toast.makeText(this, "Please Select Your Department", Toast.LENGTH_SHORT).show();
            department.requestFocus();
            return false;
        }
        if (mob.getText().toString().length() != 10) {
            mob.setError("Please Fill a valid mobile number !!");
            mob.requestFocus();
            return false;
        }
        if (whatsapp.getText().toString().length() != 10) {
            whatsapp.setError("Please Fill a valid mobile number !!");
            whatsapp.requestFocus();
            return false;
        }
        if (!isValidEmail(email.getText().toString())) {
            email.setError("Please enter a valid email !!");
            email.requestFocus();
            return false;
        }
        if (pass.getText().toString().length() < 6) {
            pass.setError("Password contains atleast 6 Characters !!");
            pass.requestFocus();
            return false;
        }
        if (!pass.getText().toString().equals(cpass.getText().toString())) {
            cpass.setError("Password missmatch !!");
            cpass.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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