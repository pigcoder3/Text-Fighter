package org.textfighter.item;

import org.textfighter.item.Item;
import org.textfighter.CustomVariable;

import java.util.ArrayList;

public class Tool extends Item {

    public static String defaultName = "toolName";
    public static String defaultDescription = "A tool";

    private final String ITEMTYPE = "tool";
    private String name = defaultName;
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
    public void setName(String s) { name=s; if(name == null) { name = defaultName; }}
    public String getDescription() { return description; }
    public void setDescription(String s) { description=s; if(description == null) { description=defaultDescription;} }

    public Tool (String name, String description, ArrayList<CustomVariable> customVariables) {
        super(name, description);
        this.name = name;
        this.description = description;
        this.customVariables = customVariables;
    }

}
