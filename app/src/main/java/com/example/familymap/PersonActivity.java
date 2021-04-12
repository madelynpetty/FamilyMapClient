package com.example.familymap;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import Models.Event;
import Models.Person;
import Utils.Globals;

public class PersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_person);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent eventIntent = new Intent(this, EventActivity.class);
        Intent familyIntent = new Intent(this, PersonActivity.class);

        if (getIntent().getStringExtra("personID") != null) {
            Person person = getPerson(getIntent().getStringExtra("personID"));

            ((TextView) findViewById(R.id.personFirstName)).setText(person.getFirstName());
            ((TextView) findViewById(R.id.personLastName)).setText(person.getLastName());
            if (person.getGender().equals("m")) {
                ((TextView) findViewById(R.id.personGender)).setText("Male");
            } else {
                ((TextView) findViewById(R.id.personGender)).setText("Female");
            }

            ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandableList);

            HashMap<String, List<ListItemData>> ExpandableListDetail = new HashMap<>();
            ExpandableListDetail.put("LIFE EVENTS", getEventsForPersonInOrder(person));
            ExpandableListDetail.put("FAMILY", getFamily(person));

            ArrayList<String> expandableListTitle = new ArrayList<>(ExpandableListDetail.keySet());
            ExpandableListAdapter expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, ExpandableListDetail);

            expandableListView.setAdapter(expandableListAdapter);
            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {

                }
            });

            expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                @Override
                public void onGroupCollapse(int groupPosition) {
                    //DO NOTHING
                }
            });

            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    if (groupPosition == 0) {
                        List<ListItemData> personsEvents = getEventsForPersonInOrder(person);
                        List<String> personsEventTypes = new ArrayList<>();

                        for (ListItemData l : personsEvents) {
                            String substring = l.description.substring(0, l.description.indexOf(":"));
                            personsEventTypes.add(substring);
                        }

                        List<ListItemData> items = ExpandableListDetail.get("LIFE EVENTS");
                        ListItemData value = items.get(childPosition);
                        String event = value.description.substring(0, value.description.indexOf(":"));

                        for (String s : personsEventTypes) {
                            if (event.equals(s)) {
                                for (Event e : Globals.getInstance().getEventListResult().getData()) {
                                    if (e.getPersonID().equals(person.getPersonID()) && e.getEventType().equals(event)) {
                                        eventIntent.putExtra("eventID", e.getEventID());
                                        startActivity(eventIntent);
                                    }
                                }
                            }
                        }
                    } else {
                        List<ListItemData> personsFamily = getFamily(person);
                        List<String> personsFamilyMembersFirstNames = new ArrayList<>();

                        for (ListItemData s : personsFamily) {
                            String substring = s.name.substring(0, s.name.indexOf(" "));
                            personsFamilyMembersFirstNames.add(substring);
                        }

                        List<ListItemData> strings = ExpandableListDetail.get("FAMILY");
                        ListItemData value = strings.get(childPosition);
                        String name = value.name.substring(0, value.name.indexOf(" "));

                        for (String s : personsFamilyMembersFirstNames) {
                            if (name.equals(s)) {
                                for (Person p : Globals.getInstance().getPersonListResult().getData()) {
                                    if (p.getAssociatedUsername().equals(person.getAssociatedUsername()) && p.getFirstName().equals(name)) {
                                        familyIntent.putExtra("personID", p.getPersonID());
                                        startActivity(familyIntent);
                                    }
                                }
                            }
                        }
                    }
                    return false;
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

    private Person getPersonIDFromName(String firstName, String lastName) {
        for (Person person : Globals.getInstance().getPersonListResult().getData()) {
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                return person;
            }
        }
        return null;
    }

    private List<ListItemData> getEventsForPersonInOrder(Person person) {
        ArrayList<Event> events = new ArrayList<>();
        for (Event event : Globals.getInstance().getEventListResult().getData()) {
            if (event.getPersonID().equals(person.getPersonID())) {
                events.add(event);
            }
        }

        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return ("" + o1.getYear()).compareTo(("" + o2.getYear()));
            }
        });

        List<ListItemData> data = new ArrayList<>();

        for (Event e : events) {
            Person p = getPersonFromPersonID(e.getPersonID());
            IconDrawable image = new IconDrawable(this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.black).sizeDp(40);
            data.add(new ListItemData(p.getFirstName() + " " + p.getLastName(), e.getEventType() + ": " + e.getCity() + ", " + e.getCountry() + " (" + e.getYear() + ")", image));
        }

        return data;
    }

    private List<ListItemData> getFamily(Person person) {
        List<ListItemData> family = new ArrayList<>();

        if (person.getFatherID() != null) {
            Person dad = getPerson(person.getFatherID());
            IconDrawable image = new IconDrawable(this, FontAwesomeIcons.fa_male).colorRes(R.color.teal_200).sizeDp(40);
            family.add(new ListItemData(dad.getFirstName() + " " + dad.getLastName() + "\nFather", "", image));
        }

        if (person.getMotherID() != null) {
            Person mom = getPerson(person.getMotherID());
            IconDrawable image = new IconDrawable(this, FontAwesomeIcons.fa_female).colorRes(R.color.purple_200).sizeDp(40);
            family.add(new ListItemData(mom.getFirstName() + " " + mom.getLastName() + "\nMother", "", image));
        }

        if (person.getSpouseID() != null) {
            Person spouse = getPerson(person.getSpouseID());
            IconDrawable image;
            if (spouse.getGender().equals("m")) {
                image = new IconDrawable(this, FontAwesomeIcons.fa_male).colorRes(R.color.teal_200).sizeDp(40);
            }
            else {
                image = new IconDrawable(this, FontAwesomeIcons.fa_female).colorRes(R.color.purple_200).sizeDp(40);
            }
            family.add(new ListItemData(spouse.getFirstName() + " " + spouse.getLastName() + "\nSpouse", "", image));
        }

        for (Person p : Globals.getInstance().getPersonListResult().getData()) {
            if (p != null) {
                if ((p.getFatherID() != null && p.getFatherID().equals(person.getPersonID())) || (p.getMotherID() != null && p.getMotherID().equals(person.getPersonID()))) {
                    IconDrawable image;
                    if (p.getGender().equals("m")) {
                        image = new IconDrawable(this, FontAwesomeIcons.fa_male).colorRes(R.color.teal_200).sizeDp(40);
                    }
                    else {
                        image = new IconDrawable(this, FontAwesomeIcons.fa_female).colorRes(R.color.purple_200).sizeDp(40);
                    }
                    family.add(new ListItemData(p.getFirstName() + " " + p.getLastName() + "\nChild", "", image));
                }
            }
        }

        return family;
    }

    private Person getPersonFromPersonID(String personID) {
        for (Person p : Globals.getInstance().getPersonListResult().getData()) {
            if (p.getPersonID().equals(personID)) {
                return p;
            }
        }
        return null;
    }
}