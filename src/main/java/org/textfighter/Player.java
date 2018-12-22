package org.textfighter;

import org.textfighter.item.*;

import org.textfighter.location.Location;

import java.util.ArrayList;

public class Player {

    static int defaulthp = 50;

    private static boolean alive = true;
    private static boolean inFight = false;

    private static int level;
    private static int experience;
    private static int score;

    private static int hp;
    private static int maxhp;

    private static int coins;
    private static int magic;

    private static Location location;

    private static ArrayList<Item> inventory = new ArrayList<Item>();

    public static boolean getAlive() { return alive; }
    public static void setAlive(boolean b) { alive=b; }
    public static boolean getInFight() { return inFight; }
    public static void setInFight(boolean b) { inFight=b; }

    public static int getLevel() { return level; }
    public static void increaseLevel(int a) { level=+a; }
    public static void decreaseLevel(int a) { level=-a; }
    public static int getExperience() { return experience; }
    public static void increaseExperience(int a) { experience=+a; }
    public static void decreaseExperience(int a) { experience=-a; }
    public static int getScore() { return score; }
    public static void increaseScore(int a) { score=+a; }
    public static void decreaseScore(int a) { score=-a; }

    public static int getHp() { return hp; }
    public static void damage(int a) { if (hp-a < 0) { hp = 0; } else { hp=-a; }}
    public static void heal(int a) { if (hp+a > maxhp) { hp = maxhp; } else { hp=+a; } }

    public static int getMaxHp() { return maxhp; }
    public static void setMaxHp(int a) { maxhp=a; }

    public static int getCoins() { return coins; }
    public static void spendCoins(int a) { if (coins-a >= 0) { coins=-a; } else { coins=-a; } }
    public static void gainCoins(int a) { coins=+a; }

    public static int getMagic() { return magic; }
    public static void spendMagic(int a) { if (magic-a >= 0) { magic=-a; } else { magic=-a; } }
    public static void gainMagic(int a) { magic=+a; }

    public static Location getLocation() { return location; }
    public static void setLocation(String loc) {
        for(Location l : TextFighter.locations) {
            if(l.getName().equals(loc)) {
                location = l;
            }
        }
    }

    public static ArrayList<Item> getInventory() { return inventory; }
    public static void addToInventory(Item item) { inventory.add(item); }
    public static void removeFromInventory(String classname) {
        for(int i=0;i<inventory.size();i++) {
            try {
                if(inventory.get(i).getClass().equals(Class.forName(classname))) { inventory.remove(i); }
            } catch (ClassNotFoundException e) { e.printStackTrace (); }
        }
    }

    public  boolean isCarrying(String classname) {
        for(Item i : inventory) {
            try {
                if(i.getClass().equals(Class.forName(classname))) { return true; }
            } catch (ClassNotFoundException e) { e.printStackTrace (); }
        }
        return false;
    }

    public static Item getFromInventory(String classname) {
        for(Item i : inventory) {
            try {
                if(i.getClass().equals(Class.forName(classname))) { return i; }
            } catch (ClassNotFoundException e) { e.printStackTrace (); }
        }
        return null;
    }

    public Player(int hp, int maxhp, int coins, int magic, int level, int experience, int score, ArrayList<Item> inventory) {
        this.hp = hp;
        this.maxhp = maxhp;
        this.coins = coins;
        this.magic = magic;
        this.level = level;
        this.experience = experience;
        this.score = score;
        this.inventory = inventory;
    }

    public Player() { }

}
