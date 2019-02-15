package org.textfighter.method;

import java.lang.reflect.*;

import java.util.ArrayList;

import org.textfighter.method.TFMethod;
import org.textfighter.method.FieldMethod;
import org.textfighter.display.Display;
import org.textfighter.location.Choice;
import org.textfighter.enemy.Enemy;

@SuppressWarnings("unchecked")

public class Requirement {

    /**
     * The default boolean the {@link #method} needs to return for the requirement to be met.
     * <p>Set to true by default.</p>
     */
    public static boolean defaultNeededBoolean = true;

    /**
     * The arguments originally given to the requirement. Not changed.
     * <p>Set to an empty arraylist of Objects.</p>
     */
    private ArrayList<Object> originalArguments = new ArrayList<Object>();
    /**
     * The arguments of the {@link #method}.
     * <p>Set to an ampty arraylist of Objects.</p>
     */
    private ArrayList<Object> arguments = new ArrayList<Object>();
    /***The method of the requirement.*/
    private Method method;
    /**
     * The field of the requirement.
     * <p>Can be a Field or a FieldMethod.</p>
     */
    private Object field;

    /**
     * Stores the boolean the {#link #method} needs to return for the requirement to be met.
     * <p>Set to {@link #defaultNeededBoolean}.</p>
     */
    private boolean neededBoolean = defaultNeededBoolean;

    /**
     * Returns the {@link #arguments}.
     * @return      {@link #arguments}
     */
    public ArrayList<Object> getArguments() { return arguments; }
    /**
     * Returns the {@link #method}.
     * @return      {@link #method}
     */
    public Method getMethod() { return method; }
    /**
     * Returns thr {@link #neededBoolean}.
     * @return      {@link #neededBoolean}
     */
    public boolean getNeededBoolean() { return neededBoolean; }

    /**
     * returns the output of the method.
     * <p>First, the game invokes all arguments that are methods and replaces the argument that the method occupied with its output.
     * Next, the game determines if the field is a Field or a FieldMethod. If it is a FieldMethod, then it invokes the
     * method and sets the fieldvalue to the output of the field method (the fieldvalue is a local variable, that is used in invoking
     * the method). If it is a regular Field, then set fieldvalue to the value that the {@link #field} stores.</p>
     * @return      The output of the method.
     */
    public boolean invokeMethod() {
		//Invokes all the arguments that are methods and sets the argument in its place to the output
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
                //If the field is a method, then set the fieldvalue to the output of the field
                fieldvalue = ((FieldMethod)field).invokeMethod();
            } else if(field.getClass().equals(Field.class)){
                //If the field is a regular field, then set the field value to the value it holds
                try { fieldvalue = ((Field)field).get(null); } catch (IllegalAccessException e) { e.printStackTrace(); resetArguments();}
            }
            if(fieldvalue == null) { return false; }
        }

        if(field != null && fieldvalue == null) { return !neededBoolean; }

		try {
            boolean output = !neededBoolean;
            if(field != null) {
                if(arguments != null && arguments.size() > 0) {
                    output = (boolean)method.invoke(fieldvalue, arguments.toArray()) == neededBoolean;
                } else {
                    output = (boolean)method.invoke(fieldvalue) == neededBoolean;
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

    /**
     * resetArguments sets the {@link #arguments} to the {@link #originalArguments}.
     * The method loops through each argument and invokes resetArguments() on each TFMethod.
     * It invokes resetArguments() on the field if it is a FieldMethod.
     */
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
    public Requirement(Method method, ArrayList<Object> arguments, Object field, boolean neededBoolean) {
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.field = field;
        this.neededBoolean = neededBoolean;
    }

}
