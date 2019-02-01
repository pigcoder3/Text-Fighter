package org.textfighter.enemy;

import java.lang.reflect.*;
import java.util.ArrayList;
import org.textfighter.method.TFMethod;
import org.textfighter.method.FieldMethod;

import org.textfighter.display.Display;

@SuppressWarnings("unchecked")

public class EnemyActionMethod {

    private Method method;
    private Object field;
    private ArrayList<Object> arguments = new ArrayList<Object>();
    private ArrayList<Object> originalArguments = new ArrayList<Object>();

    private boolean valid = true;

    public Method getMethod() { return method; }
    public Object getField() { return field; }
    public ArrayList<Object> getArguments() { return arguments; }

    public boolean getValid() { return valid; }

    public boolean invokeMethod() {
        //Invokes all the arguments that are methods and set the argument to its output
        if(arguments != null) {
            for(int i=0; i<arguments.size(); i++) {
                if(arguments.get(i) != null && arguments.get(i).getClass().equals(TFMethod.class)) {
                    arguments.set(i,((TFMethod)(arguments.get(i))).invokeMethod());
                }
            }
        }

        Object fieldvalue = null;

        if(field != null) {
            if(field.getClass().equals(FieldMethod.class)) {
                //If the field is a method, then set the output of the method to the fieldvalue
                fieldvalue = ((FieldMethod)field).invokeMethod();
            } else if(field.getClass().equals(Field.class)){
                //If the field is a regular field, then set the value of the field to the fieldvalue
                try { fieldvalue = ((Field)field).get(null); } catch (IllegalAccessException e) { e.printStackTrace(); resetArguments();}
            }
        }

        try {
            if(field != null) {
                if(arguments != null && arguments.size() > 0) {
                    method.invoke(fieldvalue, arguments.toArray());
                } else {
                    method.invoke(fieldvalue, new Object[0]);
                }
            } else {
                if(arguments != null && arguments.size() > 0) {
                    method.invoke(null, arguments.toArray());
                } else {
                    method.invoke(null, new Object[0]);
                }
            }
            resetArguments();
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        resetArguments();
        return false;
    }

    public void resetArguments() { arguments = originalArguments; }

    public EnemyActionMethod(Method method, ArrayList<Object> arguments, Object field) {
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments); }
        this.field = field;
    }
}
