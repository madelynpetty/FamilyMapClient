package com.example.familymap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
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

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import Models.Event;
import Models.Person;
import Utils.Globals;
import Utils.Settings;

public class MapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    private GoogleMap googleMap = null;
    private ArrayList<Polyline> lines = new ArrayList<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        String loggedInID = Globals.getInstance().getLoginResult().personID;
        Event loggedInBirth = getBirthEventForPerson(loggedInID);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(loggedInBirth.getLatitude(), loggedInBirth.getLongitude())));
        googleMap.setOnMarkerClickListener(this);
        placePins(googleMap);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        clearLines();
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

            drawLines(event);
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

        float[] color = new float[]{ BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_YELLOW,
                                BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_AZURE,
                                BitmapDescriptorFactory.HUE_CYAN, BitmapDescriptorFactory.HUE_ROSE};

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
            Random random = new Random();
            int randomColor = random.nextInt(color.length - 1);
            return color[randomColor];
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

    private ArrayList<Event> getEventsForPerson(Person person) {
        ArrayList<Event> events = new ArrayList<>();
        for (Event event : Globals.getInstance().getEventListResult().getData()) {
            if (event.getPersonID().equals(person.getPersonID())) {
                events.add(event);
            }
        }
        return events;
    }

    private Event getBirthEventForPerson(String personID) {
        for (Event event : Globals.getInstance().getEventListResult().getData()) {
            if (event.getEventType().equals("birth") && event.getPersonID().equals(personID)) {
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

    private void drawFamilyLines(Event event, int width) {
        Person person = getPerson(event.getPersonID());

        if (person.getMotherID() != null) {
            Event momEarliestEvent = getEarliestEvent(getPerson(person.getMotherID()));
            if (momEarliestEvent != null) {
                PolylineOptions line = new PolylineOptions().add(
                        new LatLng(event.getLatitude(), event.getLongitude()),
                        new LatLng(momEarliestEvent.getLatitude(), momEarliestEvent.getLongitude())).
                        width(width).color(Color.BLUE);
                lines.add(googleMap.addPolyline(line));
                drawFamilyLines(momEarliestEvent, width - 4);
            }
        }

        if (person.getFatherID() != null) {
            Event dadEarliestEvent = getEarliestEvent(getPerson(person.getFatherID()));
            if (dadEarliestEvent != null) {
                PolylineOptions line = new PolylineOptions().add(
                        new LatLng(event.getLatitude(), event.getLongitude()),
                        new LatLng(dadEarliestEvent.getLatitude(), dadEarliestEvent.getLongitude())).
                        width(width).color(Color.BLUE);
                lines.add(googleMap.addPolyline(line));
                drawFamilyLines(dadEarliestEvent, width - 4);
            }
        }
    }

    private void drawLifeStoryLines(Person person) {
        ArrayList<Event> events = getEventsForPerson(person);
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return ("" + o1.getYear()).compareTo(("" + o2.getYear()));
            }
        });

        for (int i = 0; i < events.size(); i++) {
            if (i < events.size() - 1) {
                PolylineOptions line = new PolylineOptions().add(
                        new LatLng(events.get(i).getLatitude(), events.get(i).getLongitude()),
                        new LatLng(events.get(i + 1).getLatitude(), events.get(i + 1).getLongitude())).width(5).color(Color.CYAN);
                lines.add(googleMap.addPolyline(line));
            }
        }
    }

    private Event getEarliestEvent(Person person) {
        ArrayList<Event> events = getEventsForPerson(person);
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return ("" + o1.getYear()).compareTo(("" + o2.getYear()));
            }
        });
        if (events.size() < 0) {
            return null;
        }
        return events.get(0);
    }

    private void drawLines(Event event) {
        Person person = getPerson(event.getPersonID());
        if (Settings.getInstance().spouseLines == true) {
            if (person.getSpouseID() != null) {
                Event spouseEvent = getEarliestEvent(getPerson(person.getSpouseID()));
                if (spouseEvent != null) {
                    PolylineOptions line = new PolylineOptions().add(
                            new LatLng(event.getLatitude(), event.getLongitude()),
                            new LatLng(spouseEvent.getLatitude(), spouseEvent.getLongitude())).width(5).color(Color.RED);
                    lines.add(googleMap.addPolyline(line));
                }
            }
        }

        if (Settings.getInstance().familyTreeLines == true) {
            drawFamilyLines(event, 20);
        }

        if (Settings.getInstance().lifeStoryLines == true) {
            drawLifeStoryLines(getPerson(event.getPersonID()));
        }
    }

    private void clearLines() {
        for (Polyline l : lines) {
            l.remove();
        }
        lines.clear();
    }

}