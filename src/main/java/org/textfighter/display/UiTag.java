package org.textfighter.display;

import org.textfighter.*;
import org.textfighter.item.Item;
import org.textfighter.display.Display;

import java.lang.reflect.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class UiTag {

    private String tag;
    private Class clazz;
    private Method method;
    private Field field;
    private Class fieldclass;
    private ArrayList<Object> arguments;

    private boolean valid;

    public String getTag() { return tag; }
    public Method getFunction() { return method; }
    public Class getClassname() { return clazz; }
    public ArrayList<Object> getArguments() { return arguments; }

    public boolean getValid() { return valid; }

    public Object invokeMethod() {
        if(!valid) { return null; }
        try {
            if(field != null) {
                if(arguments != null) {
                    return(method.invoke(field, arguments.toArray()));
                } else {
                    return(method.invoke(field, new Object[0]));
                }
            } else {
                if(arguments != null) {
                    return(method.invoke(null, arguments.toArray()));
                } else {
                    return(method.invoke(null, new Object[0]));
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        return null;
    }

    public UiTag(String tag, Method method, ArrayList<Object> arguments, Class clazz, Field field, Class fieldclass) {
        this.tag = tag;
        this.method = method;
        this.clazz = clazz;
        this.arguments = arguments;
        this.field = field;
        this.fieldclass = fieldclass;
    }
}
