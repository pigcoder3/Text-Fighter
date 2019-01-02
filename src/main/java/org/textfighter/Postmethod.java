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
    }

    public Postmethod(String method, ArrayList<String> arguments, ArrayList<Class> argumentTypes, String clazz, String field, ArrayList<Requirement> requirements) {
        try { this.clazz = Class.forName(clazz); } catch (ClassNotFoundException e){ Display.displayPackError("This postmethod has an invalid class. Omitting..."); valid = false; }
        try {
            if(field != null && !field.isEmpty()) {
                this.field = this.clazz.getField(field);
            }
        } catch (NoSuchFieldException | SecurityException e) { Display.displayPackError("This postmethod has an invalid field. Omitting..."); valid = false; return; }
        if(argumentTypes != null ) {
            try { this.method = this.clazz.getMethod(method, argumentTypes.toArray(new Class[argumentTypes.size()])); } catch (NoSuchMethodException e){ Display.displayPackError("This postmethod has an invalid method. Omitting..."); valid = false; return; }
        } else {
            try { this.method = this.clazz.getMethod(method); } catch (NoSuchMethodException e){ Display.displayPackError("This postmethod has an invalid method. Omitting..."); valid = false; return; }
        }

        if(this.method.getParameterTypes().length != argumentTypes.size()) { Display.displayWarning("There is an incorrect number of arguments for a location or enemy's premethod '" + method + "' parameters! Needed: " + this.method.getParameterTypes().length + " Got: " + argumentTypes.size()); valid = false; return;}
        if(arguments != null) {
            Class[] parameterTypes = this.method.getParameterTypes();
            for (int i=0; i<arguments.size(); i++) {
                if(parameterTypes[i].equals(Integer.class)) {
                    this.arguments.add(Integer.parseInt((String)arguments.get(i)));
                }
            }
        }

        //Filters out invalid requirements
        for(int i=0; i<requirements.size(); i++) {
            if(!requirements.get(i).getValid()) {
                this.requirements.add(requirements.get(i));
            }
        }
    }

}
