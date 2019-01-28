package org.textfighter.item;

import org.textfighter.item.Item;
import org.textfighter.CustomVariable;

import java.util.ArrayList;

public class Armor extends Item {

    public static String defaultName = "armorName";
    public static double defaultProtectionAmount = 0.0;
    public static String defaultDescription = "A armor";

    private final String ITEMTYPE = "tool";
    private String name = defaultName;
    private double protectionAmount = defaultProtectionAmount;
    protected String description = defaultDescription;

    private ArrayList<CustomVariable> customVariables = new ArrayList<CustomVariable>();

    public Object getCustomVariableFromName(String name) {
        for(CustomVariable cv : customVariables) {
            if(cv.getName().equals(name)) { return cv.getValue(); }
        }
        return null;
    }

    public String getItemType() { return ITEMTYPE; }
    public String getName() { return name; }
    public void setName(String s) { name=s; }
    public double getProtectionAmount() { return protectionAmount; }
    public void setProtectionAmount(double a) { protectionAmount=a; if(protectionAmount<0.0){protectionAmount=0.0;}}
    public String getDescription() { return description; }
    public void setDescription(String s) { description=s; if(description == null) { description=defaultDescription;} }

    public Armor (String name, String description, double protectionAmount, ArrayList<CustomVariable> customVariables) {
        super(name, description);
        this.name = name;
        this.description = description;
        this.protectionAmount = protectionAmount;
        this.customVariables = customVariables;
    }

}
