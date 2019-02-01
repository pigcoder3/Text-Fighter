package org.textfighter.enemy;

import org.textfighter.enemy.*;
import org.textfighter.*;
import org.textfighter.method.*;

import java.util.ArrayList;

public class Enemy implements Cloneable {

    public static String defaultName = "enemy";
    public static int defaultHp = 50;
    public static int defaultMaxhp = 50;
    public static int defaultStrength = 5;
    public static int defaultLevelRequirement = 1;
    public static int defaultTurnsWithInvincibiltyLeft = 0;

    private String name = defaultName;
    private int maxhp = defaultHp;
    private int hp = defaultMaxhp;
    private int strength = defaultStrength;
    private int difficulty;
    private String output;

    private int turnsWithInvincibilityLeft = defaultTurnsWithInvincibiltyLeft;

    private int levelRequirement = defaultLevelRequirement;
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

    //Custom variable stuff
    private ArrayList<CustomVariable> customVariables = new ArrayList<CustomVariable>();

    public Object getCustomVariableFromName(String name) {
        if(name == null) { return null; }
        for(CustomVariable cv : customVariables) {
            if(cv.getName().equals(name)) { return cv.getValue(); }
        }
        return null;
    }
    public void setCustomVariableByName(String name, Object value) {
        if(name == null) { return; }
        for(CustomVariable cv : customVariables) {
            if(cv.getName().equals(name)) { cv.setValue(value); return;}
        }
    }

    //name methods
    public String getName() { return name; }
    public void setName(String n) { name=n; }

    //hp methods
    public int getMaxHp() { return maxhp; }
    public void setMaxHp(int a) {
        maxhp=a;
        if(maxhp<1) { maxhp=1; }
    }
    public int getHp() { return hp; }
    public void damaged(int a, String customString) {
        if(canBeHurtThisTurn) {
            //If the enemy cannot be hurt, then dont damage it
            hp-=a;
            if (hp < 1) { hp = 0; }
            if(customString != null) { TextFighter.addToOutput(customString); }
            TextFighter.addToOutput("Your enemy has been hurt for " + a + " hp.");
        } else {
            TextFighter.addToOutput("You enemy cannot be hurt this turn!");
        }
    }
    public void heal(int a) {
        hp = hp + a;
        if (hp > maxhp) { hp = 0; } //Make sure the health doesnt go above the maxhealth
        TextFighter.addToOutput("Your enemy has been healed for " + a + " hp.");
    }

    //strength methods
    public int getStrength() { return strength; }
    public void setStrength(int a) {
        strength = a;
        if(strength < 0) { strength = 0; }
    }

    //difficulty method
    public int getDifficulty() { return difficulty; }

    //turnsWithInvincibilityLeft methods
    public int getTurnsWithInvincibilityLeft() { return turnsWithInvincibilityLeft; }
    public void increaseTurnsWithInvincibilityLeft(int a) { turnsWithInvincibilityLeft=+a; if(turnsWithInvincibilityLeft < 0) { turnsWithInvincibilityLeft = 0; } }
    public void decreaseTurnsWithInvincibilityLeft(int a) { turnsWithInvincibilityLeft=-a; if(turnsWithInvincibilityLeft < 0) { turnsWithInvincibilityLeft = 0; } }

    //output method
    public String getOutput() { return output; }

    //levelRequirement method
    public int getLevelRequirement() { return levelRequirement; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

    //possibleAction methods
    public ArrayList<EnemyAction> getPossibleActions() { filterPossibleActions(); return possibleActions; }
    public void filterPossibleActions() {
        //Filter out all of the actions that dont meet the requiremenets
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

    //finalBoss method
    public boolean getIsFinalBoss() { return finalBoss; }

    //canBeHurtThisTurn method
    public boolean getCanBeHurtThisTurn() { return canBeHurtThisTurn; }
    public void setCanBeHurtThisTurn(boolean b) { canBeHurtThisTurn=b; }

    //posmethod methods
    public void invokePostmethods() {
        filterPostmethods();
        for(TFMethod pm : possiblePostmethods) {
            pm.invokeMethod();
        }
    }
    public void filterPostmethods() {
        //Filter out any postmethods that do not meet the requirements
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

    //premethod methods
    public void invokePremethods() {
        filterPremethods();
        if(possiblePremethods != null) {
            for(TFMethod pm : possiblePremethods) {
                pm.invokeMethod();
            }
        }
    }
    public void filterPremethods() {
        //Filter out any pretmethods that do not meet the requirements
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

    //reward methods
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
        //Filter out any rewards that do not meet the requirements
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

    //Needed so that a new enemy can be cloned from the enemies array
    public Object clone() throws CloneNotSupportedException { return super.clone(); }

    //Attakc the player
    public void attack(String customString) {
        if(TextFighter.player.getCanBeHurtThisTurn()) { // Make sure the player canBeHurtThisTurn
            if(strength > 0) { //If the strength is less than 1, then dont attack
                TextFighter.player.damaged(strength, customString);
            }
        }
    }

    public Enemy(String name, int hp, int str, int levelRequirement, ArrayList<Requirement> requirements, boolean finalBoss, ArrayList<TFMethod> premethods, ArrayList<TFMethod> postMethods, ArrayList<Reward> rewardMethods, ArrayList<EnemyAction> actions, ArrayList<CustomVariable> customVariables) {

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
        this.customVariables = customVariables;
        //Filters out invalid enemy actions
        for(EnemyAction ea : actions) {
            if(ea.getValid()) { allActions.add(ea); }
        }
    }

    public Enemy(ArrayList<CustomVariable> customVariables) {
        this.customVariables = customVariables;
    }

    public Enemy() {}

}
