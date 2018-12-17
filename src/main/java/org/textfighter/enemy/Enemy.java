package org.textfighter.enemy;

public class Enemy {

    protected String name;
    protected int maxhp;
    protected int hp;
    protected int strength;

    public String getName() { return name; }
    public void setName(String n) { name=n; }

    public int getMaxHp() { return maxhp; }
    public void setMaxHp(int a) {
        maxhp=a;
        if(maxhp<1) { maxhp=1; }
    }

    public int gethp() { return hp; }
    public void damaged(int a) {
        hp = hp - a;
        if (hp < 1) { hp = 0; }
    }
    public void healed(int a) {
        hp = hp + a;
        if (hp > maxhp) { hp = 0; }
    }

    public int getStrength() { return strength; }
    public void setStrength(int a) {
        strength = a;
        if(strength < 0) { strength = 0; }
    }

    public Enemy(String name, int maxhp, int hp, int str) {
        this.name = name;
        this.maxhp = maxhp;
        this.hp = hp;
        this.strength = str;
    }

}
