package com.kasc.hall.Facultymanagement;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kasc.hall.R;

import java.util.Objects;


public class AddFacultyActivity extends AppCompatActivity {

    private EditText nameEditText, idEditText, emailEditText, passwordEditText, confirmPasswordEditText;

    private FirebaseAuth mAuth;

    private static final String COLLEGE_EMAIL_SUFFIX = "@kongunaducollege.ac.in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faculty);

        mAuth = FirebaseAuth.getInstance();

        nameEditText = findViewById(R.id.editTextAddName);
        idEditText = findViewById(R.id.editTextAddId);
        emailEditText = findViewById(R.id.editTextAddEmail);
        passwordEditText = findViewById(R.id.editTextAddPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        Button addButton = findViewById(R.id.buttonAdd);

        addButton.setOnClickListener(v -> addFaculty());
    }

    private void addFaculty() {
        final String name = nameEditText.getText().toString().trim();
        final String id = idEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(id) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email ends @kasc
        if (!email.endsWith(COLLEGE_EMAIL_SUFFIX)) {
            Toast.makeText(this, "Email must end with " + COLLEGE_EMAIL_SUFFIX, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if faculty ID is in the correct format
        if (!isValidIdFormat(id)) {
            Toast.makeText(this, "Invalid ID format. Use the format U/TS/XXX (where XXX is a 3-digit number)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if faculty ID is unique
        checkUniqueFacultyId(id, new OnFacultyIdCheckListener() {
            @Override
            public void onUnique() {
                // Check if passwords match
                if (password.equals(confirmPassword)) {
                    // Check password length
                    if (password.length() < 6) {
                        Toast.makeText(AddFacultyActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    } else {
                        // Attempt to create account and add faculty details
                        createUserAndAddFacultyDetails(name, id, email, password);
                    }
                } else {
                    Toast.makeText(AddFacultyActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNotUnique() {
                Toast.makeText(AddFacultyActivity.this, "Faculty ID already exists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidIdFormat(String id) {
        return id.matches("^U/TS/\\d{3}$");
    }

    private void checkUniqueFacultyId(String facultyId, final OnFacultyIdCheckListener listener) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("faculty");
        Query query = reference.orderByChild("facultyid").equalTo(facultyId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Faculty ID already exists
                    listener.onNotUnique();
                } else {
                    // Faculty ID is unique
                    listener.onUnique();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createUserAndAddFacultyDetails(String name, String id, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        /* Account creation success amd send verification email */
                        sendVerificationEmail();


                        addFacultyDetails(name, id, email);
                        Toast.makeText(AddFacultyActivity.this, "Faculty added successfully", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(AddFacultyActivity.this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddFacultyActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddFacultyActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void addFacultyDetails(String name, String id, String email) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("faculty");

        String facultyId = databaseReference.push().getKey();
        assert facultyId != null;
        databaseReference.child(facultyId).child("facultyname").setValue(name);
        databaseReference.child(facultyId).child("facultyid").setValue(id);
        databaseReference.child(facultyId).child("email").setValue(email);
    }


    private interface OnFacultyIdCheckListener {
        void onUnique();

        void onNotUnique();
    }
}
