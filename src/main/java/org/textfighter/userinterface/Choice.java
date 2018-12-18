package org.textfighter.userinterface;

import org.textfighter.userinterface.ChoiceRequirement;

import java.lang.reflect.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class Choice {

    private String name;
    private String description;

    private Method method;
    private ArrayList<Object> arguments;
    private Class clazz;
    private Field field;

    private ArrayList<ChoiceRequirement> requirements;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Method getMethod() { return method; }
    public ArrayList<ChoiceRequirement> getRequirements() { return requirements; }
    public Class getClazz() { return clazz; }
    public void invokeMethod() {
        try {
            if(field != null ) {
                method.invoke(field, arguments);
            } else {
                method.invoke(arguments);
            }
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
    }

    public Choice(String name, String description, String method, ArrayList<String> arguments, ArrayList<Class> argumentTypes, String clazz, String field, ArrayList<ChoiceRequirement> requirements) {
        this.name = name;
        this.description = description;
        this.requirements = requirements;
        //Created the method
        try { this.clazz = Class.forName(clazz); } catch (ClassNotFoundException e){ e.printStackTrace(); System.exit(1); }
        try {
            if(field != null) {
                this.field = this.clazz.getField(field);
            }
        } catch (NoSuchFieldException | SecurityException e) { e.printStackTrace(); System.exit(1);}
        try { this.method = this.clazz.getMethod(method, argumentTypes.toArray(new Class[arguments.size()])); } catch (NoSuchMethodException e){ e.printStackTrace(); System.exit(1); }
        if(this.method.getParameterTypes().length != arguments.size()) { System.out.println("There is an incorrect number of arguments for this choice's method parameters!"); System.exit(1); }
        Class[] parameterTypes = this.method.getParameterTypes();
        for (int i=0; i<arguments.size(); i++) {
            if(parameterTypes[i].equals(Integer.class)) {
                this.arguments.add(Integer.parseInt(arguments.get(i)));
            }
        }
    }

}
