package com.example.familymap;

import com.google.gson.Gson;

import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Models.Event;
import Models.Person;
import Request.LoadRequest;
import Request.LoginRequest;
import Result.EventListResult;
import Result.LoadResult;
import Result.LoginResult;
import Result.PersonListResult;
import Utils.Globals;
import Utils.StringUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

//Calculates family relationships (i.e., spouses, parents, children)
public class FamilyRelationshipTest {
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
    public void checkParents() throws Exception {
        clearDatabase();
        loadData();
        login();

        PersonActivity personActivity = PersonActivity.getInstance();
        Person person = getPerson(Globals.getInstance().getLoginResult().personID);
        Person mom = getPerson(person.getMotherID());
        Person dad = getPerson(person.getFatherID());
        List<ListItemData> family = personActivity.getFamily(person, false);

        for (ListItemData item : family) {
            if (item.name.contains(mom.getFirstName() + " " + mom.getLastName())) {
                String relationship = item.name.substring(item.name.indexOf("\n") + 1, item.name.length());
                assertEquals("Mother", relationship);
            }
            if (item.name.contains(dad.getFirstName() + " " + dad.getLastName())) {
                String relationship = item.name.substring(item.name.indexOf("\n") + 1, item.name.length());
                assertEquals("Father", relationship);
            }
        }
    }

    @Test
    public void checkSpouse() throws Exception {
        clearDatabase();
        loadData();
        login();

        PersonActivity personActivity = PersonActivity.getInstance();
        Person person = getPerson(Globals.getInstance().getLoginResult().personID);
        Person mom = getPerson(person.getMotherID());
        Person dad = getPerson(person.getFatherID());
        List<ListItemData> family = personActivity.getFamily(mom, false);

        for (ListItemData item : family) {
            if (item.name.contains(dad.getFirstName() + " " + dad.getLastName())) {
                String relationship = item.name.substring(item.name.indexOf("\n") + 1, item.name.length());
                assertEquals("Spouse", relationship);
                break;
            }
        }

        family = personActivity.getFamily(dad, false);
        for (ListItemData item : family) {
            if (item.name.contains(mom.getFirstName() + " " + mom.getLastName())) {
                String relationship = item.name.substring(item.name.indexOf("\n") + 1, item.name.length());
                assertEquals("Spouse", relationship);
                break;
            }
        }
    }

    @Test
    public void checkChildren() throws Exception {
        clearDatabase();
        loadData();
        login();

        PersonActivity personActivity = PersonActivity.getInstance();
        Person person = getPerson(Globals.getInstance().getLoginResult().personID);
        Person mom = getPerson(person.getMotherID());
        Person dad = getPerson(person.getFatherID());
        List<ListItemData> family = personActivity.getFamily(mom, false);

        for (ListItemData item : family) {
            if (item.name.contains(person.getFirstName() + " " + person.getLastName())) {
                String relationship = item.name.substring(item.name.indexOf("\n") + 1, item.name.length());
                assertEquals("Child", relationship);
                break;
            }
        }

        family = personActivity.getFamily(dad, false);
        for (ListItemData item : family) {
            if (item.name.contains(person.getFirstName() + " " + person.getLastName())) {
                String relationship = item.name.substring(item.name.indexOf("\n") + 1, item.name.length());
                assertEquals("Child", relationship);
                break;
            }
        }
    }



    private void loadData() throws IOException {
        URL url = new URL( "http://localhost:8080/load");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.connect();

        Gson gson = new Gson();
        LoadRequest loadRequest = gson.fromJson(loadData, LoadRequest.class); ;

        connection.connect();

        StringUtil.writeStringToStream(gson.toJson(loadRequest), connection.getOutputStream());
        connection.getOutputStream().close();

        LoadResult loadResult = null;
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String returnJSON = StringUtil.getStringFromInputStream(connection.getInputStream());
            loadResult = gson.fromJson(returnJSON, LoadResult.class);
        }

        assertNotEquals(loadResult, null);
    }

    private void login() throws Exception {
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

        assertNotEquals(loginRequest, null);
        assertEquals(loginResult.username, loginRequest.getUsername());
        Globals.getInstance().setLoginResult(loginResult);

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

        URL eventURL = new URL("http://localhost:8080/event");
        HttpURLConnection eventConnection = (HttpURLConnection) eventURL.openConnection();
        eventConnection.setRequestMethod("GET");
        eventConnection.setDoOutput(false);

        eventConnection.setRequestProperty("Authorization", loginResult.authtoken);

        eventConnection.connect();

        EventListResult eventListResult = null;
        int eventResponseCode = eventConnection.getResponseCode();
        if (eventResponseCode == HttpURLConnection.HTTP_OK) {
            String json = StringUtil.getStringFromInputStream(eventConnection.getInputStream());
            eventListResult = gson.fromJson(json, EventListResult.class);
        }
        assertNotEquals(eventListResult, null);

        EventListResult emptyEventListResult = new EventListResult(new ArrayList<Event>(), true);
        assertNotEquals(eventListResult, emptyEventListResult);
        Globals.getInstance().setEventListResult(eventListResult);
    }

    private Person getPerson(String personID) {
        for (Person person : Globals.getInstance().getPersonListResult().getData()) {
            if (person.getPersonID().equals(personID)) {
                return person;
            }
        }
        return null;
    }

    //putting this at the bottom because it is long
    private String loadData = "{\n" +
            "   \"users\":[\n" +
            "      {\n" +
            "         \"username\":\"sheila\",\n" +
            "         \"password\":\"parker\",\n" +
            "         \"email\":\"sheila@parker.com\",\n" +
            "         \"firstName\":\"Sheila\",\n" +
            "         \"lastName\":\"Parker\",\n" +
            "         \"gender\":\"f\",\n" +
            "         \"personID\":\"Sheila_Parker\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"username\":\"patrick\",\n" +
            "         \"password\":\"spencer\",\n" +
            "         \"email\":\"patrick@spencer.com\",\n" +
            "         \"firstName\":\"Patrick\",\n" +
            "         \"lastName\":\"Spencer\",\n" +
            "         \"gender\":\"m\",\n" +
            "         \"personID\":\"Patrick_Spencer\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"persons\":[\n" +
            "      {\n" +
            "         \"firstName\":\"Sheila\",\n" +
            "         \"lastName\":\"Parker\",\n" +
            "         \"gender\":\"f\",\n" +
            "         \"personID\":\"Sheila_Parker\",\n" +
            "         \"spouseID\":\"Davis_Hyer\",\n" +
            "         \"fatherID\":\"Blaine_McGary\",\n" +
            "         \"motherID\":\"Betty_White\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"Davis\",\n" +
            "         \"lastName\":\"Hyer\",\n" +
            "         \"gender\":\"m\",\n" +
            "         \"personID\":\"Davis_Hyer\",\n" +
            "         \"spouseID\":\"Sheila_Parker\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"Blaine\",\n" +
            "         \"lastName\":\"McGary\",\n" +
            "         \"gender\":\"m\",\n" +
            "         \"personID\":\"Blaine_McGary\",\n" +
            "         \"fatherID\":\"Ken_Rodham\",\n" +
            "         \"motherID\":\"Mrs_Rodham\",\n" +
            "         \"spouseID\":\"Betty_White\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"Betty\",\n" +
            "         \"lastName\":\"White\",\n" +
            "         \"gender\":\"f\",\n" +
            "         \"personID\":\"Betty_White\",\n" +
            "         \"fatherID\":\"Frank_Jones\",\n" +
            "         \"motherID\":\"Mrs_Jones\",\n" +
            "         \"spouseID\":\"Blaine_McGary\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"Ken\",\n" +
            "         \"lastName\":\"Rodham\",\n" +
            "         \"gender\":\"m\",\n" +
            "         \"personID\":\"Ken_Rodham\",\n" +
            "         \"spouseID\":\"Mrs_Rodham\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"Mrs\",\n" +
            "         \"lastName\":\"Rodham\",\n" +
            "         \"gender\":\"f\",\n" +
            "         \"personID\":\"Mrs_Rodham\",\n" +
            "         \"spouseID\":\"Ken_Rodham\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"Frank\",\n" +
            "         \"lastName\":\"Jones\",\n" +
            "         \"gender\":\"m\",\n" +
            "         \"personID\":\"Frank_Jones\",\n" +
            "         \"spouseID\":\"Mrs_Jones\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"Mrs\",\n" +
            "         \"lastName\":\"Jones\",\n" +
            "         \"gender\":\"f\",\n" +
            "         \"personID\":\"Mrs_Jones\",\n" +
            "         \"spouseID\":\"Frank_Jones\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"Patrick\",\n" +
            "         \"lastName\":\"Spencer\",\n" +
            "         \"gender\":\"m\",\n" +
            "         \"personID\":\"Patrick_Spencer\",\n" +
            "         \"associatedUsername\":\"patrick\",\n" +
            "         \"fatherID\":\"Happy_Birthday\",\n" +
            "         \"motherID\":\"Golden_Boy\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"Patrick\",\n" +
            "         \"lastName\":\"Wilson\",\n" +
            "         \"gender\":\"m\",\n" +
            "         \"personID\":\"Happy_Birthday\",\n" +
            "         \"associatedUsername\":\"patrick\",\n" +
            "         \"spouseID\":\"Golden_Boy\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"firstName\":\"Spencer\",\n" +
            "         \"lastName\":\"Seeger\",\n" +
            "         \"gender\":\"f\",\n" +
            "         \"personID\":\"Golden_Boy\",\n" +
            "         \"associatedUsername\":\"patrick\",\n" +
            "         \"spouseID\":\"Happy_Birthday\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"events\":[\n" +
            "      {\n" +
            "         \"eventType\":\"birth\",\n" +
            "         \"personID\":\"Sheila_Parker\",\n" +
            "         \"city\":\"Melbourne\",\n" +
            "         \"country\":\"Australia\",\n" +
            "         \"latitude\":-36.1833,\n" +
            "         \"longitude\":144.9667,\n" +
            "         \"year\":1970,\n" +
            "         \"eventID\":\"Sheila_Birth\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"marriage\",\n" +
            "         \"personID\":\"Sheila_Parker\",\n" +
            "         \"city\":\"Los Angeles\",\n" +
            "         \"country\":\"United States\",\n" +
            "         \"latitude\":34.0500,\n" +
            "         \"longitude\":-117.7500,\n" +
            "         \"year\":2012,\n" +
            "         \"eventID\":\"Sheila_Marriage\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"completed asteroids\",\n" +
            "         \"personID\":\"Sheila_Parker\",\n" +
            "         \"city\":\"Qaanaaq\",\n" +
            "         \"country\":\"Denmark\",\n" +
            "         \"latitude\":77.4667,\n" +
            "         \"longitude\":-68.7667,\n" +
            "         \"year\":2014,\n" +
            "         \"eventID\":\"Sheila_Asteroids\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"COMPLETED ASTEROIDS\",\n" +
            "         \"personID\":\"Sheila_Parker\",\n" +
            "         \"city\":\"Qaanaaq\",\n" +
            "         \"country\":\"Denmark\",\n" +
            "         \"latitude\":74.4667,\n" +
            "         \"longitude\":-60.7667,\n" +
            "         \"year\":2014,\n" +
            "         \"eventID\":\"Other_Asteroids\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"death\",\n" +
            "         \"personID\":\"Sheila_Parker\",\n" +
            "         \"city\":\"Provo\",\n" +
            "         \"country\":\"United States\",\n" +
            "         \"latitude\":40.2444,\n" +
            "         \"longitude\":111.6608,\n" +
            "         \"year\":2015,\n" +
            "         \"eventID\":\"Sheila_Death\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"birth\",\n" +
            "         \"personID\":\"Davis_Hyer\",\n" +
            "         \"city\":\"Hakodate\",\n" +
            "         \"country\":\"Japan\",\n" +
            "         \"latitude\":41.7667,\n" +
            "         \"longitude\":140.7333,\n" +
            "         \"year\":1970,\n" +
            "         \"eventID\":\"Davis_Birth\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"birth\",\n" +
            "         \"personID\":\"Blaine_McGary\",\n" +
            "         \"city\":\"Bratsk\",\n" +
            "         \"country\":\"Russia\",\n" +
            "         \"latitude\":56.1167,\n" +
            "         \"longitude\":101.6000,\n" +
            "         \"year\":1948,\n" +
            "         \"eventID\":\"Blaine_Birth\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"death\",\n" +
            "         \"personID\":\"Betty_White\",\n" +
            "         \"city\":\"Birmingham\",\n" +
            "         \"country\":\"United Kingdom\",\n" +
            "         \"latitude\":52.4833,\n" +
            "         \"longitude\":-0.1000,\n" +
            "         \"year\":2017,\n" +
            "         \"eventID\":\"Betty_Death\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"Graduated from BYU\",\n" +
            "         \"personID\":\"Ken_Rodham\",\n" +
            "         \"country\": \"United States\",\n" +
            "         \"city\": \"Provo\",\n" +
            "         \"latitude\": 40.2338,\n" +
            "         \"longitude\": -111.6585,\n" +
            "         \"year\":1879,\n" +
            "         \"eventID\":\"BYU_graduation\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"marriage\",\n" +
            "         \"personID\":\"Ken_Rodham\",\n" +
            "         \"country\": \"North Korea\",\n" +
            "         \"city\": \"Wonsan\",\n" +
            "         \"latitude\": 39.15,\n" +
            "         \"longitude\": 127.45,\n" +
            "         \"year\":1895,\n" +
            "         \"eventID\":\"Rodham_Marriage\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"Did a backflip\",\n" +
            "         \"personID\":\"Mrs_Rodham\",\n" +
            "         \"country\": \"Mexico\",\n" +
            "         \"city\": \"Mexicali\",\n" +
            "         \"latitude\": 32.6667,\n" +
            "         \"longitude\": -114.5333,\n" +
            "         \"year\":1890,\n" +
            "         \"eventID\":\"Mrs_Rodham_Backflip\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"learned Java\",\n" +
            "         \"personID\":\"Mrs_Rodham\",\n" +
            "         \"country\": \"Algeria\",\n" +
            "         \"city\": \"Algiers\",\n" +
            "         \"latitude\": 36.7667,\n" +
            "         \"longitude\": 3.2167,\n" +
            "         \"year\":1890,\n" +
            "         \"eventID\":\"Mrs_Rodham_Java\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"Caught a frog\",\n" +
            "         \"personID\":\"Frank_Jones\",\n" +
            "         \"country\": \"Bahamas\",\n" +
            "         \"city\": \"Nassau\",\n" +
            "         \"latitude\": 25.0667,\n" +
            "         \"longitude\": -76.6667,\n" +
            "         \"year\":1993,\n" +
            "         \"eventID\":\"Jones_Frog\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"marriage\",\n" +
            "         \"personID\":\"Frank_Jones\",\n" +
            "         \"country\": \"Ghana\",\n" +
            "         \"city\": \"Tamale\",\n" +
            "         \"latitude\": 9.4,\n" +
            "         \"longitude\": 0.85,\n" +
            "         \"year\":1997,\n" +
            "         \"eventID\":\"Jones_Marriage\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"Ate Brazilian Barbecue\",\n" +
            "         \"personID\":\"Mrs_Jones\",\n" +
            "         \"country\": \"Brazil\",\n" +
            "         \"city\": \"Curitiba\",\n" +
            "         \"latitude\": -24.5833,\n" +
            "         \"longitude\": -48.75,\n" +
            "         \"year\":2012,\n" +
            "         \"eventID\":\"Mrs_Jones_Barbecue\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"Learned to Surf\",\n" +
            "         \"personID\":\"Mrs_Jones\",\n" +
            "         \"country\": \"Australia\",\n" +
            "         \"city\": \"Gold Coast\",\n" +
            "         \"latitude\": -27.9833,\n" +
            "         \"longitude\": 153.4,\n" +
            "         \"year\": 2000,\n" +
            "         \"eventID\":\"Mrs_Jones_Surf\",\n" +
            "         \"associatedUsername\":\"sheila\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"birth\",\n" +
            "         \"personID\":\"Patrick_Spencer\",\n" +
            "         \"city\":\"Grise Fiord\",\n" +
            "         \"country\":\"Canada\",\n" +
            "         \"latitude\":76.4167,\n" +
            "         \"longitude\":-81.1,\n" +
            "         \"year\":2016,\n" +
            "         \"eventID\":\"Thanks_Woodfield\",\n" +
            "         \"associatedUsername\":\"patrick\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"marriage\",\n" +
            "         \"personID\":\"Happy_Birthday\",\n" +
            "         \"city\":\"Boise\",\n" +
            "         \"country\":\"United States\",\n" +
            "         \"latitude\":43.6167,\n" +
            "         \"longitude\":-115.8,\n" +
            "         \"year\":2016,\n" +
            "         \"eventID\":\"True_Love\",\n" +
            "         \"associatedUsername\":\"patrick\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"eventType\":\"marriage\",\n" +
            "         \"personID\":\"Golden_Boy\",\n" +
            "         \"city\":\"Boise\",\n" +
            "         \"country\":\"United States\",\n" +
            "         \"latitude\":43.6167,\n" +
            "         \"longitude\":-115.8,\n" +
            "         \"year\":2016,\n" +
            "         \"eventID\":\"Together_Forever\",\n" +
            "         \"associatedUsername\":\"patrick\"\n" +
            "      }\n" +
            "   ]\n" +
            "}\n" +
            "\n";
}
