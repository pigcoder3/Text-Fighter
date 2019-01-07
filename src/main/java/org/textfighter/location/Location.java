package org.textfighter.location;

import org.textfighter.location.*;
import org.textfighter.*;
import org.textfighter.display.*;

import java.util.ArrayList;

public class Location {

    private String name;

    private ArrayList<UserInterface> interfaces = new ArrayList<UserInterface>();

    private ArrayList<Choice> allChoices = new ArrayList<Choice>();
    private ArrayList<Choice> possibleChoices = new ArrayList<Choice>();

    private ArrayList<Premethod> allPremethods = new ArrayList<Premethod>();
    private ArrayList<Premethod> possiblePremethods = new ArrayList<Premethod>();

    private ArrayList<Postmethod> allPostmethods = new ArrayList<Postmethod>();
    private ArrayList<Postmethod> possiblePostmethods = new ArrayList<Postmethod>();

    public String getName() { return name; }

    public ArrayList<UserInterface> getInterfaces() { return interfaces; }

    public ArrayList<Choice> getAllChoices() { return allChoices; }
    public ArrayList<Choice> getPossibleChoices() { return possibleChoices; }

    public ArrayList<Premethod> getPremethods() { return possiblePremethods; }
    public ArrayList<Postmethod> getPostmethods() { return possiblePostmethods; }

    public void invokePremethods() {
        filterPremethods();
        for(Premethod pm : possiblePremethods) {
            pm.invokeMethod();
        }
    }

    public void filterPremethods() {
        possiblePremethods.clear();
        for(Premethod pm : allPremethods) {
            boolean valid = true;
            for(Requirement r : pm.getRequirements()) {
                if(!r.invokeMethod()) {
                    valid = false;
                    break;
                }
            }
            if(valid) {
                possiblePremethods.add(pm);
            }
        }
    }

    public void invokePostmethods() {
        filterPostmethods();
        for(Postmethod pm : possiblePostmethods) {
            pm.invokeMethod();
        }
    }

    public void filterPostmethods() {
        possiblePostmethods.clear();
        for(Postmethod pm : allPostmethods) {
            boolean valid = true;
            for(Requirement r : pm.getRequirements()) {
                if(!r.invokeMethod()) {
                    valid = false;
                    break;
                }
            }
            if(valid) {
                possiblePostmethods.add(pm);
            }
        }
    }


    public void filterPossibleChoices() {
        possibleChoices.clear();
        for(Choice c : allChoices) {
            boolean valid = true;
            for(Requirement r : c.getRequirements()) {
                if(!r.invokeMethod()) {
                    valid = false;
                    break;
                }
            }
            if(valid) {
                possibleChoices.add(c);
            }
        }
    }

    public Location(String name, ArrayList<UserInterface> interfaces, ArrayList<Choice> choices, ArrayList<Premethod> premethods, ArrayList<Postmethod> postmethods) {

        //Sets all variables
        this.name = name;
        this.interfaces = interfaces;
        this.allPremethods = premethods;
        this.allPostmethods = postmethods;

        //Filters out any invalid choices
        for(int i=0; i<choices.size(); i++) {
            if(choices.get(i).getValid()) { allChoices.add(choices.get(i)); }
        }
    }
}
