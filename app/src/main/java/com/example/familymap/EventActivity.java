package com.example.familymap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.view.Menu;
import android.view.MenuItem;

import Models.Event;
import Utils.Globals;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String eventID = getIntent().getStringExtra("eventID");
        Globals.getInstance().setEventForEventActivity(getEventWithID(eventID));

        showMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Event getEventWithID(String eventID) {
        for (Event e : Globals.getInstance().getEventListResult().getData()) {
            if (e.getEventID().equals(eventID)) {
                return e;
            }
        }
        return null;
    }

    private void showMap() {
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_event, new MapsFragment()).commit();
    }
}