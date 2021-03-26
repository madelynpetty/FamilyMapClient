package com.example.familymap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {
    private LoginFragment loginFragment = null;
    private MapFragment mapFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_login);

        if (loginFragment == null) {
            loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_login, loginFragment, "LOGINFRAGMENT").
                    commit();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        System.out.println("Hit onCreateView");
        return inflater.inflate(R.layout.fragment_login, parent, false);
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showMap() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(loginFragment).commit();

//        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
//
//        if (mapFragment == null) {
//            fragmentManager.beginTransaction().add(R.id.fragment_map, new MapFragment(), "MAPFRAGMENT").
//                    commit();
//        }
    }

}