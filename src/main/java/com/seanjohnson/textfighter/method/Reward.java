package com.seanjohnson.textfighter.method;

import com.seanjohnson.textfighter.location.Location;
import com.seanjohnson.textfighter.display.Display;
import com.seanjohnson.textfighter.method.*;
import com.seanjohnson.textfighter.TextFighter;

import java.util.ArrayList;

import java.lang.reflect.*;

import java.util.Random;

@SuppressWarnings("unchecked")

public class Reward {

    /**
     * The default chance for the reward to be given
     * Set to 100 by default (100% chance).
     */
    public static int defaultChance = 100;

    /***The method of this reward*/
    private Method method;
    /**
     * The field of the reward.
     * <p>Can be a Field or a FieldMethod.</p>
     */
    private Object field;
    /**
     * The requirements for the reward to be given
     * <p>Set to an empty ArrayList of Requirements.
     */
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();
    /***The arguments of the {@link #method}*/
    private ArrayList<Object> arguments;
    /**
     * Stores the classes of the {@link #arguments} and corresponds by index.
     * <p>Set to an empty ArrayList of Classes.
     */
    private ArrayList<Class> argumentTypes = new ArrayList<Class>();
    /***The arguments originally given to the reward. Does not change*/
    private ArrayList<Object> originalArguments;
    /**
     * The chance of the reward.
     * Set to {@link #defaultChance}.
     */
    private int chance = defaultChance;
    /***The String outputted when the player earns the reward.*/
    private String rewardItem;

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
     * Invokes the {@link #method} and returns the {@link #rewardItem} (If the reward invocation was successful).
     * First, the game invokes all arguments that are methods and replaces the argument
     * that the method occupied with its output. Next, the game determines if the field
     * is a Field or a TFMethod. If it is a FieldMethod, then it invokes the method and sets
     * the fieldvalue to the output of the field method (the fieldvalue is a local variable,
     * that is used in invoking the method). If it is a regular Field, then set fieldvalue
     * to the value that the field
     * @return      If successful, {@link #rewardItem}. If not, return null.
     */
    public String invokeMethod() {
        //Invokes all the arguments that are methods and sets the argument to its output
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
                try { fieldvalue = ((Field)field).get(null); } catch (IllegalAccessException e) { Display.displayError(Display.exceptionToString(e));; resetArguments();}
            }
            if(field != null && fieldvalue == null) { return ""; }
        }

        Display.writeToLogFile("[<-----------------------Start Of Method Log----------------------->]");
        Display.writeToLogFile("[Invoking method] Type: Reward");
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
            //Does the random chance thing to detemine if the player gets the reward
            Random random = new Random();
            //"random" number between 1 and 99
            int number = random.nextInt(98)+1;
            if(number > chance) { resetArguments(); return null; }
            if(fieldvalue != null ) {
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
            if(rewardItem != null) { Display.writeToLogFile("[RewardItem] " + rewardItem.toString()); }
            Display.writeToLogFile("[<------------------------End Of Method Log------------------------>]");
            return rewardItem;
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
            Display.displayError("Something went wrong, but not sure what it was. /shrug.");
            Display.displayError("method: " + method);
            Display.displayError(Display.exceptionToString(e));
            resetArguments();
        }
        resetArguments();
        Display.writeToLogFile("[<------------------------End Of Method Log------------------------>]");
        return "";
    }

    //Reset the arguments to the original arguments because arguments that are methods may have changed them
    /**
     * Sets the {@link #arguments} to the {@link #originalArguments}.
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
        //If the field is a TFMethod, then reset its arguments
        if(field != null && field.getClass().equals(FieldMethod.class)) {
            ((FieldMethod)field).resetArguments();
        }
        arguments = new ArrayList<>(originalArguments);;
    }

    public Reward(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Object field, ArrayList<Requirement> requirements, int chance, String rewardItem) {
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.argumentTypes = argumentTypes;
        this.field = field;
        this.requirements = requirements;
        this.chance = chance;
        this.rewardItem = rewardItem;
    }
}
