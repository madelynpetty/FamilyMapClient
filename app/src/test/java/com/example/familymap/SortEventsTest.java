package com.example.familymap;

import com.google.gson.Gson;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.junit.Test;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import Models.Event;
import Models.Person;
import Request.LoginRequest;
import Result.EventListResult;
import Result.LoginResult;
import Result.PersonListResult;
import Utils.Globals;
import Utils.Settings;
import Utils.StringUtil;

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Chronologically sorts a person’s individual events (birth first, death last, etc.)
public class SortEventsTest {
    @Test
    public void sortsEventsChronologically() throws Exception {
        populateEventListResult();

        EventListResult eventListResult = populateEventListResult();
        eventListResult.getData();

        Settings.getInstance().maleEvents = true;
        Settings.getInstance().femaleEvents = true;
        Settings.getInstance().mothersSide = true;
        Settings.getInstance().fathersSide = true;

        Iconify.with(new FontAwesomeModule());

        PersonActivity personActivity = PersonActivity.getInstance();

        Person loggedInPerson = getPerson(Globals.getInstance().getLoginResult().personID);
        Person mom = getPerson(loggedInPerson.getMotherID());
        List<ListItemData> eventsInOrder = personActivity.getEventsForPersonInOrder(mom, false);

        assertNotEquals(null, eventsInOrder);

        ArrayList<String> years = new ArrayList<>();
        for (ListItemData item : eventsInOrder) {
            String year = item.description.substring(item.description.indexOf("(") + 1, item.description.indexOf(")"));
            years.add(year);
        }

        for (int i = 0; i < years.size() - 1; i++) {
            assertTrue(parseInt(years.get(i)) <= parseInt(years.get(i + 1)));
        }
    }

    private EventListResult populateEventListResult() throws Exception { //method comes from LoginTest.java, but I changed a few things and need to return something
        //register sheila
        RegisterTest registerTest = new RegisterTest();
        registerTest.validRegistration();

        //logging in sheila
        LoginRequest loginRequest = new LoginRequest("sheila", "parker");
        URL loginURL = new URL( "http://localhost:8080/user/login");
        HttpURLConnection loginConnection = (HttpURLConnection) loginURL.openConnection();
        loginConnection.setRequestMethod("POST");
        loginConnection.setDoOutput(true);

        loginConnection.connect();

        Gson gson = new Gson();
        StringUtil.writeStringToStream(gson.toJson(loginRequest), loginConnection.getOutputStream());
        loginConnection.getOutputStream().close();

        LoginResult loginResult = null;

        int LoginResponseCode = loginConnection.getResponseCode();
        if (LoginResponseCode == HttpURLConnection.HTTP_OK) {
            String json = StringUtil.getStringFromInputStream(loginConnection.getInputStream());
            loginResult = gson.fromJson(json, LoginResult.class);
        }

        assertNotEquals(loginRequest, null);
        assertEquals(loginResult.username, loginRequest.getUsername());
        Globals.getInstance().setLoginResult(loginResult);

        //checking event now
        URL url = new URL("http://localhost:8080/event");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(false);

        connection.setRequestProperty("Authorization", loginResult.authtoken);

        connection.connect();

        EventListResult eventListResult = null;
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String json = StringUtil.getStringFromInputStream(connection.getInputStream());
            eventListResult = gson.fromJson(json, EventListResult.class);
        }
        assertNotEquals(eventListResult, null);

        EventListResult emptyEventListResult = new EventListResult(new ArrayList<Event>(), true);
        assertNotEquals(eventListResult, emptyEventListResult);
        Globals.getInstance().setEventListResult(eventListResult);

        //now filling person
        URL personURL = new URL( "http://localhost:8080/person");
        HttpURLConnection personConnection = (HttpURLConnection) personURL.openConnection();
        personConnection.setRequestMethod("GET");
        personConnection.setDoOutput(false);

        personConnection.addRequestProperty("Authorization", loginResult.authtoken);

        personConnection.connect();

        PersonListResult personListResult = null;

        int personResponseCode = personConnection.getResponseCode();
        if (personResponseCode == HttpURLConnection.HTTP_OK) {
            String json = StringUtil.getStringFromInputStream(personConnection.getInputStream());
            personListResult = gson.fromJson(json, PersonListResult.class);
        }

        assertEquals(personResponseCode, HttpURLConnection.HTTP_OK);
        assertNotEquals(personListResult, null);
        Globals.getInstance().setPersonListResult(personListResult);

        return eventListResult;
    }

    private Person getPerson(String personID) {
        for (Person person : Globals.getInstance().getPersonListResult().getData()) {
            if (person.getPersonID().equals(personID)) {
                return person;
            }
        }
        return null;
    }
}
