package com.seanjohnson.textfighter.item;

import com.seanjohnson.textfighter.item.Item;
import com.seanjohnson.textfighter.CustomVariable;
import com.seanjohnson.textfighter.TextFighter;

import java.util.ArrayList;

public class Armor extends Item {
    /**
     * Stores the default protection amount for armor.
     * <p>Set to "A special item".</p>
     */
    public static double defaultProtectionAmount = 0.0;
    /**
     * Stores the default description for armor.
     * <p>Set to "An armor piece".</p>
     */
    public static String defaultDescription = "An armor piece";
    /**
     * Stores the default durability for armor.
     * <p>Set to 100</p>
     */
    public static int defaultDurability = 100;
    /**
     * Store sthe default maximum durability for armor.
     * <p>Set to 100</p>
     */
    public static int defaultMaxDurability = 100;
    /**
     * Stores the default unbreakable value.
     * <p>Set to false.</p>
     */
    public static boolean defaultUnbreakable = false;
    
    /**
     * Stores the item type for tools. Cannot be changed.
     * <p>Set to "armor".</p>
     */
    private final String ITEMTYPE = "armor";
    /**
     * Stores the name of this name
     * <p>Set to null.</p>
     */
    private String name = "";
    /**
     * Stores the protection amount of this armor.
     * <p>Set to {@link #defaultDescription}.</p>
     */
    private double protectionAmount = defaultProtectionAmount;
    /**
     * Stores the description of this armor.
     * <p>Set to {@link #defaultDescription}.</p>
     */
    private String description = defaultDescription;
    /**
     * Stores the durability of this armor.
     * <p>Set tp {@link #defaultDurability}</p>
     */
    private int durability = defaultDurability;
    /**
     * Stores the maximum durability of this armor.
     * <p>Set to {@link #defaultMaxDurability}</p>
     */
    private int maxDurability = defaultMaxDurability;
    /**
     * Stores whether or not the armor can break.
     * <p>Set to {@link #defaultUnbreakable}.</p>
     */
    private boolean unbreakable = defaultUnbreakable;
    
    /**
     * Stores the custom variables for this armor.
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

    //Basic info stuff
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
     * @param a     The new value.
     */
    public void increaseDurability(int a) { if(unbreakable) { return; } durability+=a; if(durability < 1) { durability = 0; broken(); } if(durability > maxDurability) { durability = maxDurability; }  }
    /**
     * Decrease the value of {@link #durability}.
     * <p>If the weapon is {@link #unbreakable} then nothing happens. If the new value is less than 1, then it breaks.</p>
     * @param a     The new value.
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

    //protectionAmount methods
    /**
     * Returns the {@link #protectionAmount}.
     * @return       {@link #protectionAmount}
     */
    public double getProtectionAmount() { return protectionAmount; }
    /**
     * Sets the value of {@link #description}.
     * <p>value is less than one, then set it to 0.0.</p>
     * @param a     The new value.
     */
    public void setProtectionAmount(double a) { protectionAmount=a; if(protectionAmount<0.0){protectionAmount=0.0;}}

    //Just get output of the basic stuff
    /**
     * Returns the {@link #name}, {@link #ITEMTYPE}.
     * @return      {@link #name}, {@link #ITEMTYPE}
     */
    public String getSimpleOutput(){
        String output = name + " -\n" +
                "  type:  " + ITEMTYPE + "\n" +
                "  protection amount: " + protectionAmount + "\n" +
                "  durability:  "; //If unbreakable, then dont display durability
        if(unbreakable) { output += "unbreakable \n"; }
        else { output += durability + "\n"; }
        return output;

    }
    //Get the output of all the variables
    /**
     * Returns the {@link #name}, {@link #description}, {@link #ITEMTYPE}, and any {@link #customVariables} with inOutput to true.
     * @return       {@link #name}, {@link #description}, {@link #ITEMTYPE}, and any {@link #customVariables} with inOutput to true.
     */
    public String getOutput() {
        String output = name + " -\n" +
                        "  description:  " + description + "\n" +
                        "  type:  " + ITEMTYPE + "\n" +
                        "  protection amount:  " + protectionAmount + "%\n" +
                        "  durability:  "; //If unbreakable, then dont display durability
        if(unbreakable) { output += "unbreakable \n"; }
        else { output += durability + "\n"; }
        //Adds the custom variables to the output
        for(CustomVariable cv : customVariables) {
            if(cv.getInOutput()) { output=output+"  " + cv.getName() + ":  " + cv.getValue().toString() + "\n"; }
        }
        return output;
    }

    /**
     * Decrease the durability.
     * @param durability        The amount of durability used
     */
    public void use(int durability) {
        decreaseDurability(durability);
    }

    /**
     * Removes the armor from the player's inventory
     */
    public void broken() {
        if(TextFighter.player != null) { TextFighter.player.removeFromInventory(name, ITEMTYPE); }
        TextFighter.addToOutput("Your " + name + " has broken!");
    }

    public Armor (String name, String description, double protectionAmount, int durability, int maxDurability, boolean unbreakable, ArrayList<CustomVariable> customVariables) {
        super(name, description);
        this.name = name;
        this.description = description;
        this.protectionAmount = protectionAmount;
        this.durability = durability;
        this.maxDurability = maxDurability;
        this.unbreakable = unbreakable;
        this.customVariables = customVariables;
    }

}
