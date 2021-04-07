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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;

import Models.Event;
import Models.Person;
import Result.PersonListResult;
import Utils.Globals;
import Utils.Settings;

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

    private float getEventPin(Event event) {
        float birthColor = BitmapDescriptorFactory.HUE_GREEN;
        float baptismColor = BitmapDescriptorFactory.HUE_BLUE;
        float deathColor = BitmapDescriptorFactory.HUE_ORANGE;
        float marriageColor = BitmapDescriptorFactory.HUE_MAGENTA;
        float errorColor = BitmapDescriptorFactory.HUE_RED;

        if (event.getEventType().equals("birth")) {
            return birthColor;
        }
        else if (event.getEventType().equals("baptism")) {
            return baptismColor;
        }
        else if (event.getEventType().equals("death")) {
            return deathColor;
        }
        else if (event.getEventType().equals("marriage")) {
            return marriageColor;
        }
        else {
            return errorColor;
        }
    }

    private void placePins(GoogleMap googleMap) {
        if (Globals.getInstance().getEventListResult() == null) {
            Toast.makeText(getContext(), "No events to display on map", Toast.LENGTH_SHORT).show();
            return;
        }

        Person person = null;

        ArrayList<Person> momsSide = getMomsSide(getPerson(Globals.getInstance().getLoginResult().personID));
        ArrayList<Person> dadsSide = getDadsSide(getPerson(Globals.getInstance().getLoginResult().personID));

        for (Event event : Globals.getInstance().getEventListResult().getData()) {
            person = getPerson(event.getPersonID());
            if (Settings.getInstance().maleEvents == false && person.getGender().equals("m")) continue;
            if (Settings.getInstance().femaleEvents == false && person.getGender().equals("f")) continue;
            if (Settings.getInstance().mothersSide == false && momsSide.contains(person)) continue;
            if (Settings.getInstance().fathersSide == false && dadsSide.contains(person)) continue;


            LatLng pin = new LatLng(event.getLatitude(), event.getLongitude());
            Marker marker = googleMap.addMarker(new MarkerOptions().position(pin).icon(BitmapDescriptorFactory.defaultMarker(getEventPin(event))));
            marker.setTag(event.getEventID());
        }
    }

    private Event getEvent(String eventID) {
        for (Event event : Globals.getInstance().getEventListResult().getData()) {
            if (event.getEventID().equals(eventID)) {
                return event;
            }
        }
        return null;
    }

    private Person getPerson(String personID) {
        for (Person person : Globals.getInstance().getPersonListResult().getData()) {
            if (person.getPersonID().equals(personID)) {
                return person;
            }
        }
        return null;
    }

    private ArrayList<Person> getMomsSide(Person person) {
        ArrayList<Person> momsSide = new ArrayList<>();

        if (person.getMotherID() != null) {
            Person mom = getPerson(person.getMotherID());
            Person dad = getPerson(person.getFatherID());
            momsSide.add(mom);
            if (!person.getPersonID().equals(Globals.getInstance().getLoginResult().personID)) {
                momsSide.add(dad);
                momsSide.addAll(getMomsSide(dad));
            }
            momsSide.addAll(getMomsSide(mom));
        }
        return momsSide;
    }

    private ArrayList<Person> getDadsSide(Person person) {
        ArrayList<Person> momsSide = new ArrayList<>();

        if (person.getMotherID() != null) {
            Person mom = getPerson(person.getMotherID());
            Person dad = getPerson(person.getFatherID());
            momsSide.add(mom);
            if (!person.getPersonID().equals(Globals.getInstance().getLoginResult().personID)) {
                momsSide.add(dad);
                momsSide.addAll(getMomsSide(dad));
            }
            momsSide.addAll(getMomsSide(mom));
        }
        return momsSide;
    }

}