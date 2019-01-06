package org.textfighter.enemy;

import java.lang.reflect.*;
import java.util.ArrayList;

import org.textfighter.display.Display;

@SuppressWarnings("unchecked")

public class EnemyActionMethod {

    private Method method;
    private Class clazz;
    private Field field;
    private Class fieldclass;
    private ArrayList<Object> arguments = new ArrayList<Object>();

    private boolean valid = true;

    public Method getMethod() { return method; }
    public Class getClazz() { return clazz; }
    public Field getField() { return field; }
    public ArrayList<Object> getArguments() { return arguments; }

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
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        return false;
    }

    public EnemyActionMethod(Method method, ArrayList<Object> arguments, Class clazz, Field field, Class fieldclass) {

        this.method = method;
        this.arguments = arguments;
        this.clazz = clazz;
        this.field = field;
        this.fieldclass = fieldclass;

        /*
        if((argumentTypes != null && arguments == null) || (argumentTypes == null && arguments != null)) {
            Display.displayPackError("This EnemyActionMethod's arguments array is not the same size as the argumentTypes array. Omitting...");
            valid = false;
        } else if(argumentTypes == null && arguments == null) {
            valid = true;
        } else if(argumentTypes != null && arguments != null) {
            if(argumentTypes.size() == arguments.size()) {
                valid = true;
            } else {
                valid = false;
            }
        }

        //Creates the method
        try {
            this.clazz = Class.forName(clazz);
        } catch (ClassNotFoundException e){
            Display.displayPackError("This EnemyActionMethod's has an invalid class. Omitting...");
            valid = false;
        }
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
            Display.displayWarning("This EnemyActionMethod has an invalid method. Omitting...");
            valid = false;
            return;
        }
        try {
            if(argumentTypes != null) {
                this.method = this.clazz.getMethod(method, argumentTypes.toArray(new Class[argumentTypes.size()]));
            } else {
                this.method = this.clazz.getMethod(method);
            }
        } catch (NoSuchMethodException e){
            Display.displayPackError("This EnemyActionMethod's has an invalid method. Omitting...");
            valid = false;
        }
        for (int i=0; i<arguments.size(); i++) {
            if(argumentTypes.get(i).equals(int.class)) {
                this.arguments.add(Integer.parseInt(arguments.get(i)));
            } else if (argumentTypes.get(i).equals(String.class)) {
                this.arguments.add(arguments.get(i));
            } else if (argumentTypes.get(i).equals(Boolean.class)) {
                this.arguments.add(Boolean.parseBoolean(arguments.get(i)));
            }
        }
        */
    }
}
