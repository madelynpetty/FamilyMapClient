package com.example.familymap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import Utils.Globals;
import Utils.Settings;

public class MainActivity extends AppCompatActivity {
    private LoginFragment loginFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Iconify.with(new FontAwesomeModule());
        retrieveSharedPreferences();

        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }

        if (Globals.getInstance().getLoginResult() != null) {
            showMap();
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

        if (id == R.id.map_settings) {
            return false;
        }
        else if (id == R.id.map_search){
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showMap() {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_placeholder, new MapsFragment()).commit();
    }

    public void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void showSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void retrieveSharedPreferences() {
        SharedPreferences shared = getSharedPreferences("familyShared", MODE_PRIVATE);

        if (shared != null) {
            Settings.getInstance().familyTreeLines = shared.getBoolean("familyTreeLines", true);
            Settings.getInstance().lifeStoryLines = shared.getBoolean("lifeStoryLines", true);
            Settings.getInstance().spouseLines = shared.getBoolean("spouseLines", true);
            Settings.getInstance().fathersSide = shared.getBoolean("fathersSide", true);
            Settings.getInstance().mothersSide = shared.getBoolean("mothersSide", true);
            Settings.getInstance().maleEvents = shared.getBoolean("maleEvents", true);
            Settings.getInstance().femaleEvents = shared.getBoolean("femaleEvents", true);
        }
    }
}