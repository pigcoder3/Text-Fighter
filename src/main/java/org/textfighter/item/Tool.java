package org.textfighter.item;

import org.textfighter.item.Item;
import org.textfighter.CustomVariable;
import org.textfighter.TextFighter;

import java.util.ArrayList;

public class Tool extends Item {

    public static String defaultName = "toolName";
    public static String defaultDescription = "A tool";
    public static int defaultDurability = 100;

    private final String ITEMTYPE = "tool";
    private String name = defaultName;
    private String description = defaultDescription;
    private int durability = defaultDurability;

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

    //Basic infor methods
    public String getItemType() { return ITEMTYPE; }
    public String getName() { return name; }
    public void setName(String s) { name=s; if(name == null) { name = defaultName; }}
    public String getDescription() { return description; }
    public void setDescription(String s) { description=s; if(description == null) { description=defaultDescription;} }

    //durability methods
    public int getDurability() { return durability; }
    public void setDurability(int a) { durability=a; if(durability < 1) { broken(); } TextFighter.needsSaving=true; }
    public void increaseDurability(int a) { durability=+a; if(durability < 1) { broken(); } TextFighter.needsSaving=true; }
    public void decreaseDurability(int a) { durability=-a; if(durability < 1) { broken(); } TextFighter.needsSaving=true; }

    //Just get the output of the type and name
    public String getSimpleOutput(){
        return name + " -\n" +
               "  type:  " + ITEMTYPE + "\n";

    }
    //Get the output of all custom variables
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

    //If the tool breaks, then remove it from the player's inventory
    public void broken() {
        TextFighter.player.removeFromInventory(name, ITEMTYPE);
        TextFighter.addToOutput("Your " + name + " has broken!");
        TextFighter.needsSaving=true;
    }

    public Tool (String name, String description, ArrayList<CustomVariable> customVariables, int durability) {
        super(name, description);
        this.name = name;
        this.description = description;
        this.customVariables = customVariables;
        if(durability < 1) { broken(); }
        this.durability = durability;
    }

}
