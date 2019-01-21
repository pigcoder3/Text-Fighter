package org.textfighter.item;

import org.textfighter.item.Item;

public class Weapon extends Item {

    public static String defaultName = "weaponName";
    public static int defaultDamage = 5;
    public static int defaultCritChance = 0;
    public static int defaultMissChance = 10; // 10%
    public static String defaultDescription = "A weapon";

    private final String ITEMTYPE = "tool";
    private String name = defaultName;
    private int damage = defaultDamage;
    private int critchance = defaultCritChance;
    private int misschance = defaultMissChance;
    protected String description = defaultDescription;

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

    public Weapon(String name, String description, int damage, int critchance, int misschance) {
        super(name, description);
        this.damage = damage;
        this.critchance = critchance;
        this.misschance = misschance;
    }

}
