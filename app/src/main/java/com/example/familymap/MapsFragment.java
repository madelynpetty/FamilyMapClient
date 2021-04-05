package com.example.familymap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import Models.Event;
import Models.Person;

public class MapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    private GoogleMap googleMap = null;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng saltLakeCity = new LatLng(-40, 112);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(saltLakeCity));
        googleMap.setOnMarkerClickListener(this);
        placePins(googleMap);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Event event = getEvent((String) marker.getTag());

        if (event != null) {
            Person person = getPerson(event.getPersonID());

            if (person != null) {
                if (person.getGender().equals("f")) {
                    ((ImageView) getView().findViewById(R.id.ImageField)).setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).colorRes(R.color.purple_200).sizeDp(40));
                }
                else {
                    ((ImageView) getView().findViewById(R.id.ImageField)).setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).colorRes(R.color.teal_200).sizeDp(40));
                }

                ((TextView) getView().findViewById(R.id.NameText)).setText(person.getFirstName() + " " + person.getLastName());
                ((TextView) getView().findViewById(R.id.EventText)).setText(event.getEventType() +
                        ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")");
            }
        }

        return false;
    }


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
                ((MainActivity) getActivity()).showSettings();
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
            mapFragment.getMapAsync(this);
        }
    }

    private void placePins(GoogleMap googleMap) {
        if (((MainActivity) getActivity()).eventListResult == null) {
            Toast.makeText(getContext(), "No events yet", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Event event : ((MainActivity) getActivity()).eventListResult.getData()) {
            LatLng pin = new LatLng(event.getLatitude(), event.getLongitude());

//            googleMap.addMarker(new MarkerOptions().position(pin)).setTag(event.getEventID();
            Marker marker = googleMap.addMarker(new MarkerOptions().position(pin));
            marker.setTag(event.getEventID());
        }
    }


    private Event getEvent(String eventID) {
        for (Event event : ((MainActivity) getActivity()).eventListResult.getData()) {
            if (event.getEventID().equals(eventID)) {
                return event;
            }
        }
        return null;
    }

    private Person getPerson(String personID) {
        for (Person person : ((MainActivity) getActivity()).personListResult.getData()) {
            if (person.getPersonID().equals(personID)) {
                return person;
            }
        }
        return null;
    }




}