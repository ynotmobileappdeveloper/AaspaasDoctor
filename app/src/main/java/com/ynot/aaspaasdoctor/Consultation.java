package com.ynot.aaspaasdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.ynot.aaspaasdoctor.Webservice.SharedPrefManager;
import com.ynot.aaspaasdoctor.Webservice.URLs;
import com.ynot.aaspaasdoctor.Webservice.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Consultation extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    private GoogleMap mMap;
    Marker now;
    String latitiude, longititude;
    EditText district, pincode,nameclinic,city;
    Spinner type;
    String ctype="";
    String[] types={"Clinic / Hospital / Personal OP","Personal","Hospital","Clinic"};

    CardView op;
    int status=0;
    Button save,viewall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        viewall=findViewById(R.id.viewall);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Consultation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type = (Spinner) findViewById(R.id.name);
        type.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(aa);
        nameclinic=findViewById(R.id.clinicname);
        city=findViewById(R.id.city);
        district = findViewById(R.id.district);
        pincode = findViewById(R.id.pincode);
        save = findViewById(R.id.save);
        op = findViewById(R.id.op);
viewall.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(getApplicationContext(),ViewCunsultation.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
});

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAT5JG_9ijuOfjvz9Ng9Wv1JEqRHQeELGg", Locale.US);
        }
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16));
                latitiude = String.valueOf(place.getLatLng().latitude);
                longititude = String.valueOf(place.getLatLng().longitude);
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    Address obj = addresses.get(0);
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String pin = addresses.get(0).getPostalCode();
                    pincode.setText(pin + "");
                    district.setText(addresses.get(0).getSubAdminArea() + "");

                    String add = obj.getAddressLine(0);
                    add = add + "\n" + obj.getCountryName();
                    add = add + "\n" + obj.getCountryCode();
                    add = add + "\n" + obj.getAdminArea();
                    add = add + "\n" + obj.getPostalCode();
                    add = add + "\n" + obj.getSubAdminArea();
                    add = add + "\n" + obj.getLocality();
                    add = add + "\n" + obj.getSubThoroughfare();
                    Log.e("address", add);

                } catch (IOException e) {
                    e.printStackTrace();
                }


                Log.i("Place", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("Error", "An error occurred: " + status);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Validation()){
                    saveConsultation();

                }

            }
        });

        op.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), OPTimeSetting.class));
            }
        });


    }

    private boolean Validation() {
        if(nameclinic.getText().toString().length()==0){
          //  Toast.makeText(this, "please type name", Toast.LENGTH_SHORT).show();
            nameclinic.setError("Please fill name");
            nameclinic.requestFocus();

            return  false;
        }
        else if(type.getSelectedItemPosition()==0){
            Toast.makeText(this, "please select type", Toast.LENGTH_SHORT).show();
            type.requestFocus();

            return  false;
        }
        else if(latitiude.length()==0){
            Toast.makeText(this, "please select location", Toast.LENGTH_SHORT).show();
            return  false;
        }
        else if(district.getText().toString().length()==0){

          //  Toast.makeText(this, "please   name", Toast.LENGTH_SHORT).show();
            district.setError("Please fill district");
            district.requestFocus();
            return  false;
        }
        else if(city.getText().toString().length()==0){
           // Toast.makeText(this, "please type name", Toast.LENGTH_SHORT).show();
            city.setError("Please fill city");
            city.requestFocus();

            return  false;

        }
        else if(pincode.getText().toString().length()<6){
          //  Toast.makeText(this, "please type name", Toast.LENGTH_SHORT).show();
            pincode.setError("Please enter valid pincode");
            pincode.requestFocus();

            return  false;

        }
        else {
            return  true;
        }



    }

    private void saveConsultation() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.ADDCUNSULTATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progress.dismiss();
                        Log.e("postconsult", response);

                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(Consultation.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                              //  Toast.makeText(Consultation.this, "Saved Successfully !", Toast.LENGTH_SHORT).show();
startActivity(new Intent(getApplicationContext(),ViewCunsultation.class));
                            } else {
                                Toast.makeText(Consultation.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("latitiude", latitiude);
                params.put("longititude",longititude);
                params.put("type", ctype);
                params.put("name", nameclinic.getText().toString());
                params.put("district", district.getText().toString());
                params.put("city", city.getText().toString());
                params.put("pincode", pincode.getText().toString());
                Log.e("addconsultinput(", String.valueOf(params));
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       /* mMap = googleMap;

        LatLng sydney = new LatLng(Double.parseDouble(latitiude), Double.parseDouble(longititude));
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                now = mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                latitiude = String.valueOf(latLng.latitude);
                longititude = String.valueOf(latLng.longitude);


            }
        });*/


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
       // Toast.makeText(getApplicationContext(), types[position], Toast.LENGTH_LONG).show();
        if(position>0){
        ctype=String.valueOf(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
// TODO Auto-generated method stub

    }

}