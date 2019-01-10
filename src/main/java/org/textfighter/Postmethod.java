package org.textfighter;

import org.textfighter.location.Location;
import org.textfighter.display.Display;
import org.textfighter.Requirement;

import java.util.ArrayList;

import java.lang.reflect.*;

@SuppressWarnings("unchecked")

public class Postmethod {

    private ArrayList<Object> arguments;
    private Class clazz;
    private Method method;
    private Field field;
    private Class fieldclass;
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();

    private boolean valid = true;

    public ArrayList<Object> getArguments() { return arguments; }
    public Class getClazz() { return clazz; }
    public Method getMethod() { return method; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

    public boolean getValid() { return valid; }

    public void invokeMethod() {
        try {
            if(field != null) {
                if(arguments != null) {
                    method.invoke(field.get(null), arguments.toArray());
                } else {
                    method.invoke(field.get(null), new Object[0]);
                }
            } else {
                if(arguments != null) {
                    method.invoke(null, arguments.toArray());
                } else {
                    method.invoke(null, new Object[0]);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
    }

    public Postmethod(Method method, ArrayList<Object> arguments, Class clazz, Field field, Class fieldclass, ArrayList<Requirement> requirements) {
        this.method = method;
        this.arguments = arguments;
        this.clazz = clazz;
        this.field = field;
        this.fieldclass = fieldclass;
        this.requirements = requirements;
    }
}
