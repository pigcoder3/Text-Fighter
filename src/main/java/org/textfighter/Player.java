package org.textfighter;

import org.textfighter.item.*;

import java.util.ArrayList;

public class Player {

    private int level;
    private int experience;
    private int score;

    private int hp;
    private int maxhp;

    private int coins;
    private int magic;

    private String location;

    private ArrayList<Item> inventory = new ArrayList<Item>();
    private ArrayList<String> saves = new ArrayList<String>();

    public int getLevel() { return level; }
    public void increaseLevel(int a) { level=+a; }
    public void decreaseLevel(int a) { level=-a; }
    public int getExperience() { return experience; }
    public void increaseExperience(int a) { experience=+a; }
    public void decreaseExperience(int a) { experience=-a; }
    public int getScore() { return score; }
    public void increaseScore(int a) { score=+a; }
    public void decreaseScore(int a) { score=-a; }

    public int gethp() { return hp; }
    public void damage(int a) { if (hp-a < 0) { hp = 0; } else { hp=-a; }}
    public void heal(int a) { if (hp+a > maxhp) { hp = maxhp; } else { hp=+a; } }

    public int getCoins() { return coins; }
    public void spendCoins(int a) { if (coins-a >= 0) { coins=-a; } else { coins=-a; } }
    public void gainCoins(int a) { magic=+a; }

    public int getMagic() { return magic; }
    public void spendMagic(int a) { if (magic-a >= 0) { magic=-a; } else { magic=-a; } }
    public void gainMagic(int a) { magic=+a; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location=location; }

    public ArrayList<String> getSaves() { return saves; }
    public void addSave(String name) { saves.add(name); }
    public void removeSave(String name) {
        for(int i=0;i<saves.size();i++){
            if(saves.get(i).equals(name)) {
                saves.remove(i);
            }
        }
    }

    public ArrayList<Item> getInventory() { return inventory; }
    public void addToInventory(Item item) { inventory.add(item); }
    public void removeFromInventory(Class classname) {
        try {
            for(int i=0;i<inventory.size();i++) {
                if(inventory.get(i).getClass().equals(classname)) { inventory.remove(i); }
            }
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    public int isCarrying(Class classname) {
        int p=0;
        try {
            for(Item i : inventory) {
                if(i.getClass().equals(classname)) { p++; }
            }
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        return p;
    }

    public Item getFromInventory(Class classname) {
        try {
            for(Item i : inventory) {
                if(i.getClass().equals(classname)) { return i; }
            }
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        return null;
    }

    public Player() {

    }

}
