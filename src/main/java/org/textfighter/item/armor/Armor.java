package org.textfighter.item.armor;

import org.textfighter.item.Item;

public class Armor extends Item {

    protected double protectionAmount = 0.0;
    protected double baseProtection = 1;

    protected String[] methodsForCalculations = {"calculateProtectionAmount", "calculateExperienceRequirement"};

    public double getProtectionAmount() { return protectionAmount; }
    public void setProtectionAmount(int a) { protectionAmount=a; }
    public void calculateProtectionAmount() {
        int multiplier = level;
        if(level > 5) { multiplier = 5; }
        protectionAmount = level*baseProtection;
    }

    public Armor (int level, int experience) {
        super(level, experience);
    }

}
