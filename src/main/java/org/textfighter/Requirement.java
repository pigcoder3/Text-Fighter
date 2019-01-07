package org.textfighter;

import java.lang.reflect.*;

import java.util.ArrayList;

import org.textfighter.display.Display;
import org.textfighter.Premethod;
import org.textfighter.location.Choice;
import org.textfighter.enemy.Enemy;

@SuppressWarnings("unchecked")

public class Requirement {

    private String parentName;
    private Class parentType;

    private ArrayList<Object> arguments = new ArrayList<Object>();
    private Class clazz;
    private Method method;
    private Field field;
    private Class fieldclass;
    private boolean neededBoolean;

    private boolean valid;

    public ArrayList<Object> getArguments() { return arguments; }
    public Class getClazz() { return clazz; }
    public Method getMethod() { return method; }

    public boolean getValid() { return valid; }

    public boolean invokeMethod() {
        try {
            if(field != null) {
                if(arguments != null) {
                    return((boolean)method.invoke(field, arguments.toArray()) == neededBoolean);
                } else {
                    return((boolean)method.invoke(field, new Object[0]) == neededBoolean);
                }
            } else {
                if(arguments != null) {
                    return((boolean)method.invoke(null, arguments.toArray()) == neededBoolean);
                } else {
                    return((boolean)method.invoke(null, new Object[0]) == neededBoolean);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        return false;
    }

    public Requirement(String parentName, Class parentType, Method method, ArrayList<Object> arguments, Class clazz, Field field, Class fieldclass, boolean neededBoolean) {
        this.parentName = parentName;
        this.parentType = parentType;
        this.method = method;
        this.arguments = arguments;
        this.clazz = clazz;
        this.field = field;
        this.fieldclass = fieldclass;
        this.neededBoolean = neededBoolean;
    }

}
