package com.example.familymap;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import Result.EventListResult;
import Result.LoginResult;
import Result.PersonListResult;

public class MainActivity extends AppCompatActivity {
    private LoginFragment loginFragment = null;
    private MapsFragment mapsFragment = null;
    public LoginResult loginResult = null;
    public EventListResult eventListResult = null;
    public PersonListResult personListResult = null;
    public String serverHost = null;
    public String serverPort = null;

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
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        System.out.println("Hit onCreateView");
        return inflater.inflate(R.layout.fragment_login, parent, false);
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

    public void showMap(MapsFragment mapsFragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_login);
//
//        fragmentManager.beginTransaction().remove(fragment).commit();
//
//        getSupportFragmentManager().beginTransaction().add(R.id.fragment_map, new MapFragment(), "MAPFRAGMENT")
//                .commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_placeholder, mapsFragment).commit();
    }

    public void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void setLoginResult(LoginResult lr) {
        loginResult = lr;
    }
}