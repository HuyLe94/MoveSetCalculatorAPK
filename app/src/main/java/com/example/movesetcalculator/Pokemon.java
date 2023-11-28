package com.example.movesetcalculator;

import java.util.List;

public class Pokemon {
    private int id;
    private String name;
    private int hp;
    private int attack;
    private int defense;
    private int satk;
    private int sdef;
    private int speed;
    private List<String> types;

    // Constructors, getters, and setters for all attributes

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getSatk() {
        return satk;
    }

    public void setSatk(int satk) {
        this.satk = satk;
    }

    public int getSdef() {
        return sdef;
    }

    public void setSdef(int sdef) {
        this.sdef = sdef;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}



