package org.textfighter.item.armor;

import org.textfighter.item.Item;

public class Armor extends Item {

    protected double protectionAmount = 0.0;

    protected String[] typeStrings = {"normal"};

    public double getProtectionAmount() { return protectionAmount; }
    public void setProtectionAmount(int a) { protectionAmount=a; }

}
