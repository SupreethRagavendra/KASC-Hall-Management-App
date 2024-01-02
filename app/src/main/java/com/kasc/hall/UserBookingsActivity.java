package com.kasc.hall;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserBookingsActivity extends AppCompatActivity {

    private String facultyId;
    private RecyclerView recyclerViewBookings;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_bookings_activity);

        // Retrieve faculty ID from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("facultyId")) {
            facultyId = extras.getString("facultyId");
        }

        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(bookingList);
        recyclerViewBookings.setAdapter(bookingAdapter);

        // Fetch and display booking history
        fetchBookingHistory();
    }

    private void fetchBookingHistory() {
        DatabaseReference bookingReference = FirebaseDatabase.getInstance().getReference("booking");
        bookingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingList.clear();
                for (DataSnapshot hallSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot timeSlotSnapshot : hallSnapshot.getChildren()) {
                        Booking booking = timeSlotSnapshot.getValue(Booking.class);
                        if (booking != null && facultyId.equals(booking.getFacultyId())) {
                            bookingList.add(booking);
                        }
                    }
                }
                bookingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}