package org.textfighter.userinterface;

import org.textfighter.Player;
import org.textfighter.item.Item;

import java.lang.reflect.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class UiTag {

    private String tag;
    private Class classname;
    private Method function;
    private ArrayList<Object> arguments;

    public String getTag() { return tag; }
    public Method getFunction() { return function; }
    public Class getClassname() { return classname; }
    public ArrayList<Object> getArguments() { return arguments; }
    public String invokeMethod() {
        try { return((String)function.invoke(arguments)); } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        return null;
    }

    public UiTag(String tag, String function, ArrayList<String> arguments, ArrayList<Class> argumentTypes, String classname) {
        this.tag = tag;
        try { this.classname = Class.forName(classname); } catch (ClassNotFoundException e ){ e.printStackTrace(); System.exit(1); }
        try { this.function = this.classname.getMethod(function, argumentTypes.toArray(new Class[argumentTypes.size()])); } catch (NoSuchMethodException e){ e.printStackTrace(); System.exit(1); }
        if(this.function.getParameterTypes().length != arguments.size()) { System.out.println("There is an incorrect number of arguments for this choice's function parameters!"); System.exit(1); }
        Class[] parameterTypes = this.function.getParameterTypes();
        for (int i=0; i<arguments.size(); i++) { if(parameterTypes[i].equals(int.class)) {this.arguments.add(Integer.parseInt(arguments.get(i)));} }
    }
}
