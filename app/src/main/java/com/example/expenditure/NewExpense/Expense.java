package com.example.expenditure.NewExpense;

import com.example.expenditure.Utility.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Expense {

    private String mDocumentName;

    private Timestamp timestamp;
    private float amount;
    private String description;
    private String mode;

    // Do not remove this, it is required by FirebaseFirestore
    public Expense() {
    }

    public Expense(float amount, String description, String mode, Date timestamp) {
        this.timestamp = (timestamp == null) ? Timestamp.now() : new Timestamp(timestamp);
        this.mode = mode;
        this.amount = amount;
        this.description = description;
    }

    public float getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getMode() {
        return mode;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public DocumentReference getId() {
        return Firebase.
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
        objectMap.put("mode", this.mode);
        return objectMap.toString();
    }
}
