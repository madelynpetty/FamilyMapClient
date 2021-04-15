package com.example.familymap;

import com.google.gson.Gson;
import org.junit.Test;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import Models.Event;
import Request.LoginRequest;
import Result.EventListResult;
import Result.LoginResult;
import Result.PersonListResult;
import Utils.StringUtil;
import static org.junit.jupiter.api.Assertions.*;

//Login method
//Retrieving people related to a logged in user
//Retrieving events related to a logged in user

public class LoginTest {
    private void clearDatabase() throws IOException {
        URL url = new URL( "http://localhost:8080/clear");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Database cleared");
        }
        else {
            System.out.println("Error: database could not clear");
            System.out.println("Response code: " + responseCode);
        }
    }

    @Test
    public void loginPass() throws Exception {
        clearDatabase();
        RegisterTest registerTest = new RegisterTest();
        registerTest.validRegistration();

        LoginRequest loginRequest = new LoginRequest("sheila", "parker");
        URL url = new URL( "http://localhost:8080/user/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.connect();

        Gson gson = new Gson();
        StringUtil.writeStringToStream(gson.toJson(loginRequest), connection.getOutputStream());
        connection.getOutputStream().close();

        LoginResult loginResult = null;

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String json = StringUtil.getStringFromInputStream(connection.getInputStream());
            loginResult = gson.fromJson(json, LoginResult.class);
        }

        assertNotEquals(loginRequest, null);
        assertEquals(loginResult.username, loginRequest.getUsername());
    }

    @Test
    public void loginFail() throws Exception {
        clearDatabase();
        RegisterTest registerTest = new RegisterTest();
        registerTest.validRegistration();

        LoginRequest loginRequest = new LoginRequest("sheila", "WRONGPASSWORD");
        URL url = new URL( "http://localhost:8080/user/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.connect();

        Gson gson = new Gson();
        StringUtil.writeStringToStream(gson.toJson(loginRequest), connection.getOutputStream());
        connection.getOutputStream().close();

        LoginResult loginResult = null;

        int responseCode = connection.getResponseCode();
        assertEquals(responseCode, HttpURLConnection.HTTP_BAD_REQUEST);
    }

    @Test
    public void populatesEventListResult() throws Exception {
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
    }

    @Test
    public void populatesPersonListResult() throws Exception {
        //sheila is being logged in
        clearDatabase();
        RegisterTest registerTest = new RegisterTest();
        registerTest.validRegistration();

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

        int loginResponseCode = loginConnection.getResponseCode();
        if (loginResponseCode == HttpURLConnection.HTTP_OK) {
            String json = StringUtil.getStringFromInputStream(loginConnection.getInputStream());
            loginResult = gson.fromJson(json, LoginResult.class);
        }

        assertEquals(loginResponseCode, HttpURLConnection.HTTP_OK);
        assertNotEquals(loginResult, null);

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
    }
}
