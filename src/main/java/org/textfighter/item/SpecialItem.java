package org.textfighter.item;

public class SpecialItem extends Item {

    public static String defaultName = "specialItemName";
    public static String defaultDescription = "A special item";

    private final String ITEMTYPE = "tool";
    private String name = defaultName;
    protected String description = defaultDescription;

    public String getItemType() { return ITEMTYPE; }
    public String getName() { return name; }
    public void setName(String s) { name=s; if(name == null) { name=defaultName; }}
    public String getDescription() { return description; }
    public void setDescription(String s) { description=s; if(description == null) { description=defaultDescription;} }

    public SpecialItem(String name, String description) {
        super(name, description);
        this.name = name;
        this.description = description;
    }
}
