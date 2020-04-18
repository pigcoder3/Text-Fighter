package com.seanjohnson.textfighter.item;

import com.seanjohnson.textfighter.item.Item;
import com.seanjohnson.textfighter.CustomVariable;
import com.seanjohnson.textfighter.TextFighter;

import java.util.ArrayList;

public class SpecialItem extends Item {

    /**
     * Stores the default description for special items.
     * <p>Set to "A special item".</p>
     */
    public static String defaultDescription = "A special item";

    /**
     * Stores the item type for tools. Cannot be changed.
     * <p>Set to "tool".</p>
     */
    private final String ITEMTYPE = "specialitem";
    /**
     * Stores the name of this special item.
     * <p>Set to null.</p>
     */
    private String name = "";
    /**
     * Stores the description of this special item.
     * <p>Set to {@link #defaultDescription}.</p>
     */
    protected String description = defaultDescription;

    /**
     * Stores the custom variables for this special item.
     * <p>Set to an empty ArrayList of CustomVariables.</p>
     */
    private ArrayList<CustomVariable> customVariables = new ArrayList<CustomVariable>();
    /**
     * Returns the {@link #customVariables}.
     * @return      {@link #customVariables}
     */
    public ArrayList<CustomVariable> getCustomVariables() { return customVariables; }

    /**
     * Sets the {@link #customVariables}.
     * @param cv    The new arraylist
     */
    public void setCustomVariables(ArrayList<CustomVariable> cv) {
        if(cv == null) { throw new IllegalArgumentException("new ArrayList cannot be null"); }
        customVariables = cv;
    }


    /**
     * Returns the value of the custom variable in {@link #customVariables} with the name given.
     * <p>If name is null, or no variable with that name is found, return null.</p>
     * @param name  The name of the custom variable.
     * @return      The value of the customvariable with the name given. If no name given or one not found, return null.
     */
    public Object getCustomVariableFromName(String name) {
        if(name == null) { return null; }
        for(CustomVariable cv : customVariables) {
            if(cv.getName().equals(name)) { return cv.getValue(); }
        }
        return null;
    }
    /**
     * Sets the value of the variable in {@link #customVariables} with the name given.
     * <p>If no name given, then dont do anything.</p>
     * @param name      The name of the custom variable.
     * @param value     The value that the custom variable will be set to.
     */
    public void setCustomVariableByName(String name, Object value) {
        if(name == null) { return; }
        for(CustomVariable cv : customVariables) {
            if(cv.getName().equals(name)) {
                cv.setValue(value);
                return;
            }
        }
    }

    //Basic info methods
    /**
     * Returns the {@link #ITEMTYPE}.
     * @return       {@link #ITEMTYPE}
     */
    public String getItemType() { return ITEMTYPE; }
    /**
     * Returns the {@link #ITEMTYPE}.
     * @return       {@link #ITEMTYPE}
     */
    public String getName() { return name; }
    /**
     * Sets the value of {@link #name}.
     * <p>If the name given is null, then dont do anything.</p>
     * @param s     The new value.
     */
    public void setName(String s) { if(s!=null && s.trim() != null) {name=s;} }
    /**
     * Returns the {@link #description}.
     * @return       {@link #description}
     */
    public String getDescription() { return description; }
    /**
     * Sets the value of {@link #description}.
     * <p>If the value is null, then set the description to the {@link #defaultDescription}.</p>
     * @param s     The new value.
     */
    public void setDescription(String s) { description=s; if(description == null) { description=defaultDescription;} }

    //Get the output of just the type and durability
    /**
     * Returns the {@link #name}, {@link #ITEMTYPE}.
     * @return      {@link #name}, {@link #ITEMTYPE}
     */
    public String getSimpleOutput(){
        return name + " -\n" +
               "  type:  " + ITEMTYPE + "\n";
    }
    //Get the output of all the variables
    /**
     * Returns the {@link #name}, {@link #description}, {@link #ITEMTYPE}, and any {@link #customVariables} with inOutput to true.
     * @return       {@link #name}, {@link #description}, {@link #ITEMTYPE}, and any {@link #customVariables} with inOutput to true.
     */
    public String getOutput() {
        String output = name + " -\n" +
                        "  description:  " + description + "\n" +
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
