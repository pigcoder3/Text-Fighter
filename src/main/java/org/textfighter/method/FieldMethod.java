package org.textfighter.method;

import org.textfighter.method.*;

import java.util.ArrayList;
import java.lang.reflect.*;

/*

    These methods are methods that go where fields in methods go.
    They are automatically understood to be FieldMethods, so you
    dont have to specify if it is one.

*/

public class FieldMethod {

    private ArrayList<Object> arguments;
    private ArrayList<Class> argumentTypes;
    private ArrayList<Object> originalArguments;
    private Method method;
    private Object field;

    public Method getMethod() { return method; }
    public Object getField() { return field; }
    public ArrayList<Object> getArguments() { return arguments; }
    public void setArguments(ArrayList<Object> args) { arguments=args; }
    public ArrayList<Object> getOriginalArguments() { return originalArguments; }
    public ArrayList<Class> getArgumentTypes() { return argumentTypes; }

    public Object invokeMethod() {
        //Invokes all the arguments that are methods and sets the argument to the output
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
                //If the field is a method, then invoke the method and set the field value to its output
                fieldvalue = ((FieldMethod)field).invokeMethod();
            } else if(field.getClass().equals(Field.class)){
                //If the field is a regular field, then set the field value to the value it holds
                try { fieldvalue = ((Field)field).get(null); } catch (IllegalAccessException e) { e.printStackTrace(); resetArguments();}
            }
            if(fieldvalue == null) { return null; }
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
        //Return -1 if there are not enough inputarguments or an argument is incorrect and indicates there is a problem
        if(arguments != null && argumentTypes != null) {
            ArrayList<Object> methodArgs = new ArrayList<Object>();
            for(int i=0; i<argumentTypes.size(); i++) {
                //If the argument is null, just add null to the array
                //If it was to continue normally, a NullPointerException would be thrown
                if(arguments.get(i) == null) { methodArgs.add(null); continue;}
                //If the argument is a regular argument, then just put it in the methoArgs array
                if(!arguments.get(i).equals("%ph%") && !arguments.get(i).getClass().equals(TFMethod.class)) { methodArgs.add(arguments.get(i)); continue; }
                //If the argument is a method, then just put input in the arguments. If a -1 is returned, then there was a problem, and must return -1
                if(arguments.get(i).getClass() == TFMethod.class) { methodArgs.add(arguments.get(i)); inputArgsIndex = ((TFMethod)arguments.get(i)).putInputInArguments(inputArgs, inputArgsIndex); if(inputArgsIndex == -1){ resetArguments(); return -1; } continue; }
                //If it makes it this far, then the arguemnt is a placeholder ("%ph%")
                if(inputArgsIndex <= inputArgs.size() - 1) {
                    //Cast the inputArg to the correct type
                    if(argumentTypes.get(i).equals(int.class)) {
                        methodArgs.add(Integer.parseInt(inputArgs.get(inputArgsIndex)));
                    } else if(argumentTypes.get(i).equals(String.class)) {
                        methodArgs.add(inputArgs.get(inputArgsIndex));
                    } else if(argumentTypes.get(i).equals(boolean.class)) {
                        methodArgs.add(Boolean.parseBoolean(inputArgs.get(inputArgsIndex)));
                    } else if(argumentTypes.get(i).equals(double.class)) {
                        methodArgs.add(Double.parseDouble(inputArgs.get(inputArgsIndex)));
                    } else if(argumentTypes.get(i).equals(Class.class)) {
                        try { methodArgs.add(Class.forName(inputArgs.get(inputArgsIndex))); } catch(ClassNotFoundException e) { resetArguments(); return -1;  }
                    }
                    inputArgsIndex++;
                } else if(methodArgs.size() != argumentTypes.size()) {
                    return -1;
                }
            }
            arguments = methodArgs;
        }
        //If the field is a method, then put intput in the arguments
        //If a -1 is recieved, then there is a problem, and must return a -1
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
                //If the argument is a method, then reset the arguments
                if(o.getClass().equals(TFMethod.class)) {
                    ((TFMethod)o).resetArguments();
                }
            }
        }
        //If the field is a method, then reset the arguments
        if(field != null && field.getClass().equals(FieldMethod.class)) {
            ((FieldMethod)field).resetArguments();
        }
        arguments = originalArguments;
    }

    public FieldMethod(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Object field) {
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.argumentTypes = argumentTypes;
        this.field = field;
    }
}
