package org.textfighter.location;

import org.textfighter.location.*;
import org.textfighter.*;
import org.textfighter.display.*;
import org.textfighter.method.*;

import java.util.ArrayList;

public class Location {

    private String name;

    private ArrayList<UserInterface> interfaces = new ArrayList<UserInterface>();

    private ArrayList<Choice> allChoices = new ArrayList<Choice>();
    private ArrayList<Choice> possibleChoices = new ArrayList<Choice>();

    private ArrayList<TFMethod> allPremethods = new ArrayList<TFMethod>();
    private ArrayList<TFMethod> possiblePremethods = new ArrayList<TFMethod>();

    private ArrayList<TFMethod> allPostmethods = new ArrayList<TFMethod>();
    private ArrayList<TFMethod> possiblePostmethods = new ArrayList<TFMethod>();

    public String getName() { return name; }

    public ArrayList<UserInterface> getInterfaces() { return interfaces; }

    public ArrayList<Choice> getAllChoices() { return allChoices; }
    public ArrayList<Choice> getPossibleChoices() { return possibleChoices; }

    public ArrayList<TFMethod> getPremethods() { return possiblePremethods; }
    public ArrayList<TFMethod> getPostmethods() { return possiblePostmethods; }

    //premethod methods
    public void invokePremethods() {
        filterPremethods();
        for(TFMethod pm : possiblePremethods) {
            pm.invokeMethod();
        }
    }
    public void filterPremethods() {
        //Filter through all premethods that meet their requirements
        possiblePremethods.clear();
        if(allPremethods == null) { return; }
        for(TFMethod pm : allPremethods) {
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

    //postmethod methods
    public void invokePostmethods() {
        filterPostmethods();
        for(TFMethod pm : possiblePostmethods) {
            pm.invokeMethod();
        }
    }
    public void filterPostmethods() {
        //Filter through all postmethod that meet their requirements
        possiblePostmethods.clear();
        if(allPostmethods == null) { return; }
        for(TFMethod pm : allPostmethods) {
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

    //Filter out all choices that do not meet the requirements
    public void filterPossibleChoices() {
        if(allChoices == null) { return; }
        possibleChoices.clear();
        for(Choice c : allChoices) {
			boolean valid = true;
            if(c.getRequirements() != null){
                for(Requirement r : c.getRequirements()) {
                    if(!r.invokeMethod()) {
                        valid = false;
                        break;
                    }
                }
            }
            if(valid) {
                possibleChoices.add(c);
            }
        }
    }

    public Location(String name, ArrayList<UserInterface> interfaces, ArrayList<Choice> choices, ArrayList<TFMethod> premethods, ArrayList<TFMethod> postmethods) {

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
