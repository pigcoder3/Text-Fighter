package org.textfighter.location;

import org.textfighter.location.*;
import org.textfighter.TextFighter;

import java.util.ArrayList;

public class Location {

    private String name;

    private String unparsedui;
    private String ui;

    private ArrayList<Choice> allChoices = new ArrayList<Choice>();
    private ArrayList<Choice> possibleChoices = new ArrayList<Choice>();

    public String getName() { return name; }
    public String getUnparsedui() { return unparsedui; }
    public String getui() { return ui; }
    public ArrayList<Choice> getAllChoices() { return allChoices; }
    public ArrayList<Choice> getPossibleChoices() { return possibleChoices; }
    public String getParsedUI() { return ui; }

    public void parseInterface() {
        String uiInProgress = "";
        String currentTag = "";
        int beginningIndex = 0;
        boolean inTag = false;
        for(int i=0; i<unparsedui.length(); i++) {
            if(unparsedui.charAt(i) == '<') {
                currentTag="<";
                inTag = true;
            } else if (unparsedui.charAt(i) == '>' & inTag) {
                currentTag+=">";
                ArrayList<UiTag> tags = TextFighter.getInterfaceTags();
                for(UiTag t : tags) {
                    if(t.equals(currentTag)) {
                        uiInProgress+=t.invokeMethod();
                    }
                }
                inTag=false;
            } else if (inTag){
                currentTag+=unparsedui.charAt(i);
            } else {
                uiInProgress+=unparsedui.charAt(i);
            }
        }
        ui = uiInProgress;
    }

    public void filterPossibleChoices() {
        possibleChoices.clear();
        for(Choice c : allChoices) {
            boolean valid = true;
            for(ChoiceRequirement r : c.getRequirements()) {
                if(!r.invokeRequirement()) {
                    valid=false;
                    break;
                }
            }
            if(valid) {possibleChoices.add(c);}
        }
    }

    public Location(String name, String ui, ArrayList<Choice> choices) {
        this.name = name;
        this.ui = ui;
        this.allChoices = choices;
    }
}
