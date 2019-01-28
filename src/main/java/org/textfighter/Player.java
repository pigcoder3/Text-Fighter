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

    private ArrayList<SpecialItem> specialItems = new ArrayList<SpecialItem>();

    private ArrayList<Achievement> achievements = new ArrayList<Achievement>();

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

    public boolean getAlive() { return alive; }
    public void setAlive(boolean b) { alive=b; if(!alive){died();}}
    public void died() {
        TextFighter.addToOutput("You died!");
        TextFighter.removeSave(TextFighter.currentSaveFile.getName());
        TextFighter.needsSaving = false;
    }
    public boolean getInFight() { return inFight; }
    public void setInFight(boolean b) { inFight=b; TextFighter.needsSaving=true;}

    public double getTotalProtection() { return totalProtection; }
    public void calculateTotalProtection() {
        totalProtection=1;
        for(Item i : inventory) {
            if(i.getClass().getSuperclass().equals(Armor.class)) { totalProtection+=((Armor)i).getProtectionAmount(); }
        }
    }

    public Weapon getCurrentWeapon() { return currentWeapon; }
    public String getCurrentWeaponString() { if(currentWeapon != null) { return currentWeapon.getName(); } else { return null; } }
    public void setCurrentWeapon(String name) {
        if(name == null) { name = defaultCurrentWeaponString; }
        Weapon weapon = TextFighter.getWeaponByName(name);
        if(weapon == null) { return; }
        if(!isCarrying(name, "weapon")) { TextFighter.addToOutput("You do not have weapon '" + name + "'"); return; }
        else {    
            currentWeapon = weapon;
            TextFighter.addToOutput("Equiped weapon '" + name + "'");
            calculateStrength();
            return;
        }
    }
    public void calculateStrength() {
        if(currentWeapon != null) {
            strength = currentWeapon.getDamage();
        } else {
            strength = defaultStrength;
        }
    }

    public int getStrength() { return strength; }

    public void increaseHealthPotions(int a) { healthPotions+=a; TextFighter.needsSaving=true;}
    public void decreaseHealthPotions(int a) { healthPotions-=a; if(healthPotions<0){healthPotions=0;} TextFighter.needsSaving=true;}
    public void useHealthPotion() { if(healthPotions<1){return;} decreaseHealthPotions(1); heal(hpHealthPotionsGive); TextFighter.needsSaving=true;}
    public void setHealthPotions(int a) { healthPotions = a; if(healthPotions<1){healthPotions=0;} TextFighter.needsSaving=true;}
    public int getHealthPotions() { return healthPotions;}

    public void increaseStrengthPotions(int a) { strengthPotions+=a; TextFighter.needsSaving=true;}
    public void decreaseStrengthPotions(int a) { strengthPotions-=a; if(strengthPotions<0){strengthPotions=0;} TextFighter.needsSaving=true;}
    public void useStrengthPotion() { if(turnsWithStrengthLeft > 0 || strengthPotions < 1) {return;} decreaseStrengthPotions(1); turnsWithStrengthLeft = turnsStrengthPotionsGive; TextFighter.needsSaving=true;}
    public void setStrengthPotions(int a) { strengthPotions = a; if(strengthPotions<1){strengthPotions=0;} TextFighter.needsSaving=true;}
    public int getStrengthPotions() { return strengthPotions; }

    public void increaseInvincibilityPotions(int a) { invincibilityPotions+=a; TextFighter.needsSaving=true;}
    public void decreaseInvincibilityPotions(int a) { invincibilityPotions-=a; if(invincibilityPotions<0){invincibilityPotions=0;} TextFighter.needsSaving=true;}
    public void useInvincibilityPotion() { if(turnsWithInvincibilityLeft > 0 || invincibilityPotions < 1) {return;} decreaseInvincibilityPotions(1); turnsWithInvincibilityLeft = turnsInvincibilityPotionsGive; TextFighter.needsSaving=true;}
    public void setInvincibilityPotions(int a) { invincibilityPotions=a; if(invincibilityPotions<0){invincibilityPotions=0;} TextFighter.needsSaving=true;}
    public int getInvincibilityPotions() { return invincibilityPotions; }

    public void increaseTurnsWithStrengthLeft(int a) { turnsWithStrengthLeft+=a; if(turnsWithStrengthLeft<0){turnsWithStrengthLeft=0;} TextFighter.needsSaving=true;}
    public void decreaseTurnsWithStrengthLeft(int a) { turnsWithStrengthLeft-=a; if(turnsWithStrengthLeft<0){turnsWithStrengthLeft=0;} TextFighter.needsSaving=true;}
    public int getTurnsWithStrengthLeft() {return turnsWithStrengthLeft;}
    public void setTurnsWithStrengthLeft(int a) { turnsWithStrengthLeft=a; if(turnsWithStrengthLeft<0) {turnsWithStrengthLeft=0;}}

    public void increaseTurnsWithInvincibilityLeft(int a) { turnsWithInvincibilityLeft+=a; if(turnsWithInvincibilityLeft<0){turnsWithInvincibilityLeft=0;} TextFighter.needsSaving=true;}
    public void decreaseTurnsWithInvincibilityLeft(int a) { turnsWithInvincibilityLeft-=a; if(turnsWithInvincibilityLeft<0){turnsWithInvincibilityLeft=0;} TextFighter.needsSaving=true;}
    public int getTurnsWithInvincibilityLeft() {return turnsWithInvincibilityLeft;}
    public void setTurnsWithInvincibilityLeft(int a) { turnsWithInvincibilityLeft=a; if(turnsWithInvincibilityLeft<0) {turnsWithInvincibilityLeft=0;} TextFighter.needsSaving=true;}

    public int getLevel() { return level; }
    public void increaseLevel(int a) { level+=a; TextFighter.needsSaving=true;}
    public void decreaseLevel(int a) { level-=a; TextFighter.needsSaving=true;}
    public int getExperience() { return experience; }
    public void increaseExperience(int a) { experience+=a; checkForLevelUp(); TextFighter.needsSaving=true;}
    public void decreaseExperience(int a) { experience-=a; if(experience < 0) { experience = 0; } TextFighter.needsSaving=true;}
    public int getScore() { return score; }
    public void increaseScore(int a) { score+=a; TextFighter.needsSaving=true;}
    public void decreaseScore(int a) { score-=a; TextFighter.needsSaving=true;}

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

    public int getCoins() { return coins; }
    public void spendCoins(int a) { if (coins-a >= 0) { coins-=a; } else { coins=0; } TextFighter.needsSaving=true;}
    public void gainCoins(int a) { coins+=a; TextFighter.needsSaving=true; }

    public int getMagic() { return magic; }
    public void spendMagic(int a) {  if (magic-a >= 0) { magic-=a; } else { magic=0; } TextFighter.needsSaving=true;}
    public void gainMagic(int a) { if(!TextFighter.player.isCarryingSpecialItem("manapouch")) { return; }  magic+=a; TextFighter.needsSaving=true; }

    public int getMetalScraps() { return magic; }
    public void spendMetalScraps(int a) { if(metalScraps-a >=0) { metalScraps-=a; } else { metalScraps=0; } TextFighter.needsSaving=true; }
    public void gainMetalScraps (int a) { metalScraps+=a; TextFighter.needsSaving=true; }

    public Location getLocation() { return location; }
    public void setLocation(String loc) {
        for(Location l : TextFighter.locations) {
            if(l.getName().equals(loc)) {
                location = l;
                TextFighter.needsSaving=true;
            }
        }
    }

    public boolean getGameBeaten() { return gameBeaten; }
    public void setGameBeaten(boolean b) { gameBeaten = b; TextFighter.needsSaving=true;}

    public boolean getCanBeHurtThisTurn() { return canBeHurtThisTurn; }
    public void setCanBeHurtThisTurn(boolean b) { canBeHurtThisTurn = b; }

    public ArrayList<Item> getInventory() { return inventory; }
    public void addToInventory(String name, String type) {
        if(name == null || type == null) { return;}
        if(type.equals("weapon")) {
            for(Weapon i : TextFighter.weapons) {
                if(i.getName().equals(name) && i.getItemType().equals(type) && !isCarrying(name, "weapon")) {
                    inventory.add(i);
                    TextFighter.addToOutput("A " + name + " was added to your inventory.");
                    TextFighter.needsSaving=true;
                }
            }
        }
        else if(type.equals("armor")) {
            for(Armor i : TextFighter.armors) {
                if(i.getName().equals(name) && i.getItemType().equals(type) && !isCarrying(name, "armor")) {
                    inventory.add(i);
                    TextFighter.addToOutput("A " + name + " was added to your inventory.");
                    TextFighter.needsSaving=true;
                }
            }
        }
        else if(type.equals("tool")) {
            for(Tool i : TextFighter.tools) {
                if(i.getName().equals(name) && i.getItemType().equals(type) && !isCarrying(name, "tool")) {
                    inventory.add(i);
                    TextFighter.addToOutput("A " + name + " was added to your inventory.");
                    TextFighter.needsSaving=true;
                }
            }
        }
    }

    public void removeFromInventory(String name, String type) {
        for(int i=0;i<inventory.size();i++) {
            if(name.equals(inventory.get(i).getName()) && type.equals(inventory.get(i).getItemType())) {
                if(inventory.get(i).equals(currentWeapon)) { setCurrentWeapon(null); }
                inventory.remove(i);
                TextFighter.needsSaving=true;
                break;
            }
        }
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
        if(name == null || type == null) { return null;}
        for(Item i : inventory) {
            if(i.getName().equals(name) && i.getItemType().equals(type)) {
                return i;
            }
        }
        return null;
    }

    public ArrayList<SpecialItem> getSpecialItems() { return specialItems; }

    public void addToSpecialItems(String name) {
        if(name == null) { return; }
        for(SpecialItem sp : TextFighter.specialItems) {
            if(sp.getName().equals(name)){
                specialItems.add(sp);
                TextFighter.addToOutput("A " + name + " was added to your inventory.");
                TextFighter.needsSaving = true;
            }
        }
    }

    public void removeFromSpecialItems(String name) {
        for(int i=0;i<specialItems.size();i++) {
            if(name.equals(specialItems.get(i).getName())) {
                specialItems.remove(i);
                TextFighter.needsSaving=true;
                break;
            }
        }
    }

    public boolean isCarryingSpecialItem(String name) {
        if(name == null) { return false;}
        for(SpecialItem i : specialItems) {
            if(i.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public SpecialItem getFromSpecialItems(String name) {
        if(name == null) { return null;}
        for(SpecialItem i : specialItems) {
            if(i.getName().equals(name)) {
                return i;
            }
        }
        return null;
    }

    public void achievementEarned(Achievement a) {
        achievements.add(a);
        Display.achievementEarned(a.getName());
    }

    public ArrayList<Achievement> getAchievements() { return achievements; }

    public Player(int hp, int maxhp, int coins, int magic, int metalScraps, int level, int experience, int score, int healthPotions, int strengthPotions, int invincibilityPotions, Weapon currentWeapon, boolean gameBeaten, ArrayList<Item> inventory, ArrayList<Achievement> achievements, ArrayList<SpecialItem> specialItems) {
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
        this.specialItems = specialItems;
        for(Location l : TextFighter.locations) {
            if(l.getName().equals("saves")) { this.location = l; }
        }
        calculateTotalProtection();
    }

    public Player(Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

    public Player() { }
}
