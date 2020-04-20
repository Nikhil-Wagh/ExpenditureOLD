package com.example.expenditure;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class Expense {

    private Timestamp timestamp;
    private float amount;
    private String description;

    public Expense() { timestamp = Timestamp.now(); }

    public Expense(float amount, String description){
        this.timestamp = Timestamp.now();
        this.amount = amount;
        this.description = description;
    }

    public float getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("timestamp", this.timestamp);
        objectMap.put("amount", this.amount);
        objectMap.put("description", this.description);
        return objectMap.toString();
    }

    public String getTimestamp() {
        return timestamp.toString();
    }
}
