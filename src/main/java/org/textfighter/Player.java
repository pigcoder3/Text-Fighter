package org.textfighter;

import org.textfighter.item.Item;

import java.util.ArrayList;

public class Player {

    private int hp;
    private int maxhp;

    private int coins;
    private int magic;

    private String location;

    private ArrayList<Item> inventory = new ArrayList<Item>();
    private ArrayList<String> saves = new ArrayList<String>();

    public int gethp() { return hp; }
    public void damage(int a) { if (hp-a < 0) { hp = 0; } else { hp=-a; }}
    public void heal(int a) { if (hp+a > maxhp) { hp = maxhp; } else { hp=+a; } }

    public int getCoins() { return coins; }
    public void spendCoins(int a) { if (magic-a >= 0) { magic=-a; }}
    public void gainCoins(int a) { magic=+1; }

    public int getMagic() { return magic; }
    public void spendMagic(int a) { if (magic-a >= 0) { magic=-a; }}
    public void gainMagic(int a) { magic=+1; }

    public String getLocation() { return location; }

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

    public Player() {

    }

}
