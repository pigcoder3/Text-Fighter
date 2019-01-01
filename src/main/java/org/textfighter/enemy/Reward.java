package org.textfighter.enemy;

import org.textfighter.location.Location;
import org.textfighter.display.Display;
import org.textfighter.Requirement;
import org.textfighter.TextFighter;

import java.util.ArrayList;

import java.lang.reflect.*;

import java.util.Random;

@SuppressWarnings("unchecked")

public class Reward {

    private ArrayList<Object> arguments;
    private Class clazz;
    private Method method;
    private Field field;
    private Class fieldclass;
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();
    //Chance is 1-100
    private int chance;
    private String rewardItem;

    private boolean valid;

    public ArrayList<Object> getArguments() { return arguments; }
    public Class getClazz() { return clazz; }
    public Method getMethod() { return method; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

    public boolean getValid() { return valid; }

    public void invokeMethod() {
        try {
            //Does the random chance thing
            Random random = new Random();
            int number = random.nextInt(100-1)+1;
            if(number > chance) { return; }
            if(field != null ) {
                method.invoke(field, arguments);
                TextFighter.addToOutput(rewardItem);
            } else {
                method.invoke(arguments);
                TextFighter.addToOutput(rewardItem);
            }
        } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); return; }
    }

    public Reward(String method, ArrayList<String> arguments, ArrayList<Class> argumentTypes, String clazz, String field, String fieldclass, ArrayList<Requirement> requirements, int chance, String rewardItem) {
        try { this.clazz = Class.forName(clazz); } catch (ClassNotFoundException e){ Display.displayWarning("This reward has an invalid class. Omitting..."); valid = false; return; }
        if(field != null && fieldclass != null) {
            try { this.fieldclass = Class.forName(fieldclass); } catch (ClassNotFoundException e) { Display.displayPackError("No such class '" + fieldclass + "'. Omitting..."); valid = false; return;}
            try { this.field = this.clazz.getDeclaredField(field); } catch (NoSuchFieldException e) { Display.displayPackError("No such field '" + field + "' in class '" + clazz + "'. Omitting..."); valid = false; return;}
        }
        if(argumentTypes != null ) {
            if(this.field != null) {
                try{
                    this.method = this.fieldclass.getMethod(method, argumentTypes.toArray(new Class[argumentTypes.size()]));
                } catch (NoSuchMethodException e){
                    e.printStackTrace();
                    Display.displayWarning("This reward has an invalid method. Omitting...");
                    valid = false;
                    return;
                }
            } else {
                try{
                    this.method = this.clazz.getMethod(method, argumentTypes.toArray(new Class[argumentTypes.size()]));
                } catch (NoSuchMethodException e){
                    Display.displayWarning("This reward has an invalid method. Omitting...");
                    valid = false;
                    return;
                }
            }
        } else {
            if(this.field != null) {
                try{
                    this.method = this.fieldclass.getMethod(method);
                } catch (NoSuchMethodException e){
                    Display.displayWarning("This reward has an invalid method. Omitting...");
                    valid = false;
                    return;
                }
            } else {
                try{
                    this.method = this.clazz.getMethod(method);
                } catch (NoSuchMethodException e){
                    Display.displayWarning("This reward has an invalid method. Omitting...");
                    valid = false;
                    return;
                }
            }
        }
        if(this.method.getParameterTypes().length != argumentTypes.size()) { Display.displayWarning("There is an incorrect number of arguments for a location or enemy's premethod '" + method + "' parameters! Needed: " + this.method.getParameterTypes().length + " Got: " + argumentTypes.size()); valid = false; return;}
        if(arguments != null) {
            Class[] parameterTypes = this.method.getParameterTypes();
            for (int i=0; i<arguments.size(); i++) {
                if(parameterTypes[i].equals(Integer.class)) {
                    this.arguments.add(Integer.parseInt((String)arguments.get(i)));
                }
            }
        }
        this.requirements = requirements;
        this.chance = chance;
        this.rewardItem = rewardItem;
    }

}
