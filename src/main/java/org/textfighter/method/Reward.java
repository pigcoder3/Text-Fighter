package org.textfighter.method;

import org.textfighter.location.Location;
import org.textfighter.display.Display;
import org.textfighter.method.*;
import org.textfighter.TextFighter;

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
                try { fieldvalue = ((Field)field).get(null); } catch (IllegalAccessException e) { e.printStackTrace(); resetArguments();}
            }
        }

        try {
            //Does the random chance thing to detemine if the player gets the reward
            Random random = new Random();
            //"random" number between 1 and 99
            int number = random.nextInt(98)+1;
            if(number > chance) { resetArguments(); return null; }
            if(field != null ) {
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
            return rewardItem;
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace();}
        resetArguments();
        return "";
    }

    //Reset the arguments to the original arguments because arguments that are methods may have changed them
    /**
     * Sets the {@link #arguments} to the {@link #originalArguments}.
     * The method loops through each argument and invokes resetArguments() on each TFMethod.
     * It invokes resetArguments() on the field if it is a FieldMethod.
     */
    public void resetArguments() {
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

    public Reward(Method method, ArrayList<Object> arguments, Object field, ArrayList<Requirement> requirements, int chance, String rewardItem) {
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.field = field;
        this.requirements = requirements;
        this.chance = chance;
        this.rewardItem = rewardItem;
    }
}
