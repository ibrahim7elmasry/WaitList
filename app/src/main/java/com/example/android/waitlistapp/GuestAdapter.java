package com.example.android.waitlistapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.waitlistapp.GuestAdapter.Guestviewholder;
import com.example.android.waitlistapp.Data.WaitlistContract.WaitlistEntry;

public class GuestAdapter extends RecyclerView.Adapter<Guestviewholder>
{
    Context context;
    Cursor cursor;

    public GuestAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public Guestviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        return new  Guestviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Guestviewholder holder, int position)
    {
        if (!cursor.moveToPosition(position))
        {
            return;
        }

        String name = cursor.getString(cursor.getColumnIndex(WaitlistEntry.COLUMN_GUEST_NAME));
        String size = cursor.getString(cursor.getColumnIndex(WaitlistEntry.COLUMN_PARTY_SIZE));

        long id = cursor.getLong(cursor.getColumnIndex(WaitlistEntry._ID));

        holder.name.setText(name);
        holder.size.setText(size);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (cursor != null)
        {
            cursor.close();
        }

        cursor = newCursor;

        if (newCursor != null) {
            // Refresh the RecyclerView
            this.notifyDataSetChanged();
        }
    }

    public static final class Guestviewholder extends RecyclerView.ViewHolder
    {
           TextView name, size;

        public Guestviewholder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.guest_name);
            size = (TextView) itemView.findViewById(R.id.party_size);

        }
    }
}
