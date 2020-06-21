package com.example.expenditure.Utility;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.expenditure.History.ExpenditureDetailActivity;
import com.example.expenditure.History.ExpenditureDetailFragment;
import com.example.expenditure.NewExpense.Expense;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.threeten.bp.LocalDate;

import java.util.GregorianCalendar;

public class Firebase {

    private static final String TAG = "FirebaseUtility";

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

    private static FirebaseUser getCurrentUser() {
        try {
            return FirebaseAuth.getInstance().getCurrentUser();
        } catch (NullPointerException null_pointer_exception) {
            // TODO: Better exception handling here, should return to Login Activity and Login again
            return null;
        }
    }


    @SuppressWarnings("ConstantConditions")
    public static String getUserId() {
        return getCurrentUser().getUid();
    }

    @SuppressWarnings("ConstantConditions")
    public static String getUsername() {
        return getCurrentUser().getDisplayName();
    }

    public static String getUserFirstName() {
        String[] username = getUsername().split(" ");
        if (username.length >= 1)
            return username[0];
        return "User";
    }

    @SuppressWarnings("ConstantConditions")
    public static String getEmail() {
        return getCurrentUser().getEmail();
    }

    public static FirestoreRecyclerOptions<Expense> getOptions(Query query) {
        FirestoreRecyclerOptions<Expense> options = new FirestoreRecyclerOptions.Builder<Expense>()
                .setQuery(query, Expense.class)
                .build();
        return options;
    }

    public static View.OnClickListener expendituresOnClickListener() {
        View.OnClickListener expendituresOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Item Clicked", Toast.LENGTH_SHORT).show();
                Expense item = (Expense) view.getTag();
                if (item == null) {
                    Log.e(TAG, "Firebase:: item null");
                    // TODO: Think of something better here
                }
                Context context = view.getContext();
                Intent intent = new Intent(context, ExpenditureDetailActivity.class);
                intent.putExtra(ExpenditureDetailFragment.ARG_ITEM_ID, item.getDocumentName());
                context.startActivity(intent);
            }
        };
        return expendituresOnClickListener;
    }

    public static Query selectSummaryForDate(LocalDate date) {
        Timestamp currentDay = getTimestamp(date);
        Log.d(TAG, "currentDay=" + currentDay);

        Timestamp nextDay = getTimestamp(date.plusDays(1));
        Log.d(TAG, "nextDay=" + nextDay);

        Query query = Firebase
                .expenses()
                .orderBy("timestamp")
                .whereGreaterThanOrEqualTo("timestamp", currentDay)
                .whereLessThan("timestamp", nextDay);
        return query;
    }

    public static Timestamp getTimestamp(LocalDate date) {
        int secInDay = 86400;
        int offset = new GregorianCalendar().getTimeZone().getRawOffset() / (1000);
        return new Timestamp(date.toEpochDay() * secInDay - offset, 0);
    }
}
