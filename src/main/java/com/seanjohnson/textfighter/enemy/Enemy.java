package com.seanjohnson.textfighter.enemy;

import com.seanjohnson.textfighter.enemy.*;
import com.seanjohnson.textfighter.*;
import com.seanjohnson.textfighter.method.*;

import java.util.ArrayList;
import java.util.Random;

public class Enemy implements Cloneable {

    /**
     * Stores the default name for enemies.
     * <p>Set to "enemy".</p>
     */
    public static String defaultName = "enemy";
    /**
     * Stores the default description for enemies.
     * <p>Set to "An enemy".</p>
     */
    public static String defaultDescription = "An enemy";
    /**
     * Stores the default health for enemies.
     * <p>Set to 50.</p>
     */
    public static int defaultHp = 50;
    /**
     * Stores the default max health for enemies.
     * <p>Set to 50.</p>
     */
    public static int defaultMaxhp = 50;
    /**
     * Stores the default strength for enemies.
     * <p>Set to 5.</p>
     */
    public static int defaultStrength = 5;
    /**
     * Stores the default level requirement for enemies.
     * <p>Set to 1.</p>
     */
    public static int defaultLevelRequirement = 1;
    /**
     * Stores the default turns with invincibility left for enemies.
     * <p>Set to 0.</p>
     */
    public static int defaultTurnsWithInvincibiltyLeft = 0;

    /**
     * Stores the name of this enemy.
     * <p>Set to {@link #defaultName}.</p>
     */
    private String name = defaultName;
    /**
     * Stores the description of this enemy.
     * <p>Set to {@link #defaultDescription}.</p>
     */
    private String description = defaultDescription;
    /**
     * Stores the max health of this enemy.
     * <p>Set to {@link #defaultMaxhp}.</p>
     */
    private int maxhp = defaultMaxhp;
    /**
     * Stores the health of this enemy.
     * <p>Set to {@link #defaultHp}.</p>
     */
    private int hp = defaultHp;
    /**
     * Stores the original health from at the start of a fight.
     */
    private int originalHp = defaultHp;
    /**
     * Stores the strength of this enemy.
     * <p>Set to {@link #defaultStrength}.</p>
     */
    private int strength = defaultStrength;

    /**
     * Stores the turns left with invincibility of this enemy.
     * <p>Set to {@link #defaultTurnsWithInvincibiltyLeft}.</p>
     */
    private int turnsWithInvincibilityLeft = defaultTurnsWithInvincibiltyLeft;

    /**
     * Stores the level required to fight this enemy.
     * <p>Set to {@link #defaultLevelRequirement}.</p>
     */
    private int levelRequirement = defaultLevelRequirement;
    /**
     * Stores the difficulty of this enemy.
     * <p>Difficulty is determined with: Math.round({@link #hp} * {@link #strength} * {@link #levelRequirement} / 100)</p>
     */
    private int difficulty = Math.round(hp * strength * levelRequirement / 100);
    
    /**
     * Stores the requirements to fight this enemy.
     * <p>Set to an empty ArrayList of Requirements.</p>
     */
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();

    /**
     * Stores all actions of this enemy.
     * <p>Set to an empty ArrayList of EnemyActions.</p>
     */
    private ArrayList<EnemyAction> allActions = new ArrayList<EnemyAction>();
    /**
     * Stores actions of this enemy that meet their own requirements.
     * <p>Set to an empty ArrayList of EnemyActions.</p>
     */
    private ArrayList<EnemyAction> possibleActions = new ArrayList<EnemyAction>();

    /**
     * Stores all postmethods (methods that are invoked when the enemy dies) of this enemy.
     * <p>Set to an empty ArrayList of TFMethods.</p>
     */
    private ArrayList<TFMethod> allPostmethods = new ArrayList<TFMethod>();
    /**
     * Stores all postmethods (methods that are invoked when the enemy dies) of this enemy that meet their own requirements.
     * <p>Set to an empty ArrayList of TFMethods.</p>
     */
    private ArrayList<TFMethod> possiblePostmethods = new ArrayList<TFMethod>();

    /**
     * Stores all postmethods (methods that are invoked when the enemy is just starting to be fought) of this enemy.
     * <p>Set to an empty ArrayList of TFMethods.</p>
     */
    private ArrayList<TFMethod> allPremethods = new ArrayList<TFMethod>();
    /**
     * Stores all premethods (methods that are invoked when the enemy is just starting to be fought) of this enemy that meet their own requirements.
     * <p>Set to an empty ArrayList of TFMethods.</p>
     */
    private ArrayList<TFMethod> possiblePremethods = new ArrayList<TFMethod>();

    /**
     * Stores all rewards (methods that are invoked when the enemy dies) of this enemy.
     * <p>Set to an empty ArrayList of Rewards.</p>
     */
    private ArrayList<Reward> allRewardMethods = new ArrayList<Reward>();
    /**
     * Stores all rewards (methods that are invoked when the enemy dies) of this enemy that meet their own requirements.
     * <p>Set to an empty ArrayList of Rewards.</p>
     */
    private ArrayList<Reward> possibleRewardMethods = new ArrayList<Reward>();

    /***Stores whether or not this enemy is a final boss*/
    private boolean finalBoss;

    /**
     * Stores the custom variables for this enemy.
     * <p>Set to an empty ArrayList of CustomVariables.</p>
     */
    private ArrayList<CustomVariable> customVariables = new ArrayList<CustomVariable>();

    /**
     * Returns the {@link #customVariables}.
     * @return      {@link #customVariables}
     */
    public ArrayList<CustomVariable> getCustomVariables() { return customVariables; }

    /**
     * Returns the value of the custom variable in {@link #customVariables} with the name given.
     * <p>If name is null, or no variable with that name is found, return null.</p>
     * @param name  The name of the custom variable.
     * @return      The value of the customvariable with the name given. If no name given or one not found, return null.
     */
    public Object getCustomVariableFromName(String name) {
        if(name == null) { return null; }
        for(CustomVariable cv : customVariables) {
            if(cv.getName().equals(name)) { return cv.getValue(); }
        }
        return null;
    }
    /**
     * Sets the value of the variable in {@link #customVariables} with the name given.
     * <p>If no name given, then dont do anything.</p>
     * @param name      The name of the custom variable.
     * @param value     The value that the custom variable will be set to.
     */
    public void setCustomVariableByName(String name, Object value) {
        if(name == null) { return; }
        for(CustomVariable cv : customVariables) {
            if(cv.getName().equals(name)) {
                cv.setValue(value);
                return;
            }
        }
    }

    /**
     * Returns the output of the basic values.
     * <p>Returns {@link #name}, {@link #description}, {@link #difficulty},
     * and if the enemy is a {@link #finalBoss}, then append "  !FINALBOSS!" to the end.</p>
     * @return      The output of basic values.
     */
    public String getSimpleOutput() {
        String s = name + " -\n" +
                "  description:  " + description + "\n" +
                "  difficulty:  " + difficulty + "\n";
        if(finalBoss) {s=s+"  !FINALBOSS!\n";}
        return s;
    }

    /**
     * Returns the output of all values.
     * <p>Returns {@link #name}, {@link #description}, {@link #difficulty}, {@link #hp}, {@link #maxhp}, {@link #strength}, {@link #turnsWithInvincibilityLeft} ny {@link #customVariables} with inOutput set to true,
     * and if the enemy is a {@link #finalBoss}, then append "  !FINALBOSS!" to the end.</p>
     * @return      The output of all values
     */
    public String getOutput() {
        String s = name + " -\n" +
                "  description:  " + description + "\n" +
                "  difficulty:  " + difficulty + "\n" +
                "  health:  " + hp + "\n" +
                "  maximum health:  " + maxhp + "\n" +
                "  strength:  " + strength + "\n" +
                "  turns with invincibility left:  " + turnsWithInvincibilityLeft + "\n";
        for(CustomVariable cv : customVariables) {
            if(cv.getInOutput()) { s=s+"  " + cv.getName() + ":  " + cv.getValue().toString() + "\n"; }
        }
        if(finalBoss) {s=s+"  !FINALBOSS!\n";}
        return s;
    }

    //name methods
    /**
     * Returns the {@link #name}.
     * @return      {@link #name}
     */
    public String getName() { return name; }
    /**
     * Sets the value of {@link #name} to the value given.
     * <p>If the value given is null, then don't do anthing.</p>
     * @param n     The new value.
     */
    public void setName(String n) { if(n != null && n.trim() != null) { name=n; } }

    /**
     * Return the {@link #description}.
     * @return      {@link #description}
     */
    public String getDescription() { return description; }

    //hp methods
    /**
     * Returns the {@link #maxhp}.
     * @return      {@link #maxhp}
     */
    public int getMaxHp() { return maxhp; }
    /**
     * Sets the value of {@link #maxhp} to the value given.
     * <p>If the value given is less than 1, Then set it to 1. If {@link #hp} is more than {@link #maxhp}, then set the health to maxhp.</p>
     * @param a     The new value.
     */
    public void setMaxHp(int a) {
        if(a < 1) { a = 1; }
        maxhp=a;
        if(hp > maxhp) { hp = maxhp; }
    }
    /**
     * Returns the {@link #hp}.
     * @return      {@link #hp}
     */
    public int getHp() { return hp; }
    /**
     * Returns the {@link #originalHp}
     * @return      {@link #originalHp}
     */
    public int getOriginalHp() { return originalHp; }
    /**
     * Sets the {@link #originalHp}
     * @return      {@link #originalHp}
     */
    public void setOriginalHp(int hp) {
        if(hp <= maxhp) {
            originalHp = hp;
        } else {
            originalHp = maxhp;
        }
    }

    /**
     * Decreases the value of {@link #hp} by the value given assuming the enemy can be hurt this turn.
     * <p>If the new value given is less than 0, Then set it to 0. If greater than {@link #maxhp}, then set it to the maxhp.</p>
     * @param a             The amount damaged.
     * @param customString  The custom string to be printed out with the default output.
     */
    public void damaged(int a, String customString) {
        if(turnsWithInvincibilityLeft < 1) {
            //If the enemy cannot be hurt, then dont damage it
            hp-=a;
            if(hp < 1) { hp = 0; }
            if(hp > maxhp) { hp = maxhp; }
            if(customString != null) { TextFighter.addToOutput(customString); }
            TextFighter.addToOutput("Your enemy has been hurt for " + a + " hp.");
        } else {
            TextFighter.addToOutput("Your enemy cannot be hurt this turn!");
        }
    }
    /**
     * Increases the value of {@link #hp} by the value given.
     * <p>If the new value given is less than 0, Then set it to 0. If greater than {@link #maxhp}, then set it to the maxhp.</p>
     * @param a     The amount healed.
     */
    public void heal(int a) {
        hp = hp + a;
        if(hp > maxhp) { hp = maxhp; } //Make sure the health doesnt go above the maxhealth
        if(hp < 0) { hp = 0; }
        TextFighter.addToOutput("Your enemy has been healed for " + a + " hp.");
    }
    //strength methods
    /**
     * Returns the {@link #strength}.
     * @return      {@link #strength}
     */
    public int getStrength() { return strength; }
    /**
     * Sets the value of {@link #strength} to the value given.
     * <p>If the value given is less than 0, then set it to 0.</p>
     * @param a     The new value.
     */
    public void setStrength(int a) {
        if(a < 0) { a = 0; }
        strength = a;
    }

    //difficulty method
    /**
     * Calculates and returns the {@link #difficulty}.
     * @return      {@link #difficulty}
     */
    public int getDifficulty() { difficulty = Math.round(originalHp * strength * levelRequirement / 100); return difficulty; }

    //turnsWithInvincibilityLeft methods
    /**
     * Returns the {@link #turnsWithInvincibilityLeft}.
     * @return      {@link #turnsWithInvincibilityLeft}
     */
    public int getTurnsWithInvincibilityLeft() { return turnsWithInvincibilityLeft; }

    /**
     * Increases the value of {@link #hp} by the value given.
     * <p>If the new value is less than 0, Then set it to 0.</p>
     * @param a             The amount increased.
     */
    public void increaseTurnsWithInvincibilityLeft(int a) { turnsWithInvincibilityLeft+=a; if(turnsWithInvincibilityLeft < 0) { turnsWithInvincibilityLeft = 0; } }
    /**
     * Decreases the value of {@link #hp} by the value given.
     * <p>If the new value is less than 0, Then set it to 0.</p>
     * @param a             The amount decreased.
     */
    public void decreaseTurnsWithInvincibilityLeft(int a) { turnsWithInvincibilityLeft-=a; if(turnsWithInvincibilityLeft < 0) { turnsWithInvincibilityLeft = 0; } }

    /**
     * Returns the {@link #levelRequirement}.
     * @return      {@link #levelRequirement}
     */
    public int getLevelRequirement() { return levelRequirement; }
    /**
     * Returns the {@link #requirements}.
     * @return      {@link #requirements}
     */
    public ArrayList<Requirement> getRequirements() { return requirements; }

    //possibleAction methods
    /**
     * Returns the {@link #possibleActions} after calling {@link #filterPossibleActions}.
     * @return      {@link #possibleActions}
     */
    public ArrayList<EnemyAction> getPossibleActions() { filterPossibleActions(); return possibleActions; }
    /*** Loops through {@link #allActions} and puts all that meet their requirements into {@link #possibleActions}.*/
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

    /**
     * Performs a random enemy action.
     * @param includeAttack     Whether or not the Random instance should consider the attack action (not given through packs).
     */
    public void performRandomAction(boolean includeAttack) {
        // Does enemy actions
        if((possibleActions != null && possibleActions.size() > 0) || (includeAttack && strength > 0)) {
			if(possibleActions != null && possibleActions.size() > 0) {
				Random random = new Random();
            	int number = 0;
            	if(!includeAttack || strength < 1) { //If the enemy has no attack, then dont allow the attack action to happen
					number = (Integer)(random.nextInt(possibleActions.size()))+1;
            	} else {
					number = (Integer)(random.nextInt(possibleActions.size()*2))+1;
            	}
            	//Perform the action based on number index
            	if(number > possibleActions.size()) { //Attack the player
                	attack(strength, null);
            	} else { //Perform an action other than attack
                	possibleActions.get(number).invokeMethods();
            	}
			} else {
			 	attack(strength, null);
			}
        }
        decreaseTurnsWithInvincibilityLeft(1);
    }

    /**
     * Returns the {@link #finalBoss}.
     * @return      {@link #finalBoss}
     */
    public boolean getIsFinalBoss() { return finalBoss; }

    //posmethod methods
    /**
     * Invokes all the {@link #possiblePostmethods}.
     * <p>First calls {@link #filterPostmethods}, then invokes them.</p>
     */
    public void invokePostmethods() {
        filterPostmethods();
        for(TFMethod pm : possiblePostmethods) {
            pm.invokeMethod();
        }
    }
    /**
     * Loops over all postmethods and adds all postmethods that meet their own requirements to {@link #possiblePostmethods}.
     * <p>Adds valid postmethods to a new ArrayList that the {@link #possiblePostmethods} is set to after.</p>
     */
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
    /**
     * Invokes all the {@link #possiblePremethods}.
     * <p>First calls {@link #filterPremethods}, then invokes them.</p>
     */
    public void invokePremethods() {
        filterPremethods();
        if(possiblePremethods != null) {
            for(TFMethod pm : possiblePremethods) {
                pm.invokeMethod();
            }
        }
    }
    /**
     * Loops over all premethods and adds all premethods that meet their own requirements to {@link #allPremethods}.
     * <p>Adds valid premethods to a new ArrayList that the {@link #possiblePremethods} is set to after.</p>
     */
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
    /**
     * Invokes all the {@link #possibleRewardMethods}.
     * <p>First calls {@link #filterRewardMethods}, then invokes them.</p>
     */
    public void invokeRewardMethods() {
        filterRewardMethods();
        ArrayList<String> rewardStrings = new ArrayList<String>();
        //Give the player the rewards
        if(possibleRewardMethods != null) {
            for(Reward r : possibleRewardMethods) {
                String output = r.invokeMethod();
                System.out.println(output);
                if(output != null) { rewardStrings.add(output); }
            }
        }
        //Print out all of the rewards the player recieved
        if(rewardStrings.size() > 0) {
            TextFighter.addToOutput("Rewards:");
            for(int i=0; i<rewardStrings.size(); i++) {
                String s = rewardStrings.get(i);
                if(i != rewardStrings.size()-1) {
                    s+=","; //If this is the last reward do not put a comma
                }
                TextFighter.addToOutput(s);
            }
        }
    }
    /**
     * Loops over all rewardMethods and adds all that meet their own requirements to {@link #allRewardMethods}.
     * <p>Adds valid rewardMethods to a new ArrayList that the {@link #possibleRewardMethods} is set to after.</p>
     */
    public void filterRewardMethods() {
        System.out.println("Filtering rewards");
        //Filter out any rewards that do not meet the requirements
        possibleRewardMethods.clear();
        if(allRewardMethods != null){
            System.out.println("There are some possible rewards");
            for(Reward r : allRewardMethods) {
                boolean valid = true;
                if(r.getRequirements() != null) {
                    System.out.println("This reward has requirements");
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
    /**
     * Clones this enemy.
     * @return      A clone of this enemy.
     */
    public Object clone() throws CloneNotSupportedException { return super.clone(); }

    /**
     * Attacks the player.
     * <p>Calls the damaged methods of the Player instance. If you want the enemy to attack with the {@link #strength}, then use that as an argument.</p>
     * @param a             The amount of damage to deal.
     * @param customString  The string to be displayed with the default output.
     */
    public void attack(int a, String customString) {
        if(TextFighter.player.getTurnsWithInvincibilityLeft() < 1 && a > 0) { // Make sure the player is not invincible
			TextFighter.player.damaged(a, customString);
        }
    }

    public Enemy(String name, String description, int hp, int str, int levelRequirement, boolean finalBoss, ArrayList<Requirement> requirements, ArrayList<TFMethod> premethods, ArrayList<TFMethod> postMethods, ArrayList<Reward> rewardMethods, ArrayList<EnemyAction> actions, ArrayList<CustomVariable> customVariables) {

        //Sets the variables
        this.name = name;
        this.description = description;
        this.maxhp = hp;
        this.hp = hp;
        this.strength = str;
        this.levelRequirement = levelRequirement;
        this.difficulty = Math.round(hp * str * levelRequirement / 100);
        this.requirements = requirements;
        this.finalBoss = finalBoss;
        this.allActions = actions;
        this.allPremethods = premethods;
        this.allPostmethods = allPostmethods;
        this.allRewardMethods = rewardMethods;
        this.customVariables = customVariables;
    }

    public Enemy(ArrayList<CustomVariable> customVariables) {
        this.customVariables = customVariables;
    }

    public Enemy() {}

}
