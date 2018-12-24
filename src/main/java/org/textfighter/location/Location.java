package org.textfighter.location;

import org.textfighter.location.*;
import org.textfighter.*;
import org.textfighter.display.UserInterface;

import java.util.ArrayList;

public class Location {

    private String name;

    private ArrayList<UserInterface> interfaces = new ArrayList<UserInterface>();

    private ArrayList<Choice> allChoices = new ArrayList<Choice>();
    private ArrayList<Choice> possibleChoices = new ArrayList<Choice>();

    public String getName() { return name; }

    public ArrayList<UserInterface> getInterfaces() { return interfaces; }

    public ArrayList<Choice> getAllChoices() { return allChoices; }
    public ArrayList<Choice> getPossibleChoices() { return possibleChoices; }

    public void filterPossibleChoices() {
        possibleChoices.clear();
        for(Choice c : allChoices) {
            boolean valid = true;
            for(Requirement r : c.getRequirements()) {
                if(!r.invokeMethod()) {
                    valid=false;
                    break;
                }
            }
            if(valid) {possibleChoices.add(c);}
        }
    }

    public Location(String name, ArrayList<UserInterface> interfaces, ArrayList<Choice> choices) {
        this.name = name;
        this.interfaces = interfaces;
        this.allChoices = choices;
        for(int i=0; i<allChoices.size(); i++) {
            for(ChoiceMethod m : allChoices.get(i).getMethods()) {
                for(Class c : m.getMethod().getParameterTypes()) {
                    if(c != int.class && c != String.class) {
                        allChoices.remove(i);
                    }
                }
            }
        }
    }
}
