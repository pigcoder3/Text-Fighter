package org.textfighter.location;

import org.textfighter.*;
import org.textfighter.item.Item;

import java.lang.reflect.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class UiTag {

    private String tag;
    private Class clazz;
    private Method method;
    private ArrayList<Object> arguments;

    public String getTag() { return tag; }
    public Method getFunction() { return method; }
    public Class getClassname() { return clazz; }
    public ArrayList<Object> getArguments() { return arguments; }
    public Object invokeMethod() {
        try { return(method.invoke(arguments)); } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        return null;
    }

    public UiTag(String tag, Method method, ArrayList<String> arguments, ArrayList<Class> argumentTypes, Class classname) {
        this.tag = tag;
        this.method = method;
        this.clazz = classname;
        if(argumentTypes.size() != arguments.size()) { Display.displayErrorWarning("There is an incorrect number of arguments for this tag's function parameters!");}
        Class[] parameterTypes = method.getParameterTypes();
        for (int i=0; i<arguments.size(); i++) { if(parameterTypes[i].equals(int.class)) {this.arguments.add(Integer.parseInt(arguments.get(i)));} }
    }
}
