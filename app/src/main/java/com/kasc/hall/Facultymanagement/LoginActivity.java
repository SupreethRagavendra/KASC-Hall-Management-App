
package com.kasc.hall.Facultymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kasc.hall.MainActivity;
import com.kasc.hall.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;

    private DatabaseReference facultyReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.editTextLoginEmail);
        passwordEditText = findViewById(R.id.editTextLoginPassword);
        Button loginButton = findViewById(R.id.buttonLogin);
        TextView addFacultyButton = findViewById(R.id.buttonAddFaculty);
        TextView resetPasswordButton = findViewById(R.id.buttonResetPassword);

        facultyReference = FirebaseDatabase.getInstance().getReference("faculty");
        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(
                v -> loginUser());

        addFacultyButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AddFacultyActivity.class);
            startActivity(intent);
        });

        resetPasswordButton.setOnClickListener(v -> handlePasswordReset());
    }

    public void loginUser() {
        final String enteredEmail = emailEditText.getText().toString().trim();
        final String enteredPassword = passwordEditText.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(enteredEmail, enteredPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        retrieveFacultyData(enteredEmail);
                    } else {
                        Toast.makeText(LoginActivity.this, " Invalid email and password please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void retrieveFacultyData(String email) {
        facultyReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot facultySnapshot : dataSnapshot.getChildren()) {
                        String facultyName = facultySnapshot.child("facultyname").getValue(String.class);
                        String facultyId = facultySnapshot.child("facultyid").getValue(String.class);


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null && user.isEmailVerified()) {

                            SharedPreferences preferences = getSharedPreferences("FacultyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("facultyName", facultyName);
                            editor.putString("facultyId", facultyId);
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User data not found. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Error accessing database: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePasswordReset() {
        final String enteredEmail = emailEditText.getText().toString().trim();

        if (enteredEmail.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter your email to reset the password", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.sendPasswordResetEmail(enteredEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to send password reset email: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
