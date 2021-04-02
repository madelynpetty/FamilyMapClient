package com.example.familymap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import Models.Event;
import Result.EventListResult;
import Result.LoginResult;
import Utils.StringUtil;

public class MapsFragment extends Fragment {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng saltLakeCity = new LatLng(-40, 112);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(saltLakeCity));
            placePins(googleMap);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_menu, menu);
        menu.findItem(R.id.map_search).setIcon( new IconDrawable(getActivity(), FontAwesomeIcons.fa_search).colorRes(R.color.white).sizeDp(40));
        menu.findItem(R.id.map_settings).setIcon( new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear).colorRes(R.color.white).sizeDp(40));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.map_search:
                Toast.makeText(getActivity(), "Calls Search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.map_settings:
                Toast.makeText(getActivity(), "Calls Settings", Toast.LENGTH_SHORT).show();
                return true;
            default:
                Toast.makeText(getActivity(), "Invalid Option", Toast.LENGTH_SHORT).show();
                return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Family Map");
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_maps);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void placePins(GoogleMap googleMap) {
        if (((MainActivity) getActivity()).eventListResult == null) {
            Toast.makeText(getContext(), "No events yet", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Event event : ((MainActivity) getActivity()).eventListResult.getData()) {
            LatLng pin = new LatLng(event.getLatitude(), event.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(pin).title(event.getEventType()));
        }
    }
}