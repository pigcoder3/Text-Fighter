package com.seanjohnson.textfighter.item;

import com.seanjohnson.textfighter.item.Item;
import com.seanjohnson.textfighter.CustomVariable;
import com.seanjohnson.textfighter.TextFighter;

import java.util.ArrayList;

public class Tool extends Item {

    /**
     * Stores the default description for tools.
     * <p>Set to "A tool".</p>
     */
    public static String defaultDescription = "A tool";
    /**
     * Stores the default durability(Number of uses) for tools.
     * <p>Set to 100 (100 uses).</p>
     */
    public static int defaultDurability = 100;
    /**
     * Stores the default boolean for unbreakable for tools
     * <p>Set to false.</p>
     */
    public static boolean defaultUnbreakable = false;
    /**
     * Stores the default maximum durability for tool.
     * <p>Set to 100.</p>
     */
    public static int defaultMaxDurability = 100;

    /**
     * Stores the item type for tools. Cannot be changed.
     * <p>Set to "tool".</p>
     */
    private final String ITEMTYPE = "tool";
    /**
     * Stores the name of this tool.
     * <p>Set to null.</p>
     */
    private String name = "";
    /**
     * Stores the description of this tool.
     * <p>Set to {@link #defaultDescription}.</p>
     */
    private String description = defaultDescription;
    /**
     * Stores the durability(number of uses) of this tool.
     * <p>Set to {@link #defaultDurability}.</p>
     */
    private int durability = defaultDurability;
    /**
     * Stores whether or not this tool is unbreakable
     * <p>Set to {@link #defaultUnbreakable}.</p>
     */
    public boolean unbreakable = defaultUnbreakable;
    /**
     * Stores the maximum durability for this tool
     * <p>Set to {@link #defaultMaxDurability}.</p>
     */
    private int maxDurability = defaultMaxDurability;

    /**
     * Stores the custom variables for this tool.
     * <p>Set to an empty ArrayList of CustomVariables.</p>
     */
    private ArrayList<CustomVariable> customVariables = new ArrayList<CustomVariable>();

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

    //Basic infor methods
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
     * Sets the value of {@link #description}.
     * @return      {@link #description}
     */
    public String getDescription() { return description; }
    /**
     * Sets the value of {@link #description}.
     * <p>If the value is null, then set the description to the {@link #defaultDescription}.</p>
     * @param s     The new value.
     */
    public void setDescription(String s) { description=s; if(description == null) { description=defaultDescription;} }

    //durability methods
    /**
     * Sets the value of {@link #durability}.
     * <p>If the new value is less than 1, then the weapon breaks.</p>
     * @param a     The new value.
     */
    public void setDurability(int a) { if(unbreakable) { return; } durability=a; if(durability < 1) { durability = 0; broken(); } if(durability > maxDurability) { durability = maxDurability; }  }
    /**
     * Gets the value of {@link #durability}.
     * @return      The value.
     */
    public int getDurability() { return durability; }

    /**
     * Increases the value of {@link #durability}.
     * <p>If the weapon is {@link #unbreakable} then nothing happens. If the new value is less than 1, then it breaks.</p>
     * @param a     The amount increased.
     */
    public void increaseDurability(int a) { if(unbreakable) { return; } durability+=a; if(durability < 1) { durability = 0; broken(); } if(durability > maxDurability) { durability = maxDurability; }  }
    /**
     * Decrease the value of {@link #durability}.
     * <p>If the weapon is {@link #unbreakable} then nothing happens. If the new value is less than 1, then it breaks.</p>
     * @param a     The amount decreased.
     */
    public void decreaseDurability(int a) { if(unbreakable) { return; } durability-=a; if(durability < 1) { durability = 0; broken(); } if(durability > maxDurability) { durability = maxDurability; }  }
    /**
     * Returns {@link #unbreakable}.
     * @return      {@link #unbreakable}
     */
    public boolean getUnbreakable() { return unbreakable; }
    /**
     * Returns {@link #maxDurability}.
     * @return      {@link #maxDurability}
     */
    public int getMaxDurability() { return maxDurability; }

    //Just get the output of the type and name
    /**
     * Returns the {@link #name}, {@link #ITEMTYPE}, and {@link #durability}.
     * @return      {@link #name}, {@link #ITEMTYPE}, and {@link #durability}
     */
    public String getSimpleOutput(){
        String output = name + " -\n" +
               "  type:  " + ITEMTYPE + "\n" +
               "  durability:  ";
        if(unbreakable) { output += "unbreakable \n"; }
        else { output += durability + "\n"; }
        return output;
    }
    /**
     * Returns the {@link #name}, {@link #description}, {@link #ITEMTYPE}, {@link #durability} and any {@link #customVariables} with inOutput to true.
     * @return       {@link #name}, {@link #description}, {@link #ITEMTYPE}, {@link #durability} and any {@link #customVariables} with inOutput to true.
     */
    public String getOutput() {
        String output = name + " -\n" +
                        "  description:  " + description + "\n" +
                        "  type:  " + ITEMTYPE + "\n" +
                        "  durability  ";
        if(unbreakable) { output += "unbreakable \n"; }
        else { output += durability + "\n"; }
        //Adds the custom variables to the output
        for(CustomVariable cv : customVariables) {
            if(cv.getInOutput()) { output=output+"  " + cv.getName() + ":  " + cv.getValue().toString() + "\n"; }
        }
        return output;
    }

    /**
     * Uses the tool and decreases the durability accordingly.
     * @param durability        The amount of durability used
     */
    public void use(int durability) {
        decreaseDurability(durability);
    }

    /***Removes this from the player's inventory*/
    public void broken() {
        if(TextFighter.player != null) { TextFighter.player.removeFromInventory(name, ITEMTYPE); }
        TextFighter.addToOutput("Your " + name + " has broken!");
    }

    public Tool (String name, String description, ArrayList<CustomVariable> customVariables, int durability, int maxDurability, boolean unbreakable) {
        super(name, description);
        this.name = name;
        this.description = description;
        this.customVariables = customVariables;
        if(durability < 1 && !unbreakable) { broken(); }
        this.durability = durability;
        this.maxDurability = maxDurability;
        this.unbreakable = unbreakable;
    }

}
