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
    private Method method;
    private Object field;
    private ArrayList<Requirement> requirements;

    private boolean valid = true;

    public ArrayList<Object> getArguments() { return arguments; }
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

        Object fieldvalue = null;

        if(field != null) {
            if(field.getClass().equals(FieldMethod.class)) {
                //If the field is a method, then invoke the method and set the fieldvalue to the output
                fieldvalue = ((FieldMethod)field).invokeMethod();
            } else if(field.getClass().equals(Field.class)){
                //If the field is a regular field, then set the field value to the value it holds
                try { fieldvalue = ((Field)field).get(null); } catch (IllegalAccessException e) {e.printStackTrace(); return null; }
            }
        }

        Object a;

        //Invokes this method's base method
        try {
            if(fieldvalue != null) {
                if(arguments != null && arguments.size() > 0) {
                    a=method.invoke(fieldvalue, arguments.toArray());
                } else {
                    a=method.invoke(fieldvalue, new Object[0]);
                }
            } else {
                if(arguments != null && arguments.size() > 0) {
                    a=method.invoke(null, arguments.toArray());
                } else {
                    a=method.invoke(null, new Object[0]);
                }
            }
            return a;
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        return null;
    }

    public int putInputInArguments(ArrayList<String> inputArgs, int inputArgsIndex) {
        //Returns -1 if there are not enough inputArgs and indicates there is a problem
        if(arguments != null && argumentTypes != null) {
            ArrayList<Object> methodArgs = new ArrayList<Object>();
            for(int i=0; i<argumentTypes.size(); i++) {
                //If the argument is null, just add null to the array
                //If it was to continue normally, a NullPointerException would be thrown
                if(originalArguments.get(i) == null) { methodArgs.add(null); continue;}
                //If it is not a placeholder or method, just put it in the array
                if(!arguments.get(i).equals("%ph%") && !arguments.get(i).getClass().equals(TFMethod.class)) { methodArgs.add(arguments.get(i)); continue; }
                //If it is a method, then put arguments into the method.
                if(arguments.get(i).getClass() == TFMethod.class) { methodArgs.add(arguments.get(i)); inputArgsIndex = ((TFMethod)arguments.get(i)).putInputInArguments(inputArgs, inputArgsIndex); if(inputArgsIndex == -1){ return -1; } continue; }
                //If it has gotten this far, then the original argument is a placeholder ("%ph%")
                if(inputArgsIndex <= inputArgs.size() - 1) {
                    //Cast the placeholder to the correct type
                    if(argumentTypes.get(i).equals(int.class)) {
                        methodArgs.add(Integer.parseInt(inputArgs.get(inputArgsIndex)));
                    } else if(argumentTypes.get(i).equals(String.class)) {
                        methodArgs.add(inputArgs.get(inputArgsIndex));
                    } else if(argumentTypes.get(i).equals(boolean.class)) {
                        methodArgs.add(Boolean.parseBoolean(inputArgs.get(inputArgsIndex)));
                    } else if(argumentTypes.get(i).equals(double.class)) {
                        methodArgs.add(Double.parseDouble(inputArgs.get(inputArgsIndex)));
                    } else if(argumentTypes.get(i).equals(Class.class)) {
                        try { methodArgs.add(Class.forName(inputArgs.get(inputArgsIndex))); } catch(ClassNotFoundException e) { return -1;  }
                    }
                    inputArgsIndex++;
                } else if(methodArgs.size() != argumentTypes.size()) {
                    return -1;
                }
            }
            arguments = methodArgs;
        }
        //If the field is a method, then put input argumetns in the method
        //If a -1 is recieved, then there was a problem, and must return a -1
        if(field != null && field.getClass().equals(FieldMethod.class)) {
            inputArgsIndex = ((FieldMethod)field).putInputInArguments(inputArgs, inputArgsIndex);
            if(inputArgsIndex == -1) { return -1 ;}
        }
        return inputArgsIndex;
    }

    public void resetArguments() {
        //Reset the arguments to the original arguments because arguments that are methods may have changed them
        if(arguments != null) {
            for(Object o : arguments) {
                if(o == null) { continue; }
                //If the argument is a TFMethod, then reset its arguments
                if(o.getClass().equals(TFMethod.class)) {
                    ((TFMethod)o).resetArguments();
                }
            }
        }
        //If the field is a TFMethod, then reset its arguments
        if(field != null && field.getClass().equals(FieldMethod.class)) {
            ((FieldMethod)field).resetArguments();
        }
        arguments = originalArguments;
    }

    public TFMethod(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Object field, ArrayList<Requirement> requirements) {
        this.method = method;
        this.arguments = arguments;
        this.argumentTypes = argumentTypes;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.field = field;
        this.requirements = requirements;
    }
}
