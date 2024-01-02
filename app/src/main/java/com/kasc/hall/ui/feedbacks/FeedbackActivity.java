package com.kasc.hall.ui.feedbacks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.kasc.hall.R;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class FeedbackActivity extends AppCompatActivity {

    EditText edtname, edtnumber, edtmessage;
    MaterialButton btnsend;

    DatabaseReference databaseReference;
    private Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        toolbar = findViewById(R.id.appbarFeed);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Feedback");
        toolbar.setTitleTextAppearance(this, R.style.poppins_bold);

        edtname = findViewById(R.id.feedname);
        edtnumber = findViewById(R.id.feednumber);
        edtmessage = findViewById(R.id.feedmessage);
        btnsend = findViewById(R.id.feedbtnsend);

        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");

        // Retrieve facultyName and facultyId from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("FacultyPrefs", MODE_PRIVATE);
        String facultyName = preferences.getString("facultyName", "");
        String facultyId = preferences.getString("facultyId", "");

        // Set the facultyName and facultyId to the corresponding EditTexts
        edtname.setText(  facultyName);
        edtnumber.setText( facultyId);

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
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
        Toast.makeText(this, "Your Feedback is sent!👍👍 ", Toast.LENGTH_SHORT).show();
    }
}
