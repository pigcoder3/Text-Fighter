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
    private ArrayList<Object> arguments = new ArrayList<Object>();
    private ArrayList<Class> argumentTypes = new ArrayList<Class>();

    private boolean valid;

    public Method getMethod() { return method; }
    public Class getClazz() { return clazz; }
    public Field getField() { return field; }
    public ArrayList<Object> getArguments() { return arguments; }
    public void setArguments(ArrayList<Object> args) { arguments=args; }
    public ArrayList<Class> getArgumentTypes() { return argumentTypes; }

    public boolean getValid() { return valid; }

    public boolean invokeMethod() {
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
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        return false;
    }

    public ChoiceMethod(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Class clazz, Field field, Class fieldclass) {
        this.argumentTypes = argumentTypes;
        this.method = method;
        this.arguments = arguments;
        this.clazz = clazz;
        this.field = field;
        this.fieldclass = fieldclass;
    }
}
