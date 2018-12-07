package org.textfighter.userinterface;

import java.lang.reflect.Method;

public class Choice {

    String name;
    String description;
    Method function;
    String requirement;
    Class classname;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Method getFunction() { return function; }
    public String getRequirement() { return requirement; }
    public Class getClassname() { return classname; }

    public Choice(String name, String description, String function, String requirement, String classname) {
        this.name = name;
        this.description = description;
        this.requirement = requirement;
        try { this.classname = Class.forName(classname); } catch (ClassNotFoundException e ){ e.printStackTrace(); System.exit(1); }
        try { this.function = this.classname.getMethod(function); } catch (NoSuchMethodException e){ e.printStackTrace(); System.exit(1); }
    }

}
