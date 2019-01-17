package org.textfighter.item.weapon;

import org.textfighter.item.Item;

public class Weapon extends Item {

    protected int damage;
    protected int baseDamage = 1;

    //calculateExperienceRequirement hasnt been added yet
    protected String[] methodsForCalculations = {"calculateDamage", "calculateExperienceRequirement"};

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
