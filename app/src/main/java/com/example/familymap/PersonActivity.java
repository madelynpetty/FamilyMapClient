package com.example.familymap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Models.Event;
import Models.Person;
import Utils.Globals;

public class PersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (Globals.getInstance().getLoginResult().personID != null) {
            Person person = getPerson(Globals.getInstance().getLoginResult().personID);
//            if (person.getGender().equals("f")) {
//                ((ImageView) findViewById(R.id.ImageField)).setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_female).colorRes(R.color.purple_200).sizeDp(40));
//            }
//            else {
//                ((ImageView) findViewById(R.id.ImageField)).setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_male).colorRes(R.color.teal_200).sizeDp(40));
//            }

            ((TextView) findViewById(R.id.personFirstName)).setText(person.getFirstName());
            ((TextView) findViewById(R.id.personLastName)).setText(person.getLastName());
            if (person.getGender().equals("m")) {
                ((TextView) findViewById(R.id.personGender)).setText("Male");
            }
            else {
                ((TextView) findViewById(R.id.personGender)).setText("Female");
            }

            //first expandable list code - life events
            ArrayList<Event> events = getEventsForPerson(person);
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event o1, Event o2) {
                    return ("" + o1.getYear()).compareTo(("" + o2.getYear()));
                }
            });


        }
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

    private Person getPerson(String personID) {
        for (Person person : Globals.getInstance().getPersonListResult().getData()) {
            if (person.getPersonID().equals(personID)) {
                return person;
            }
        }
        return null;
    }

    private ArrayList<Event> getEventsForPerson(Person person) {
        ArrayList<Event> events = new ArrayList<>();
        for (Event event : Globals.getInstance().getEventListResult().getData()) {
            if (event.getPersonID().equals(person.getPersonID())) {
                events.add(event);
            }
        }
        return events;
    }
}