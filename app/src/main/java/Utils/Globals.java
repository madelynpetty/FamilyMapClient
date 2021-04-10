package Utils;

import android.app.Application;

import Models.Event;
import Result.EventListResult;
import Result.LoginResult;
import Result.PersonListResult;

public class Globals {
    private static Globals instance = null;

    private String serverHost = null;
    private String serverPort = null;
    private LoginResult loginResult = null;
    private EventListResult eventListResult = null;
    private PersonListResult personListResult = null;
    private Event eventForEventActivity = null;

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

}
