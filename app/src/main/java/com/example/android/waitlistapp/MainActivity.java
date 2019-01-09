package com.example.android.waitlistapp;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.waitlistapp.Data.WaitlistContract;
import com.example.android.waitlistapp.Data.WaitlistDBHelper;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler_view;
    FloatingActionButton add_new_guest;
    ImageView wait_list_image;

    WaitlistDBHelper waitlistDBHelper;
    GuestAdapter guestAdapter;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler_view = (RecyclerView)findViewById(R.id.recycler_view) ;
        add_new_guest = (FloatingActionButton) findViewById(R.id.add_new_guest);
        wait_list_image = (ImageView) findViewById(R.id.wait_list_image);

        recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_view.setHasFixedSize(true);
        recycler_view.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        waitlistDBHelper = new WaitlistDBHelper(getApplicationContext());

        sqLiteDatabase = waitlistDBHelper.getWritableDatabase();

        cursor = getAllGuests();

        guestAdapter = new GuestAdapter(getApplicationContext(), cursor);

        recycler_view.setAdapter(guestAdapter);

        if (cursor.getCount() != 0)
        {
            wait_list_image.setVisibility(View.GONE);
        }
        else
        {
            wait_list_image.setVisibility(View.VISIBLE);
        }

        add_new_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showCustomDialog();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //get the id of the item being swiped
                long id = (long) viewHolder.itemView.getTag();
                //remove from DB
                removeGuest(id);
                //update the list
                guestAdapter.swapCursor(getAllGuests());

                if (getAllGuests().getCount() == 0)
                {
                    wait_list_image.setVisibility(View.VISIBLE);
                }
            }

            //COMPLETED (11) attach the ItemTouchHelper to the waitlistRecyclerView
        }).attachToRecyclerView(recycler_view);

    }

    

    private boolean removeGuest(long id)
    {
        return sqLiteDatabase.delete(WaitlistContract.WaitlistEntry.TABLE_NAME, WaitlistContract.WaitlistEntry._ID + "=" + id, null) > 0;
    }

    private long addNewGuest(String name, String partySize)
    {
       long l;
        ContentValues cv = new ContentValues();
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);
        l = sqLiteDatabase.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);
        return l;
    }

    private Cursor getAllGuests()
    {
        return sqLiteDatabase.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
    }
    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.newguest_dialoge);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final Button add_guest = (Button) dialog.findViewById(R.id.add_guest_btn);
        final Button back = (Button) dialog.findViewById(R.id.back_btn);

        final EditText guest_name = (EditText) dialog.findViewById(R.id.guest_name);
        final EditText guest_number = (EditText) dialog.findViewById(R.id.guest_number);

        final View layout = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setTextColor(Color.WHITE);
        text.setText("Please enter valid data");
        CardView lyt_card = (CardView) layout.findViewById(R.id.lyt_card);
        lyt_card.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));

        final Toast toast = new Toast(getApplicationContext());

        add_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String name = guest_name.getText().toString();
                String number = guest_number.getText().toString();

                if (name.length() == 0 || number.length() == 0)
                {
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
                else
                {
                    addNewGuest(name,number);
                    guestAdapter.swapCursor(getAllGuests());

                    wait_list_image.setVisibility(View.GONE);

                    dialog.dismiss();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                toast.cancel();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
