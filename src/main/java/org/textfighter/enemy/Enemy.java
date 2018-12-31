package org.textfighter.enemy;

import org.textfighter.Requirement;
import org.textfighter.enemy.Reward;
import org.textfighter.Postmethod;

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

    private ArrayList<Postmethod> postMethods = new ArrayList<Postmethod>();
    private ArrayList<Reward> rewardMethods = new ArrayList<Reward>();

    private boolean finalBoss;

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

    public boolean getIsFinalBoss() { return finalBoss; }

    public void invokePostMethods() {
        for(Postmethod pm : postMethods) {
            boolean valid = true;
            for(Requirement r : pm.getRequirements()) {
                if (!r.invokeMethod()) {
                    valid = false;
                    break;
                }
            }
            if(valid) {
                pm.invokeMethod();
            }
        }
    }

    public void invokeRewardMethods() {
        for(Reward pm : rewardMethods) {
            boolean valid = true;
            for(Requirement r : pm.getRequirements()) {
                if (!r.invokeMethod()) {
                    valid = false;
                    break;
                }
            }
            if(valid) {
                pm.invokeMethod();
            }
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Enemy(String name, int hp, int str, int levelRequirement, ArrayList<Requirement> requirements, boolean finalBoss, ArrayList<Postmethod> postMethods, ArrayList<Reward> rewardMethods) {
        this.name = name;
        this.maxhp = hp;
        this.hp = hp;
        this.strength = str;
        this.levelRequirement = levelRequirement;
        this.difficulty = Math.round(hp * str * levelRequirement / 100);
        this.output = name + " - " + difficulty;
        if(finalBoss) { output+= " - FINAL BOSS"; }
        this.requirements = requirements;
        this.finalBoss = finalBoss;
        this.postMethods = postMethods;
        this.rewardMethods = rewardMethods;
    }

    public Enemy() { }

}
