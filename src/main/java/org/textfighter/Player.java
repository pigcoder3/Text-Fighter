package org.textfighter;

import org.textfighter.item.*;

import java.util.ArrayList;

public class Player {

    private static int level;
    private static int experience;
    private static int score;

    private static int hp;
    private static int maxhp;

    private static int coins;
    private static int magic;

    private static String location;

    private static ArrayList<Item> inventory = new ArrayList<Item>();
    private static ArrayList<String> saves = new ArrayList<String>();

    public static int getLevel() { return level; }
    public static void increaseLevel(int a) { level=+a; }
    public static void decreaseLevel(int a) { level=-a; }
    public static int getExperience() { return experience; }
    public static void increaseExperience(int a) { experience=+a; }
    public static void decreaseExperience(int a) { experience=-a; }
    public static int getScore() { return score; }
    public static void increaseScore(int a) { score=+a; }
    public static void decreaseScore(int a) { score=-a; }

    public static int gethp() { return hp; }
    public static void damage(int a) { if (hp-a < 0) { hp = 0; } else { hp=-a; }}
    public static void heal(int a) { if (hp+a > maxhp) { hp = maxhp; } else { hp=+a; } }

    public static int getCoins() { return coins; }
    public static void spendCoins(int a) { if (coins-a >= 0) { coins=-a; } else { coins=-a; } }
    public static void gainCoins(int a) { magic=+a; }

    public static int getMagic() { return magic; }
    public static void spendMagic(int a) { if (magic-a >= 0) { magic=-a; } else { magic=-a; } }
    public static void gainMagic(int a) { magic=+a; }

    public static String getLocation() { return location; }
    public static void setLocation(String loc) { location=loc; }

    public static ArrayList<String> getSaves() { return saves; }
    public static void addSave(String name) { saves.add(name); }
    public static void removeSave(String name) {
        for(int i=0;i<saves.size();i++){
            if(saves.get(i).equals(name)) {
                saves.remove(i);
            }
        }
    }

    public static ArrayList<Item> getInventory() { return inventory; }
    public static void addToInventory(Item item) { inventory.add(item); }
    public static void removeFromInventory(Class classname) {
        try {
            for(int i=0;i<inventory.size();i++) {
                if(inventory.get(i).getClass().equals(classname)) { inventory.remove(i); }
            }
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    public static int isCarrying(Class classname) {
        int p=0;
        try {
            for(Item i : inventory) {
                if(i.getClass().equals(classname)) { p++; }
            }
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        return p;
    }

    public static Item getFromInventory(Class classname) {
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
