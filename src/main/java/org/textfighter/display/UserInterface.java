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
                if(currentTag.equals("<choices>")) {
                    for(Choice c : TextFighter.player.getLocation().getPossibleChoices()) {
                        c.setOutput(c.getName() + "\n" +
                                    "    desc  - " + c.getDescription() + "\n" +
                                    "    usage - " + c.getUsage());
                        uiInProgress+=c.getOutput()+"\n";
                    }
                }
                ArrayList<UiTag> tags = Display.interfaceTags;
                for(UiTag t : tags) {
                    if(t.getTag().equals(currentTag)) {
                        Object output = t.invokeMethod();
                        if(output instanceof Integer) {
                            uiInProgress+=Integer.parseInt((String)output);
                        } else if (output instanceof String) {
                            uiInProgress+=output;
                        } else if (output instanceof ArrayList) {
                            if(((ArrayList)output).size() > 0 && ((ArrayList)output).get(0) instanceof String) {
                                if(t.getTag().substring(t.getTag().length()-2, t.getTag().length()-1).equals("\\n")){
                                    for(int p=0; p<((ArrayList)output).size(); p++) {
                                        uiInProgress+=((ArrayList)output).get(p);
                                    }
                                } else {
                                    for(int p=0; p<((ArrayList)output).size(); p++) {
                                        uiInProgress+=((ArrayList)output).get(p) + "\n";
                                    }
                                }
                            }
                        }
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

    public UserInterface(String name, String unparsedui) {
        this.name = name;
        this.unparsedui = unparsedui;
    }

}
