package org.textfighter.method;

import org.textfighter.location.Location;
import org.textfighter.display.Display;
import org.textfighter.method.*;

import java.util.ArrayList;

import java.lang.reflect.*;

@SuppressWarnings("unchecked")

public class TFMethod {

    private ArrayList<Object> arguments;
    private ArrayList<Class> argumentTypes;
    private ArrayList<Object> originalArguments;
    private Class clazz;
    private Method method;
    private Field field;
    private Class fieldclass;
    private ArrayList<Requirement> requirements;

    private boolean valid = true;

    public ArrayList<Object> getArguments() { return arguments; }
    public Class getClazz() { return clazz; }
    public Method getMethod() { return method; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

    public boolean getValid() { return valid; }

    public Object invokeMethod() {
        //Invokes all the arguments that are methods
        if(arguments != null) {
            for(int i=0; i<arguments.size(); i++) {
                if(arguments.get(i) != null && arguments.get(i).getClass().equals(TFMethod.class)) {
					arguments.set(i,((TFMethod)(arguments.get(i))).invokeMethod());
                }
            }
        }

        Object a;

        //Invokes this method's base method
        try {
            if(field != null) {
                if(arguments != null && arguments.size() > 0) {
                    a=method.invoke(field.get(null), arguments.toArray());
                } else {
                    a=method.invoke(field.get(null), new Object[0]);
                }
            } else {
                if(arguments != null && arguments.size() > 0) {
                    a=method.invoke(null, arguments.toArray());
                } else {
                    a=method.invoke(null, new Object[0]);
                }
            }
            resetArguments();
            return a;
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        resetArguments();
        return null;
    }

    public int putInputInArguments(ArrayList<String> inputArgs, int inputArgsIndex) {
        ArrayList<Object> methodArgs = new ArrayList<Object>();
        if(arguments == null || argumentTypes == null) { return inputArgsIndex; }
        for(int i=0; i<argumentTypes.size(); i++) {
            if(originalArguments.get(i) == null) { methodArgs.add(null); continue;}
            if(!arguments.get(i).equals("%ph%") && !arguments.get(i).getClass().equals(TFMethod.class)) { methodArgs.add(arguments.get(i)); continue; }
            if(arguments.get(i).getClass() == TFMethod.class) { inputArgsIndex = ((TFMethod)arguments.get(i)).putInputInArguments(inputArgs, inputArgsIndex); if(inputArgsIndex == -1){ return -1; } continue; }
            if(inputArgsIndex <= inputArgs.size() - 1) {
                if(argumentTypes.get(i).equals(int.class)) {
                    methodArgs.add(Integer.parseInt(inputArgs.get(inputArgsIndex)));
                } else if(argumentTypes.get(i).equals(String.class)) {
                    methodArgs.add(inputArgs.get(inputArgsIndex));
                } else if(argumentTypes.get(i).equals(boolean.class)) {
                    methodArgs.add(Boolean.parseBoolean(inputArgs.get(inputArgsIndex)));
                } else if(argumentTypes.get(i).equals(double.class)) {
                    methodArgs.add(Double.parseDouble(inputArgs.get(inputArgsIndex)));
                } else if(argumentTypes.get(i).equals(Class.class)) {
                    try { methodArgs.add(Class.forName(inputArgs.get(inputArgsIndex))); } catch(ClassNotFoundException e) { return inputArgsIndex;  }
                }
                inputArgsIndex++;
            }
        }
        arguments = methodArgs;
        return inputArgsIndex;
    }

    public void resetArguments() { arguments = originalArguments; }

    public TFMethod(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Class clazz, Field field, Class fieldclass, ArrayList<Requirement> requirements) {
        this.method = method;
        this.arguments = arguments;
        this.argumentTypes = argumentTypes;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.clazz = clazz;
        this.field = field;
        this.fieldclass = fieldclass;
        this.requirements = requirements;
    }
}
