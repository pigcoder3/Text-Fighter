package org.textfighter.item;

import org.textfighter.item.Item;
import org.textfighter.CustomVariable;
import org.textfighter.TextFighter;

import java.util.ArrayList;

public class SpecialItem extends Item {

    public static String defaultName = "specialItemName";
    public static String defaultDescription = "A special item";

    private final String ITEMTYPE = "tool";
    private String name = defaultName;
    protected String description = defaultDescription;

    //Custom variable stuff
    private ArrayList<CustomVariable> customVariables = new ArrayList<CustomVariable>();

    public Object getCustomVariableFromName(String name) {
        if(name == null) { return null; }
        for(CustomVariable cv : customVariables) {
            if(cv.getName().equals(name)) { return cv.getValue(); }
        }
        return null;
    }
    public void setCustomVariableByName(String name, Object value) {
        if(name == null) { return; }
        for(CustomVariable cv : customVariables) {
            if(cv.getName().equals(name)) { cv.setValue(value); return;}
        }
    }

    //Basic info methods
    public String getItemType() { return ITEMTYPE; }
    public String getName() { return name; }
    public void setName(String s) { name=s; if(name == null) { name=defaultName; }}
    public String getDescription() { return description; }
    public void setDescription(String s) { description=s; if(description == null) { description=defaultDescription;} }

    //Get the output of just the type and durability
    public String getSimpleOutput(){
        return name + " -\n" +
               "  type:  " + ITEMTYPE + "\n";
    }
    //Get the output of all the variables
    public String getOutput() {
        String output = name + " -\n" +
                        "  desc:  " + description + "\n" +
                        "  type:  " + ITEMTYPE + "\n";
        //Adds the custom variables to the output
        for(CustomVariable cv : customVariables) {
            if(cv.getInOutput()) { output=output+"  " + cv.getName() + ":  " + cv.getValue().toString() + "\n"; }
        }
        return output;
    }

    public SpecialItem(String name, String description, ArrayList<CustomVariable> customVariables) {
        super(name, description);
        this.name = name;
        this.description = description;
        this.customVariables = customVariables;
    }
}
