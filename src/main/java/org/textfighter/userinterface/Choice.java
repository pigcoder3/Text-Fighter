package org.textfighter.userinterface;

import java.lang.reflect.Method;

public class Choice {

    String name;
    String description;
    Method function;
    String requirement;
    String classname;
    Object[] arguments;
    Class[] argumentTypes;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Method getFunction() { return function; }
    public String getRequirement() { return requirement; }
    public String getClassname() { return classname; }
    public String[] arguments() { return arguments; }
    public Class[] argumentTypes() { return argumentTypes; }

    public Choice(String name, String description, String function, String requirement, String classname, Object[] arguments) {
        this.name = name;
        this.description = description;
        this.requirement = requirement;
        this.classname = Class.forName(classname);
        this.function = classname.getMethod(function);
        this.argumentTypes = function.getParameterTypes();
        if(argmuents.length != argumentTypes.length) {
            System.out.println("The interface files are corrupted!");
            System.exit(0);
        }
        for(int i=0; i<arguments.length; i++) {
            try {
                arguments[i] = argumentTypes[i].cast(arguments);
            } catch (ClassCatchException e) { e.printStackTrace(); System.exit(0); }
        }
        this.arguments = arguments;
    }

}
