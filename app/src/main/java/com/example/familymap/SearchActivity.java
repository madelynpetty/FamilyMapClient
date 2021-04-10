package com.example.familymap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

import Models.Event;
import Models.Person;
import Utils.Globals;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private ArrayList<ListItemData> results;

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
                    filteredList.add(new ListItemData(p.getFirstName() + " " + p.getLastName(), ""));
                }
            }
        }

//        for (Event e : Globals.getInstance().getEventListResult().getData()) {
//            if (item.getCourseName().toLowerCase().contains(text.toLowerCase())) {
//                filteredList.add(item);
//            }
//        }

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
}