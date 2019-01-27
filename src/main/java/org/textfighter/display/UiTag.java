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
    private Class clazz;
    private Method method;
    private Field field;
    private Class fieldclass;
    private ArrayList<Object> arguments = new ArrayList<Object>();
    private ArrayList<Object> originalArguments = new ArrayList<Object>();
    private ArrayList<Requirement> requirements;

    public String getTag() { return tag; }
    public Method getFunction() { return method; }
    public Class getClassname() { return clazz; }
    public ArrayList<Object> getArguments() { return arguments; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

    public Object invokeMethod() {
        //Invokes all the arguments that are methods
        if(arguments != null) {
            for(int i=0; i<arguments.size(); i++) {
                if(arguments.get(i) != null && arguments.get(i).getClass().equals(TFMethod.class)) {
                    arguments.set(i,((TFMethod)(arguments.get(i))).invokeMethod());
                }
            }
        }
        try {
            Object output = null;
            if(field != null) {
                if(arguments != null && arguments.size() > 0) {
                    output = method.invoke(field.get(null), arguments.toArray());
                } else {
                    output = method.invoke(field.get(null));
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

    public UiTag(String tag, Method method, ArrayList<Object> arguments, Class clazz, Field field, Class fieldclass, ArrayList<Requirement> requirements) {
        this.tag = tag;
        this.method = method;
        this.clazz = clazz;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.field = field;
        this.fieldclass = fieldclass;
        this.requirements = requirements;
    }
}
