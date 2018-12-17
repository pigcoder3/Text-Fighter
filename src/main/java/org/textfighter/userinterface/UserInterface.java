package org.textfighter.userinterface;

import org.textfighter.userinterface.Choice;
import org.textfighter.TextFighter;
import org.textfighter.userinterface.UiTag;

import java.util.ArrayList;

public class UserInterface {

    private String unParsedUI;
    private String parsedUI;

    private String name;

    //All choices possible for this interface
    private ArrayList<Choice> allChoices = new ArrayList<Choice>();
    //All the choices the user can currently choose
    private ArrayList<Choice> possibleChoices = new ArrayList<Choice>();

    public UserInterface(String name, String UI, ArrayList<Choice> choices) {
        this.name = name;
        this.unParsedUI = UI;
        this.allChoices = choices;
    }

    public String getParsedUI() { return parsedUI; }

    public void parseInterface() {
        String UI = "";
        String currentTag = "";
        int beginningIndex = 0;
        boolean inTag = false;
        for(int i=0; i<unParsedUI.length(); i++) {
            if(unParsedUI.charAt(i) == '<') {
                currentTag="<";
                inTag = true;
            } else if (unParsedUI.charAt(i) == '>' & inTag) {
                currentTag+=">";
                ArrayList<UiTag> tags = TextFighter.getInterfaceTags();
                for(UiTag t : tags) {
                    if(t.equals(currentTag)) {
                        UI+=t.invokeMethod();
                    }
                }
                inTag=false;
            } else if (inTag){
                currentTag+=unParsedUI.charAt(i);
            } else {
                UI+=unParsedUI.charAt(i);
            }
        }
        parsedUI = UI;
    }

    public ArrayList<Choice> getPossibleChoices() { return possibleChoices; }

    public void setPossibleChoices() {
        for(Choice c : allChoices) {

        }
    }

}
