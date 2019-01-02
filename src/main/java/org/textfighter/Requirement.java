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

    private boolean valid;

    public ArrayList<Object> getArguments() { return arguments; }
    public Class getClazz() { return clazz; }
    public Method getMethod() { return method; }

    public boolean getValid() { return valid; }

    public boolean invokeMethod() {
        try {
            if(field != null) {
                if(arguments != null) {
                    method.invoke(field, arguments.toArray());
                } else {
                    method.invoke(field, new Object[0]);
                }
            } else {
                if(arguments != null) {
                    method.invoke(null, arguments.toArray());
                } else {
                    method.invoke(null, new Object[0]);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        return false;
    }

    public Requirement(String parentName, Class parentType, String method, ArrayList<String> arguments, ArrayList<Class> argumentTypes, String clazz, String field) {
        this.parentName = parentName;
        this.parentType = parentType;
        try { this.clazz = Class.forName(clazz); } catch (ClassNotFoundException e){ Display.displayPackError("This requirement has an invalid class. Parent: " + parentName + ". Omitting..."); valid = false; }
        try {
            if(field != null && !field.isEmpty()) {
                this.field = this.clazz.getField(field);
            }
        } catch (NoSuchFieldException | SecurityException e) { Display.displayPackError("This requirement has an invalid field. Parent: " + parentName+ ". Omitting..."); valid = false;}
        if(argumentTypes != null ) {
            try { this.method = this.clazz.getMethod(method, argumentTypes.toArray(new Class[argumentTypes.size()])); } catch (NoSuchMethodException e){ Display.displayPackError("This requirement has an invalid method. Parent: " + parentName + ". Omitting..."); valid = false; }
        } else {
            try { this.method = this.clazz.getMethod(method); } catch (NoSuchMethodException e){ Display.displayPackError("This requirement has an invalid method. Parent: " + parentName + ". Omitting..."); valid = false;}
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
    }

}
