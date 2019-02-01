package org.textfighter;

import org.textfighter.item.*;
import org.textfighter.location.Location;
import org.textfighter.*;
import org.textfighter.display.Display;
import org.textfighter.item.SpecialItem;
import org.textfighter.method.*;

import java.lang.reflect.*;

import java.util.*;

public class Player {

    static int defaultLevel = 1;
    static int defaultExperience = 0;
    static int defaultScore = 0;

    static int defaulthp = 50;
    static int defaultMaxhp = 50;

    static int defaultCoins = 50;
    static int defaultMagic = 0;
    static int defaultMetalscraps = 0;

    static int defaultStrength = 5;  //Strength of just your fists

    static String defaultCurrentWeaponString = "fists";

    static int defaultHealthPotions = 0;
    static int defaultStrengthPotions = 0;
    static int defaultInvincibilityPotions = 0;

    static int defaultTurnsWithStrengthLeft = 0;
    static int defaultTurnsWithInvincibilityLeft = 0;

    static int hpHealthPotionsGive = 30;
    static int turnsStrengthPotionsGive = 2;
    static int turnsInvincibilityPotionsGive = 2;

    private boolean alive = true;
    private boolean inFight = false;

    private double totalProtection = 1;

    private Weapon currentWeapon;

    private int level = defaultLevel;
    private int experience = defaultExperience;
    private int score = defaultScore;

    private int hp = defaulthp;
    private int maxhp = defaulthp;

    private int strength = defaultStrength;

    private int healthPotions = defaultHealthPotions;
    private int strengthPotions = defaultStrengthPotions;
    private int invincibilityPotions = defaultInvincibilityPotions;

    private int turnsWithStrengthLeft = defaultTurnsWithStrengthLeft;
    private int turnsWithInvincibilityLeft = defaultTurnsWithInvincibilityLeft;

    private int coins = defaultCoins;
    private int magic = defaultMagic;
    private int metalScraps = defaultMetalscraps;

    private Location location;

    private boolean gameBeaten = false;

    private boolean canBeHurtThisTurn = true;

    private ArrayList<Item> inventory = new ArrayList<Item>();

    private ArrayList<Achievement> achievements = new ArrayList<Achievement>();

    private ArrayList<CustomVariable> customVariables = new ArrayList<CustomVariable>();

    public ArrayList<CustomVariable> getCustomVariables() { return customVariables; }

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

    public void attack(String customString) {
        int newStrength = strength;
        if(currentWeapon == null) { TextFighter.addToOutput("You do not have a weapon, so you attack with your fists."); }
        else {
            //Deals with critical hits and misses
            Random random = new Random();
            int number = random.nextInt(99)+1;
            if(number > currentWeapon.getMissChance()) { TextFighter.addToOutput("Your attack missed your enemy."); return; }
            random = new Random();
            number = random.nextInt(99)+1;
            if(number > currentWeapon.getCritChance()) { newStrength*=1.5; TextFighter.addToOutput("Your attack was a critical hit!"); }
        }
        if(turnsWithStrengthLeft>0) {
            TextFighter.currentEnemy.damaged(newStrength*2, customString);
        } else {
            TextFighter.currentEnemy.damaged(newStrength, customString);
        }
    }

    //Stuff that deals with the player dying
    public boolean getAlive() { return alive; }
    public void setAlive(boolean b) { alive=b; if(!alive){died();}}
    public void died() {
        TextFighter.addToOutput("You died!");
        TextFighter.removeSave(TextFighter.currentSaveFile.getName());
        TextFighter.needsSaving = false;
    }

    //inFight methods
    public boolean getInFight() { return inFight; }
    public void setInFight(boolean b) { inFight=b; TextFighter.needsSaving=true;}

    //totalProtection methods
    public double getTotalProtection() { return totalProtection; }
    public void calculateTotalProtection() {
        totalProtection=1;
        for(Item i : inventory) {
            if(i.getClass().getSuperclass().equals(Armor.class)) { totalProtection+=((Armor)i).getProtectionAmount(); }
        }
    }

    //currentWeapon methods
    public Weapon getCurrentWeapon() { return currentWeapon; }
    public String getCurrentWeaponString() { if(currentWeapon != null) { return currentWeapon.getName(); } else { return null; } }
    public void setCurrentWeapon(String name) {
        if(name == null) { name = defaultCurrentWeaponString; }
        Weapon weapon = TextFighter.getWeaponByName(name);
        if(weapon == null) { return; }
        if(name != "fists" && !isCarrying(name, "weapon")) { TextFighter.addToOutput("You do not have weapon '" + name + "'"); return; }
        else {
            try { currentWeapon = (Weapon)weapon.clone();  } catch(CloneNotSupportedException e) { e.printStackTrace(); }
            TextFighter.addToOutput("Equiped weapon '" + name + "'");
            calculateStrength();
            return;
        }
    }

    //Strength methods
    public void calculateStrength() {
        if(currentWeapon != null) {
            strength = currentWeapon.getDamage();
        } else {
            strength = defaultStrength;
        }
    }
    public int getStrength() { return strength; }

    //healthPotion methods
    public void increaseHealthPotions(int a) { healthPotions+=a; TextFighter.needsSaving=true;}
    public void decreaseHealthPotions(int a) { healthPotions-=a; if(healthPotions<0){healthPotions=0;} TextFighter.needsSaving=true;}
    public void useHealthPotion() { if(healthPotions<1){return;} decreaseHealthPotions(1); heal(hpHealthPotionsGive); TextFighter.needsSaving=true;}
    public void setHealthPotions(int a) { healthPotions = a; if(healthPotions<1){healthPotions=0;} TextFighter.needsSaving=true;}
    public int getHealthPotions() { return healthPotions;}

    //strengthPotion methods
    public void increaseStrengthPotions(int a) { strengthPotions+=a; TextFighter.needsSaving=true;}
    public void decreaseStrengthPotions(int a) { strengthPotions-=a; if(strengthPotions<0){strengthPotions=0;} TextFighter.needsSaving=true;}
    public void useStrengthPotion() { if(turnsWithStrengthLeft > 0 || strengthPotions < 1) {return;} decreaseStrengthPotions(1); turnsWithStrengthLeft = turnsStrengthPotionsGive; TextFighter.needsSaving=true;}
    public void setStrengthPotions(int a) { strengthPotions = a; if(strengthPotions<1){strengthPotions=0;} TextFighter.needsSaving=true;}
    public int getStrengthPotions() { return strengthPotions; }

    //invincibilityPotion methods
    public void increaseInvincibilityPotions(int a) { invincibilityPotions+=a; TextFighter.needsSaving=true;}
    public void decreaseInvincibilityPotions(int a) { invincibilityPotions-=a; if(invincibilityPotions<0){invincibilityPotions=0;} TextFighter.needsSaving=true;}
    public void useInvincibilityPotion() { if(turnsWithInvincibilityLeft > 0 || invincibilityPotions < 1) {return;} decreaseInvincibilityPotions(1); turnsWithInvincibilityLeft = turnsInvincibilityPotionsGive; TextFighter.needsSaving=true;}
    public void setInvincibilityPotions(int a) { invincibilityPotions=a; if(invincibilityPotions<0){invincibilityPotions=0;} TextFighter.needsSaving=true;}
    public int getInvincibilityPotions() { return invincibilityPotions; }

    //turnsWithInvisibilityLeft methods
    public void increaseTurnsWithStrengthLeft(int a) { turnsWithStrengthLeft+=a; if(turnsWithStrengthLeft<0){turnsWithStrengthLeft=0;} TextFighter.needsSaving=true;}
    public void decreaseTurnsWithStrengthLeft(int a) { turnsWithStrengthLeft-=a; if(turnsWithStrengthLeft<0){turnsWithStrengthLeft=0;} TextFighter.needsSaving=true;}
    public int getTurnsWithStrengthLeft() {return turnsWithStrengthLeft;}
    public void setTurnsWithStrengthLeft(int a) { turnsWithStrengthLeft=a; if(turnsWithStrengthLeft<0) {turnsWithStrengthLeft=0;}}

    //turnsWithInvicibilityLeft methods
    public void increaseTurnsWithInvincibilityLeft(int a) { turnsWithInvincibilityLeft+=a; if(turnsWithInvincibilityLeft<0){turnsWithInvincibilityLeft=0;} TextFighter.needsSaving=true;}
    public void decreaseTurnsWithInvincibilityLeft(int a) { turnsWithInvincibilityLeft-=a; if(turnsWithInvincibilityLeft<0){turnsWithInvincibilityLeft=0;} TextFighter.needsSaving=true;}
    public int getTurnsWithInvincibilityLeft() {return turnsWithInvincibilityLeft;}
    public void setTurnsWithInvincibilityLeft(int a) { turnsWithInvincibilityLeft=a; if(turnsWithInvincibilityLeft<0) {turnsWithInvincibilityLeft=0;} TextFighter.needsSaving=true;}

    //leveling methods
    public int getLevel() { return level; }
    public void increaseLevel(int a) { level+=a; TextFighter.needsSaving=true;}
    public void decreaseLevel(int a) { level-=a; TextFighter.needsSaving=true;}
    public int getExperience() { return experience; }
    public void increaseExperience(int a) { experience+=a; checkForLevelUp(); TextFighter.needsSaving=true;}
    public void decreaseExperience(int a) { experience-=a; if(experience < 0) { experience = 0; } TextFighter.needsSaving=true;}
    public boolean checkForLevelUp() {
        if(experience > level*10+100) {
            experience = experience - level*10+100;
            increaseLevel(1);
            TextFighter.addToOutput("You leveled up! You are now level " + level +"!");
            return true;
        } else {
            return false;
        }
    }

    //Score methods
    public int getScore() { return score; }
    public void increaseScore(int a) { score+=a; TextFighter.needsSaving=true;}
    public void decreaseScore(int a) { score-=a; TextFighter.needsSaving=true;}

    //health methods
    public int getHp() { return hp; }
    public void damaged(int a, String customString) {
        if(!canBeHurtThisTurn || turnsWithInvincibilityLeft>0) { return; }
        calculateTotalProtection();
        if (hp-(a/totalProtection) < 0) { hp = 0; }
        else { hp-=(a/totalProtection); }
        TextFighter.needsSaving=true;
        if(customString != null) { TextFighter.addToOutput(customString);}
        TextFighter.addToOutput("You have been hurt for " + a + " hp.");
    }
    public void heal(int a) { if (hp+a > maxhp) { hp = maxhp; } else { hp+=a; } TextFighter.needsSaving=true;}
    public int getMaxHp() { return maxhp; }
    public void setMaxHp(int a) { maxhp=a; TextFighter.needsSaving=true; }

    //Coins methods
    public int getCoins() { return coins; }
    public void spendCoins(int a) { if (coins-a >= 0) { coins-=a; } else { coins=0; } TextFighter.needsSaving=true;}
    public void gainCoins(int a) { coins+=a; TextFighter.needsSaving=true; }

    //Magic methods
    public int getMagic() { return magic; }
    public void spendMagic(int a) {  if (magic-a >= 0) { magic-=a; } else { magic=0; } TextFighter.needsSaving=true;}
    public void gainMagic(int a) { magic+=a; TextFighter.needsSaving=true; }

    //Metal scraps methods
    public int getMetalScraps() { return magic; }
    public void spendMetalScraps(int a) { if(metalScraps-a >=0) { metalScraps-=a; } else { metalScraps=0; } TextFighter.needsSaving=true; }
    public void gainMetalScraps (int a) { metalScraps+=a; TextFighter.needsSaving=true; }

    //Location methods
    public Location getLocation() { return location; }
    public void setLocation(String loc) {
        for(Location l : TextFighter.locations) {
            if(l.getName().equals(loc)) {
                location = l;
                TextFighter.needsSaving=true;
            }
        }
    }

    //gameBeaten methods
    public boolean getGameBeaten() { return gameBeaten; }
    public void setGameBeaten(boolean b) { gameBeaten = b; TextFighter.needsSaving=true;}

    //canBeHurtThisTurn methods
    public boolean getCanBeHurtThisTurn() { return canBeHurtThisTurn; }
    public void setCanBeHurtThisTurn(boolean b) { canBeHurtThisTurn = b; }

    //inventory methods
    public ArrayList<Item> getInventory() { return inventory; }
    public void addToInventory(String name, String type) {
        if(name == null || type == null) { return;}
        if(type.equals("weapon")) {
            if(isCarrying(name, "weapon")) { TextFighter.addToOutput("A '" + name + "' of type '" + type + "' is already in your inventory"); return; }
            Weapon item = TextFighter.getWeaponByName(name);
            if(item != null) {
                try { inventory.add((Weapon)item.clone()); } catch(CloneNotSupportedException e) { e.printStackTrace(); }
                TextFighter.addToOutput("A " + name + " was added to your inventory.");
                TextFighter.needsSaving=true;
            }
        }
        else if(type.equals("armor")) {
            if(isCarrying(name, "armor")) { TextFighter.addToOutput("A '" + name + "' of type '" + type + "' is already in your inventory"); return; }
            Armor item = TextFighter.getArmorByName(name);
            if(item != null) {
                try { inventory.add((Armor)item.clone());  } catch(CloneNotSupportedException e) { e.printStackTrace(); }
                TextFighter.addToOutput("A " + name + " was added to your inventory.");
                TextFighter.needsSaving=true;
            }
        }
        else if(type.equals("tool")) {
            if(isCarrying(name, "tool")) { TextFighter.addToOutput("A '" + name + "' of type '" + type + "' is already in your inventory"); return; }
            Tool item = TextFighter.getToolByName(name);
            if(item != null) {
                try { inventory.add((Tool)item.clone()); } catch(CloneNotSupportedException e) { e.printStackTrace(); }
                TextFighter.addToOutput("A " + name + " was added to your inventory.");
                TextFighter.needsSaving=true;
            }
        }
        else if(type.equals("specialitem")) {
            if(isCarrying(name, "specialitem")) { TextFighter.addToOutput("A '" + name + "' of type '" + type + "' is already in your inventory"); return; }
            SpecialItem item = TextFighter.getSpecialItemByName(name);
            if(item != null) {
                try { inventory.add((SpecialItem)item.clone());  } catch(CloneNotSupportedException e) { e.printStackTrace(); }
                TextFighter.addToOutput("A " + name + " was added to your inventory.");
                TextFighter.needsSaving=true;
            }
        }
    }
    public void removeFromInventory(String name, String type) {
        for(int i=0;i<inventory.size();i++) {
            if(name.equals(inventory.get(i).getName()) && type.equals(inventory.get(i).getItemType())) {
                if(inventory.get(i).equals(currentWeapon)) { setCurrentWeapon(null); }
				inventory.remove(i);
				TextFighter.addToOutput("'" + name + "' has been removed from your inventory");
				TextFighter.needsSaving=true;
                return;
            }
        }
		TextFighter.addToOutput("You are not carrying a(n) '" + name + "' of type '" + type + "'");
    }
    public boolean isCarrying(String name, String type) {
        if(name == null || type == null) { return false;}
        for(Item i : inventory) {
            if(i.getItemType().equals(type) && i.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    public Item getFromInventory(String name, String type) {
        if(name == null || type == null) { TextFighter.addToOutput("The method was given invalid input."); return null;}
        for(Item i : inventory) {
            System.out.println(i.getName() + " " + i.getItemType());
            if(i.getName().equals(name) && i.getItemType().equals(type)) {
                return i;
            }
        }
        TextFighter.addToOutput("You are not carrying a(n) '" + name + "' of type + '" + type + "'");
        return null;
    }

    //achievement methods
    public void achievementEarned(Achievement a) {
        achievements.add(a);
        Display.achievementEarned(a.getName());
    }
    public ArrayList<Achievement> getAchievements() { return achievements; }

    public Player(int hp, int maxhp, int coins, int magic, int metalScraps, int level, int experience, int score, int healthPotions, int strengthPotions, int invincibilityPotions, Weapon currentWeapon, boolean gameBeaten, ArrayList<Item> inventory, ArrayList<Achievement> achievements, ArrayList<CustomVariable> customVariables) {
        this.hp = hp;
        this.maxhp = maxhp;
        this.coins = coins;
        this.magic = magic;
        this.metalScraps = metalScraps;
        this.level = level;
        this.experience = experience;
        this.score = score;
        this.healthPotions = healthPotions;
        this.strengthPotions = strengthPotions;
        this.invincibilityPotions = invincibilityPotions;
        this.currentWeapon = currentWeapon;
        this.gameBeaten = gameBeaten;
        this.inventory = inventory;
        this.achievements = achievements;
        this.customVariables = customVariables;
        for(Location l : TextFighter.locations) {
            if(l.getName().equals("saves")) { this.location = l; }
        }
        calculateTotalProtection();
    }

    public Player(Weapon currentWeapon, ArrayList<CustomVariable> customVariables) {
        this.currentWeapon = currentWeapon;
        this.customVariables = customVariables;
    }

    public Player(ArrayList<CustomVariable> customVariables) {
        this.customVariables = customVariables;
    }

    public Player() {}
}
