package com.kasc.hall.Booking;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kasc.hall.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowBookings extends AppCompatActivity {

    private String facultyId;
    private String desiredHallName;
    private RecyclerView recyclerViewBookings;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_bookings_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("facultyId")) {
            facultyId = extras.getString("facultyId");
        }
        if (extras != null && extras.containsKey("hallName")) {
            desiredHallName = extras.getString("hallName");


        }

        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(bookingList);

        toolbar = findViewById(R.id.appbarFeed);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("My Bookings");
        toolbar.setTitleTextAppearance(this, R.style.poppins_bold);




        bookingAdapter.setOnItemClickListener(new BookingAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                Booking booking = bookingList.get(position);
                editBooking(booking);
            }

            @Override
            public void onCancelClick(int position) {

            }

            @Override
            public void onDeleteClick(Booking booking) {
                deleteBooking(booking);
            }

            @Override
            public void onClick(View view) {

            }


        });

        recyclerViewBookings.setAdapter(bookingAdapter);

        fetchBookingHistory();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



    private void editBooking(Booking booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Booking");

        View editView = LayoutInflater.from(this).inflate(R.layout.edit_booking_dialog, null);
        builder.setView(editView);

        EditText editEventName = editView.findViewById(R.id.editEventName);
        Spinner spinnerYear = editView.findViewById(R.id.spinnerYear);
        Spinner spinnerDepartment = editView.findViewById(R.id.spinnerDepartment);

        editEventName.setText(booking.getEventName());

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.year_options, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        String[] yearOptions = getResources().getStringArray(R.array.year_options);
        for (int i = 0; i < yearOptions.length; i++) {
            if (yearOptions[i].equals(booking.getYear())) {
                spinnerYear.setSelection(i);
                break;
            }
        }

        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(this,
                R.array.department_options, android.R.layout.simple_spinner_item);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(departmentAdapter);

        String[] departmentOptions = getResources().getStringArray(R.array.department_options);
        for (int i = 0; i < departmentOptions.length; i++) {
            if (departmentOptions[i].equals(booking.getDepartment())) {
                spinnerDepartment.setSelection(i);
                break;
            }
        }

        Button saveButton = editView.findViewById(R.id.saveButton);
        Button cancelButton = editView.findViewById(R.id.cancelButton);

        AlertDialog dialog = builder.create();
        dialog.show();

        saveButton.setOnClickListener(v -> {
            String newEventName = editEventName.getText().toString().trim();
            String selectedYear = spinnerYear.getSelectedItem().toString();
            String selectedDepartment = spinnerDepartment.getSelectedItem().toString();

            if (!TextUtils.isEmpty(newEventName)) {
                updateBookingInDatabase(booking, newEventName, selectedYear, selectedDepartment);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }




    private void updateBookingInDatabase(Booking booking, String newEventName, String selectedYear, String selectedDepartment) {
        DatabaseReference bookingReference = FirebaseDatabase.getInstance().getReference("booking");

        String dateKey = booking.getDate();
        String timeSlotKey = booking.getTimeSlot();
        String dateTimeKey = dateKey + "_" + timeSlotKey;

        DatabaseReference specificBookingReference = bookingReference.child(booking.getHallName()).child(dateTimeKey);

        specificBookingReference.child("eventName").setValue(newEventName);
        specificBookingReference.child("year").setValue(selectedYear);
        specificBookingReference.child("department").setValue(selectedDepartment);

        Toast.makeText(this, "Booking updated successfully", Toast.LENGTH_SHORT).show();
        fetchBookingHistory();
    }

    private void deleteBooking(Booking booking) {
        DatabaseReference bookingReference = FirebaseDatabase.getInstance().getReference("booking");

        DatabaseReference facultyBookingReference = bookingReference.child(booking.getHallName());

        String dateKey = booking.getDate();
        String timeSlotKey = booking.getTimeSlot();
        String dateTimeKey = dateKey + "_" + timeSlotKey;

        DatabaseReference specificBookingReference = facultyBookingReference.child(dateTimeKey);

        specificBookingReference.removeValue((error, ref) -> {
            if (error == null) {
                Toast.makeText(ShowBookings.this, "Booking deleted successfully", Toast.LENGTH_SHORT).show();
                fetchBookingHistory();
            } else {
                Toast.makeText(ShowBookings.this, "Failed to delete booking", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchBookingHistory() {
        DatabaseReference bookingReference = FirebaseDatabase.getInstance().getReference("booking");
        bookingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingList.clear();
                for (DataSnapshot hallSnapshot : dataSnapshot.getChildren()) {
                    String hallName = hallSnapshot.getKey(); // Get the hallName

                    for (DataSnapshot timeSlotSnapshot : hallSnapshot.getChildren()) {
                        Booking booking = timeSlotSnapshot.getValue(Booking.class);

                        if (booking != null && facultyId.equals(booking.getFacultyId()) && hallNameFilterMatches(hallName)) {
                            bookingList.add(booking);
                        }
                    }
                }
                bookingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ShowBookings.this, "Error fetching booking history", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hallNameFilterMatches(String hallName) {

        return TextUtils.isEmpty(desiredHallName) || desiredHallName.equals(hallName);

        // under construction
    }
}
