package org.textfighter.enemy;

import org.textfighter.Requirement;

import java.util.ArrayList;

public class Enemy implements Cloneable {

    private String name;
    private int maxhp;
    private int hp;
    private int strength;
    private int difficulty;
    private String output;

    private int levelRequirement;
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();

    public String getName() { return name; }
    public void setName(String n) { name=n; }

    public int getMaxHp() { return maxhp; }
    public void setMaxHp(int a) {
        maxhp=a;
        if(maxhp<1) { maxhp=1; }
    }

    public int getHp() { return hp; }
    public void damaged(int a) {
        hp = hp - a;
        if (hp < 1) { hp = 0; }
    }
    public void healed(int a) {
        hp = hp + a;
        if (hp > maxhp) { hp = 0; }
    }

    public int getStrength() { return strength; }
    public void setStrength(int a) {
        strength = a;
        if(strength < 0) { strength = 0; }
    }

    public int getDifficulty() { return difficulty; }

    public String getOutput() { return output; }

    public int getLevelRequirement() { return levelRequirement; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Enemy(String name, int hp, int str, int levelRequirement, ArrayList<Requirement> requirements) {
        this.name = name;
        this.maxhp = hp;
        this.hp = hp;
        this.strength = str;
        this.levelRequirement = levelRequirement;
        this.difficulty = hp * str * levelRequirement;
        this.output = " - " + name + " \t:|: " + Integer.toString(difficulty) + " \t:|: " + levelRequirement;
        this.requirements = requirements;
    }

    public Enemy() { }

}
