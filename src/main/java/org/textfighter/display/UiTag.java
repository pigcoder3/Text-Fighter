package org.textfighter.display;

import org.textfighter.*;
import org.textfighter.item.Item;
import org.textfighter.display.Display;
import org.textfighter.method.*;

import java.lang.reflect.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class UiTag {

    private String tag;
    private Method method;
    private Object field;
    private ArrayList<Object> arguments = new ArrayList<Object>();
    private ArrayList<Object> originalArguments = new ArrayList<Object>();
    private ArrayList<Requirement> requirements;

    public String getTag() { return tag; }
    public Method getMethod() { return method; }
    public ArrayList<Object> getArguments() { return arguments; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

    public Object invokeMethod() {
        //Invokes all the arguments that are methods and put the output of the method in as the argument
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
                //If the field is a class, set the field value to the output of the method
                fieldvalue = ((FieldMethod)field).invokeMethod();
            } else if(field.getClass().equals(Field.class)){
                //If the field is a regular field, set the field value to the value of the field
                try { fieldvalue = ((Field)field).get(null); } catch (IllegalAccessException e) { e.printStackTrace(); resetArguments();}
            }
        }

        try {
            Object output = null;
            if(field != null) {
                if(arguments != null && arguments.size() > 0) {
                    output = method.invoke(fieldvalue, arguments.toArray());
                } else {
                    output = method.invoke(fieldvalue);
                }
            } else {
                if(arguments != null && arguments.size() > 0) {
                    output = method.invoke(null, arguments.toArray());
                } else {
                    output = method.invoke(null);
                }
            }
            resetArguments();
            return output;
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        resetArguments();
        return null;
    }

    public void resetArguments() { arguments = originalArguments; }

    public UiTag(String tag, Method method, ArrayList<Object> arguments, Object field, ArrayList<Requirement> requirements) {
        this.tag = tag;
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.field = field;
        this.requirements = requirements;
    }
}
