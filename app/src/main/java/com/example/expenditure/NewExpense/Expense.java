package com.example.expenditure.NewExpense;

import com.example.expenditure.Firebase.Helpers;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Expense implements Serializable {

    private String mDocumentName;
    private String TAG = "Expense";

    private Timestamp timestamp;
    private float amount;
    private String description;

    // Do not remove this, it is required by FirebaseFirestore
    public Expense() {
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

    public DocumentReference getId() {
        return Helpers.
                expenses().
                document(this.mDocumentName);
    }

    public String getDocumentName() {
        return mDocumentName;
    }

    public void setDocumentName(String documentName) {
        this.mDocumentName = documentName;
    }

    public String toString() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("timestamp", this.timestamp);
        objectMap.put("amount", this.amount);
        objectMap.put("description", this.description);
        return objectMap.toString();
    }
}
