package org.textfighter.method;

import org.textfighter.location.Location;
import org.textfighter.display.Display;
import org.textfighter.method.*;

import java.util.ArrayList;

import java.lang.reflect.*;

@SuppressWarnings("unchecked")

public class TFMethod {

    /**
     * Stores the arguments of the method.
     * <p>Arguments can store ints, doubles, Strings, classes, and other TFMethods</p>
     */
    private ArrayList<Object> arguments;
    /**
     * Stores the classes of the arguments.
     * <p>Each argument type corresponds with an argument by index</p>
     */
    private ArrayList<Class> argumentTypes;
    /**
     * Stores the original arguments of this TFMethod.
     * <p>These are useful because arguments that are methods are replaced with their output in the arguments.</p>
     * <p>This will allow the methods to be used again.</p>
     */
    private ArrayList<Object> originalArguments;
    /**
     * Stores the method of the TFMethod to be invoked.
     */
    private Method method;
    /**
     * Stores the Object that the {@link #method} will be invoked on.
     * </p>Fields can be of the field class or TFMethods that return a value that is then used as the field.</p>
     */
    private Object field;
    /**
     * Stores the requirements of the method (Only used in premethods and postmethods of locations and enemies).
     * </p>These are used to determine if the conditions are right for the method to be invoked.
     */
    private ArrayList<Requirement> requirements;

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
     * Returns the {@link #requirements}.
     * @return      {@link #requirements}
     */
    public ArrayList<Requirement> getRequirements() { return requirements; }

    /**
     * returns the output of the (method)[] - ADD A LINK.
     * <p>First, the game invokes all arguments that are methods and replaces the argument that the method occupied with its output.
     * Next, the game determines if the (field)[] - ADD A LINK is a Field or a TFMethod. If it is a TFMethod, then it invokes the
     * method and sets the fieldvalue to the output of the field method (the fieldvalue is a local variable, that is used in invoking
     * the method). If it is a regular Field, then set fieldvalue to the value that the (field)[] - ADD A LINK stores.</p>
     * @return      The output of the method.
     */
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

    /**
     * The method replaces any placeholders ("%ph%") with inputArgs and replaces any placeholders in any arguments that are TFMethods.
     *
     * <p>This is only called if it is a child of a ChoiceMethod or a distant child of one.</p>
     * <p>If everything goes well, the method returns the inputArgsIndex. The method returns -1 if there were not enough inputArgs to
     * fill the (arguments)[] - ADD A LINK array to the correct size (The size of the (argumentTypes)[] - ADD A LINK array).</p>
     * <p>If an argument (not the inputArgs) is null, the game just places null into the new methodArgs array.</p>
     * <p>If an argument is just a regular argument, the game just places the argument into the new methodArgs array.</p>
     * <p>If an argument is a TFMethod, thez the game invokes putInputInArguments() on it (Replaces placeholders with the inputArgs).
     * If a -1 is returned from this, then return a -1, because that means there is a problem. If an argument is a placeholder ("%ph%"),
     * then the game does inputArgs.get(inputArgsIndex), casts the output to the (argumentType), and increases inputArgsIndex by one.
     * When done, (arguments)[] - ADD A LINK is set to methodArgs.</p>
     * <p>If the field is a FieldMethod, then invoke putInputInArguments on it (Which just does the exact same thing as explained here). 
     * If that returns -1, then return -1, because that means there is a problem (Likely from not enough input arguments).</p>
     */
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

    public TFMethod(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Object field, ArrayList<Requirement> requirements) {
        this.method = method;
        this.arguments = arguments;
        this.argumentTypes = argumentTypes;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.field = field;
        this.requirements = requirements;
    }
}
