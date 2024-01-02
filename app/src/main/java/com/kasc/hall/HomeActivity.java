package com.kasc.hall;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.kasc.hall.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        // Set up listeners for each hall
        findViewById(R.id.layoutSeminarHall).setOnClickListener(view -> openBookingActivity("Seminar Hall"));
        findViewById(R.id.layoutNivedithaHall).setOnClickListener(view -> openBookingActivity("Niveditha Hall"));
        findViewById(R.id.layoutConferenceHall).setOnClickListener(view -> openBookingActivity("Conference Hall"));
        findViewById(R.id.layoutAuditorium).setOnClickListener(view -> openBookingActivity("Auditorium"));
    }

    private void openBookingActivity(String hallName) {
        Intent intent = new Intent(HomeActivity.this, BookingActivity.class);
        intent.putExtra("hallName", hallName);
        startActivity(intent);
    }
}
