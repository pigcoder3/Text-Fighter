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
        this.allChoices = choices;

        for(int i=0; i<allChoices.size(); i++) {
            for(ChoiceMethod m : allChoices.get(i).getMethods()) {
                if(m != null && m.getMethod() != null && m.getMethod().getParameterTypes() != null) {
                    if(m.getMethod().getParameterTypes() != null) {
                        for(Class c : m.getMethod().getParameterTypes()) {
                            if(c != int.class && c != String.class && c != boolean.class) {
                                allChoices.remove(i);
                                Display.displayPackError("The choice '" + allChoices.get(i).getName() + "' in location '" + name + "' had a method that took arguments other than String and int. Omitting choice...");
                            }
                        }
                    }
                }
            }
        }

        //Filters through preMethods that are not valid
        for(int i=0; i<premethods.size(); i++) {
            if(!premethods.get(i).getValid()) {
                allPreMethods.add(premethods.get(i));
            }
        }

        for(int i=0; i<this.allPreMethods.size(); i++) {
            for(Class c : premethods.get(i).getMethod().getParameterTypes()) {
                if(c != int.class && c != String.class) {
                    premethods.remove(i);
                    Display.displayPackError("A premethod in location '" + name + "' took arguments other than String and int. Omitting method...");
                }
            }
        }
    }
}
