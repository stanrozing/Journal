package com.example.rozin.journal;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    // make a global database and adapter
    private EntryDatabase db;
    private EntryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instantiate database and adapter
        db = EntryDatabase.getInstance(getApplicationContext());
        Cursor cursor = db.selectAll();
        adapter = new EntryAdapter(this, cursor);

        // set adapter and listeners to listview
        ListView list = findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new onItemClickListener());
        list.setOnItemLongClickListener(new onItemLongClickListener());
    }

    public void fbOnClicked(View view) {

        // go to InputActivity when clicked on button
        Intent intent = new Intent(MainActivity.this, InputActivity.class);
        startActivity(intent);
    }

    private class onItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            // get the cursor that was clicked on
            Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);

            // get all the indices voor the values needed
            int title_index = cursor.getColumnIndex("title");
            int mood_index = cursor.getColumnIndex("mood");
            int time_index = cursor.getColumnIndex("timestamp");
            int content_index = cursor.getColumnIndex("content");

            // get all the values needed and put them in an journalentry
            String title = cursor.getString(title_index);
            String mood = cursor.getString(mood_index);
            String time = cursor.getString(time_index);
            String content = cursor.getString(content_index);
            JournalEntry clickedEntry = new JournalEntry(title, content, mood, time);

            // ga naar de detailactivity en geef de entry waarop is geklikt mee
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("clickedEntry", clickedEntry);
            startActivity(intent);
        }
    }

    private class onItemLongClickListener implements ListView.OnItemLongClickListener{
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

            // get cursor that was longclicked on
            Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);

            // get the id of this cursor
            int index = cursor.getColumnIndex("_id");
            Long id = cursor.getLong(index);

            // delete the cursor (journalentry) from the database
            db = EntryDatabase.getInstance(getApplicationContext());
            db.delete(id);

            // update het scherm
            updateData();
            return true;
        }
    }

    // function that updates the adapter
    private void updateData() {
        Cursor newCursor = db.selectAll();
        adapter.swapCursor(newCursor);
    }
}
