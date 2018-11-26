package org.textfighter;

import org.textfighter.item.Item;

import java.util.ArrayList;

public class Player {

    private int hp;
    private int maxhp;

    private ArrayList<Item> inventory = new ArrayList<Item>();

    private String location;

    public int gethp() { return hp; }
    public void damaged(int a) { hp = hp - a; if (hp < 0) { hp = 0; }}
    public void healed(int a) { hp = hp + 1; if (hp > maxhp) { hp = maxhp; }}

    public String getLocation() { return location; }

    public void addToInv(Item item) { inventory.add(item); }

    public Player() {

    }

}
