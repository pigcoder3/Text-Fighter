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

    private ArrayList<Premethod> allPreMethods = new ArrayList<Premethod>();
    private ArrayList<Premethod> possiblePremethods = new ArrayList<Premethod>();

    public String getName() { return name; }

    public ArrayList<UserInterface> getInterfaces() { return interfaces; }

    public ArrayList<Choice> getAllChoices() { return allChoices; }
    public ArrayList<Choice> getPossibleChoices() { return possibleChoices; }

    public ArrayList<Premethod> getPremethods() { return possiblePremethods; }

    public void invokePremethods() {
        filterPremethods();
        for(Premethod pm : possiblePremethods) {
            pm.invokeMethod();
        }
    }

    public void filterPremethods() {
        possiblePremethods.clear();
        for(Premethod pm : allPreMethods) {
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

    public Location(String name, ArrayList<UserInterface> interfaces, ArrayList<Choice> choices, ArrayList<Premethod> premethods) {
        this.name = name;
        this.interfaces = interfaces;

        for(int i=0; i<choices.size(); i++) {
            if(!choices.get(i).getValid()) {
                Display.displayPackError("Location '" + name + "' had an invalid choice. Omitting choice...");
            } else { allChoices.add(choices.get(i)); }
        }
    }
}
