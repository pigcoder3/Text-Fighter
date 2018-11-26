package org.textfighter;

public class UserInterface {

    private String unParsedUI;
    private String parsedUI;

    private String name;

    private String[] choices;

    public UserInterface(String name, String UI) {
        this.name = name;
        this.unParsedUI = UI;
    }

    public String getParsedUI() { return parsedUI; }

    public void parseInterface() {
        //This will add all things in the interfaces (such as money amounts, etc.)
        //<tag> is used to indicate a place to be parsed
        String UI = parsedUI;
        parsedUI = UI;

    }

}
