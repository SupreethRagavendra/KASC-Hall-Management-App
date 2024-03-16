package com.kasc.hall.Booking;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasc.hall.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> implements ListAdapter {

    private List<Booking> bookingList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onEditClick(int position);

        void onCancelClick(int position);

        void onDeleteClick(Booking booking);

        void onClick(View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.textEventName.setText(booking.getEventName());
        holder.textHallName.setText(booking.getHallName());

        // Format the date
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = inputFormat.parse(booking.getDate());
            String formattedDate = outputFormat.format(date);
            holder.textDate.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            holder.textDate.setText(booking.getDate()); // Set the original date if parsing fails
        }

        holder.textTimeSlot.setText(booking.getTimeSlot());

        // Set click listeners for edit and delete buttons
        holder.btnEdit.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onEditClick(position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onDeleteClick(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textEventName;
        TextView textHallName;
        TextView textDate;
        TextView textTimeSlot;
        Button btnEdit;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textEventName = itemView.findViewById(R.id.textEventName);
            textHallName = itemView.findViewById(R.id.textHallName);
            textDate = itemView.findViewById(R.id.textDate);
            textTimeSlot = itemView.findViewById(R.id.textTimeSlot);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.deleteButton);
        }
    }
}
