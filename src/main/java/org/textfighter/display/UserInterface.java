package org.textfighter.display;

import org.textfighter.TextFighter;
import org.textfighter.location.Choice;

import java.util.ArrayList;

public class UserInterface {

    private String name;

    private String unparsedui;
    private String ui;

    public String getName() { return name; }
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
                boolean tagUsed = false;
                //Matches the tag string with a tag in the interfaceTags
                for(UiTag t : Display.interfaceTags) {
                    if(t.getTag().equals(currentTag)) {
                        Object output = t.invokeMethod();
                        if(output instanceof Integer) { //If it returns an integer
                            uiInProgress+=output.toString();
                        } else if (output instanceof String) { //If it returns a string
                            uiInProgress+=output;
                        } else if (output instanceof ArrayList) { //If it returns an ArrayList of Strings
                            if(((ArrayList)output).size() > 0 && ((ArrayList)output).get(0) instanceof String) {
                                for(int p=0; p<((ArrayList)output).size(); p++) {
                                    uiInProgress+=((ArrayList)output).get(p);
                                    if(p != ((ArrayList)output).size()-1) {uiInProgress += "\n"; }
                                }
                            }
                        }
                        tagUsed = true;
                    }
                }
                if(!tagUsed) { uiInProgress+=currentTag; }
                tagUsed=false;
                inTag=false;
            } else if (inTag){
                currentTag+=unparsedui.charAt(i);
            } else {
                uiInProgress+=unparsedui.charAt(i);
            }
        }
        ui = uiInProgress;
    }

    public UserInterface(String name, String unparsedui) {
        this.name = name;
        this.unparsedui = unparsedui;
    }

}
