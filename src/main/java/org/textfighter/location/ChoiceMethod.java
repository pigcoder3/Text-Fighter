package org.textfighter.location;

import org.textfighter.display.Display;
import org.textfighter.TextFighter;
import org.textfighter.method.*;

import java.lang.reflect.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class ChoiceMethod {

    private Method method;
    private Class clazz;
    private Field field;
    private Class fieldclass;
    private ArrayList<Object> arguments = new ArrayList<Object>();
    private ArrayList<Object> originalArguments = new ArrayList<Object>();
    private ArrayList<Class> argumentTypes = new ArrayList<Class>();
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();

    private boolean valid;

    public Method getMethod() { return method; }
    public Class getClazz() { return clazz; }
    public Field getField() { return field; }
    public ArrayList<Object> getArguments() { return arguments; }
    public void setArguments(ArrayList<Object> args) { arguments=args; }
    public ArrayList<Object> getOriginalArguments() { return originalArguments; }
    public ArrayList<Class> getArgumentTypes() { return argumentTypes; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

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
        if(!((arguments != null && argumentTypes != null) && (arguments.size() == argumentTypes.size()) || (argumentTypes == null && arguments == null))) { resetArguments(); return false;}
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
            resetArguments();
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); resetArguments();}
        resetArguments();
        return false;
    }

    public void resetArguments() { arguments = originalArguments; }

    public ChoiceMethod(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Class clazz, Field field, Class fieldclass, ArrayList<Requirement> requirements) {
        this.argumentTypes = argumentTypes;
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.clazz = clazz;
        this.field = field;
        this.fieldclass = fieldclass;
        this.requirements = requirements;
    }
}
