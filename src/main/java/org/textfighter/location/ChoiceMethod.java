package org.textfighter.location;

import org.textfighter.display.Display;
import org.textfighter.TextFighter;
import org.textfighter.method.*;

import java.lang.reflect.*;

import java.util.ArrayList;

@SuppressWarnings("unchecked")

public class ChoiceMethod {

    /***Stores the method*/
    private Method method;
    /**
     * Stores the field
     * <p>Can be a Field or a FieldMethod.</p>
     */
    private Object field;
    /**
     * Stores the arguments.
     * <p>Set to an empty ArrayList of Objects.</p>
     */
    private ArrayList<Object> arguments = new ArrayList<Object>();
    /**
     * Stores the arguments that are originally given to the ChoiceMethod.
     * <p>Set to an empty ArrayList of Objects.</p>
     */
    private ArrayList<Object> originalArguments = new ArrayList<Object>();
    /**
     * Stores the classes of the {@link #arguments} and corresponds by index.
     * <p>Set to an empty ArrayList of Classes.
     */
    private ArrayList<Class> argumentTypes = new ArrayList<Class>();
    /**
     * Stores the requirements of this ChoiceMethod.
     * <p>Set to an empty ArrayList of Requirements.
     */
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();

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
                try {
					fieldvalue = ((Field)field).get(null);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					resetArguments();
				}
            }
        }

        if(field != null && fieldvalue == null) { return false; }

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

    /**
     * resetArguments sets the {@link #arguments} to the {@link #originalArguments}.
     * The method loops through each argument and invokes resetArguments() on each TFMethod.
     * It invokes resetArguments() on the field if it is a FieldMethod.
     */
    public void resetArguments() {
        if(arguments != null) {
            for(Object o : arguments) {
                if(o == null) { continue; }
                //If the argument is a method, then reset arguments
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
