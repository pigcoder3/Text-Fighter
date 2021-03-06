package com.seanjohnson.textfighter.item;

import com.seanjohnson.textfighter.item.Item;
import com.seanjohnson.textfighter.CustomVariable;
import com.seanjohnson.textfighter.TextFighter;

import java.util.ArrayList;

public class Weapon extends Item {

    /**
     * Stores the default damage for weapons.
     * <p>Set to 5.</p>
     */
    public static int defaultDamage = 5;
    /**
     * Stores the default critial hit for weapons.
     * <p>Set to 0 (0% chance).</p>
     */
    public static int defaultCritChance = 0;
    /**
     * Stores the default name for weapons.
     * <p>Set to 10 (10% chance).</p>
     */
    public static int defaultMissChance = 10;
    /**
     * Stores the default description for weapons.
     * <p>Set to "A weapon".</p>
     */
    public static String defaultDescription = "A weapon";
    /**
     * Stores the default durability(Number of uses) for weapons.
     * <p>Set to 100 (100 uses).</p>
     */
    public static int defaultDurability = 100;
    /**
     * Stores the default maximum durability for weapons.
     * <p>Set to 100.</p>
     */
    public static int defaultMaxDurability = 100;
    /**
     * Stores the default unbreakable value.
     * <p>Set to false.</p>
     */
    public static boolean defaultUnbreakable = false;

    /**
     * Stores the item type for weapons. Cannot be changed.
     * <p>Set to "weapon".</p>
     */
    private final String ITEMTYPE = "weapon";
    /**
     * Stores the name of this weapon.
     * <p>Set to "".</p>
     */
    private String name = "";
    /**
     * Stores the damage of this weapon.
     * <p>Set to {@link #defaultDamage}.</p>
     */
    private int damage = defaultDamage;
    /**
     * Stores the cirital hit chance of this weapon.
     * <p>Set to {@link #defaultCritChance}.</p>
     */
    private int critchance = defaultCritChance;
    /**
     * Stores the miss chance of this weapon.
     * <p>Set to {@link #defaultMissChance}.</p>
     */
    private int misschance = defaultMissChance;
    /**
     * Stores the description of this weapon.
     * <p>Set to {@link #defaultDescription}.</p>
     */
    private String description = defaultDescription;
    /**
     * Stores the durability(number of uses) of this weapon.
     * <p>Set to {@link #defaultDurability}.</p>
     */
    private int durability = defaultDurability;
    /**
     * Stores the maximum durability for this weapon
     * <p>Set to {@link #defaultMaxDurability}.</p>
     */
    private int maxDurability = defaultMaxDurability;
    /**
     * Stores whether or not the weapon can break.
     * <p>Set to {@link #defaultUnbreakable}.</p>
     */
    private boolean unbreakable = defaultUnbreakable;
    /**
     * Stores the custom variables for this weapon.
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
            if(cv.getName().equals(name)) {
                return cv.getValue();
            }
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
     * @param s     The new value.
     */
    public void setName(String s) { if(s!=null && s.trim() != null) {name=s;} }
    /**
     * Returns the {@link #damage}.
     * @return       {@link #damage}
     */
    public int getDamage(){ return damage; }
    /**
     * Sets the value of {@link #damage}.
     * <p>If the new value is less than 0, then it is set to 0.</p>
     * @param a     The new value.
     */
    public void setDamage(int a){ damage=a; if(damage<0){damage=0;} }

    //critChance methods
    /**
     * Returns the {@link #critchance}.
     * @return       {@link #critchance}
     */
    public int getCritChance() { return critchance; }
    /**
     * Sets the value of {@link #critchance}.
     * <p>If the new value is greater than 100, then it is set to 100. If less than 0, then set to 0.</p>
     * @param a     The new value.
     */
    public void setCritChance(int a) { critchance=a; if(critchance<0){critchance=0;}else if(critchance>100){critchance=100;}}

    //misschance methods
    /**
     * Returns the {@link #misschance}.
     * @return       {@link #misschance}
     */
    public int getMissChance() { return misschance; }
    /**
     * Sets the value of {@link #misschance}.
     * <p>If the new value is greater than 100, then it is set to 100. If less than 0, then set to 0.</p>
     * @param a     The new value.
     */
    public void setMissChance(int a) { misschance=a; if(misschance<0){misschance=0;}else if(misschance>100){misschance=100;}}

    //description methods
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

    //Get the output of just the type and durability
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

    //Get the output of all the variables
    /**
     * Returns the {@link #name}, {@link #description}, {@link #ITEMTYPE}, {@link #damage}, {@link #critchance}, {@link #misschance}, {@link #durability} and any {@link #customVariables} with inOutput to true.
     * @return       {@link #name}, {@link #description}, {@link #ITEMTYPE}, {@link #damage}, {@link #critchance}, {@link #misschance}, {@link #durability} and any {@link #customVariables} with inOutput to true.
     */
    public String getOutput() {
        String output = name + " -\n" +
                        "  description:  " + description + "\n" +
                        "  type:  " + ITEMTYPE + "\n" +
                        "  damage:  " + damage + "\n" +
                        "  crit chance:  " + critchance + "%\n" +
                        "  miss chance:  " + misschance + "%\n" +
                        "  durability:  ";
        if(unbreakable) { output += "unbreakable \n"; }
        else { output += durability + "\n"; }
        //Adds the custom variables to the output
        for(CustomVariable cv : customVariables) {
            if(cv.getInOutput()) { output=output+"  " + cv.getName() + ":  " + cv.getValue().toString() + "\n"; }
        }
        return output;
    }

    //Equip the weapon (Set it to the currentWeapon)
    /*** Sets the player's current weapon to this.*/
    public void equip() { TextFighter.player.setCurrentWeapon(name); }

    /**
     * Uses the weapon and decreases the durability accordingly
     * @param customString      The customString that is passed to the attack method
     * @param durability        The amount of durability used
     */
    public void use(String customString, int durability) {
        TextFighter.player.attack(customString);
        decreaseDurability(durability);
    }

    //When the weapon breaks, remove it from the player's inventory
    /***Removes this from the player's inventory*/
    public void broken() {
        if(unbreakable) { return; }
        TextFighter.player.removeFromInventory(name, ITEMTYPE);
        TextFighter.addToOutput("Your " + name + " has broken!");
    }

    public Weapon(String name, String description, int damage, int critchance, int misschance, ArrayList<CustomVariable> customVariables, int durability, int maxDurability, boolean unbreakable) {
        super(name, description);
        this.name = name;
        this.description = description;
        this.damage = damage;
        this.critchance = critchance;
        this.misschance = misschance;
        this.customVariables = customVariables;
        if(durability < 1 && !unbreakable) { broken(); }
        this.durability = durability;
        this.maxDurability = maxDurability;
        this.unbreakable = unbreakable;
    }

}
