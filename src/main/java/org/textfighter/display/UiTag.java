package org.textfighter.display;

import org.textfighter.*;
import org.textfighter.item.Item;
import org.textfighter.display.Display;
import org.textfighter.method.TFMethod;

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

    public String getTag() { return tag; }
    public Method getFunction() { return method; }
    public Class getClassname() { return clazz; }
    public ArrayList<Object> getArguments() { return arguments; }

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
            if(field != null) {
                if(arguments != null) {
                    return(method.invoke(field.get(null), arguments.toArray()));
                } else {
                    return(method.invoke(field.get(null)));
                }
            } else {
                if(arguments != null) {
                    return(method.invoke(null, arguments.toArray()));
                } else {
                    return(method.invoke(null));
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
