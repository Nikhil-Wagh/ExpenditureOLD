package com.example.expenditure;

public class Expense {

    private int id;
    private float amount;
    private String description;

    public Expense(int id, float amount, String description){
        this.id = id;
        this.amount = amount;
        this.description = description;
    }

    public Expense() {
        this.id = 0;
    }

    public int getId() {
        return id;
    }

    public float getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return "id: " + this.id + " amount: " + this.amount + " description: " + this.description;
    }
}
