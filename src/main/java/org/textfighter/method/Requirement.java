package org.textfighter.method;

import java.lang.reflect.*;

import java.util.ArrayList;

import org.textfighter.method.TFMethod;

import org.textfighter.display.Display;
import org.textfighter.location.Choice;
import org.textfighter.enemy.Enemy;

@SuppressWarnings("unchecked")

public class Requirement {

    public static boolean defaultNeededBoolean = true;

    private ArrayList<Object> originalArguments = new ArrayList<Object>();
    private ArrayList<Object> arguments = new ArrayList<Object>();
    private Class clazz;
    private Method method;
    private Field field;
    private Class fieldclass;
    private boolean neededBoolean = defaultNeededBoolean;

    private boolean valid;

    public ArrayList<Object> getArguments() { return arguments; }
    public Class getClazz() { return clazz; }
    public Method getMethod() { return method; }
    public boolean getNeededBoolean() { return neededBoolean; }

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
            boolean output = !neededBoolean;
            if(field != null) {
                if(arguments != null && arguments.size() > 0) {
                    output = (boolean)method.invoke(field.get(null), arguments.toArray()) == neededBoolean;
                } else {
                    output = (boolean)method.invoke(field.get(null)) == neededBoolean;
                }
            } else {
                if(arguments != null && arguments.size() > 0) {
                    output = (boolean)method.invoke(null, arguments.toArray()) == neededBoolean;
                } else {
                    output = (boolean)method.invoke(null) == neededBoolean;
                }
            }
            resetArguments();
            return output;
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        resetArguments();
        return false;
    }

    public void resetArguments() { arguments = originalArguments; }

    public Requirement(Method method, ArrayList<Object> arguments, Class clazz, Field field, Class fieldclass, boolean neededBoolean) {
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.clazz = clazz;
        this.field = field;
        this.fieldclass = fieldclass;
        this.neededBoolean = neededBoolean;
    }

}
