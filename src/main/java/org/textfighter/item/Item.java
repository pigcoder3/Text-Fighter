package org.textfighter.item;

import java.lang.reflect.*;

public class Item {

    protected int level;
    protected int experience;
    protected int experienceRequirement;
    protected int baseExperienceRequirement = 50;

    protected String[] methodsForCalculations = {"calculateExperienceRequirement"};

    public int getLevel(){ return level; }
    public void setLevel(int a){ level=a; calculateVariables(); }
    public boolean checkForLevelUp() { if(experience > experienceRequirement) { return true; } else { return false; } }

    public int getExperience(){ return experience; }
    public void setExperience(int a) {
        experience=+a;
        if (!checkForLevelUp()) {
            setLevel(level+1);
            experience = experience - experienceRequirement;
        }
    }

    public int getExperienceRequirement(){ return experienceRequirement; }
    public void setExperienceRequirement(int a){ experienceRequirement=a; }
    public void calculateExperienceRequirement(){ experienceRequirement=(baseExperienceRequirement*level)*(baseExperienceRequirement*level); }

    public void calculateVariables() {
        if(methodsForCalculations.length > 1) {
            for(String m : methodsForCalculations) {
                try{ this.getClass().getMethod(m).invoke(new Object[0]); } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) { e.printStackTrace(); }
            }
        }
    }

    public Item (int level, int experience) {
        this.level = level;
        this.experience = experience;
        calculateVariables();
    }

}
