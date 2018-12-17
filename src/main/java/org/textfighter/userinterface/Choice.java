package org.textfighter.userinterface;

import java.lang.reflect.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class Choice {

    private String name;
    private String description;
    private Method function;
    private String requirement;
    private ArrayList<Object> arguments;
    private Class classname;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Method getFunction() { return function; }
    public String getRequirement() { return requirement; }
    public Class getClassname() { return classname; }
    public void invokeMethod() {
        try { function.invoke(arguments); } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
    }

    public Choice(String name, String description, String function, ArrayList<String> arguments, ArrayList<Class> argumentTypes, String requirement, String classname) {
        this.name = name;
        this.description = description;
        this.requirement = requirement;
        try { this.classname = Class.forName(classname); } catch (ClassNotFoundException e){ e.printStackTrace(); System.exit(1); }
        try { this.function = this.classname.getMethod(function, argumentTypes.toArray(new Class[arguments.size()])); } catch (NoSuchMethodException e){ e.printStackTrace(); System.exit(1); }
        if(this.function.getParameterTypes().length != arguments.size()) { System.out.println("There is an incorrect number of arguments for this choice's function parameters!"); System.exit(1); }
        Class[] parameterTypes = this.function.getParameterTypes();
        for (int i=0; i<arguments.size(); i++) { if(parameterTypes[i].equals(Integer.class)) {this.arguments.add(Integer.parseInt(arguments.get(i)));} }
    }

}
