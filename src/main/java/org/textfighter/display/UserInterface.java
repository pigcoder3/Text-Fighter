package org.textfighter.display;

import org.textfighter.TextFighter;
import org.textfighter.location.Choice;
import org.textfighter.method.Requirement;

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
            //If there is a beginning of a tag (Starts recording to see if it is one)
            if(unparsedui.charAt(i) == '<') {
                currentTag="<";
                inTag = true;
            //If there is an end of a tag (Still needs to find out if it is one)
            } else if (unparsedui.charAt(i) == '>' & inTag) {
                currentTag+=">";
                boolean tagUsed = false;
                //Matches the tag string with a tag in the interfaceTags
                for(UiTag t : Display.interfaceTags) {
                    if(t.getTag().equals(currentTag)) {
                        //Make sure the uitag's requirements are met.
                        //If not, then just remove the tag from the string and dont put anything in
                        if(t.getRequirements() != null) {
                            boolean valid = true;
                            for(Requirement r : t.getRequirements()) {
                                if(!r.invokeMethod()) {
                                    valid = false;
                                    break;
                                }
                            }
                            if(!valid) { tagUsed = true; continue;}
                        }
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
                //If it is not used or doesnt exist, just add the tag back to the interface
                if(!tagUsed) { uiInProgress+=currentTag; }
                tagUsed=false;
                inTag=false;
            } else if (inTag){
                //Continue recording the tag
                currentTag+=unparsedui.charAt(i);
            } else {
                //Just put the character to the parsed array
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
