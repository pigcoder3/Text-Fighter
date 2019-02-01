package org.textfighter.item;

import org.textfighter.item.Item;
import org.textfighter.CustomVariable;
import org.textfighter.TextFighter;

import java.util.ArrayList;

public class Armor extends Item {

    public static String defaultName = "armorName";
    public static double defaultProtectionAmount = 0.0;
    public static String defaultDescription = "A armor";

    private final String ITEMTYPE = "tool";
    private String name = defaultName;
    private double protectionAmount = defaultProtectionAmount;
    private String description = defaultDescription;

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

    //Basic info stuff
    public String getItemType() { return ITEMTYPE; }
    public String getName() { return name; }
    public void setName(String s) { name=s; }
    public String getDescription() { return description; }
    public void setDescription(String s) { description=s; if(description == null) { description=defaultDescription;} }

    //protectionAmount methods
    public double getProtectionAmount() { return protectionAmount; }
    public void setProtectionAmount(double a) { protectionAmount=a; if(protectionAmount<0.0){protectionAmount=0.0;}}

    //Just get output of the basic stuff
    public String getSimpleOutput(){
        return name + " -\n" +
               "  type:  " + ITEMTYPE + "\n" +
               "  protection amount: " + protectionAmount + "\n";

    }
    //Get the output of all the variables
    public String getOutput() {
        String output = name + " -\n" +
                        "  desc:  " + description + "\n" +
                        "  type:  " + ITEMTYPE + "\n" +
                        "  protection amount:  " + protectionAmount + "%\n";
        //Adds the custom variables to the output
        for(CustomVariable cv : customVariables) {
            if(cv.getInOutput()) { output=output+"  " + cv.getName() + ":  " + cv.getValue().toString() + "\n"; }
        }
        return output;
    }

    public Armor (String name, String description, double protectionAmount, ArrayList<CustomVariable> customVariables) {
        super(name, description);
        this.name = name;
        this.description = description;
        this.protectionAmount = protectionAmount;
        this.customVariables = customVariables;
    }

}
