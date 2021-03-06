package com.seanjohnson.textfighter.location;

import com.seanjohnson.textfighter.display.Display;
import com.seanjohnson.textfighter.TextFighter;
import com.seanjohnson.textfighter.method.*;

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
                    Display.displayError("method: " + method);
                    Display.displayError(Display.exceptionToString(e));
                    resetArguments();
				}
            }
            if(fieldvalue == null) { resetArguments(); return false; }
        }

        Display.writeToLogFile("[<-----------------------Start Of Method Log----------------------->]");
        Display.writeToLogFile("[Invoking method] Type: ChoiceMethod");
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


        if(!((arguments != null && argumentTypes != null) && (arguments.size() == argumentTypes.size()) || (argumentTypes == null && arguments == null))) { resetArguments(); return false;}
        try {
            if(fieldvalue != null) {
                if(arguments != null && arguments.size() > 0) {
                    method.invoke(fieldvalue, arguments.toArray());
                } else {
                    method.invoke(fieldvalue);
                }
            } else {
                if(arguments != null && arguments.size() > 0) {
                    method.invoke(null, arguments.toArray());
                } else {
                    method.invoke(null);
                }
            }
            resetArguments();
            Display.writeToLogFile("[<------------------------End Of Method Log------------------------>]");
            return true;
        } catch (IllegalAccessException e) {
            Display.displayError("The pack attempted to access a method that is private.");
            Display.displayError("method: " + method);
            Display.displayError(Display.exceptionToString(e));
            resetArguments();
        } catch (InvocationTargetException e) {
            Display.displayError("An error was thrown inside of the method being invoked.");
            Display.displayError("method: " + method);
            Display.displayError(Display.exceptionToString(e));
            resetArguments();
        } catch (IllegalArgumentException e) {
            Display.displayError("The pack used an argument of the wrong type for the parameters of the method. This shouldn't happen if there is nothing wrong with the base code, tell .");
            Display.displayError("method: " + method);
            Display.displayError(Display.exceptionToString(e));
            resetArguments();
        } //This is impossible to have happen I think (Assuming the base code is not messed up), but I'm not sure
        catch (NullPointerException e) {
            Display.displayError("There is a missing field or fieldclass. Check to make sure one is specified in the pack.");
            Display.displayError("method: " + method);
            Display.displayError(Display.exceptionToString(e));;
            resetArguments();
        } catch (Exception e) {
            Display.displayError("Something happened /shrug.");
            Display.displayError("method: " + method);
            Display.displayError(Display.exceptionToString(e));
            resetArguments();
        }
        resetArguments();
        Display.writeToLogFile("[<------------------------End Of Method Log------------------------>]");
        return false;
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
                            try { methodArgs.add(Class.forName(inputArgs.get(index))); } catch(ClassNotFoundException e) { ; return false;  }
                        }
                        continue;
                    } catch (IndexOutOfBoundsException e) { ; return false; } //There are not enough arguments given
                    catch(Exception e) {;} //This is not supposed to be a placeholder, so just continue on
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
        //If the field is a method, then put input arguments in the method
        //If false is recieved, then there was a problem, and must return false
        if(field != null && field.getClass().equals(FieldMethod.class)) {
            if(!((FieldMethod)field).putInputInArguments(inputArgs)) { return false; } // Something has gone wrong
        }
        //Put arguments in the ChoiceMethod requirements
        //If false is recieved, then there was a problem, and must return false
        if(requirements != null) {
            for(Requirement r : requirements) {
                if(!r.putInputInArguments(inputArgs)) { return false; }
            }
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
        if(field != null && field.getClass().equals(FieldMethod.class)) {
            ((FieldMethod)field).resetArguments();
        }
        if(requirements != null) {
            for(Requirement r : requirements) {
                r.resetArguments();
            }
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
