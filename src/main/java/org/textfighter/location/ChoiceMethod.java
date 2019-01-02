package org.textfighter.location;

import org.textfighter.display.Display;

import java.lang.reflect.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class ChoiceMethod {

    private Method method;
    private Class clazz;
    private Field field;
    private int originalArgumentsNumber;
    private ArrayList<Object> arguments = new ArrayList<Object>();
    private ArrayList<Class> argumentTypes = new ArrayList<Class>();

    private boolean valid;

    public Method getMethod() { return method; }
    public Class getClazz() { return clazz; }
    public Field getField() { return field; }
    public ArrayList<Object> getArguments() { return arguments; }
    public void setArguments(ArrayList<Object> args) { arguments=args; }
    public ArrayList<Class> getArgumentTypes() { return argumentTypes; }
    public int getOriginalArgumentsNumber() { return originalArgumentsNumber; }

    public boolean getValid() { return valid; }

    public boolean invokeMethod() {
        if(arguments.size() != argumentTypes.size()) {
            removeAllAfterIndex();
            return false;
        } else {
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
                removeAllAfterIndex();
                return true;
            } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        }
        removeAllAfterIndex();
        return false;
    }

    public void removeAllAfterIndex() {
        for(int i=originalArgumentsNumber; i<arguments.size(); i++) {
            arguments.remove(i);
        }
    }

    public ChoiceMethod(String method, ArrayList<String> arguments, ArrayList<Class> argumentTypes, String clazz, String field) {

        this.argumentTypes = argumentTypes;
        //Creates the method
        try {
            this.clazz = Class.forName(clazz);
        } catch (ClassNotFoundException e){
            Display.displayPackError("This choice has an invalid class. Omitting...");
            valid = false;
        }
        try {
            if(field != null) {
                this.field = this.clazz.getField(field);
            }
        } catch (NoSuchFieldException | SecurityException e) { Display.displayPackError("This choice has an invalid method. Omitting..."); valid = false; }
        try {
            if(argumentTypes != null) {
                this.method = this.clazz.getMethod(method, argumentTypes.toArray(new Class[argumentTypes.size()]));
            } else {
                this.method = this.clazz.getMethod(method);
            }
        } catch (NoSuchMethodException e){
            Display.displayPackError("This choice has an invalid method. Omitting...");
            valid = false;
        }
        for (int i=0; i<arguments.size(); i++) {
            if(this.method.getParameterTypes()[i].equals(int.class)) {
                this.arguments.add(Integer.parseInt(arguments.get(i)));
            } else if (this.method.getParameterTypes()[i].equals(String.class)) {
                this.arguments.add(arguments.get(i));
            }
        }
        this.originalArgumentsNumber = this.arguments.size();
    }
}
