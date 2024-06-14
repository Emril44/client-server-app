package org.example.models;

public class Product {
    private int id;
    private String name;
    private String description;
    private String producer;
    private int amount;
    private double price;

    public int getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getProducer() {return this.producer;}

    public int getAmount() {
        return this.amount;
    }

    public double getPrice() {
        return this.price;
    }

    public String toString(){
        return "[ID " + id + "] " + name + "; Amount: " + amount + "; Description: " + description + "; Producer: " + producer + " — $" + price;
    }
}
