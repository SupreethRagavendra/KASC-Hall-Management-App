package com.kasc.hall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText nameEditText, idEditText;
    private Button loginButton, addFacultyButton;
    private DatabaseReference facultyReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameEditText = findViewById(R.id.editTextLoginName);
        idEditText = findViewById(R.id.editTextLoginId);
        loginButton = findViewById(R.id.buttonLogin);
        addFacultyButton = findViewById(R.id.buttonAddFaculty);

        facultyReference = FirebaseDatabase.getInstance().getReference("faculty");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        addFacultyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AddFacultyActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        final String enteredName = nameEditText.getText().toString().trim();
        final String enteredId = idEditText.getText().toString().trim();

        facultyReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean loginSuccessful = false;
                String facultyName = "";
                String facultyId = "";

                for (DataSnapshot facultySnapshot : dataSnapshot.getChildren()) {
                    facultyName = facultySnapshot.child("facultyname").getValue(String.class);
                    facultyId = facultySnapshot.child("facultyid").getValue(String.class);

                    // Check if enteredId matches the stored value
                    if (enteredId.equals(facultyId)) {
                        // Check if enteredName matches for the same faculty ID
                        if (enteredName.equals(facultyName)) {
                            // Login successful
                            loginSuccessful = true;
                            break;
                        }
                    }
                }

                if (loginSuccessful) {
                    // Store faculty name and id in SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("FacultyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("facultyName", facultyName);
                    editor.putString("facultyId", facultyId);
                    editor.apply();

                    // Proceed to home screen or perform required actions
                    Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Login unsuccessful
                    Toast.makeText(LoginActivity.this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Error accessing database: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
