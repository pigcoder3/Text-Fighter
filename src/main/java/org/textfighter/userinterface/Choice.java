package org.textfighter.userinterface;

import java.lang.reflect.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class Choice {

    private String name;
    private String description;

    private Method function;
    private ArrayList<Object> arguments;
    private Class classname;

    private Method requirement;
    private ArrayList<Object> requirementArgs;
    private Class requirementClass;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Method getFunction() { return function; }
    public Method getRequirement() { return requirement; }
    public Class getClassname() { return classname; }
    public void invokeMethod() {
        try { function.invoke(arguments); } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
    }

    public boolean invokerequirement() {
        try { return((boolean)requirement.invoke(requirementArgs)); } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        return false;
    }

    public void createMethod(String function, ArrayList<String> arguments, ArrayList<Class> argumentTypes, String classname) {
        //Gets methods and arguments for the correct method
        try { this.classname = Class.forName(classname); } catch (ClassNotFoundException e){ e.printStackTrace(); System.exit(1); }
        try { this.function = this.classname.getMethod(function, argumentTypes.toArray(new Class[arguments.size()])); } catch (NoSuchMethodException e){ e.printStackTrace(); System.exit(1); }
        if(this.function.getParameterTypes().length != arguments.size()) { System.out.println("There is an incorrect number of arguments for this choice's function parameters!"); System.exit(1); }
        Class[] parameterTypes = this.function.getParameterTypes();
        for (int i=0; i<arguments.size(); i++) { if(parameterTypes[i].equals(Integer.class)) {this.arguments.add(Integer.parseInt(arguments.get(i)));} }
    }

    public Choice(String name, String description, String function, ArrayList<String> arguments, ArrayList<Class> argumentTypes, String classname, String requirement, ArrayList<String> requirementArgs, ArrayList<Class> requirementArgTypes, String requirementClass) {
        this.name = name;
        this.description = description;
        createMethod(function, arguments, argumentTypes, classname);
        //Gets method and arguments for the requirement method
        try { this.requirementClass = Class.forName(classname); } catch (ClassNotFoundException e){ e.printStackTrace(); System.exit(1); }
        try { this.requirement = this.requirementClass.getMethod(function,requirementArgTypes.toArray(
            new Class[requirementArgs.size()])); } catch (NoSuchMethodException e){ e.printStackTrace(); System.exit(1); }
        if(this.requirement.getParameterTypes().length != arguments.size()) { System.out.println("There is an incorrect number of arguments for this choice's requirement function parameters! ( " + name + " )"); System.exit(1); }
        Class[] requirementParameterTypes = this.requirement.getParameterTypes();
        for (int i=0; i<arguments.size(); i++) { if(requirementParameterTypes[i].equals(Integer.class)) {this.requirementArgs.add(Integer.parseInt(requirementArgs.get(i)));} }
    }

    public Choice(String name, String description, String function, ArrayList<String> arguments, ArrayList<Class> argumentTypes, String classname) {
        this.name = name;
        this.description = description;
        createMethod(function, arguments, argumentTypes, classname);
    }

}
