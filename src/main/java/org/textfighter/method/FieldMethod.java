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
     * The method replaces any placeholders ("%ph%") with inputArgs and replaces any placeholders in any arguments that are TFMethods.
     *
     * <p>This is only called if it is a child of a ChoiceMethod or a distant child of one.</p>
     * <p>If everything goes well, the method returns the inputArgsIndex. The method returns -1 if there were not enough inputArgs to
     * fill the {@link #arguments} array to the correct size (The size of the {@link #argumentTypes}).</p>
     * <p>If an argument (not the inputArgs) is null, the game just places null into the new methodArgs array.</p>
     * <p>If an argument is just a regular argument, the game just places the argument into the new methodArgs array.</p>
     * <p>If an argument is a TFMethod, thez the game invokes putInputInArguments() on it (Replaces placeholders with the inputArgs).
     * If a -1 is returned from this, then return a -1, because that means there is a problem. If an argument is a placeholder ("%ph%"),
     * then the game does inputArgs.get(inputArgsIndex), casts the output to the (argumentType), and increases inputArgsIndex by one.
     * When done, {@link #arguments} is set to methodArgs.</p>
     * <p>If the field is a FieldMethod, then invoke putInputInArguments on it (Which just does the exact same thing as explained here).
     * If that returns -1, then return -1, because that means there is a problem (Likely from not enough input arguments).</p>
     * @param inputArgs         The arraylist of the player's input arguments.
     * @param inputArgsIndex    The current index of the inputArgs arraylist argument.
     * @return                  The inputArgsIndex after input arguments have been put into the arguments.
     */
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

    /**
     * resetArguments sets the {@link #arguments} to the {@link #originalArguments}.
     * The method loops through each argument and invokes resetArguments() on each TFMethod.
     * It invokes resetArguments() on the field if it is a FieldMethod.
     */
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
