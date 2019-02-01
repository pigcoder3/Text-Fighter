package org.textfighter.location;

import org.textfighter.display.Display;
import org.textfighter.TextFighter;
import org.textfighter.method.*;

import java.lang.reflect.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class ChoiceMethod {

    private Method method;
    private Object field;
    private ArrayList<Object> arguments = new ArrayList<Object>();
    private ArrayList<Object> originalArguments = new ArrayList<Object>();
    private ArrayList<Class> argumentTypes = new ArrayList<Class>();
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();

    private boolean valid;

    public Method getMethod() { return method; }
    public Object getField() { return field; }
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

        Object fieldvalue = null;

        if(field != null) {
            if(field.getClass().equals(FieldMethod.class)) {
                fieldvalue = ((FieldMethod)field).invokeMethod();
            } else if(field.getClass().equals(Field.class)){
                try { fieldvalue = ((Field)field).get(null); } catch (IllegalAccessException e) { e.printStackTrace(); resetArguments();}
            }
            if(fieldvalue == null) { return false; }
        }

        if(!((arguments != null && argumentTypes != null) && (arguments.size() == argumentTypes.size()) || (argumentTypes == null && arguments == null))) { resetArguments(); return false;}
        try {
            if(fieldvalue != null) {
                if(arguments != null && arguments.size() > 0) {
                    method.invoke(fieldvalue, arguments.toArray());
                } else {
                    method.invoke(fieldvalue, new Object[0]);
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

    public int putInputInArguments(ArrayList<String> inputArgs, int inputArgsIndex) {
        //returns -1 if there are not enough arguments and there is a problem
        if(arguments != null && argumentTypes != null) {
            ArrayList<Object> methodArgs = new ArrayList<Object>();
            for(int i=0; i<argumentTypes.size(); i++) {
                //If the argument is null, just add null to the array
                //If it was to continue normally, a NullPointerException would be thrown
                if(arguments.get(i) == null) { methodArgs.add(null); continue;}
                //If the argument is a regular argument, then just put it in the methodArgs array
                if(!arguments.get(i).equals("%ph%") && !arguments.get(i).getClass().equals(TFMethod.class)) { methodArgs.add(arguments.get(i)); continue; }
                //If the argument is a method, then put input in the arguments
                if(arguments.get(i).getClass() == TFMethod.class) { methodArgs.add(arguments.get(i)); inputArgsIndex = ((TFMethod)arguments.get(i)).putInputInArguments(inputArgs, inputArgsIndex); if(inputArgsIndex == -1){ return -1; } continue; }
                //If it has made it this far then the argument is a placeholder ("%ph%")
                if(inputArgsIndex < inputArgs.size()) {
                    //Cast the input to the correct type
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
        if(arguments != null) {
            for(Object o : arguments) {
                if(o == null) { continue; }
                //If the argument is a method, then reset its methods
                if(o.getClass().equals(TFMethod.class)) {
                    ((TFMethod)o).resetArguments();
                }
            }
        }
        if(field != null && field.getClass().equals(FieldMethod.class)) {
            ((FieldMethod)field).resetArguments();
        }
        arguments = originalArguments;
    }

    public ChoiceMethod(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Object field, ArrayList<Requirement> requirements) {
        this.argumentTypes = argumentTypes;
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.field = field;
        this.requirements = requirements;
    }
}
