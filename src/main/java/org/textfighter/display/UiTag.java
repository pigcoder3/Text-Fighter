package org.textfighter.display;

import org.textfighter.*;
import org.textfighter.item.Item;
import org.textfighter.display.Display;
import org.textfighter.method.*;

import java.lang.reflect.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class UiTag {

    /***The tag including the angle brackets*/
    private String tag;
    /*** Stores the method of the TFMethod to be invoked.*/
    private Method method;
    /**
     * Stores the Object that the {@link #method} will be invoked on.
     * <p>Fields can be of the field class or TFMethods that return a value that is then used as the field.</p>
     */
    private Object field;
    /**
     * Stores the arguments of the method.
     * <p>Arguments can store ints, doubles, Strings, classes, and other TFMethods</p>
     */
    private ArrayList<Object> arguments = new ArrayList<Object>();
    /** 
     * Stores the original arguments of this TFMethod.
     * <p>These are useful because arguments that are methods are replaced with their output in the arguments.</p>
     * <p>This will allow the methods to be used again.</p>
     */
    private ArrayList<Object> originalArguments = new ArrayList<Object>();
    /**
     * Stores the requirements of the method (Only used in premethods and postmethods of locations and enemies).
     * <p>These are used to determine if the conditions are right for the method to be invoked. If not invoked, the tag is just removed from the parsed interface.</p>
     */
    private ArrayList<Requirement> requirements;

    /**
     * Returns the {@link #tag}.
     * @return      {@link #tag}
     */
    public String getTag() { return tag; }
    /**
     * Returns the {@link #method}.
     * @return      {@link #method}
     */
    public Method getMethod() { return method; }
    /**
     * Returns the {@link #arguments}.
     * @return      {@link #arguments}
     */
    public ArrayList<Object> getArguments() { return arguments; }
    /**
     * Returns the {@link #requirements}.
     * @return      {@link #requirements}
     */
    public ArrayList<Requirement> getRequirements() { return requirements; }

    /**
     * returns the output of the method.
     * <p>First, the game invokes all arguments that are methods and replaces the argument that the method occupied with its output.
     * Next, the game determines if the field is a Field or a FieldMethod. If it is a FieldMethod, then it invokes the
     * method and sets the fieldvalue to the output of the field method (the fieldvalue is a local variable, that is used in invoking
     * the method). If it is a regular Field, then set fieldvalue to the value that the {@link #field} stores.</p>
     * @return      The output of the method.
     */
    public Object invokeMethod() {
        //Invokes all the arguments that are methods and put the output of the method in as the argument
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
                //If the field is a class, set the field value to the output of the method
                fieldvalue = ((FieldMethod)field).invokeMethod();
            } else if(field.getClass().equals(Field.class)){
                //If the field is a regular field, set the field value to the value of the field
                try { fieldvalue = ((Field)field).get(null); } catch (IllegalAccessException e) { e.printStackTrace(); resetArguments();}
            }
            if(fieldvalue == null) { return null; }
        }

        try {
            Object output = null;
            if(fieldvalue != null) {
                if(arguments != null && arguments.size() > 0) {
                    output = method.invoke(fieldvalue, arguments.toArray());
                } else {
                    output = method.invoke(fieldvalue);
                }
            } else {
                if(arguments != null && arguments.size() > 0) {
                    output = method.invoke(null, arguments.toArray());
                } else {
                    output = method.invoke(null);
                }
            }
            resetArguments();
            return output;
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        resetArguments();
        return null;
    }

    /**
     * resetArguments sets the {@link #arguments} to the {@link #originalArguments}.
     * The method loops through each argument and invokes resetArguments() on each TFMethod.
     * It invokes resetArguments() on the field if it is a FieldMethod.
     */
    public void resetArguments() {
        //Reset the arguments to the original arguments because arguments that are methods may have changed them
        if(originalArguments != null) {
            for(Object o : originalArguments) {
                if(o == null) { continue; }
                //If the argument is a TFMethod, then reset its arguments
                if(o instanceof TFMethod) {
                    ((TFMethod)o).resetArguments();
                }
            }
        }
        //If the field is a TFMethod, then reset its arguments
        if(field != null && field.getClass().equals(FieldMethod.class)) {
            ((FieldMethod)field).resetArguments();
        }
        arguments = new ArrayList<>(originalArguments);;
     }

    public UiTag(String tag, Method method, ArrayList<Object> arguments, Object field, ArrayList<Requirement> requirements) {
        this.tag = tag;
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.field = field;
        this.requirements = requirements;
    }
}
