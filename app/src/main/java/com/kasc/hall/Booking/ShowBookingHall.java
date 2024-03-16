package com.kasc.hall.Booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kasc.hall.R;

import java.util.Objects;

public class ShowBookingHall extends AppCompatActivity {
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_hall);
        toolbar = findViewById(R.id.appbarFeed);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Choose a Venue");
        toolbar.setTitleTextAppearance(this, R.style.poppins_bold);

        // Set up listeners for each hall
        findViewById(R.id.layoutSeminarHall).setOnClickListener(view -> openBookingActivity("Seminar Hall"));
        findViewById(R.id.layoutNivedithaHall).setOnClickListener(view -> openBookingActivity("Niveditha Hall"));
        findViewById(R.id.layoutConferenceHall).setOnClickListener(view -> openBookingActivity("Conference Hall"));
        findViewById(R.id.layoutAuditorium).setOnClickListener(view -> openBookingActivity("Auditorium"));
        // back button created using toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void openBookingActivity(String hallName) {
        Intent intent = new Intent(ShowBookingHall.this, BookingActivity.class);
        intent.putExtra("hallName", hallName);
        startActivity(intent);
    }
}
