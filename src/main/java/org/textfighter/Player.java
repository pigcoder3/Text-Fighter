package org.textfighter;

import org.textfighter.item.*;
import org.textfighter.item.armor.*;
import org.textfighter.item.tool.*;
import org.textfighter.item.weapon.*;
import org.textfighter.location.Location;
import org.textfighter.*;
import org.textfighter.display.Display;
import org.textfighter.item.specialitem.SpecialItem;
import org.textfighter.method.*;

import java.lang.reflect.*;

import java.util.ArrayList;

public class Player {

    static int defaulthp = 50;
    static int defaultMaxhp = 50;

    static int defaultcoins = 10;
    static int defaultmagic = 0;
    static int defaultmetalscraps = 0;

    static int defaultStrength = 5;  //Strength of just your fists

    static int defaultHealthPotions = 0;
    static int defaultStrengthPotions = 0;
    static int defaultInvinsibilityPotions = 0;

    static int defaultTurnsWithStrengthLeft = 0;
    static int defaultTurnsWithInvinsibilityLeft = 0;

    static int hpHealthPotionsGive = 30;
    static int turnsStrengthPotionsGive = 2;
    static int turnsInvinsibilityPotionsGive = 2;

    private boolean alive = true;
    private boolean inFight = false;

    private double totalProtection = 1;

    private Weapon currentWeapon;

    private int level = 1;
    private int experience = 0;
    private int score = 0;

    private int hp = defaulthp;
    private int maxhp = defaulthp;

    private int strength = defaultStrength;

    private int healthPotions = defaultHealthPotions;
    private int strengthPotions = defaultStrengthPotions;
    private int invinsibilityPotions = defaultInvinsibilityPotions;

    private int turnsWithStrengthLeft = defaultTurnsWithStrengthLeft;
    private int turnsWithInvinsibilityLeft = defaultTurnsWithInvinsibilityLeft;

    private int coins = defaultcoins;
    private int magic = defaultmagic;
    private int metalScraps = defaultmetalscraps;

    private Location location;

    private boolean gameBeaten = false;

    private boolean canBeHurtThisTurn = true;

    private ArrayList<Item> inventory = new ArrayList<Item>();

    private ArrayList<SpecialItem> specialItems = new ArrayList<SpecialItem>();

    private ArrayList<Achievement> achievements = new ArrayList<Achievement>();

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
    public void setCurrentWeapon(Weapon weapon) { currentWeapon = weapon; calculateStrength();}
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

    public void increaseInvinsibilityPotions(int a) { invinsibilityPotions+=a; TextFighter.needsSaving=true;}
    public void decreaseInvinsibilityPotions(int a) { invinsibilityPotions-=a; if(invinsibilityPotions<0){invinsibilityPotions=0;} TextFighter.needsSaving=true;}
    public void useInvinsibilityPotion() { if(turnsWithInvinsibilityLeft > 0 || invinsibilityPotions < 1) {return;} decreaseInvinsibilityPotions(1); turnsWithInvinsibilityLeft = turnsInvinsibilityPotionsGive; TextFighter.needsSaving=true;}
    public void setInvinsibilityPotions(int a) { invinsibilityPotions=a; if(invinsibilityPotions<0){invinsibilityPotions=0;} TextFighter.needsSaving=true;}
    public int getInvinsibilityPotions() { return invinsibilityPotions; }

    public void increaseTurnsWithStrengthLeft(int a) { turnsWithStrengthLeft+=a; TextFighter.needsSaving=true;}
    public void decreaseTurnsWithStrengthLeft(int a) { turnsWithStrengthLeft-=a; if(turnsWithStrengthLeft<0){turnsWithStrengthLeft=0;} TextFighter.needsSaving=true;}
    public int getTurnsWithStrengthLeft() {return turnsWithStrengthLeft;}

    public void increaseTurnsWithInvinsibilityLeft(int a) { turnsWithInvinsibilityLeft+=a;}
    public void decreaseTurnsWithInvinsibilityLeft(int a) { turnsWithInvinsibilityLeft-=a; if(turnsWithInvinsibilityLeft<0){turnsWithInvinsibilityLeft=0;} TextFighter.needsSaving=true;}
    public int getTurnsWithInvinsibilityLeft() {return turnsWithInvinsibilityLeft;}

    public int getLevel() { return level; }
    public void increaseLevel(int a) { level+=a; TextFighter.needsSaving=true;}
    public void decreaseLevel(int a) { level-=a; TextFighter.needsSaving=true;}
    public int getExperience() { return experience; }
    public void increaseExperience(int a) { experience+=a; TextFighter.needsSaving=true;}
    public void decreaseExperience(int a) { experience-=a; TextFighter.needsSaving=true;}
    public int getScore() { return score; }
    public void increaseScore(int a) { score+=a; TextFighter.needsSaving=true;}
    public void decreaseScore(int a) { score-=a; TextFighter.needsSaving=true;}

    public int getHp() { return hp; }
    public void damaged(int a, String customString) {
        if(!canBeHurtThisTurn || turnsWithInvinsibilityLeft>0) { return; }
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

    public int getMagic() { if(!TextFighter.player.isCarryingSpecialItem("ManaPouch")) { return 0; } return magic; }
    public void spendMagic(int a) { if(!TextFighter.player.isCarryingSpecialItem("ManaPouch")) { return; } if (magic-a >= 0) { magic-=a; } else { magic=0; } TextFighter.needsSaving=true;}
    public void gainMagic(int a) {if(!TextFighter.player.isCarryingSpecialItem("ManaPouch")) { return; }  magic+=a; TextFighter.needsSaving=true; }

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
    public void addToInventory(String classname) {
        if(classname != null && !isCarrying(classname)) {
            try {
                try {
                    Item item = (Item)Class.forName(classname).getDeclaredConstructor(new Class[] {int.class,int.class,int.class}).newInstance();
                    inventory.add(item);
                } catch (NoSuchMethodException | InvocationTargetException e) {}
            } catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) { Display.displayPackError("Unknown class for item '" + classname + "'"); return; }
            TextFighter.needsSaving=true;
        }
    }
    public void removeFromInventory(String classname) {
        for(int i=0;i<inventory.size();i++) {
            try {
                if(inventory.get(i).getClass().equals(Class.forName(classname))) {
                    if(inventory.get(i).equals(currentWeapon)) { setCurrentWeapon(null); }
                    inventory.remove(i);
                    TextFighter.needsSaving=true;
                    break;
                }
            } catch (ClassNotFoundException e) { Display.displayPackError("Unknown class for item '" + classname + "'"); return ; }
        }
    }

    public boolean isCarrying(String classname) {
        for(Item i : inventory) {
            try {
                if(i.getClass().equals(Class.forName(classname))) { return true; }
            } catch (ClassNotFoundException e) { Display.displayPackError("Unknown class for item '" + classname + "'"); return false; }
        }
        return false;
    }

    public Item getFromInventory(String classname) {
        for(Item i : inventory) {
            try {
                if(i.getClass().equals(Class.forName(classname))) { return i; }
            } catch (ClassNotFoundException e) { Display.displayPackError("Unknown class for item '" + classname + "'"); return null; }
        }
        return null;
    }

    public ArrayList<SpecialItem> getSpecialItems() { return specialItems; }

    public void addToSpecialItems(String classname) {
        if(classname != null && !isCarryingSpecialItem(classname)) {
            try {
                try {
                    SpecialItem specialitem = (SpecialItem)Class.forName(classname).getDeclaredConstructor().newInstance();
                    specialItems.add(specialitem);
                } catch (NoSuchMethodException | InvocationTargetException e) {}
            } catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) { Display.displayPackError("Unknown class for special item '" + classname + "'"); return;}
            TextFighter.needsSaving = true;
        }
    }

    public void removeFromSpecialItems(String classname) {
        for(int i=0;i<specialItems.size();i++) {
            try {
                if(specialItems.get(i).getClass().equals(Class.forName(classname))) {
                    specialItems.remove(i);
                    TextFighter.needsSaving=true;
                    break;
                }
            } catch (ClassNotFoundException e) { Display.displayPackError("Unknown class for spcial item '" + classname + "'"); return; }
        }
    }

    public boolean isCarryingSpecialItem(String classname) {
        for(SpecialItem i : specialItems) {
            try {
                if(i.getClass().equals(Class.forName(classname))) { return true; }
            } catch (ClassNotFoundException e) { Display.displayPackError("Unknown class for special item '" + classname + "'"); return false; }
        }
        return false;
    }

    public SpecialItem getFromSpecialItems(String classname) {
        for(SpecialItem i : specialItems) {
            try {
                if(i.getClass().equals(Class.forName(classname))) { return i; }
            } catch (ClassNotFoundException e) { Display.displayPackError("Unknown class for special item '" + classname + "'"); return null; }
        }
        return null;
    }

    public void achievementEarned(Achievement a) {
        achievements.add(a);
        Display.achievementEarned(a.getName());
    }

    public ArrayList<Achievement> getAchievements() { return achievements; }

    public void attack(String customString) {
        if(strength<1) { TextFighter.addToOutput("Your strength is 0, you did not attack.");}
        if(turnsWithStrengthLeft>0) {
            TextFighter.currentEnemy.damaged(strength*2, customString);
        } else {
            TextFighter.currentEnemy.damaged(strength, customString);
        }
    }

    public Player(int hp, int maxhp, int coins, int magic, int level, int experience, int score, int healthPotions, int strengthPotions, boolean gameBeaten, ArrayList<Item> inventory, ArrayList<Achievement> achievements, ArrayList<SpecialItem> specialItems) {
        this.hp = hp;
        this.maxhp = maxhp;
        this.coins = coins;
        this.magic = magic;
        this.level = level;
        this.experience = experience;
        this.score = score;
        this.healthPotions = healthPotions;
        this.strengthPotions = strengthPotions;
        this.gameBeaten = gameBeaten;
        this.inventory = inventory;
        this.achievements = achievements;
        this.specialItems = specialItems;
        for(Location l : TextFighter.locations) {
            if(l.getName().equals("saves")) { this.location = l; }
        }
        calculateTotalProtection();
    }

    public Player() { }

}
