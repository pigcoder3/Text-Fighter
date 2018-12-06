package org.textfighter.userinterface;

public class Choice {

    String name;
    String description;
    String function;
    String requirement;
    String classname;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getFunction() { return function; }
    public String getRequirement() { return requirement; }
    public String getClass() { return classname; }

    public Choice(String name, String description, String function, String requirement, String classname) {
        this.name = name;
        this.description = description;
        this.function = function;
        this.requirement = requirement;
        this.classname = classname;
    }

}
