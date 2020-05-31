package com.example.expenditure.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class Helpers {
    public static CollectionReference expenses() {
        return FirebaseFirestore.
                getInstance().
                collection("users").
                document(getUserId()).
                collection("expenses");
    }

    public static DocumentReference getExpenseReference(String document_id) {
        return expenses().document(document_id);
    }

    public static String getUserId() {
        try {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException exception) {
            // TODO: Better exception handling here
            return null;
        }
    }

    public static String getUsername() {
        // TODO: Better exception handling here
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    public static String getUserFirstName() {
        String[] username = getUsername().split(" ");
        if (username.length >= 1)
            return username[0];
        return "User";
    }
}
