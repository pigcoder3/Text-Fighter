package org.textfighter.item;

import java.lang.reflect.*;

public class Item implements Cloneable {

    public static String defaultName = "itemName";
    public static String defaultDescription = "An item";

    private final String ITEMTYPE = "tool";
    protected String name = defaultName;
    protected String description = defaultDescription;

    public String getItemType() { return ITEMTYPE; }
    public String getName() { return name; }
    public void setName(String s) { name = s; }
    public String getDescription() { return description; }
    public void setDescription(String s) { description=s; if(description == null) { description=defaultDescription;} }

    public Object clone() throws CloneNotSupportedException { return super.clone(); }

    public Item (String name, String description) {
        this.name = name;
        this.description = description;
    }

}
