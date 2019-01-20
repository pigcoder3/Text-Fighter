package org.textfighter.method;

import org.textfighter.location.Location;
import org.textfighter.display.Display;
import org.textfighter.method.*;

import java.util.ArrayList;

import java.lang.reflect.*;

@SuppressWarnings("unchecked")

public class TFMethod {

    private ArrayList<Object> arguments;
    private Class clazz;
    private Method method;
    private Field field;
    private Class fieldclass;
    private ArrayList<Requirement> requirements;

    private boolean valid = true;

    public ArrayList<Object> getArguments() { return arguments; }
    public Class getClazz() { return clazz; }
    public Method getMethod() { return method; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

    public boolean getValid() { return valid; }

    public Object invokeMethod() {
        //Invokes all the arguments that are methods
        if(arguments != null) {
            for(int i=0; i<arguments.size(); i++) {
                if(arguments.get(i) != null && arguments.get(i).getClass().equals(TFMethod.class)) {
                    arguments.set(i,((TFMethod)(arguments.get(i))).invokeMethod());
                }
            }
        }

        Object a;

        //Invokes this method's base method
        try {
            if(field != null) {
                if(arguments != null && arguments.size() > 0) {
                    a=method.invoke(field.get(null), arguments.toArray());
                } else {
                    a=method.invoke(field.get(null), new Object[0]);
                }
            } else {
                if(arguments != null && arguments.size() > 0) {
                    a=method.invoke(null, arguments.toArray());
                } else {
                    a=method.invoke(null, new Object[0]);
                }
            }
            if(method.getReturnType() != void.class) { return a; } else { return null; }
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        return null;
    }

    public TFMethod(Method method, ArrayList<Object> arguments, Class clazz, Field field, Class fieldclass, ArrayList<Requirement> requirements) {
        this.method = method;
        this.arguments = arguments;
        this.clazz = clazz;
        this.field = field;
        this.fieldclass = fieldclass;
        this.requirements = requirements;
    }
}
