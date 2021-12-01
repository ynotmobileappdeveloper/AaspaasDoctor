package com.ynot.aaspaasdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class OPTimeSetting extends AppCompatActivity {

    CardView hospital, clinic, personal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_p_time_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("OP Time Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hospital = findViewById(R.id.hospital);
        clinic = findViewById(R.id.clini);
        personal = findViewById(R.id.personal);

        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TimeSetting.class);
                i.putExtra("title", "Hospital OP Time");
                i.putExtra("type", "2");
                startActivity(i);
            }
        });
        clinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TimeSetting.class);
                i.putExtra("title", "Clinic OP Time");
                i.putExtra("type", "3");
                startActivity(i);
            }
        });
        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TimeSetting.class);
                i.putExtra("title", "Personal OP Time");
                i.putExtra("type", "1");
                startActivity(i);
            }
        });


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