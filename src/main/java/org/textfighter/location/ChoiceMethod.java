package org.textfighter.location;

import org.textfighter.display.Display;
import org.textfighter.TextFighter;

import java.lang.reflect.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class ChoiceMethod {

    private Method method;
    private Class clazz;
    private Field field;
    private Class fieldclass;
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

    public ChoiceMethod(Method method, ArrayList<Object> arguments, Class clazz, Field field, Class fieldclass) {
        this.argumentTypes = argumentTypes;
        this.method = method;
        this.arguments = arguments;
        this.clazz = clazz;
        this.field = field;
        this.fieldclass = fieldclass;
        this.originalArgumentsNumber = this.arguments.size();
    }
}
