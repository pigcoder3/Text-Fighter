package org.textfighter.enemy;

import java.lang.reflect.*;
import java.util.ArrayList;
import org.textfighter.method.TFMethod;

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
        //Invokes all the arguments that are methods
        if(arguments != null) {
            for(int i=0; i<arguments.size(); i++) {
                if(arguments.get(i) != null && arguments.get(i).getClass().equals(TFMethod.class)) {
                    arguments.set(i,((TFMethod)(arguments.get(i))).invokeMethod());
                }
            }
        }
        try {
            if(field != null) {
                if(arguments != null && arguments.size() > 0) {
                    method.invoke(field.get(null), arguments.toArray());
                } else {
                    method.invoke(field.get(null), new Object[0]);
                }
            } else {
                if(arguments != null && arguments.size() > 0) {
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
    }
}
