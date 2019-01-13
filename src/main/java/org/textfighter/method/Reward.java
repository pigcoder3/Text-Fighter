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

    private Class clazz;
    private Method method;
    private Field field;
    private Class fieldclass;
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();
    private ArrayList<Object> arguments;
    //Chance is 1-100
    private int chance;
    private String rewardItem;

    private boolean valid;

    public ArrayList<Object> getArguments() { return arguments; }
    public Class getClazz() { return clazz; }
    public Method getMethod() { return method; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

    public boolean getValid() { return valid; }

    public String invokeMethod() {
        try {
            //Does the random chance thing
            Random random = new Random();
            int number = random.nextInt(100-1)+1;
            if(number > chance) { return null; }
            if(field != null ) {
                if(arguments != null) {
                    method.invoke(field.get(null), arguments);
                } else {
                    method.invoke(field.get(null));
                }
            } else {
                if(arguments != null) {
                    method.invoke(null, arguments);
                } else {
                    method.invoke(null);
                }
            }
            return rewardItem;
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); return null; }
    }

    public Reward(Method method, ArrayList<Object> arguments, Class clazz, Field field, Class fieldclass, ArrayList<Requirement> requirements, int chance, String rewardItem) {
        this.method = method;
        this.arguments = arguments;
        this.clazz = clazz;
        this.field = field;
        this.fieldclass = fieldclass;
        this.requirements = requirements;
        this.chance = chance;
        this.rewardItem = rewardItem;
    }
}
