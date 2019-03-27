package org.textfighter.display;

import org.textfighter.TextFighter;
import org.textfighter.location.Choice;
import org.textfighter.method.Requirement;

import java.util.ArrayList;

public class UserInterface {

    /***The name of the interface*/
    private String name;

    /***The raw interface that contains tags*/
    private String unparsedui;
    /***The parsed ui*/
    private String ui;

    /**
     * Returns the {@link #name}.
     * @return      {@link #name}
     */
    public String getName() { return name; }
    /**
     * Returns the {@link #ui}.
     * @return      {@link #ui}
     */
    public String getParsedUI() { return ui; }

    /*** Parses interface tags and replaces each tag with the output of that tag's method. Then sets the {@link #ui} to the parsed ui.*/
    public void parseInterface() {
        String uiInProgress = "";
        String currentTag = "";
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
                        if (output instanceof String) { //If it returns a string
                            uiInProgress+=output;
                        } else if(output instanceof Integer) { //If it returns an integer
                            uiInProgress+=output.toString();
                        } else if(output instanceof Double) { //If it returns a double
                            uiInProgress+=output.toString();
                        } else if(output instanceof Boolean) { //If it returns an boolean
                            uiInProgress+=output.toString();
                        } else if (output instanceof Class) { //If it returns a class
                            uiInProgress+=output.toString();
                        } else if (output instanceof ArrayList) { //If it returns an ArrayList of Strings
                            if(((ArrayList)output).size() > 0 && ((ArrayList)output).get(0) instanceof String) {
                                for(int p=0; p<((ArrayList)output).size(); p++) {
                                    uiInProgress+=((ArrayList)output).get(p);
                                    if(p != ((ArrayList)output).size()-1) {uiInProgress += "\n"; }
                                }
                            }
                        }
                        tagUsed = true;
                        break;
                    }
                }
                //If it is not used or doesnt exist, just add the tag back to the interface
                if(!tagUsed) { uiInProgress+=currentTag; }
                inTag=false;
            } else if (inTag){
                //Continue recording the tag
                currentTag+=unparsedui.charAt(i);
                //If still recording a tag and the unparseui has ended, just add the tag in progress to the parsed ui because there is no actual tag there.
                if(i == unparsedui.length()-1) { uiInProgress+=currentTag; }
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
