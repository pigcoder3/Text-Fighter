package org.textfighter.item;

import org.textfighter.item.Item;
import org.textfighter.CustomVariable;
import org.textfighter.TextFighter;

import java.util.ArrayList;

public class Weapon extends Item {

    public static String defaultName = "weaponName";
    public static int defaultDamage = 5;
    public static int defaultCritChance = 0;
    public static int defaultMissChance = 10; // 10%
    public static String defaultDescription = "A weapon";
    public static int defaultDurability = 100;

    private final String ITEMTYPE = "weapon";
    private String name = defaultName;
    private int damage = defaultDamage;
    private int critchance = defaultCritChance;
    private int misschance = defaultMissChance;
    private String description = defaultDescription;
    private int durability = defaultDurability;

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

    public String getItemType() { return ITEMTYPE; }
    public String getName() { return name; }
    public void setName(String s) { name = s; }
    public int getDamage(){ return damage; }
    public void setDamage(int a){ damage=a; }

    public int getCritChance() { return critchance; }
    public void setCritChance(int a) { critchance=a; if(critchance<0){critchance=0;}else if(critchance>100){critchance=100;}}

    public int getMissChance() { return misschance; }
    public void setMissChance(int a) { misschance=a; if(misschance<0){misschance=0;}else if(misschance>100){misschance=100;}}

    public String getDescription() { return description; }
    public void setDescription(String s) { description=s; if(description == null) { description=defaultDescription;} }

    public int getDurability() { return durability; }
    public void setDurability(int a) { durability=a; if(durability < 1) { broken(); } TextFighter.needsSaving=true; }
    public void increaseDurability(int a) { durability=+a; if(durability < 1) { broken(); } TextFighter.needsSaving=true; }
    public void decreaseDurability(int a) { durability=-a; if(durability < 1) { broken(); } TextFighter.needsSaving=true; }

    public String getSimpleOutput(){
        return name + " -\n " +
               "  type:  " + ITEMTYPE + "\n" +
               "    durability: " + durability;

    }
    public String getOutput() {
        String output = name + " -\n" +
                        "  desc:  " + description + "\n" +
                        "  type:  " + ITEMTYPE + "\n" +
                        "  damage:  " + damage + "\n" +
                        "  crit chance:  " + critchance + "\n" +
                        "  miss chance:  " + misschance + "\n" +
                        "  durability:  " + durability + "\n";
        //Adds the custom variables to the output
        for(CustomVariable cv : customVariables) {
            if(cv.getInOutput()) { output=output+"  " + cv.getName() + ":  " + cv.getValue().toString() + "\n"; }
        }
        System.out.println("Giving the output to the player.");
        return output;
    }

    public void equip() { TextFighter.player.setCurrentWeapon(name); }

    public void broken() {
        TextFighter.player.removeFromInventory(name, ITEMTYPE);
        TextFighter.addToOutput("Your " + name + " has broken!");
        TextFighter.needsSaving=true;
    }

    public Weapon(String name, String description, int damage, int critchance, int misschance, ArrayList<CustomVariable> customVariables, int durability) {
        super(name, description);
        this.name = name;
        this.description = description;
        this.damage = damage;
        this.critchance = critchance;
        this.misschance = misschance;
        this.customVariables = customVariables;
        if(durability < 1) { broken(); }
        this.durability = durability;
    }

}
