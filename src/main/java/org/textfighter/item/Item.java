package org.textfighter.item;

public class Item {

    protected int level;
    protected int experience;
    protected int experienceRequirement;

    protected String[] typeStrings;

    protected int type;

    public int getLevel(){ return level; }
    public void increaseLevel() { level++; }
    public void decreaseLevel() { level--; }
    public boolean checkForLevelUp() { if(experience > experienceRequirement) { return true; } else { return false; } }

    public int getExperience(){ return experience; }
    public void increaseExperience(int a) {
        experience=+a;
        if (!checkForLevelUp()) {
            increaseLevel();
            experience = experience - experienceRequirement;
        }
    }
    public void decreaseExperience(int a) { if( experience-a < 0) { experience=0; } else { experience=-a; }}

    public int getExperienceRequirement(){ return experienceRequirement; }
    public void setExperienceRequirement(int a){ experienceRequirement=a; }
    public void calculateExperienceRequirement(){
        //calculate it (Not sure about the equation to use yet)
    }

    public String getTypeString(int index) { return typeStrings[index]; }

    public int getType(){ return type; }
    public void setType(int t){ type=t; }

}
