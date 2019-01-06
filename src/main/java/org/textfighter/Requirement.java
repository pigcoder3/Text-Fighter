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
        /*
        try { this.clazz = Class.forName(clazz); } catch (ClassNotFoundException e){ Display.displayPackError("This requirement has an invalid class. Parent: " + parentName + ". Omitting..."); valid = false; }
        if(fieldclass != null && field != null) { try { this.fieldclass = Class.forName(fieldclass); } catch (ClassNotFoundException e){ Display.displayPackError("This requirement has an invalid fieldclass. Parent: " + parentName + ". Omitting..."); valid = false; } }
        try {
            if(field != null && !field.isEmpty()) {
                this.field = this.clazz.getField(field);
            }
        } catch (NoSuchFieldException | SecurityException e) { Display.displayPackError("This requirement has an invalid field. Parent: " + parentName+ ". Omitting..."); valid = false;}
        try {
            if(argumentTypes != null ) {
                if(this.field != null) {
                    this.method = this.fieldclass.getMethod(method, argumentTypes.toArray(new Class[argumentTypes.size()]));
                } else {
                    this.method = this.clazz.getMethod(method, argumentTypes.toArray(new Class[argumentTypes.size()]));
                }
            } else {
                if(this.field != null) {
                    this.method = this.fieldclass.getMethod(method);
                } else {
                    this.method = this.clazz.getMethod(method);
                }
            }
        } catch (NoSuchMethodException e){
            Display.displayWarning("This requirement has an invalid method. Omitting...");
            valid = false;
            return;
        }

        if(this.method.getParameterTypes().length != argumentTypes.size()) {
            if(parentType == Choice.class) { Display.displayWarning("There is an incorrect number of arguments for the choice '" + parentName + "' requirement's method parameters! Needed: " + this.method.getParameterTypes().length + " Got: " + argumentTypes.size());}
            else if(parentType == Enemy.class) { Display.displayWarning("There is an incorrect number of arguments for the enemy'" + parentName + "' requirement's method parameters! Needed: " + this.method.getParameterTypes().length + " Got: " + argumentTypes.size());}
            else if(parentType == Premethod.class) { Display.displayWarning("There is an incorrect number of arguments for a premethod requirement's method parameters. Needed: " + this.method.getParameterTypes().length + " Got: " + argumentTypes.size());}
        }
        if(arguments != null) {
            Class[] parameterTypes = this.method.getParameterTypes();
            for (int i=0; i<arguments.size(); i++) {
                if(parameterTypes[i].equals(Integer.class)) {
                    this.arguments.add(Integer.parseInt((String)arguments.get(i)));
                }
            }
        }
        */
    }

}
