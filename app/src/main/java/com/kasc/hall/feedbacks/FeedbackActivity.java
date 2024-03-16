package com.kasc.hall.feedbacks;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kasc.hall.R;

import java.util.Objects;

public class FeedbackActivity extends AppCompatActivity {

    EditText edtname, edtnumber, edtmessage;
    MaterialButton btnsend;

    DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = findViewById(R.id.appbarFeed);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Feedback");
        toolbar.setTitleTextAppearance(this, R.style.poppins_bold);

        edtname = findViewById(R.id.feedname);
        edtnumber = findViewById(R.id.feednumber);
        edtmessage = findViewById(R.id.feedmessage);
        btnsend = findViewById(R.id.feedbtnsend);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");

      // from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("FacultyPrefs", MODE_PRIVATE);
        String facultyName = preferences.getString("facultyName", "");
        String facultyId = preferences.getString("facultyId", "");

        edtname.setText(  facultyName);
        edtnumber.setText( facultyId);

        btnsend.setOnClickListener(view -> checkValidation());
    }

    private void checkValidation() {
        String name = edtname.getText().toString();
        String number = edtnumber.getText().toString();
        String message = edtmessage.getText().toString();

        if (name.isEmpty()) {
            edtname.setError("Empty");
            edtname.requestFocus();
        } else if (number.isEmpty()) {
            edtnumber.setError("Empty");
            edtnumber.requestFocus();
        } else if (message.isEmpty()) {
            edtmessage.setError("Empty");
            edtmessage.requestFocus();
        } else {
            getFeedback(name, number, message);
        }
    }

    private void getFeedback(String name, String number, String message) {
        FeedbackData feedbackData = new FeedbackData(name, number, message);

        databaseReference.push().setValue(feedbackData);


        Intent intent = new Intent(FeedbackActivity.this, AnimationActivity.class);
        startActivity(intent);


        finish();
    }

}
