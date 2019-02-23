package org.textfighter.enemy;

import java.lang.reflect.*;
import java.util.ArrayList;
import org.textfighter.method.TFMethod;
import org.textfighter.method.FieldMethod;
import org.textfighter.display.Display;

import org.textfighter.display.Display;

@SuppressWarnings("unchecked")

public class EnemyActionMethod {

    /***Stores the method*/
    private Method method;
    /***Stores the field*/
    private Object field;
    /***Stores the arguments of the method.*/
    private ArrayList<Object> arguments = new ArrayList<Object>();
    /**
     * Stores the classes of the {@link #arguments} and corresponds by index.
     * <p>Set to an empty ArrayList of Classes.
     */
    private ArrayList<Class> argumentTypes = new ArrayList<Class>();
    /***Stores the original arguments that are given when the FieldMethod is created*/
    private ArrayList<Object> originalArguments = new ArrayList<Object>();

    /**
     * Returns the {@link #method}.
     * @return      {@link #method}
     */
    public Method getMethod() { return method; }
    /**
     * Returns the {@link #field}.
     * @return      {@link #field}
     */
    public Object getField() { return field; }
    /**
     * Returns the {@link #arguments}.
     * @return      {@link #arguments}
     */
    public ArrayList<Object> getArguments() { return arguments; }

    /**
     * returns the output of the method.
     * <p>First, the game invokes all arguments that are methods and replaces the argument that the method occupied with its output.
     * Next, the game determines if the field is a Field or a FieldMethod. If it is a FieldMethod, then it invokes the
     * method and sets the fieldvalue to the output of the field method (the fieldvalue is a local variable, that is used in invoking
     * the method). If it is a regular Field, then set fieldvalue to the value that the {@link #field} stores.</p>
     * @return      Whether or not successful.
     */
    public boolean invokeMethod() {
        //Invokes all the arguments that are methods and set the argument to its output
        if(arguments != null) {
            for(int i=0; i<arguments.size(); i++) {
                if(arguments.get(i) != null && arguments.get(i).getClass().equals(TFMethod.class)) {
                    arguments.set(i,((TFMethod)(arguments.get(i))).invokeMethod());
                }
            }
        }

        Object fieldvalue = null;

        if(field != null) {
            if(field instanceof FieldMethod) {
                //If the field is a method, then set the output of the method to the fieldvalue
                fieldvalue = ((FieldMethod)field).invokeMethod();
            } else if(field instanceof Field){
                //If the field is a regular field, then set the value of the field to the fieldvalue
                try { fieldvalue = ((Field)field).get(null); } catch (IllegalAccessException e) { Display.displayError("method: " + method); Display.displayError(Display.exceptionToString(e)); resetArguments();}
            }
            if(fieldvalue == null) { return false; }
        }

        Display.writeToLogFile("[<-----------------------Start Of Method Log----------------------->]");
        Display.writeToLogFile("[Invoking method] Type: EnemyActionMethod");
        Display.writeToLogFile("Method: " + method);
        if(arguments != null) {
            Display.writeToLogFile("Arguments: " + arguments);
            Display.writeToLogFile("argumentTypes: " + argumentTypes);
        } else {
            Display.writeToLogFile("Arguments: None");
        }
        if(fieldvalue != null && field != null) {
            Display.writeToLogFile("Field value: " + fieldvalue);
            if(field instanceof FieldMethod) {
                Display.writeToLogFile("Field (FieldMethod): " + ((FieldMethod)field).getMethod());
            }
            if(field instanceof Field) {
                Display.writeToLogFile("Field: " + ((Field)field).getName());
            }
        } else {
            Display.writeToLogFile("Field value: None");
        }


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
            Display.writeToLogFile("[<------------------------End Of Method Log------------------------>]");
            return true;
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) { Display.displayError("method: " + method); Display.displayError(Display.exceptionToString(e)); resetArguments(); }
        catch (NullPointerException e) {
            Display.displayError("There is a missing field or fieldclass. Check to make sure one is specified in the pack.");
            Display.displayError("method: " + method);
            e.printStackTrace();
            resetArguments();
        }
        catch (Exception e) { Display.displayError("method: " + method); Display.displayError(Display.exceptionToString(e)); resetArguments(); }
        resetArguments();
        Display.writeToLogFile("[<------------------------End Of Method Log------------------------>]");
        return false;
    }

    /**
     * resetArguments sets the {@link #arguments} to the {@link #originalArguments}.
     * The method loops through each argument and invokes resetArguments() on each TFMethod.
     * It invokes resetArguments() on the field if it is a FieldMethod.
     */
    public void resetArguments() {
        if(originalArguments != null) {
            for(Object o : originalArguments) {
                if(o == null) { continue; }
                //If the argument is a TFMethod, then reset its arguments
                if(o instanceof TFMethod) {
                    ((TFMethod)o).resetArguments();
                }
            }
        }
        //If the field is a method, then reset the arguments
        if(field != null && field.getClass().equals(FieldMethod.class)) {
            ((FieldMethod)field).resetArguments();
        }
        arguments = new ArrayList<>(originalArguments);;
    }

    public EnemyActionMethod(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Object field) {
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments); }
        this.argumentTypes = argumentTypes;
        this.field = field;
    }
}
