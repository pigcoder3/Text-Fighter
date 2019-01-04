package org.textfighter;

import org.textfighter.item.*;
import org.textfighter.location.Location;
import org.textfighter.TextFighter;
import org.textfighter.display.Display;
import org.textfighter.item.specialitem.SpecialItem;

import java.lang.reflect.*;

import java.util.ArrayList;

public class Player {

    static int defaulthp = 50;

    private boolean alive = true;
    private boolean inFight = false;

    private int level;
    private int experience;
    private int score;

    private int hp;
    private int maxhp;

    private int coins;
    private int magic;
    private int metalScraps;

    private Location location;

    private boolean gameBeaten;

    private boolean canBeHurtThisTurn = true;

    private ArrayList<Item> inventory = new ArrayList<Item>();

    private ArrayList<SpecialItem> specialItems = new ArrayList<SpecialItem>();

    public boolean getAlive() { return alive; }
    public void setAlive(boolean b) { alive=b; TextFighter.removeSave(TextFighter.currentSaveFile.getName()); TextFighter.needsSaving=true;}
    public void died(String cause) {
        TextFighter.addToOutput("You died!");
        TextFighter.addToOutput(cause);
    }
    public boolean getInFight() { return inFight; }
    public void setInFight(boolean b) { inFight=b; TextFighter.needsSaving=true;}

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
    public void damage(int a) { if (hp-a < 0) { hp = 0; } else { hp-=a; } TextFighter.needsSaving=true;}
    public void heal(int a) { if (hp+a > maxhp) { hp = maxhp; } else { hp+=a; } TextFighter.needsSaving=true;}

    public int getMaxHp() { return maxhp; }
    public void setMaxHp(int a) { maxhp=a; TextFighter.needsSaving=true; }

    public int getCoins() { return coins; }
    public void spendCoins(int a) { if (coins-a >= 0) { coins-=a; } else { coins=0; } TextFighter.needsSaving=true;}
    public void gainCoins(int a) { coins=+a; TextFighter.needsSaving=true; }

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

    public Player(int hp, int maxhp, int coins, int magic, int level, int experience, int score, boolean gameBeaten, ArrayList<Item> inventory) {
        this.hp = hp;
        this.maxhp = maxhp;
        this.coins = coins;
        this.magic = magic;
        this.level = level;
        this.experience = experience;
        this.score = score;
        this.gameBeaten = gameBeaten;
        this.inventory = inventory;
        for(Location l : TextFighter.locations) {
            if(l.getName().equals("saves")) { this.location = l; }
        }

    }

    public Player() { }

}
