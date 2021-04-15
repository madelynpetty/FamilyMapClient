package Utils;

import com.example.familymap.ListItemData;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import Models.Event;
import Request.RegisterRequest;
import Result.EventListResult;
import Result.LoginResult;
import Result.PersonListResult;
import Result.RegisterResult;

public class Globals {
    private static Globals instance = null;

    private String serverHost = null;
    private String serverPort = null;
    private LoginResult loginResult = null;
    private EventListResult eventListResult = null;
    private PersonListResult personListResult = null;
    private Event eventForEventActivity = null;
    private Marker activeMarker = null;

    private ArrayList<ListItemData> filteredList = null; //for testing purposes

    public static Globals getInstance() {
        if (instance == null) {
            instance = new Globals();
        }
        return instance;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public LoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public EventListResult getEventListResult() {
        return eventListResult;
    }

    public void setEventListResult(EventListResult eventListResult) {
        this.eventListResult = eventListResult;
    }

    public PersonListResult getPersonListResult() {
        return personListResult;
    }

    public void setPersonListResult(PersonListResult personListResult) {
        this.personListResult = personListResult;
    }

    public Event getEventForEventActivity() {
        return eventForEventActivity;
    }

    public void setEventForEventActivity(Event event) {
        this.eventForEventActivity = event;
    }

    public Marker getActiveMarker() {
        return activeMarker;
    }

    public void setActiveMarker(Marker activeMarker) {
        this.activeMarker = activeMarker;
    }

    public ArrayList<ListItemData> getFilteredList() {
        return filteredList;
    }

    public void setFilteredList(ArrayList<ListItemData> filteredList) {
        this.filteredList = filteredList;
    }
}
