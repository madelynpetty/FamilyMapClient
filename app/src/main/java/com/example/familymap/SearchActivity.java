package com.example.familymap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;

import Models.Event;
import Models.Person;
import Utils.Globals;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchAdapter adapter;

    private static SearchActivity instance = null;

    public static SearchActivity getInstance() {
        if (instance == null) {
            instance = new SearchActivity();
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Family Map Search");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.searchRecyclerView);
        buildRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_action);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        return true;
    }

    private void filter(String text) {
        ArrayList<ListItemData> filteredList = new ArrayList<>();

        for (Person p : Globals.getInstance().getPersonListResult().getData()) {
            if (p != null) {
                if ((p.getFirstName() != null && p.getFirstName().toLowerCase().contains(text.toLowerCase())) ||
                        (p.getLastName() != null && p.getLastName().toLowerCase().contains(text.toLowerCase()))) {
                    IconDrawable image;
                    if (p.getGender().equals("m")) {
                        image = new IconDrawable(this, FontAwesomeIcons.fa_male).colorRes(R.color.teal_200).sizeDp(40);
                    }
                    else {
                        image = new IconDrawable(this, FontAwesomeIcons.fa_female).colorRes(R.color.purple_200).sizeDp(40);
                    }

                    filteredList.add(new ListItemData(p.getFirstName() + " " + p.getLastName(), "", image));
                }
            }
        }

        for (Event e : Globals.getInstance().getEventListResult().getData()) {
            if (e != null) {
                if ((e.getCountry() != null && e.getCountry().toLowerCase().contains(text.toLowerCase())) ||
                        (e.getCity() != null && e.getCity().toLowerCase().contains(text.toLowerCase())) ||
                        (e.getEventType() != null && e.getEventType().toLowerCase().contains(text.toLowerCase())) ||
                        ("" + e.getYear()).contains(text.toLowerCase())) {
                    Person p = getPersonFromPersonID(e.getPersonID());
                    IconDrawable image = new IconDrawable(this, FontAwesomeIcons.fa_map_marker).colorRes(R.color.black).sizeDp(40);
                    filteredList.add(new ListItemData(p.getFirstName() + " " + p.getLastName(),
                            e.getEventType() + ": " + e.getCity() + ", " + e.getCountry() + " (" + e.getYear() + ")", image));
                }
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No Data Found.", Toast.LENGTH_SHORT).show();
        }
        adapter.filterList(filteredList);

    }

    private void buildRecyclerView() {
        adapter = new SearchAdapter(new ArrayList<ListItemData>(), SearchActivity.this);

        LinearLayoutManager manager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
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

    private Person getPersonFromPersonID(String personID) {
        for (Person p : Globals.getInstance().getPersonListResult().getData()) {
            if (p.getPersonID().equals(personID)) {
                return p;
            }
        }
        return null;
    }

    public void showEventActivity(String name, String description, Context context) {
        String eventType = description.substring(0, description.indexOf(":"));
        for (Event e : Globals.getInstance().getEventListResult().getData()) {
            Person p = getPersonFromPersonID(e.getPersonID());
            if (e.getEventType().equals(eventType) && name.equals(p.getFirstName() + " " + p.getLastName())) {
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra("eventID", e.getEventID());
                context.startActivity(intent);
            }
        }
    }

    public void showPersonActivity(String name, Context context) {
        for (Person p : Globals.getInstance().getPersonListResult().getData()) {
            if (name.equals(p.getFirstName() + " " + p.getLastName())) {
                Intent intent = new Intent(context, PersonActivity.class);
                intent.putExtra("personID", p.getPersonID());
                context.startActivity(intent);
            }
        }
    }
}