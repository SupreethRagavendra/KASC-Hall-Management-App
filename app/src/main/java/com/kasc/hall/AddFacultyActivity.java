package com.kasc.hall;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddFacultyActivity extends AppCompatActivity {

    private EditText nameEditText, idEditText;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faculty);

        nameEditText = findViewById(R.id.editTextAddName);
        idEditText = findViewById(R.id.editTextAddId);
        addButton = findViewById(R.id.buttonAdd);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFaculty();
            }
        });
    }

    private void addFaculty() {
        final String name = nameEditText.getText().toString().trim();
        final String id = idEditText.getText().toString().trim();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("faculty");

        // Check if the entered ID already exists
        databaseReference.orderByChild("facultyid").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // ID already exists, show an error message
                    Toast.makeText(AddFacultyActivity.this, "Faculty with this ID already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    // ID is unique, add the faculty
                    String facultyId = databaseReference.push().getKey();
                    databaseReference.child(facultyId).child("facultyname").setValue(name);
                    databaseReference.child(facultyId).child("facultyid").setValue(id);
                    Toast.makeText(AddFacultyActivity.this, "Faculty added successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddFacultyActivity.this, "Error accessing database: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
