package com.example.familymap;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Parcelable;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.io.Serializable;

import Result.EventListResult;
import Result.LoginResult;
import Result.PersonListResult;
import Utils.Globals;

public class MainActivity extends AppCompatActivity {
    private LoginFragment loginFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Iconify.with(new FontAwesomeModule());

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
}