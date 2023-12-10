package com.farad.tables;

import java.io.Serializable;

public class Car implements Serializable {
    private static final long serialVersionUID = 3L;
    private int id;
    private String brand;
    private String model;
    private String equipment;
    private String color;
    private String fuel;
    private int price;
    private int amount;

    public Car(int id, String brand, String model, String equipment, String color, String fuel, int price, int amount) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.equipment = equipment;
        this.color = color;
        this.fuel = fuel;
        this.price = price;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
