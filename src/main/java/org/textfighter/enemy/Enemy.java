package org.textfighter.enemy;

import org.textfighter.enemy.*;
import org.textfighter.*;
import org.textfighter.method.*;

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

    private ArrayList<TFMethod> allPostmethods = new ArrayList<TFMethod>();
    private ArrayList<TFMethod> possiblePostmethods = new ArrayList<TFMethod>();

    private ArrayList<TFMethod> allPremethods = new ArrayList<TFMethod>();
    private ArrayList<TFMethod> possiblePremethods = new ArrayList<TFMethod>();

    private ArrayList<Reward> allRewardMethods = new ArrayList<Reward>();
    private ArrayList<Reward> possibleRewardMethods = new ArrayList<Reward>();

    private boolean finalBoss;

    private boolean canBeHurtThisTurn = true;

    public String getName() { return name; }
    public void setName(String n) { name=n; }

    public int getMaxHp() { return maxhp; }
    public void setMaxHp(int a) {
        maxhp=a;
        if(maxhp<1) { maxhp=1; }
    }

    public int getHp() { return hp; }
    public void damaged(int a) {
        if(canBeHurtThisTurn) {
            hp = hp - a;
            if (hp < 1) { hp = 0; }
            TextFighter.addToOutput("Your enemy has been hurt for " + a + " hp.");
        }
    }
    public void healed(int a) {
        hp = hp + a;
        if (hp > maxhp) { hp = 0; }
        TextFighter.addToOutput("Your enemy has been healed for " + a + " hp.");
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

    public boolean getCanBeHurtThisTurn() { return canBeHurtThisTurn; }
    public void setCanBeHurtThisTurn(boolean b) { canBeHurtThisTurn=b; }

    public void invokePostmethods() {
        filterPostmethods();
        for(TFMethod pm : possiblePostmethods) {
            pm.invokeMethod();
        }
    }

    public void filterPostmethods() {
        possiblePostmethods.clear();
        if(allPostmethods != null) {
            for(TFMethod pm : allPostmethods) {
                boolean valid = true;
                if(pm.getRequirements() != null){
                    for(Requirement r : pm.getRequirements()) {
                        if(!r.invokeMethod()) {
                            valid = false;
                            break;
                        }
                    }
                    if(valid) {
                        possiblePostmethods.add(pm);
                    }
                }
            }
        }
    }

    public void invokePremethods() {
        filterPremethods();
        if(possiblePremethods != null) {
            for(TFMethod pm : possiblePremethods) {
                pm.invokeMethod();
            }
        }
    }

    public void filterPremethods() {
        possiblePremethods.clear();
        if(allPremethods != null){
            for(TFMethod pm : allPremethods) {
                boolean valid = true;
                if(pm.getRequirements() != null) {
                    for(Requirement r : pm.getRequirements()) {
                        if(!r.invokeMethod()) {
                            valid = false;
                            break;
                        }
                    }
                    if(valid) {
                        possiblePremethods.add(pm);
                    }
                }
            }
        }
    }

    public void invokeRewardMethods() {
        filterRewardMethods();
        if(possibleRewardMethods != null) {
            for(Reward r : possibleRewardMethods) {
                String output = r.invokeMethod();
                if(output != null) { TextFighter.addToOutput(output); }
            }
        }
    }

    public void filterRewardMethods() {
        possibleRewardMethods.clear();
        if(allRewardMethods != null){
            for(Reward r : allRewardMethods) {
                boolean valid = true;
                if(r.getRequirements() != null) {
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
                else {
                    possibleRewardMethods.add(r);
                }
            }
        }
    }

    public Object clone() throws CloneNotSupportedException { return super.clone(); }

    public void attack() { if(strength > 0) { TextFighter.player.damaged(strength); } }

    public Enemy(String name, int hp, int str, int levelRequirement, ArrayList<Requirement> requirements, boolean finalBoss, ArrayList<TFMethod> premethods, ArrayList<TFMethod> postMethods, ArrayList<Reward> rewardMethods, ArrayList<EnemyAction> actions) {

        //Sets the variables
        this.name = name;
        this.maxhp = hp;
        this.hp = hp;
        this.strength = str;
        this.levelRequirement = levelRequirement;
        this.difficulty = Math.round(hp * str * levelRequirement / 100);
        this.output = name + " - " + difficulty;
        //Puts the final boss tag that tells the user that it is the final boss (Or one of them, there could be multiple)
        if(finalBoss) { output+= " - FINAL BOSS"; }
        this.requirements = requirements;
        this.finalBoss = finalBoss;
        this.allPremethods = premethods;
        this.allPostmethods = allPostmethods;
        this.allRewardMethods = rewardMethods;
        //Filters out invalid enemy actions
        for(EnemyAction ea : actions) {
            if(ea.getValid()) { allActions.add(ea); }
        }
    }

    public Enemy() { }

}
