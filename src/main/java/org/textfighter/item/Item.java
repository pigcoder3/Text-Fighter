package org.textfighter.item;

public class Item {

    protected int level;
    protected int experience;
    protected int experienceRequirement;
    protected int type;

    protected String[] typeStrings;

    public int getLevel(){ return level; }
    public void setLevel(int a){ level=a; }
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
    public void calculateExperienceRequirement(){
        //calculate it (Not sure about the equation to use yet)
    }

    public String getTypeString(int index) { return typeStrings[index]; }

    public int getType(){ return type; }
    public void setType(int t){ type=t; }

}
