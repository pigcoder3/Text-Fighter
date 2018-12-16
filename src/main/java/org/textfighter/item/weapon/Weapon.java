package org.textfighter.item.weapon;

import org.textfighter.item.Item;

public class Weapon extends Item {

    protected int damage;
    protected int baseDamage = 1;

    protected String[] methodsForCalculations = {"calculateDamage", "calculateExperienceRequirement"};

    protected String[] typeStrings = {"normal"};

    public int getDamage(){ return damage; }
    public void setDamage(int a){ damage=a;}
    public void calculateDamage() {
        int multiplier = level;
        if(level > 5) {multiplier = 5;}
        damage = multiplier*baseDamage;
    }

    public Weapon(int level, int experience, int type) {
        super(level, experience, type);
        calculateDamage();
    }

}
