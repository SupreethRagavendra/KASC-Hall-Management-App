

package com.kasc.hall;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private TextView textViewHallName, textViewDate, textViewFacultyId, textViewFacultyName;
    private Spinner spinnerDepartment, spinnerYear, spinnerTimeSlots;
    private EditText eventNameEditText;
    private Button bookButton, viewBookingsButton, selectDateButton;
    private DatabaseReference auditoriumReference, bookingReference;
    private Calendar selectedDate = Calendar.getInstance();
    private String hallName, facultyName, facultyId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        hallName = getIntent().getStringExtra("hallName");

        // Retrieve faculty name and ID from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("FacultyPrefs", MODE_PRIVATE);
        facultyName = preferences.getString("facultyName", "");
        facultyId = preferences.getString("facultyId", "");

        auditoriumReference = FirebaseDatabase.getInstance().getReference("auditorium").child(hallName);
        bookingReference = FirebaseDatabase.getInstance().getReference("booking").child(hallName);

        textViewHallName = findViewById(R.id.textViewHallName);
        textViewDate = findViewById(R.id.textViewDate);
        textViewFacultyId = findViewById(R.id.textViewFacultyId);
        textViewFacultyName = findViewById(R.id.textViewFacultyName);
        eventNameEditText = findViewById(R.id.editTextEventName);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerTimeSlots = findViewById(R.id.spinnerTimeSlots);
        bookButton = findViewById(R.id.buttonBook);
        viewBookingsButton = findViewById(R.id.buttonViewBookings);
        selectDateButton = findViewById(R.id.buttonSelectDate);

        // Set the selected hall name
        textViewHallName.setText(hallName);

        // Set faculty name and ID in the TextViews
        textViewFacultyId.setText("Faculty ID: " + facultyId);
        textViewFacultyName.setText("Faculty Name: " + facultyName);

        // Add animations
        fadeInAnimation(selectDateButton);
        fadeInAnimation(bookButton);
        fadeInAnimation(viewBookingsButton);

        // Set up spinners
        setUpSpinners();

        selectDateButton.setOnClickListener(this::showDatePickerDialog);
        bookButton.setOnClickListener(this::checkAvailability);
        viewBookingsButton.setOnClickListener(this::viewBookings);
    }

    private void setUpSpinners() {
        // Department spinner
        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.department_options,
                android.R.layout.simple_spinner_item
        );
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(departmentAdapter);

        // Year spinner
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.year_options,
                android.R.layout.simple_spinner_item
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // Time slots spinner
        ArrayAdapter<CharSequence> timeSlotAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.time_slot_options,
                android.R.layout.simple_spinner_item
        );
        timeSlotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeSlots.setAdapter(timeSlotAdapter);
    }

    private void fadeInAnimation(View view) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);
        view.startAnimation(fadeIn);
    }

    private void showDatePickerDialog(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, monthOfYear);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateButtonText();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        // Prevent past dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

        // Add animation
        fadeInAnimation(view);
    }

    private void updateDateButtonText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        textViewDate.setText(dateFormat.format(selectedDate.getTime()));

        // Add animation
        fadeInAnimation(textViewDate);
    }

    private void checkAvailability(View view) {
        // Input validation
        if (!validateInput()) {
            return;
        }

        // Extract date and time information
        String dateKey = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(selectedDate.getTime());
        String selectedTimeSlot = spinnerTimeSlots.getSelectedItem().toString();
        String dateTimeKey = dateKey + "_" + selectedTimeSlot;

        showProgressDialog("Checking availability...");

        // Check if the selected time slot is already booked by another faculty
        bookingReference.child(dateTimeKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The time slot is already booked
                    hideProgressDialog();
                    showErrorMessage("Selected time slot is already booked");
                } else {
                    // Check if the same hall, date, and time slot are booked by any faculty
                    auditoriumReference.child(dateKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot timeSlot : dataSnapshot.getChildren()) {
                                    String slotKey = timeSlot.getKey();
                                    String slotTimeSlot = (String) timeSlot.child("timeSlot").getValue();

                                    if (selectedTimeSlot.equals(slotTimeSlot)) {
                                        // Check if the same hall is booked by another faculty
                                        String bookedFacultyId = (String) timeSlot.child("facultyId").getValue();
                                        if (!bookedFacultyId.isEmpty() && !bookedFacultyId.equals(facultyId)) {
                                            // The hall is booked by another faculty for the selected date and time slot
                                            hideProgressDialog();
                                            showErrorMessage("Selected hall is already booked by another faculty");
                                            return;
                                        }
                                    }
                                }
                            }

                            // The time slot and hall are available, proceed with booking
                            hideProgressDialog();
                            showConfirmationDialog();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            hideProgressDialog();
                            showErrorMessage("Error accessing database");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog();
                showErrorMessage("Error accessing database");
            }
        });

        // Add animation
        fadeInAnimation(view);
    }

    private boolean validateInput() {
        // Validate event name
        String eventName = eventNameEditText.getText().toString().trim();
        if (eventName.isEmpty()) {
            eventNameEditText.setError("Event name is required");
            return false;
        }

        return true;
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Do you want to book this event?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform booking
                performBooking();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User canceled the booking
                Toast.makeText(BookingActivity.this, "Booking canceled", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void performBooking() {
        String dateKey = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(selectedDate.getTime());
        String selectedTimeSlot = spinnerTimeSlots.getSelectedItem().toString();
        String dateTimeKey = dateKey + "_" + selectedTimeSlot;

        // Show loading indicator
        showProgressDialog("Booking event...");

        // Store the date, time slot, and other details
        bookingReference.child(dateTimeKey).child("date").setValue(dateKey);
        bookingReference.child(dateTimeKey).child("timeSlot").setValue(selectedTimeSlot);
        bookingReference.child(dateTimeKey).child("hallName").setValue(hallName);
        bookingReference.child(dateTimeKey).child("eventName").setValue(eventNameEditText.getText().toString().trim());
        bookingReference.child(dateTimeKey).child("facultyName").setValue(facultyName);
        bookingReference.child(dateTimeKey).child("facultyId").setValue(facultyId);
        bookingReference.child(dateTimeKey).child("department").setValue(spinnerDepartment.getSelectedItem().toString());
        bookingReference.child(dateTimeKey).child("year").setValue(spinnerYear.getSelectedItem().toString());

        // Hide loading indicator
        hideProgressDialog();

        Toast.makeText(BookingActivity.this, "Event booked successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void viewBookings(View view) {
        // Navigate to a new activity to display the user's bookings
        Intent intent = new Intent(BookingActivity.this, UserBookingsActivity.class);
        intent.putExtra("facultyId", facultyId);
        startActivity(intent);
    }


    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showErrorMessage(String message) {
        Toast.makeText(BookingActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}