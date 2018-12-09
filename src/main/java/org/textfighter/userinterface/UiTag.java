package org.textfighter.userinterface;

import org.textfighter.Player;

import java.lang.reflect.Method;

import java.util.ArrayList;

public class UiTag {

    private String tag;
    private Class classname;
    private Method function;
    private ArrayList<Object> arguments;

    public UiTag(String tag, String function, ArrayList<String> arguments, ArrayList<Class> argumentTypes, String classname) {
        this.tag = tag;
        try { this.classname = Class.forName(classname); } catch (ClassNotFoundException e ){ e.printStackTrace(); System.exit(1); }
        try { this.function = this.classname.getMethod(function, argumentTypes.toArray(new Class[argumentTypes.size()])); } catch (NoSuchMethodException e){ e.printStackTrace(); System.exit(1); }
        if(this.function.getParameterTypes().length != arguments.size()) { System.out.println("There is an incorrect number of arguments for this choice's function parameters!"); System.exit(1); }
        Class[] parameterTypes = this.function.getParameterTypes();
        for (int i=0; i<arguments.size(); i++) { if(parameterTypes[i].equals(int.class)) {this.arguments.add(Integer.parseInt(arguments.get(i)));} }
    }
}
