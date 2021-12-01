package com.ynot.aaspaasdoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MedicineType extends AppCompatActivity {

    TextView name, gender, age;
    CardView upload_medicine, type_medicine;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_type);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Medicine");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        name = findViewById(R.id.name);
        gender = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        upload_medicine = findViewById(R.id.upload_medicine);
        type_medicine = findViewById(R.id.type_medicine);

        name.setText(getIntent().getStringExtra("name"));
        Log.e("name", getIntent().getStringExtra("name"));
        gender.setText(getIntent().getStringExtra("gender"));
        age.setText(getIntent().getStringExtra("age"));
        id = getIntent().getStringExtra("id");
        Log.e("op_id", id);
        type_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Medicines.class);
                i.putExtra("id", id);
                startActivity(i);
            }
        });

        upload_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UploadMedicine.class);
                i.putExtra("id", id);
                i.putExtra("name", name.getText().toString());
                i.putExtra("age", age.getText().toString());
                i.putExtra("gender", gender.getText().toString());
                startActivity(i);
            }
        });


    }
}