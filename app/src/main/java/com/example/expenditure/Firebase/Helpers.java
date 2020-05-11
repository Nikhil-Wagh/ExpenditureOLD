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
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
