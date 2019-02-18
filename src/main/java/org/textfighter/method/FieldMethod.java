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

    /***Stores the arguments of the method.*/
    private ArrayList<Object> arguments;
    /***Stores all types of the arguments that correspond by index*/
    private ArrayList<Class> argumentTypes;
    /***Stores the original arguments that are given when the FieldMethod is created*/
    private ArrayList<Object> originalArguments;
    /***Stores the method*/
    private Method method;
    /***Stores the field*/
    private Object field;

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
     * Returns the {@link #originalArguments}.
     * @return      {@link #originalArguments}
     */
    public ArrayList<Object> getOriginalArguments() { return originalArguments; }
    /**
     * Returns the {@link #argumentTypes}.
     * @return      {@link #argumentTypes}
     */
    public ArrayList<Class> getArgumentTypes() { return argumentTypes; }

    /**
     * returns the output of the method.
     * <p>First, the game invokes all arguments that are methods and replaces the argument that the method occupied with its output.
     * Next, the game determines if the field is a Field or a FieldMethod. If it is a FieldMethod, then it invokes the
     * method and sets the fieldvalue to the output of the field method (the fieldvalue is a local variable, that is used in invoking
     * the method). If it is a regular Field, then set fieldvalue to the value that the {@link #field} stores.</p>
     * @return      The output of the method.
     */
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

        if(field != null && fieldvalue == null) { return null; }

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

    /**
     * The method replaces any placeholders ("%ph%[indexOfInput]") with inputArgs and replaces any placeholders in any arguments that are TFMethods.
     *
     * <p>This is only called if it is a child of a ChoiceMethod or a distant child of one.</p>
     * <p>If everything goes well, the method returns the true. The method returns false if an argument is messed up
     * fill the {@link #arguments} array to the correct size (The size of the {@link #argumentTypes}).</p>
     * <p>If an argument (not the inputArgs) is null, the game just places null into the new methodArgs array.</p>
     * <p>If an argument is just a regular argument, the game just places the argument into the new methodArgs array.</p>
     * <p>If an argument is a TFMethod, thez the game invokes putInputInArguments() on it (Replaces placeholders with the inputArgs).
     * If false is returned from this, then return false, because that means there is a problem. If an argument is a placeholder ("%ph%"),
     * then the game gets the number to the right of "%ph%" and calls inputArgs.get(index) and casts the output to the argumentType.
     * When done, {@link #arguments} is set to methodArgs.</p>
     * <p>If the field is a FieldMethod, then invoke putInputInArguments on it (Which just does the exact same thing as explained here).
     * If that returns -1, then return -1, because that means there is a problem (Likely from not enough input arguments).</p>
     * @param inputArgs         The arraylist of the player's input arguments.
     * @return                  whether or not successful.
     */
    public boolean putInputInArguments(ArrayList<String> inputArgs) {
        //Returns -1 if there are not enough inputArgs and indicates there is a problem
        if(arguments != null && argumentTypes != null) {
            ArrayList<Object> methodArgs = new ArrayList<Object>();
            for(int i=0; i<argumentTypes.size(); i++) {
                Object arg = arguments.get(i);
                //If the argument is null, just add null to the array
                //If it was to continue normally, a NullPointerException would be thrown
                if(arg == null) { methodArgs.add(null); continue; }
                //If it is a placeholder then replace it
                if(arg instanceof String && ((String)arg).contains("%ph%") && ((String)arg).length() > 4) {
                    try {
                        int index = Integer.parseInt(((String)arg).substring(4,((String)arg).length()));
                        //Cast the placeholder to the correct type
                        if(argumentTypes.get(i).equals(int.class)) {
                            methodArgs.add(Integer.parseInt(inputArgs.get(index)));
                        } else if(argumentTypes.get(i).equals(String.class)) {
                            methodArgs.add(inputArgs.get(index));
                        } else if(argumentTypes.get(i).equals(boolean.class)) {
                            methodArgs.add(Boolean.parseBoolean(inputArgs.get(index)));
                        } else if(argumentTypes.get(i).equals(double.class)) {
                            methodArgs.add(Double.parseDouble(inputArgs.get(index)));
                        } else if(argumentTypes.get(i).equals(Class.class)) {
                            try { methodArgs.add(Class.forName(inputArgs.get(index))); } catch(ClassNotFoundException e) { return false;  }
                        }
                        continue;
                    } catch (IndexOutOfBoundsException e) { return false; } //There are not enough arguments given
                    catch(Exception e) {} //This is not supposed to be a placeholder, so just continue on
                }
                //If it is a method, then put arguments into the method.
                if(arg.getClass() == TFMethod.class) {
                    if(!((TFMethod)arg).putInputInArguments(inputArgs)) { return false; } //Something has gone wrong
                }
                //Put the arg in the new arraylist
                methodArgs.add(arg);

            }
            arguments = methodArgs;
        }
        //If the field is a method, then put input argumetns in the method
        //If false is recieved, then there was a problem, and must return false
        if(field != null && field.getClass().equals(FieldMethod.class)) {
            if(!((FieldMethod)field).putInputInArguments(inputArgs)) { return false; } // Something has gone wrong
        }
        return true;
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

    public FieldMethod(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Object field) {
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.argumentTypes = argumentTypes;
        this.field = field;
    }
}
