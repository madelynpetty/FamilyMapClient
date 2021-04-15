package Utils;

import java.util.ArrayList;
import Models.Event;
import Models.Person;

public class Filter {
    public ArrayList<Event> filterEvents() {
        ArrayList<Person> momsSide = getMomsSide(getPerson(Globals.getInstance().getLoginResult().personID));
        ArrayList<Person> dadsSide = getDadsSide(getPerson(Globals.getInstance().getLoginResult().personID));
        ArrayList<Event> events = new ArrayList<>();

        for (Event event : Globals.getInstance().getEventListResult().getData()) {
            Person person = getPerson(event.getPersonID());
            if (!Settings.getInstance().maleEvents && person.getGender().equals("m")) continue;
            if (!Settings.getInstance().femaleEvents && person.getGender().equals("f")) continue;
            if (!Settings.getInstance().mothersSide && momsSide.contains(person)) continue;
            if (!Settings.getInstance().fathersSide && dadsSide.contains(person)) continue;

            events.add(event);
        }
        return events;
    }

    private Person getPerson(String personID) {
        for (Person person : Globals.getInstance().getPersonListResult().getData()) {
            if (person.getPersonID().equals(personID)) {
                return person;
            }
        }
        return null;
    }

    public ArrayList<Person> getMomsSide(Person person) {
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

    public ArrayList<Person> getDadsSide(Person person) {
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
