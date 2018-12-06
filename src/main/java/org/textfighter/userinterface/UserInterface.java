package org.textfighter.userinterface;

import org.textfighter.userinterface.Choice;

import java.util.ArrayList;

public class UserInterface {

    private String unParsedUI;
    private String parsedUI;

    private String name;

    //All choices possible for this interface
    private ArrayList<Choice> allChoices = new ArrayList<Choice>();
    //All the choices the user can currently choose
    private ArrayList<Choice> possibleChoices = new ArrayList<Choice>();

    public UserInterface(String name, String UI, ArrayList choices) {
        this.name = name;
        this.unParsedUI = UI;
        this.allChoices = choices;
    }

    public String getParsedUI() { return parsedUI; }

    public void parseInterface() {
        String UI = parsedUI;
        parsedUI = UI;
    }

    public ArrayList<Choice> getPossibleChoices() { return possibleChoices; }

    public void setPossibleChoices() {
        for(Choice c : allChoices) {
                  
        }
    }

}
