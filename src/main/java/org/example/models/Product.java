package org.example.models;

public class Product {
    private final int id;
    private String name;
    private String description;
    private String producer;
    private int amount;
    private double price;
    private int groupID;

    public Product(int id, String name, String description, String producer, int amount, double price, int groupID) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.producer = producer;
        this.amount = amount;
        this.price = price;
        this.groupID = groupID;
    }

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

    public int getGroupID() {
        return groupID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String toString(){
        return "[ID " + id + "] " + name + "; Amount: " + amount + "; Description: " + description + "; Producer: " + producer + " — $" + price;
    }
}
