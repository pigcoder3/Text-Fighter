package org.textfighter;

import java.lang.reflect.*;

import java.util.ArrayList;

import org.textfighter.display.Display;

@SuppressWarnings("unchecked")

public class Requirement {

    private ArrayList<Object> arguments;
    private Class clazz;
    private Method method;
    private Field field;

    public ArrayList<Object> getArguments() { return arguments; }
    public Class getClazz() { return clazz; }
    public Method getMethod() { return method; }

    public boolean invokeMethod() {
        try {
            if(field != null ) {
                return((boolean)method.invoke(field, arguments));
            } else {
                return((boolean)method.invoke(arguments));
            }
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        return false;
    }

    public Requirement(String method, ArrayList<String> arguments, ArrayList<Class> argumentTypes, String clazz, String field) {
        try { this.clazz = Class.forName(clazz); } catch (ClassNotFoundException e){ e.printStackTrace(); System.exit(1); }
        try {
            if(!field.isEmpty()) {
                this.field = this.clazz.getField(field);
            }
        } catch (NoSuchFieldException | SecurityException e) { e.printStackTrace(); System.exit(1);}
        try { this.method = this.clazz.getMethod(method, argumentTypes.toArray(new Class[arguments.size()])); } catch (NoSuchMethodException e){ e.printStackTrace(); System.exit(1); }
        if(this.method.getParameterTypes().length != arguments.size()) { Display.displayWarning("There is an incorrect number of arguments for this choice's requirement's function parameters!"); }
        Class[] parameterTypes = this.method.getParameterTypes();
        for (int i=0; i<arguments.size(); i++) {
            if(parameterTypes[i].equals(Integer.class)) {
                this.arguments.add(Integer.parseInt((String)arguments.get(i)));
            }
        }
    }

}
