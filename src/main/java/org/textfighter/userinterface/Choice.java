package org.textfighter.userinterface;

public class Choice {

    String name;
    String description;
    String function;
    String requirement;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getFunction() { return function; }
    public String getRequirement() { return requirement; }

    public Choice(String name, String description, String function, String requirement) {
        this.name = name;
        this.description = description;
        this.function = function;
        this.requirement = requirement;
    }

}
