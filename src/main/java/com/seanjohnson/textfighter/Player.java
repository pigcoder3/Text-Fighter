package com.seanjohnson.textfighter;

import com.seanjohnson.textfighter.location.Location;
import com.seanjohnson.textfighter.*;
import com.seanjohnson.textfighter.display.Display;
import com.seanjohnson.textfighter.item.*;
import com.seanjohnson.textfighter.method.*;

import java.util.*;

public class Player {

    /**
     * Default level.
     * <p>Set to 1.</p>
     */
    public static int defaultLevel = 1;
    /**
     * Default experience.
     * <p>Set to 0.</p>
     */
    public static int defaultExperience = 0;
    /**
     * Default score.
     * <p>Set to 0.</p>
     */
    public static int defaultScore = 0;

    /**
     * Default health.
     * <p>Set to 50.</p>
     */
    public static int defaulthp = 50;
    /**
     * Default maximum health.
     * <p>Set to 50.</p>
     */
    public static int defaultMaxhp = 50;

    /**
     * Default coins.
     * <p>Set to 50.</p>
     */
    public static int defaultCoins = 50;
    /**
     * Default magic points.
     * <p>Set to 0.</p>
     */
    public static int defaultMagic = 0;
    /**
     * Default metalScraps.
     * <p>Set to 0.</p>
     */
    public static int defaultMetalscraps = 0;

    /**
     * Default strength.
     * <p>The strength of your fists.</p>
     * <p>Set to 5.</p>
     */
    public static int defaultStrength = 5;  //Strength of just your fists

    /**
     * Default current weapon string.
     * <p>Set to "fists".</p>
     */
    public static String defaultCurrentWeaponName = "fists";

    /**
     * Default health potions.
     * <p>Set to 0.</p>
     */
    public static int defaultHealthPotions = 0;
    /**
     * Default strength potions.
     * <p>Set to 0.</p>
     */
    public static int defaultStrengthPotions = 0;
    /**
     * Default invincibility potions.
     * <p>Set to 0.</p>
     */
    public static int defaultInvincibilityPotions = 0;

    /**
     * Default turns with strength left.
     * <p>Set to 0.</p>
     */
    public static int defaultTurnsWithStrengthLeft = 0;
    /**
     * Default turns with invincibility left.
     * <p>Set to 0.</p>
     */
    public static int defaultTurnsWithInvincibilityLeft = 0;

    /**
     * Default hit point that health potions give.
     * <p>Set to 30.</p>
     */
    public static int defaultHpHealthPotionsGive = 30;
    /**
     * Default turns strength potions give.
     * <p>Set to 2.</p>
     */
    public static int defaultTurnsStrengthPotionsGive = 2;
    /**
     * Default turns invincibility potions give
     * <p>Set to 2.</p>
     */
    public static int defaultTurnsInvincibilityPotionsGive = 2;
    /**
     * Default total protection.
     * <p>Set to 0.</p>
     */
    public static double defaultTotalProtection = 0;
    /**
     * Default maximum protection.
     * <p>Set to 75.</p>
     */
    public static double defaultMaxProtection = 75;
    /**
     * Default strength potion multiplier
     * <p>Set to 2.</p>
     */
    public static double defaultStrengthPotionMultiplier = 2;

    /**
     * Stores whether or not the player is alive.
     * <p>Set to true.</p>
     */
    private boolean alive = true;
    /***Stores the number of deaths.*/
    private int deaths = 0;
    /***Stores the number of kills.*/
    private int kills = 0;

    /**
     * Stores whether or not the player is in a fight.
     * <p>Set to false.</p>
     */
    private boolean inFight = false;

    /**
     * Stores the total protection.
     * <p>Set to {@link #defaultTotalProtection} and can only go up to 75% protection default.</p>
     */
    private double totalProtection = defaultTotalProtection;

    /**
     * Stores the maximum protection percent the player can have.
     * <p>Set to {@link #defaultMaxProtection}.</p>
     */
    private double maxProtection = defaultMaxProtection;

    /**
     * Stores the equipped weapon.
     * <p>Set to null.</p>
     */
    private Weapon currentWeapon;

    /**
     * Stores the player's level.
     * <p>Set to {@link #defaultLevel}.</p>
     */
    private int level = defaultLevel;
    /**
     * Stores the player's experience.
     * <p>Set to {@link #defaultExperience}.</p>
     */
    private int experience = defaultExperience;
    /**
     * Stores the experience needed to level up.
     * <p>Set to {@link #level}^2 * 200
     */
    private int experienceNeeded = (int)Math.pow(level, 2) * 200;
    /**
     * Stores the player's score.
     * <p>Set to {@link #defaultScore}.</p>
     */
    private int score = defaultScore;

    /**
     * Stores the player's health.
     * <p>Set to {@link #defaulthp}.</p>
     */
    private int hp = defaulthp;
    /**
     * Stores the player's max health.
     * <p>Set to {@link #defaultMaxhp}.</p>
     */
    private int maxhp = defaultMaxhp;

    /**
     * Stores the player's strength.
     * <p>Set to {@link #defaultStrength}.</p>
     */
    private int strength = defaultStrength;

    /**
     * Stores the player's health potion amount.
     * <p>Set to {@link #defaultHealthPotions}.</p>
     */
    private int healthPotions = defaultHealthPotions;
    /**
     * Stores the player's strength potion amount.
     * <p>Set to {@link #defaultStrengthPotions}.</p>
     */
    private int strengthPotions = defaultStrengthPotions;
    /**
     * Stores the player's invincibility potion amount.
     * <p>Set to {@link #defaultInvincibilityPotions}.</p>
     */
    private int invincibilityPotions = defaultInvincibilityPotions;

    /**
     * Stores the player's turns with strength left.
     * <p>Set to {@link #defaultTurnsWithStrengthLeft}.</p>
     */
    private int turnsWithStrengthLeft = defaultTurnsWithStrengthLeft;
    /**
     * Stores the player's turns with invincibility left.
     * <p>Set to {@link #defaultTurnsWithInvincibilityLeft}.</p>
     */
    private int turnsWithInvincibilityLeft = defaultTurnsWithInvincibilityLeft;

    /**
     * Stores the player's coins.
     * <p>Set to {@link #defaultCoins}.</p>
     */
    private int coins = defaultCoins;
    /**
     * Stores the player's magic points.
     * <p>Set to {@link #defaultMagic}.</p>
     */
    private int magic = defaultMagic;
    /**
     * Stores the player's metal scraps.
     * <p>Set to {@link #defaultMetalscraps}.</p>
     */
    private int metalScraps = defaultMetalscraps;

    /**
     * Stores the player's current location.
     * <p>Set to null.</p>
     */
    private Location location;

    /**
     * Stores the player's last location.
     * <p>Set to null.</p>
     */
    private Location lastLocation;

    /**
     * Stores whether or not the game has been beaten.
     * <p>Set to false.</p>
     */
    private boolean gameBeaten = false;

    /**
     * Stores the player's inventory.
     * <p>Set to an empty ArrayList of Item.</p>
     */
    private ArrayList<Item> inventory = new ArrayList<Item>();

    /**
     * Stores the earned achievements.
     * <p>Set to an empty ArrayList of Achievement.</p>
     */
    private ArrayList<Achievement> achievements = new ArrayList<Achievement>();

    /**
     * Stores the custom variables.
     * <p>Set to an empty ArrayList of CustomVariable.</p>
     */
    private ArrayList<CustomVariable> customVariables = new ArrayList<CustomVariable>();

    /**
     * Stores the methods that are invoked when the player dies.
     * <p>Set to an empty ArrayList of TFMethods.</p>
     */
    private ArrayList<IDMethod> allDeathMethods = new ArrayList<IDMethod>();
     /**
      * Stores the death methods that meet their own requirements.
      * <p>Set to an empty ArrayList of TFMethods.</p>
      */
    private ArrayList<IDMethod> possibleDeathMethods = new ArrayList<IDMethod>();

    /**
     * Stores the methods that are invoked when the player levels up.
     * <p>Set to an empty ArrayList of TFMethods.</p>
     */
    private ArrayList<IDMethod> allLevelupMethods = new ArrayList<IDMethod>();
     /**
      * Stores the level up methods that meet their own requirements.
      * <p>Set to an empty ArrayList of TFMethods.</p>
      */
    private ArrayList<IDMethod> possibleLevelupMethods = new ArrayList<IDMethod>();

    /**
     * Returns the {@link #customVariables}.
     * @return      {@link #customVariables}
     */
    public ArrayList<CustomVariable> getCustomVariables() { return customVariables; }

    /**
     * Sets the {@link #customVariables}.
     * @param cv    The new arraylist
     */
    public void setCustomVariables(ArrayList<CustomVariable> cv) {
        if(cv == null) { throw new IllegalArgumentException("new ArrayList cannot be null"); }
        customVariables = cv;
    }

    /**
     * Returns the value of the custom variable in {@link #customVariables} with the name given.
     * <p>If name is null, or no variable with that name is found, return null.</p>
     * @param name  The name of the custom variable.
     * @return      The value of the customvariable with the name given. If no name given or one not found, return null.
     */
    public Object getCustomVariableFromName(String name) {
        if(name == null) { throw new IllegalArgumentException("Name cannot be null"); }
        for(CustomVariable cv : customVariables) {
            if(cv.getName().equals(name)) {
                return cv.getValue();
            }
        }
        throw new IllegalArgumentException("No custom variable with the name '" + name + "'.");
    }
    /**
     * Sets the value of the variable in {@link #customVariables} with the name given.
     * <p>If no name given, then dont do anything.</p>
     * @param name      The name of the custom variable.
     * @param value     The value that the custom variable will be set to.
     */
    public void setCustomVariableByName(String name, Object value) throws IllegalArgumentException {
        if(name == null) { throw new IllegalArgumentException("Name cannot be null"); }
        for(CustomVariable cv : customVariables) {
            if(cv.getName().equals(name)) {
                cv.setValue(value);
                //if(cv.getIsSaved()) {  }
                return;
            }
        }
        throw new IllegalArgumentException("No custom variable with the name '" + name + "'.");
    }

    /**
     * Returns the {@link #allDeathMethods}.
     * @return      {@link #allDeathMethods}.
     */
    public ArrayList<IDMethod> getDeathMethods() { return allDeathMethods; }

    /**
     * Sets the {@link #allDeathMethods}.
     * @param dm    The new arraylist
     */
    public void setAllDeathMethods(ArrayList<IDMethod> dm) {
        if(dm == null) { throw new IllegalArgumentException("new ArrayList cannot be null"); }
        allDeathMethods = dm;
    }

    /**
     * Returns the {@link #possibleDeathMethods}.
     * @return      {@link #possibleDeathMethods}.
     */
    public ArrayList<IDMethod> getPossibleDeathMethods() { return possibleDeathMethods; }

    /**
     * Invokes all the {@link #possibleDeathMethods}.
     * <p>First calls {@link #filterDeathMethods}, then invokes them.</p>
     */
    public void invokeDeathMethods() {
        filterDeathMethods();
        if(possibleDeathMethods != null) {
            for(IDMethod dm : possibleDeathMethods) {
                dm.invokeMethod();
            }
        }
    }

    /**
     * Loops over {@link #allDeathMethods} and adds all death methods that meet their own requirements to {@link #allDeathMethods}.
     * <p>Adds valid premethods to a new ArrayList that the {@link #possibleDeathMethods} is set to after.</p>
     */
    public void filterDeathMethods() {
        //Filter out any death methods that do not meet the requirements
        possibleDeathMethods.clear();
        if(allDeathMethods != null){
            for(IDMethod m : allDeathMethods) {
                boolean valid = true;
                if(m.getMethod().getRequirements() != null) {
                    for(Requirement r : m.getMethod().getRequirements()) {
                        if(!r.invokeMethod()) {
                            valid = false;
                            break;
                        }
                    }
                }
                if(valid) {
                    possibleDeathMethods.add(m);
                }
            }
        }
    }

    /**
     * Returns the {@link #allLevelupMethods}.
     * @return      {@link #allLevelupMethods}.
     */
    public ArrayList<IDMethod> getLevelupMethods() { return allLevelupMethods; }

    /**
     * Sets the {@link #allLevelupMethods}.
     * @param lm    The new arraylist
     */
    public void setAllLevelupMethods(ArrayList<IDMethod> lm) {
        if(lm == null) { throw new IllegalArgumentException("new ArrayList cannot be null"); }
        allLevelupMethods = lm;
    }

    /**
     * Returns the {@link #possibleLevelupMethods}.
     * @return      {@link #possibleLevelupMethods}.
     */
    public ArrayList<IDMethod> getPossibleLevelupMethods() { return possibleLevelupMethods; }

    /**
     * Invokes all the {@link #possibleLevelupMethods}.
     * <p>First calls {@link #filterLevelupMethods}, then invokes them.</p>
     */
    public void invokeLevelupMethods() {
        filterLevelupMethods();
        if(possibleLevelupMethods != null) {
            for(IDMethod m : possibleLevelupMethods) {
                m.invokeMethod();
            }
        }
    }

    /**
     * Loops over {@link #allLevelupMethods} and adds all death methods that meet their own requirements to {@link #allLevelupMethods}.
     * <p>Adds valid premethods to a new ArrayList that the {@link #possibleLevelupMethods} is set to after.</p>
     */
    public void filterLevelupMethods() {
        //Filter out any death methods that do not meet the requirements
        possibleLevelupMethods.clear();
        if(allLevelupMethods != null && allLevelupMethods.size() > 0){
            for(IDMethod m : allLevelupMethods) {
                boolean valid = true;
                if(m.getMethod().getRequirements() != null) {
                    for(Requirement r : m.getMethod().getRequirements()) {
                        if(!r.invokeMethod()) {
                            valid = false;
                            break;
                        }
                    }
                }
                if(valid) {
                    possibleLevelupMethods.add(m);
                }
            }
        }
    }

    /**
     * Attacks the enemy with the {@link #currentWeapon}.
     * <p>Calls the damaged method of the Enemy instance. If you want to just damage the player, then just use the damaged method directly.</p>
     * @param customString  The string to be displayed with the default output. A null customString is allowed, and nothing will be printed.
     */
    public void attack(String customString) {
        int newStrength = strength;
        //Misses
        int number = new Random().nextInt(99)+1;
        if(number < currentWeapon.getMissChance()) { TextFighter.addToOutput("Your attack missed your enemy."); return; }
        //Critical hits
		number = new Random().nextInt(99)+1;
        if(number < currentWeapon.getCritChance()) { newStrength*=1.5; TextFighter.addToOutput("Your attack was a critical hit!"); }
        //Attack
        TextFighter.currentEnemy.damaged(newStrength, customString);
    }

    /**
     * Returns {@link #alive}.
     * @return      {@link #alive}
     */
    public boolean getAlive() { return alive; }
    /**
     * Sets {@link #alive} to the value given.
     * <p>If {@link #alive} is new false, then call the {@link #died} method.</p>
     * @param b     The new value.
     */
    public void setAlive(boolean b) { alive=b; if(!alive){died();}}
    /*** Tells the player that they have dies and removes the save.*/
    public void died() {
        TextFighter.addToOutput("You died!");
        deaths++;
        setHp(maxhp); //Restore the player's health
        invokeDeathMethods();
        //Modmakers decide where to move the player.
    }
    /**
     * Returns {@link #deaths}.
     * @return      {@link #deaths}
     */
    public int getDeaths() { return deaths; }

    /**
     * Returns {@link #kills}.
     * @return      {@link #kills}
     */
    public int getKills() { return kills; }

    /**
     * Increases the {@link #kills}.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The amount to increase by.
     */
    public void increaseKills(int a) { kills+=a; if(kills < 0) { kills = 0; } }

    /**
     * Decreases the {@link #kills}.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The amount to decrease by.
     */
    public void decreaseKills(int a) { kills-=a; if(kills < 0) { kills = 0; } }

    /**
     * Sets {@link #kills} to the value given.
     * <p>If {@link #kills} is less than 0, then set it to 0.</p>
     * @param a     The new value.
     */
    public void setKills(int a) {
        kills = a;
        if(kills < 0) { kills = 0; }
    }

    //inFight methods
    /**
     * Returns {@link #inFight}.
     * @return      {@link #inFight}
     */
    public boolean getInFight() { return inFight; }
    /**
     * Sets {@link #inFight} to the value given.
     * @param b     The new value.
     */
    public void setInFight(boolean b) { inFight=b; if(!b){ TextFighter.actedSinceStartOfFight = false; } }

    //totalProtection methods
    /**
     * Gets the {@link #totalProtection}.
     * @return      {@link #totalProtection}
     */
    public double getTotalProtection() { return totalProtection; } //Increase by one here because that is what happens in the damaged method
    /***
     * Calculates the total protection and sets {@link #totalProtection} to the new amount (Can only go up to 75%).
     * @return      the total protection from the {@link #defaultTotalProtection} + the protection gained from the armors up to 75%
     */
    public double calculateTotalProtection() {
        totalProtection=defaultTotalProtection;
        for(Item i : inventory) {
            if(i instanceof Armor) { totalProtection+=((Armor)i).getProtectionAmount(); }
        }
		// I use 75 (not 75) because I increase the total protection in the damaged method so that it doesn't divide by 0
        if(totalProtection > maxProtection) { totalProtection = maxProtection; }
        return totalProtection;
    }

    //currentWeapon methods
    /**
     * Gets the {@link #currentWeapon}.
     * @return      {@link #currentWeapon}
     */
    public Weapon getCurrentWeapon() { return currentWeapon; }
    /**
     * Gets the {@link #currentWeapon}'s name.
     * @return      {@link #currentWeapon}'s name.
     */
    public String getCurrentWeaponName() { if(currentWeapon != null) { return currentWeapon.getName(); } else { return null; } }
    /**
     * Sets {@link #inFight} to the new weapoon with the name given. If the player is not carrying a weapon with that name (with the exception of fists), then don't do anything.
     * @param name  The new weapon's name.
     */
    public void setCurrentWeapon(String name) {
        if(name == "fists" || name == null) {
            currentWeapon = TextFighter.getWeaponByName("fists");
            TextFighter.addToOutput("Equipped weapon 'fists'");
            calculateStrength();
            return;
        }
        Weapon weapon = (Weapon)getFromInventory(name, "weapon");
        if(weapon == null) { TextFighter.addToOutput("The player has no weapon of name '" + name + "'"); return; }
        else {
            currentWeapon = weapon;
            TextFighter.addToOutput("Equipped weapon '" + name + "'");
            calculateStrength();
            return;
        }
    }
    /**
     * Returns whether or not the equipped weapon has the same name as the name given.
     * @param name      The name of the weapon.
     * @return          Whether or not the weapon is equipped.
     */
    public boolean equippedWeapon(String name) {
        if(currentWeapon == null && (name == null || name.equals("fists"))) { return true;}
        if(currentWeapon != null) {
            if(currentWeapon.getName() != null) {
                return currentWeapon.getName() == name;
            }
        }
        return false;
    }

    //Strength methods
    /***
     * Calculates the strength of the player. Takes the player's weapon damage (If one is equipped). If a strength potion is in effect, multiply by {@link #defaultStrengthPotionMultiplier}.
     * @return      The player's total strength from their weapon damage and the strength potion multiplier
     */
    public int calculateStrength() {
        if(currentWeapon != null) {
            if(turnsWithStrengthLeft > 0) {
                strength = (int)Math.round(currentWeapon.getDamage()*defaultStrengthPotionMultiplier);
            } else {
                strength = currentWeapon.getDamage();
            }
        } else {
            if(turnsWithStrengthLeft > 0) {
                strength = (int)Math.round(defaultStrength*defaultStrengthPotionMultiplier);
            } else {
                strength = defaultStrength;
            }
        }
        return strength;
    }
    /**
     * Gets the {@link #strength}.
     * @return      {@link #strength}
     */
    public int getStrength() { return strength; }

    //healthPotion methods
    /**
     * Increases the {@link #healthPotions} by the value given.
     * <p>If the new value is less than 0, then set it to 0.
     * @param a     The value to increase by.
     */
    public void increaseHealthPotions(int a) { healthPotions+=a; if(healthPotions<0){healthPotions=0;}}
    /**
     * Decreases the {@link #healthPotions} by the value given.
     * <p>If the new value is less than 0, then set it to 0.
     * @param a     The value to decrease by.
     */
    public void decreaseHealthPotions(int a) { healthPotions-=a; if(healthPotions<0){healthPotions=0;} }
    /*** Heals the player and decreases the number of {@link #healthPotions} by one if the player has one.*/
    public void useHealthPotion() { if(healthPotions<1){return;} decreaseHealthPotions(1); heal(defaultHpHealthPotionsGive); }
    /**
     * Sets the {@link #healthPotions}.
     * <p>If the new value is less than 0, then it is set to 0.</p>
     * @param a     The new value;
     */
    public void setHealthPotions(int a) { healthPotions = a; if(healthPotions<0){healthPotions=0;} }
    /**
     * Gets the {@link #healthPotions}.
     * @return      {@link #healthPotions}
     */
    public int getHealthPotions() { return healthPotions;}

    //strengthPotion methods
    /**
     * Increases the {@link #strengthPotions} by the value given.
     * <p>If the new value is less than 0, then set it to 0.
     * @param a     The value to increase by.
     */
    public void increaseStrengthPotions(int a) { strengthPotions+=a; if(strengthPotions<0){strengthPotions=0;} }
    /**
     * Decreases the {@link #strengthPotions} by the value given.
     * <p>If the new value is less than 0, then set it to 0.
     * @param a     The value to decrease by.
     */
    public void decreaseStrengthPotions(int a) { strengthPotions-=a; if(strengthPotions<0){strengthPotions=0;} }
    /*** Sets {@link #turnsWithStrengthLeft} to {@link #defaultTurnsStrengthPotionsGive} and decreases the number of {@link #strengthPotions} by one if the player has one.*/
    public void useStrengthPotion() { if(strengthPotions < 1) {return;} decreaseStrengthPotions(1); turnsWithStrengthLeft = defaultTurnsStrengthPotionsGive; calculateStrength(); }
    /**
     * Sets the {@link #strengthPotions}.
     * <p>If the new value is less than 0, then it is set to 0.</p>
     * @param a     The new value;
     */
    public void setStrengthPotions(int a) { strengthPotions = a; if(strengthPotions<0){strengthPotions=0;} }
    /**
     * Gets the {@link #strengthPotions}.
     * @return      {@link #strengthPotions}
     */
    public int getStrengthPotions() { return strengthPotions; }

    //invincibilityPotion methods
    /**
     * Increases the {@link #invincibilityPotions} by the value given.
     * <p>If the new value is less than 0, then set it to 0.
     * @param a     The value to increase by.
     */
    public void increaseInvincibilityPotions(int a) { invincibilityPotions+=a; if(invincibilityPotions<0){invincibilityPotions=0;} }
    /**
     * Decreases the {@link #invincibilityPotions} by the value given.
     * <p>If the new value is less than 0, then set it to 0.
     * @param a     The value to decrease by.
     */
    public void decreaseInvincibilityPotions(int a) { invincibilityPotions-=a; if(invincibilityPotions<0){invincibilityPotions=0;} }
    /*** Sets {@link #turnsWithInvincibilityLeft} to {@link #defaultTurnsStrengthPotionsGive} and decreases the number of {@link #invincibilityPotions} by one if the player has one.*/
    public void useInvincibilityPotion() { if(invincibilityPotions < 1) {return;} decreaseInvincibilityPotions(1); turnsWithInvincibilityLeft = defaultTurnsStrengthPotionsGive; }
    /**
     * Sets the {@link #invincibilityPotions}.
     * <p>If the new value is less than 0, then it is set to 0.</p>
     * @param a     The new value;
     */
    public void setInvincibilityPotions(int a) { invincibilityPotions=a; if(invincibilityPotions<0){invincibilityPotions=0;} }
    /**
     * Gets the {@link #invincibilityPotions}.
     * @return      {@link #invincibilityPotions}
     */
    public int getInvincibilityPotions() { return invincibilityPotions; }

    //turnsWithInvisibilityLeft methods
    /**
     * Increases the {@link #turnsWithStrengthLeft} by the value given.
     * <p>If the new value is less than 0, then set it to 0.
     * @param a     The value to increase by.
     */
    public void increaseTurnsWithStrengthLeft(int a) { turnsWithStrengthLeft+=a; if(turnsWithStrengthLeft<0){turnsWithStrengthLeft=0;} }
    /**
     * Decreases the {@link #turnsWithStrengthLeft} by the value given.
     * <p>If the new value is less than 0, then set it to 0.
     * @param a     The value to decrease by.
     */
    public void decreaseTurnsWithStrengthLeft(int a) { turnsWithStrengthLeft-=a; if(turnsWithStrengthLeft<0){turnsWithStrengthLeft=0;} }
    /**
     * Gets the {@link #turnsWithStrengthLeft}.
     * @return      {@link #turnsWithStrengthLeft}
     */
    public int getTurnsWithStrengthLeft() {return turnsWithStrengthLeft;}
    /**
     * Sets the {@link #turnsWithStrengthLeft}.
     * <p>If the new value is less than 0, then it is set to 0.</p>
     * @param a     The new value;
     */
    public void setTurnsWithStrengthLeft(int a) { turnsWithStrengthLeft=a; if(turnsWithStrengthLeft<0) {turnsWithStrengthLeft=0;}}

    //turnsWithInvincibilityLeft methods
    /**
     * Increases the {@link #turnsWithInvincibilityLeft} by the value given.
     * <p>If the new value is less than 0, then set it to 0.
     * @param a     The value to increase by.
     */
    public void increaseTurnsWithInvincibilityLeft(int a) { turnsWithInvincibilityLeft+=a; if(turnsWithInvincibilityLeft<0){turnsWithInvincibilityLeft=0;} }
    /**
     * Decreases the {@link #turnsWithInvincibilityLeft} by the value given.
     * <p>If the new value is less than 0, then set it to 0.
     * @param a     The value to decrease by.
     */
    public void decreaseTurnsWithInvincibilityLeft(int a) { turnsWithInvincibilityLeft-=a; if(turnsWithInvincibilityLeft<0){turnsWithInvincibilityLeft=0;} }
    /**
     * Gets the {@link #turnsWithInvincibilityLeft}.
     * @return      {@link #turnsWithInvincibilityLeft}
     */
    public int getTurnsWithInvincibilityLeft() {return turnsWithInvincibilityLeft;}
    /**
     * Sets the {@link #turnsWithStrengthLeft}.
     * <p>If the new value is less than 0, then it is set to 0.</p>
     * @param a     The new value;
     */
    public void setTurnsWithInvincibilityLeft(int a) { turnsWithInvincibilityLeft=a; if(turnsWithInvincibilityLeft<0) {turnsWithInvincibilityLeft=0;} }

    //leveling methods
    /**
     * Returns the {@link #level}.
     * @return      {@link #level}
     */
    public int getLevel() { return level; }
    /**
     * Sets the {@link #level}.
     * @param level {@link #level}
     */
    public void setLevel(int level) {
        this.level = level;
        if(level < 1) { level = 1; }
    }
    /**
     * Increases the {@link #level}.
     * <p>If the new value is less than 1, set it to 1.</p>
     * @param a     The amount to increase by.
     */
    public void increaseLevel(int a) {
        level+=a; if(level<1){level = 1;}
    }
    /**
     * Decreases the {@link #level}.
     * <p>If the new value is less than 1, set it to 1.</p>
     * @param a     The amount to decrease by.
     */
    public void decreaseLevel(int a) { level-=a; if(level<1){level = 1;} }
    /**
     * Returns the {@link #experience}.
     * @return      {@link #experience}
     */
    public int getExperience() { return experience; }
    /**
     * Sets the {@link #experience}.
     * @param exp   The new value
     */
    public void setExperience(int exp) {
        experience = exp;
        if(experience < 0) { experience = 0; }
        checkForLevelUp();
    }
    /**
     * Increases the {@link #experience}.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The amount to increase by.
     */
    public void increaseExperience(int a) { experience+=a; if(experience < 0) { experience = 0; } checkForLevelUp(); }
    /**
     * Decreases the {@link #experience}.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The amount to decrease by.
     */
    public void decreaseExperience(int a) { experience-=a; if(experience < 0) { experience = 0; } checkForLevelUp(); }
    /**
     * Returns {@link #experienceNeeded}.
     * @return      {@link #experienceNeeded}.
     */
    public int getExperienceNeeded() { return experienceNeeded; }
    /**
     * Checks to see if the player can level up, and increases the level if so and sets experience to 0.
     * <p>The player levels up if the experience is greater than level*10+100.</p>
     * @return      Whether or not the player has leveled up.
     */
    public boolean checkForLevelUp() {
        experienceNeeded = (int)Math.pow(level, 2) * 200;
        if(experience >= experienceNeeded) {
            decreaseExperience(experienceNeeded);
            increaseLevel(1);
            TextFighter.addToOutput("You leveled up! You are now level " + level +"!");
            experienceNeeded = (int)Math.pow(level, 2) * 200;
            invokeLevelupMethods();
            return true;
        } else {
            return false;
        }
    }

    //Score methods
    /**
     * Returns the {@link #score}.
     * @return      {@link #score}
     */
    public int getScore() { return score; }
    /**
     * Increases the {@link #score}.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The amount to increase by.
     */
    public void increaseScore(int a) { score+=a; if(score < 0) { score = 0; } }
    /**
     * Decreases the {@link #score}.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The amount to decrease by.
     */
    public void decreaseScore(int a) { score-=a; if(score < 0) { score = 0; } }
    /**
     * Sets {@link #score} to the value given.
     * <p>If {@link #score} is less than 0, then set it to 0.</p>
     * @param a     The new value.
     */
    public void setScore(int a) {
        score = a;
        if(score < 0) { score = 0; }
    }

    //health methods
    /**
     * Returns the {@link #hp}.
     * @return      {@link #hp}
     */
    public int getHp() { return hp; }
    /**
     * Sets the value of {@link #hp} to the value given.
     * <p>If the value given is less than 1, Then set it to 1. If the new health is more than {@link #maxhp}, then set the health to the new maxhp.</p>
     * @param a     The new value.
     */
     public void setHp(int a) {
         hp=a;
         if(hp < 1) { hp = 0; }
         if(hp > maxhp) { hp = maxhp; }
     }
    /**
     * Decreases the {@link #hp} if the player can be hurt this turn.
     * <p>If the new value is less than 0, set it to 0. If greater than {@link #maxhp}, then set it to the maxhp.</p>
     * @param a             The amount to of damage dealt.
     * @param customString  The custom string to be outted next to the default output.
     */
    public void damaged(int a, String customString) {
        if(turnsWithInvincibilityLeft>0) { TextFighter.addToOutput("You have not been hurt because you are invincibile this turn."); return; }
        calculateTotalProtection();
        int damageTaken = a;
        if(totalProtection >= 1) { //We dont want to divide by 0
            damageTaken = (int) Math.round((double)a * (1.0 - (totalProtection / 100.0)));
        }
        hp = hp - damageTaken;

		if(hp < 0) { hp = 0; }
		if(hp > maxhp) { hp = maxhp; }

        for(Item i : inventory) {
            if(i instanceof Armor) {
                ((Armor)i).use(1); //Decrease its durability by 1
            }
        }

        if(customString != null) { TextFighter.addToOutput(customString);}
        TextFighter.addToOutput("You have been hurt for " + damageTaken + " hp.");
    }
    /**
     * Increases the value of {@link #hp} by the value given.
     * <p>If the new value given is less than 0, Then set it to 0. If greater than {@link #maxhp}, then set it to the maxhp.</p>
     * @param a     The amount healed.
     */
    public void heal(int a) {
        if (hp+a > maxhp) { hp = maxhp; } else { hp+=a; }
        if(hp < 0) { hp = 0; }
    }
    /**
     * Returns the {@link #maxhp}.
     * @return      {@link #maxhp}
     */
    public int getMaxHp() { return maxhp; }
    /**
     * Sets the value of {@link #maxhp} to the value given.
     * <p>If the value given is less than 1, Then set it to 1. If the new maxhp is less than health, then set the health to the new maxhp.</p>
     * @param a     The new value.
     */
    public void setMaxHp(int a) {
        maxhp=a;
        if(maxhp < 1) { maxhp = 0; }
        if(hp > maxhp) { hp = maxhp; }
    }

    //Coins methods
    /**
     * Returns the {@link #maxhp}.
     * @return      {@link #maxhp}
     */
    public int getCoins() { return coins; }
    /**
     * Set the {@link #coins} to the value given.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The new value.
     */
    public void setCoins(int a) { coins = a; if(coins < 0) { coins = 0; } }
    /**
     * Spends {@link #coins}. Do not use this for just decreasing coins.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The amount to decrease by.
     */
    public void spendCoins(int a) { if (coins-a >= 0) { coins-=a; } else { TextFighter.addToOutput("You cannot spend " + a + " coins because you do not have enough"); } }
    /**
     * Increases the {@link #coins}.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The amount to increase by.
     */
    public void gainCoins(int a) { if (coins+a >= 0) { coins+=a; } else { coins=0; }  }

    //Magic methods
    /**
     * Returns the {@link #magic}.
     * @return      {@link #magic}
     */
    public int getMagic() { return magic; }
    /**
     * Set the {@link #magic} to the value given.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The new value.
     */
    public void setMagic(int a) { magic = a; if(magic < 0) { magic = 0; } }
    /**
     * Decreases the {@link #magic}.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The amount to decrease by.
     */
    public void spendMagic(int a) {  if (magic-a >= 0) { magic-=a; } else { TextFighter.addToOutput("You cannot spend " + a + " magic because you do not have enough"); } }
    /**
     * Increases the {@link #magic}.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The amount to increase by.
     */
    public void gainMagic(int a) { if(magic+a >=0) { magic+=a; } else { magic=0; }  }

    //Metal scraps methods
    /**
     * Returns the {@link #metalScraps}.
     * @return      {@link #metalScraps}
     */
    public int getMetalScraps() { return metalScraps; }
    /**
     * Set the {@link #metalScraps} to the value given.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The new value.
     */
    public void setMetalScraps(int a) {metalScraps = a; if(metalScraps < 0) { metalScraps = 0; } }
    /**
     * Decreases the {@link #metalScraps}.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The amount to decrease by.
     */
    public void spendMetalScraps(int a) { if(metalScraps-a >=0) { metalScraps-=a; } else { TextFighter.addToOutput("You cannot spend " + a + " metal scraps because you do not have enough"); } }
    /**
     * Increases the {@link #metalScraps}.
     * <p>If the new value is less than 0, set it to 0.</p>
     * @param a     The amount to increase by.
     */
    public void gainMetalScraps (int a) { if(metalScraps+a >=0) { metalScraps+=a; } else { metalScraps=0; }  }

    //Location methods
    /**
     * Returns the {@link #location}.
     * @return      {@link #location}
     */
    public Location getLocation() { return location; }
    /**
     * Sets the value of {@link #location} to the location with the name given.
     * @param loc     The name of the new location.
     */
    public void setLocation(String loc) {
        for(Location l : TextFighter.locations) {
            if(l.getName().equals(loc)) {
                location = l;

            }
        }
    }
    /**
     * Returns the {@link #lastLocation}.
     * @return      {@link #lastLocation}
     */
    public Location getLastLocation() { return lastLocation; }
    /**
     * Sets the value of {@link #lastLocation} to the location with the name given.
     * @param loc     The name of the last location.
     */
    public void setLastLocation(String loc) {
        for(Location l : TextFighter.locations) {
            if(l.getName().equals(loc)) {
                lastLocation = l;
            }
        }
    }

    //gameBeaten methods
    /**
     * Returns the {@link #gameBeaten}.
     * @return      {@link #gameBeaten}
     */
    public boolean getGameBeaten() { return gameBeaten; }
    /**
     * Sets the value of {@link #gameBeaten} to the given value.
     * @param b     The new value.
     */
    public void setGameBeaten(boolean b) { gameBeaten = b; }

    //inventory methods
    /**
     * Returns the {@link #inventory}.
     * @return      {@link #inventory}
     */
    public ArrayList<Item> getInventory() { return inventory; }
    /**
     * Adds an item to the player's inventory from TextFighter's item ArrayList.
     * <p>If either the name or type is null, do nothing.</p>
     * <p>Loops through the TextFighter arrayList of the given type (eg. weapon, tool) and adds a clone of the item found with that name to the inventory.
     * If that item does not exist, it tells the player.</p>
     * @param name      The name of the item.
     * @param type      The type of the item.
     */
    public void addToInventory(String name, String type) {
        if(name.equalsIgnoreCase("fists")) { TextFighter.addToOutput("You cannot add your fists to your inventory"); }
        if(type == null) { return;}
        if(type.equals("weapon")) {
            if(isCarrying(name, "weapon")) { TextFighter.addToOutput("A '" + name + "' of type '" + type + "' is already in your inventory"); return; }
            Weapon item = TextFighter.getWeaponByName(name);
            if(item != null) {
                try { inventory.add((Weapon)item.clone()); } catch(CloneNotSupportedException e) { Display.displayError(Display.exceptionToString(e)); return; }
                TextFighter.addToOutput("A " + name + " was added to your inventory.");
            } else {
                Display.displayError("Unkown weapon '" + name + "'");
            }
        }
        else if(type.equals("armor")) {
            if(isCarrying(name, "armor")) { TextFighter.addToOutput("A '" + name + "' of type '" + type + "' is already in your inventory"); return; }
            Armor item = TextFighter.getArmorByName(name);
            if(item != null) {
                try { inventory.add((Armor)item.clone());  } catch(CloneNotSupportedException e) { Display.displayError(Display.exceptionToString(e)); return; }
                TextFighter.addToOutput("A " + name + " was added to your inventory.");
                calculateTotalProtection(); //Recalculate the total protection because there is more armor
            } else {
                Display.displayError("Unkown armor piece '" + name + "'");
            }
        }
        else if(type.equals("tool")) {
            if(isCarrying(name, "tool")) { TextFighter.addToOutput("A '" + name + "' of type '" + type + "' is already in your inventory"); return; }
            Tool item = TextFighter.getToolByName(name);
            if(item != null) {
                try { inventory.add((Tool)item.clone()); } catch(CloneNotSupportedException e) { Display.displayError(Display.exceptionToString(e)); return; }
                TextFighter.addToOutput("A " + name + " was added to your inventory.");
            } else {
                Display.displayError("Unkown tool '" + name + "'");
            }
        }
        else if(type.equals("specialitem")) {
            if(isCarrying(name, "specialitem")) { TextFighter.addToOutput("A '" + name + "' of type '" + type + "' is already in your inventory"); return; }
            SpecialItem item = TextFighter.getSpecialItemByName(name);
            if(item != null) {
                try { inventory.add((SpecialItem)item.clone());  } catch(CloneNotSupportedException e) { Display.displayError(Display.exceptionToString(e)); return; }
                TextFighter.addToOutput("A " + name + " was added to your inventory.");
            } else {
                Display.displayError("Unkown specialitem '" + name + "'");
            }
        }
    }

    public void removeFromInventory(String name, String type) {
        for(int i=0;i<inventory.size();i++) {
            if(name.equals(inventory.get(i).getName()) && type.equals(inventory.get(i).getItemType())) {
                if(currentWeapon != null && name.equals(currentWeapon.getName())) { setCurrentWeapon(null); } //sets to fists
				inventory.remove(i);
				TextFighter.addToOutput("'" + name + "' has been removed from your inventory");
                return;
            }
        }
		TextFighter.addToOutput("You are not carrying a(n) '" + name + "' of type '" + type + "'");
    }

    /**
     * Returns whether or not the player is carrying an item with the name and type given.
     * <p>If the name or type is null, return false.</p>
     * @param name  The name of the item.
     * @param type  The type of the item.
     * @return      Whether or not the player is carrying an item with the name and type given.
     */
    public boolean isCarrying(String name, String type) {
        if(name == null || type == null) { return false;}
        for(Item i : inventory) {
            if(i.getItemType().equals(type) && i.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Returns an item with the given name and type. If there is none or name or type is null, then return null.
     * @param name  The name of the item.
     * @param type  The type of the item.
     * @return      hether or not the player is carrying an item with the name and type given. If there is none or name or type is null, then return null.
     */
    public Item getFromInventory(String name, String type) {
        if(name.equalsIgnoreCase("fists") && type.equalsIgnoreCase("weapon")) { //Ensure that the player will always have fists in their inventory
            return TextFighter.getWeaponByName("fists");
        }
        if(name == null || type == null) { TextFighter.addToOutput("The method was given invalid input."); return null;}
        for(Item i : inventory) {
            if(i.getName().equals(name) && i.getItemType().equals(type)) {
                return i;
            }
        }
        TextFighter.addToOutput("You are not carrying a(n) '" + name + "' of type '" + type + "'");
        return null;
    }

    //achievement methods
    /**
     * Adds the achievement given to the {@link #achievements} arraylist.
     * @param a     The achievement that is earned.
     */
    public void achievementEarned(Achievement a) {
        if(isAchievementEarned(a.getName())) { return; } //The player already has this achievement tho
        achievements.add(a);
        a.invokeRewardMethods();
        Display.achievementEarned(a.getName());
    }

    /**
     * Returns whether or not the achievement with the given name has been earned.
     * @param achievementName   The name of the achievement to test.
     * @return                  Whether or not the player has earned the achievement with the given name
     */
    public boolean isAchievementEarned(String achievementName) {

        for(Achievement achievement : achievements) {
            if(achievement.getName().equalsIgnoreCase(achievementName)) {
                return true;
            }
        }
        return false; //Gets here if no achievements the player has earned have this name (also includes if the achievement doesnt exist).
    }

    /**
     * Sets the {@link #achievements}.
     * @param ach    The new arraylist
     */
    public void setAchievements(ArrayList<Achievement> ach) {
        if(ach == null) { throw new IllegalArgumentException("new ArrayList cannot be null"); }
        achievements = ach;
    }

    /**
     * Returns {@link #achievements}.
     * @return      {@link #achievements}
     */
    public ArrayList<Achievement> getAchievements() { return achievements; }

    public Player(int deaths, int kills, Location location, int hp, int maxhp, int coins, int magic, int metalScraps, int level, int experience, int score, int healthPotions, int strengthPotions, int invincibilityPotions, int turnsWithStrengthLeft, int turnsWithInvincibilityLeft, Weapon currentWeapon, boolean gameBeaten, ArrayList<Item> inventory, ArrayList<Achievement> achievements, ArrayList<CustomVariable> customVariables, ArrayList<IDMethod> deathMethods, ArrayList<IDMethod> levelupmethods) {
        this.location = location;
        this.deaths = deaths;
        this.kills = kills;
        this.hp = hp;
        this.maxhp = maxhp;
        this.coins = coins;
        this.magic = magic;
        this.metalScraps = metalScraps;
        this.level = level;
        this.experience = experience;
        this.experienceNeeded = (int)Math.pow(level, 2) * 200;
        this.score = score;
        this.healthPotions = healthPotions;
        this.strengthPotions = strengthPotions;
        this.invincibilityPotions = invincibilityPotions;
        this.turnsWithStrengthLeft = turnsWithStrengthLeft;
        this.turnsWithInvincibilityLeft = turnsWithInvincibilityLeft;
        this.currentWeapon = currentWeapon;
        this.gameBeaten = gameBeaten;
        this.inventory = inventory;
        this.achievements = achievements;
        this.customVariables = customVariables;
        this.allDeathMethods = deathMethods;
        this.allLevelupMethods = levelupmethods;
        calculateTotalProtection();
        calculateStrength();
    }

    public Player(Location location, Weapon currentWeapon, ArrayList<CustomVariable> customVariables, ArrayList<IDMethod> deathMethods, ArrayList<IDMethod> levelupmethods) {
        this.location = location;
        this.currentWeapon = currentWeapon;
        this.customVariables = customVariables;
        this.allDeathMethods = deathMethods;
        this.allLevelupMethods = levelupmethods;
        calculateStrength();
    }

    public Player(Location location, ArrayList<CustomVariable> customVariables, ArrayList<IDMethod> deathMethods, ArrayList<IDMethod> levelupmethods) {
        this.location = location;
        this.customVariables = customVariables;
        this.allDeathMethods = deathMethods;
        this.allLevelupMethods = levelupmethods;
    }

    public Player() { }
}
