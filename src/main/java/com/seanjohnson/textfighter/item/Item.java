package com.seanjohnson.textfighter.item;

import com.seanjohnson.textfighter.CustomVariable;

import java.lang.reflect.*;

public class Item implements Cloneable {

    /**
     * Stores the default name for items.
     * <p>Set to "itemName".</p>
     */
    public static String defaultName = "itemName";
    /**
     * Stores the default description for items.
     * <p>Set to "An item".</p>
     */
    public static String defaultDescription = "An item";

    /**
     * Stores the item type for tools. Cannot be changed.
     * <p>Set to "tool".</p>
     */
    private final String ITEMTYPE = "item";
    /**
     * Stores the name of this special item.
     * <p>Set to {@link #defaultName}.</p>
     */
    protected String name = defaultName;
    /**
     * Stores the description of this special item.
     * <p>Set to {@link #defaultDescription}.</p>
     */
    protected String description = defaultDescription;

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
     * Returns the {@link #description}.
     * <p>If the name given is null, then dont do anything.</p>
     * @param s     The new value.
     */
    public void setName(String s) { name = s; }
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

    //Needed so that the game can clone a new item for the player's inventory
    /**
     * Returns a clone of the item.
     * @return      A clone of the item.
     */
    public Object clone() throws CloneNotSupportedException { return super.clone(); }

    public Item (String name, String description) {
        this.name = name;
        this.description = description;
    }

}
