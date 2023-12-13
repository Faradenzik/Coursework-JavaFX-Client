package com.farad.tables;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Order implements Serializable {
    private static final long serialVersionUID = 4L;
    private int id;
    private int id_customer;
    private String username;
    private Map<Integer, Integer> id_cars;
    private int price;
    private LocalDateTime date;

    public Order(int id, int id_customer, String username, Map<Integer, Integer> id_cars, int price, LocalDateTime date) {
        this.id = id;
        this.id_customer = id_customer;
        this.username = username;
        this.id_cars = id_cars;
        this.price = price;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_customer() {
        return id_customer;
    }

    public String getUsername() {
        return username;
    }

    public Map<Integer, Integer> getId_cars() {
        return id_cars;
    }

    public int getPrice() {
        return price;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        String str = "";
        for (int i : getId_cars().keySet()) {
            str += i + " ";
        }

        for (int i : getId_cars().values()) {
            str += i + " ";
        }
        return str;
    }
}
