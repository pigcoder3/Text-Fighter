package org.textfighter.item;

import org.textfighter.item.Item;

public class Armor extends Item {

    public static String defaultName = "armorName";
    public static double defaultProtectionAmount = 0.0;
    public static String defaultDescription = "A armor";

    private final String ITEMTYPE = "tool";
    private String name = defaultName;
    private double protectionAmount = defaultProtectionAmount;
    protected String description = defaultDescription;

    public String getItemType() { return ITEMTYPE; }
    public String getName() { return name; }
    public void setName(String s) { name=s; }
    public double getProtectionAmount() { return protectionAmount; }
    public void setProtectionAmount(double a) { protectionAmount=a; if(protectionAmount<0.0){protectionAmount=0.0;}}
    public String getDescription() { return description; }
    public void setDescription(String s) { description=s; if(description == null) { description=defaultDescription;} }

    public Armor (String name, String description, double protectionAmount) {
        super(name, description);
        this.protectionAmount = protectionAmount;
    }

}
