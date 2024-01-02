
package com.kasc.hall;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> implements ListAdapter {

    private List<Booking> bookingList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onEditClick(int position);

        void onCancelClick(int position);
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
        holder.textDate.setText(booking.getDate());
        holder.textTimeSlot.setText(booking.getTimeSlot());

        // Set click listeners for edit and cancel
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onEditClick(position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onCancelClick(position);
            }
            return true;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textEventName = itemView.findViewById(R.id.textEventName);
            textHallName = itemView.findViewById(R.id.textHallName);
            textDate = itemView.findViewById(R.id.textDate);
            textTimeSlot = itemView.findViewById(R.id.textTimeSlot);
        }
    }
}