package org.textfighter.enemy;

public class Enemy {

    protected String name;
    protected int maxhp;
    protected int hp;
    protected int strength;

    public String getName() { return name; }

    public int gethp() { return hp; }

    public void damaged(int a) {
        hp = hp - a;
        if (hp < 1) { hp = 0; }
    }

    public void healed(int a) {
        hp = hp + a;
        if (hp > maxhp) { hp = 0; }
    }

    public Enemy(String name, int maxhp, int hp, int str) {
        this.name = name;
        this.maxhp = maxhp;
        this.hp = hp;
        this.strength = str;
    }

}
