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

    public static int defaultChance = 100;

    private Class clazz;
    private Method method;
    private Field field;
    private Class fieldclass;
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();
    private ArrayList<Object> arguments;
    private ArrayList<Object> originalArguments;
    //Chance is 1-100
    private int chance = defaultChance;
    private String rewardItem;

    private boolean valid;

    public ArrayList<Object> getArguments() { return arguments; }
    public Class getClazz() { return clazz; }
    public Method getMethod() { return method; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

    public boolean getValid() { return valid; }

    public String invokeMethod() {
        //Invokes all the arguments that are methods
        if(arguments != null) {
            for(int i=0; i<arguments.size(); i++) {
                if(arguments.get(i) != null && arguments.get(i).getClass().equals(TFMethod.class)) {
                    arguments.set(i,((TFMethod)(arguments.get(i))).invokeMethod());
                }
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
                    method.invoke(field.get(null), arguments.toArray());
                } else {
                    method.invoke(field.get(null));
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
        } catch (IllegalAccessException | InvocationTargetException e) { resetArguments(); e.printStackTrace(); return null; }
    }

public void resetArguments() { arguments = originalArguments; }

    public Reward(Method method, ArrayList<Object> arguments, Class clazz, Field field, Class fieldclass, ArrayList<Requirement> requirements, int chance, String rewardItem) {
        this.method = method;
        this.arguments = arguments;
        if(arguments != null) { this.originalArguments = new ArrayList<Object>(arguments);}
        this.clazz = clazz;
        this.field = field;
        this.fieldclass = fieldclass;
        this.requirements = requirements;
        this.chance = chance;
        this.rewardItem = rewardItem;
    }
}
