package org.textfighter.enemy;

import org.textfighter.Requirement;
import org.textfighter.enemy.*;
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

    private ArrayList<EnemyAction> allActions = new ArrayList<EnemyAction>();
    private ArrayList<EnemyAction> possibleActions = new ArrayList<EnemyAction>();

    private ArrayList<Postmethod> allPostMethods = new ArrayList<Postmethod>();
    private ArrayList<Postmethod> possiblePostMethods = new ArrayList<Postmethod>();

    private ArrayList<Reward> allRewardMethods = new ArrayList<Reward>();
    private ArrayList<Reward> possibleRewardMethods = new ArrayList<Reward>();

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

    public ArrayList<EnemyAction> getPossibleActions() { filterPossibleActions(); return possibleActions; }
    public void filterPossibleActions() {
        possibleActions.clear();
        for(EnemyAction ea : allActions) {
            boolean valid = true;
            for(Requirement r : ea.getRequirements()) {
                if(!r.invokeMethod()) {
                    valid = false;
                    break;
                }
            }
            if(valid) {
                possibleActions.add(ea);
            }
        }
    }
    public boolean getIsFinalBoss() { return finalBoss; }

    public void invokePostMethods() {
        filterPostMethods();
        for(Postmethod pm : possiblePostMethods) {
            pm.invokeMethod();
        }
    }

    public void filterPostMethods() {
        possiblePostMethods.clear();
        for(Postmethod pm : allPostMethods) {
            boolean valid = true;
            for(Requirement r : pm.getRequirements()) {
                if(!r.invokeMethod()) {
                    valid = false;
                    break;
                }
            }
            if(valid) {
                possiblePostMethods.add(pm);
            }
        }
    }

    public void invokeRewardMethods() {
        filterRewardMethods();
        for(Reward r : possibleRewardMethods) {
            r.invokeMethod();
        }
    }

    public void filterRewardMethods() {
        possibleRewardMethods.clear();
        for(Reward r : allRewardMethods) {
            boolean valid = true;
            for(Requirement rq : r.getRequirements()) {
                if(!rq.invokeMethod()) {
                    valid = false;
                    break;
                }
            }
            if(valid) {
                possibleRewardMethods.add(r);
            }
        }
    }

    public Object clone() throws CloneNotSupportedException { return super.clone(); }

    public Enemy(String name, int hp, int str, int levelRequirement, ArrayList<Requirement> requirements, boolean finalBoss, ArrayList<Postmethod> postMethods, ArrayList<Reward> rewardMethods, ArrayList<EnemyAction> actions) {
        this.name = name;
        this.maxhp = hp;
        this.hp = hp;
        this.strength = str;
        this.levelRequirement = levelRequirement;
        this.difficulty = Math.round(hp * str * levelRequirement / 100);
        this.output = name + " - " + difficulty;
        if(finalBoss) { output+= " - FINAL BOSS"; }
        //Filters out invalid requirements
        if(requirements != null) {
            for(int i=0; i<requirements.size(); i++) {
                if(!requirements.get(i).getValid()) {
                    this.requirements.add(requirements.get(i));
                }
            }
        }
        this.finalBoss = finalBoss;
    }

    public Enemy() { }

}
