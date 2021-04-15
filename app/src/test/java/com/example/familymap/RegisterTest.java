package com.example.familymap;

import com.google.gson.Gson;
import org.junit.Test;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import Request.RegisterRequest;
import Result.EventListResult;
import Result.LoginResult;
import Result.PersonListResult;
import Utils.StringUtil;
import static org.junit.jupiter.api.Assertions.*;

//Login method
//Registering a new user
//Retrieving people related to a registered user
//Retrieving events related to a registered user

public class RegisterTest {
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
    public void validRegistration() throws IOException {
        clearDatabase();
        String username = "sheila";

        RegisterRequest registerRequest = new RegisterRequest(username, "parker", "sheila@parker.com",
                "Sheila", "Parker", "f");

        URL url = new URL( "http://localhost:8080/user/register");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.connect();

        Gson gson = new Gson();
        StringUtil.writeStringToStream(gson.toJson(registerRequest), connection.getOutputStream());
        connection.getOutputStream().close();

        LoginResult loginResult = null;
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String json = StringUtil.getStringFromInputStream(connection.getInputStream());
            loginResult = gson.fromJson(json, LoginResult.class);
        }
        assertEquals(responseCode, HttpURLConnection.HTTP_OK);

        LoginResult expectedResult = new LoginResult(null, username, null);

        assertEquals(expectedResult.username, loginResult.username);
    }

    @Test
    public void usernameTaken() throws IOException {
        validRegistration();

        //SECOND REGISTRATION
        RegisterRequest registerRequest2 = new RegisterRequest("sheila", "pass", "hi@gmail.com",
                "bob", "smith", "m");
        URL url2 = new URL( "http://localhost:8080/user/register");
        HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
        connection2.setRequestMethod("POST");
        connection2.setDoOutput(true);
        connection2.connect();

        Gson gson2 = new Gson();
        StringUtil.writeStringToStream(gson2.toJson(registerRequest2), connection2.getOutputStream());
        connection2.getOutputStream().close();

        int responseCode2 = connection2.getResponseCode();
        assertEquals(responseCode2, HttpURLConnection.HTTP_BAD_REQUEST);
    }

    @Test
    public void hasRequiredFields() throws IOException {
        clearDatabase();
        String username = "sheila";
        RegisterRequest registerRequest = new RegisterRequest(username, "parker", "sheila@parker.com",
                "Sheila", "Parker", "f");

        URL url = new URL( "http://localhost:8080/user/register");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.connect();

        Gson gson = new Gson();
        StringUtil.writeStringToStream(gson.toJson(registerRequest), connection.getOutputStream());
        connection.getOutputStream().close();

        LoginResult loginResult = null;
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String json = StringUtil.getStringFromInputStream(connection.getInputStream());
            loginResult = gson.fromJson(json, LoginResult.class);
        }
        assertEquals(responseCode, HttpURLConnection.HTTP_OK);

        LoginResult expectedResult = new LoginResult(null, username, null);

        assert loginResult != null;
        assertEquals(expectedResult.username, loginResult.username);
        assertNotEquals(expectedResult.authtoken, loginResult.authtoken); //checking to see if not null
        assertNotEquals(expectedResult.personID, loginResult.authtoken); //checking to see if not null
        assertEquals(expectedResult.success, loginResult.success);
    }

    @Test
    public void canHaveSameFirstName() throws IOException { //two users being registered with same name
        String firstName = "sheila";
        validRegistration();

        //SECOND REGISTRATION
        RegisterRequest registerRequest2 = new RegisterRequest("differentUser", "pass", "hi@gmail.com",
                firstName, "smith", "m");
        URL url2 = new URL( "http://localhost:8080/user/register");
        HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
        connection2.setRequestMethod("POST");
        connection2.setDoOutput(true);
        connection2.connect();

        Gson gson2 = new Gson();
        StringUtil.writeStringToStream(gson2.toJson(registerRequest2), connection2.getOutputStream());
        connection2.getOutputStream().close();

        int responseCode2 = connection2.getResponseCode();
        assertEquals(responseCode2, HttpURLConnection.HTTP_OK); //if you could not have two users with the same first name, then it would have thrown a 400
    }

    @Test
    public void populatesEventListResult() throws IOException {
        clearDatabase();
        LoginResult loginResult = registerUser();
        if (loginResult == null) {
            assertEquals(0, 1); //fails the test since registration was unsuccessful in registerUser()
        }

        URL url = new URL("http://localhost:8080/event");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(false);
        connection.setRequestProperty("Authorization", loginResult.authtoken);

        connection.connect();

        EventListResult eventListResult = null;

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            String json = StringUtil.getStringFromInputStream(connection.getInputStream());
            eventListResult = gson.fromJson(json, EventListResult.class);
        }

        assertNotEquals(eventListResult, null);
    }


    @Test
    public void populatesPersonListResult() throws IOException {
        clearDatabase();
        LoginResult loginResult = registerUser();
        if (loginResult == null) {
            assertEquals(0, 1); //fails the test since registration was unsuccessful in registerUser()
        }

        URL url = new URL( "http://localhost:8080/person");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(false);
        connection.addRequestProperty("Authorization", loginResult.authtoken);

        connection.connect();

        PersonListResult personListResult = null;

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            String json = StringUtil.getStringFromInputStream(connection.getInputStream());
            personListResult = gson.fromJson(json, PersonListResult.class);
        }

        assertNotEquals(personListResult, null);
    }

    private LoginResult registerUser() throws IOException {
        clearDatabase();
        String username = "sheila";

        RegisterRequest registerRequest = new RegisterRequest(username, "parker", "sheila@parker.com",
                "Sheila", "Parker", "f");

        URL url = new URL( "http://localhost:8080/user/register");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.connect();

        Gson gson = new Gson();
        StringUtil.writeStringToStream(gson.toJson(registerRequest), connection.getOutputStream());
        connection.getOutputStream().close();

        LoginResult loginResult = null;
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String json = StringUtil.getStringFromInputStream(connection.getInputStream());
            loginResult = gson.fromJson(json, LoginResult.class);
        }

        return loginResult;
    }
}