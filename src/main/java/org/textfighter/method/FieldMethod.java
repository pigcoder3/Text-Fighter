package org.textfighter.method;

import org.textfighter.method.*;

import java.util.ArrayList;
import java.lang.reflect.*;

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
            resetArguments();
            return a;
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        resetArguments();
        return null;
    }

    public int putInputInArguments(ArrayList<String> inputArgs, int inputArgsIndex) {
        if(arguments != null && argumentTypes != null) {
            ArrayList<Object> methodArgs = new ArrayList<Object>();
            for(int i=0; i<argumentTypes.size(); i++) {
                if(arguments.get(i) == null) { methodArgs.add(null); continue;}
                if(!arguments.get(i).equals("%ph%") && !arguments.get(i).getClass().equals(TFMethod.class)) { methodArgs.add(arguments.get(i)); continue; }
                if(arguments.get(i).getClass() == TFMethod.class) { methodArgs.add(arguments.get(i)); inputArgsIndex = ((TFMethod)arguments.get(i)).putInputInArguments(inputArgs, inputArgsIndex); if(inputArgsIndex == -1){ resetArguments(); return -1; } continue; }
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
                        try { methodArgs.add(Class.forName(inputArgs.get(inputArgsIndex))); } catch(ClassNotFoundException e) { resetArguments(); return -1;  }
                    }
                    inputArgsIndex++;
                }
            }
            arguments = methodArgs;
        }
        if(field != null && field.getClass().equals(FieldMethod.class)) {
            inputArgsIndex = ((FieldMethod)field).putInputInArguments(inputArgs, inputArgsIndex);
        }
        return inputArgsIndex;
    }

    public void resetArguments() {
        if(arguments != null) {
            for(Object o : arguments) {
                if(o == null) { continue; }
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

    public FieldMethod(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Object field) {
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.argumentTypes = argumentTypes;
        this.field = field;
    }
}
