package com.example.expenditure.NewExpense;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Expense {

    private String TAG = "Expense";

    private Timestamp timestamp;
    private float amount;
    private String description;

    // Do not remove this, it is required by FirebaseFirestore
    public Expense() {
    }

    public Expense(float amount, String description){
        Log.d(TAG, "Without date");
        this.timestamp = Timestamp.now();
        this.amount = amount;
        this.description = description;
    }

    public Expense(float amount, String description, Date timestamp) {
        this.timestamp = (timestamp == null) ? Timestamp.now() : new Timestamp(timestamp);
        this.amount = amount;
        this.description = description;
    }

    public float getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String toString() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("timestamp", this.timestamp);
        objectMap.put("amount", this.amount);
        objectMap.put("description", this.description);
        return objectMap.toString();
    }
}
