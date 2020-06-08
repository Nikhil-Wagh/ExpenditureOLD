package com.example.expenditure.Utility;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.expenditure.History.ExpenditureDetailActivity;
import com.example.expenditure.History.ExpenditureDetailFragment;
import com.example.expenditure.NewExpense.Expense;
import com.example.expenditure.NewExpense.ExpenseViewHolder;
import com.example.expenditure.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

    public static FirestoreRecyclerAdapter getExpensesRecyclerViewAdapter(Query query) {
        FirestoreRecyclerOptions<Expense> options = new FirestoreRecyclerOptions.Builder<Expense>()
                .setQuery(query, Expense.class)
                .build();

        final View.OnClickListener expendituresOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Expense item = (Expense) view.getTag();
                if (item == null) {
                    // TODO: Think of something better here
                }
                Context context = view.getContext();
                Intent intent = new Intent(context, ExpenditureDetailActivity.class);
                intent.putExtra(ExpenditureDetailFragment.ARG_ITEM_ID, item.getDocumentName());
                context.startActivity(intent);
            }
        };

        return new FirestoreRecyclerAdapter<Expense, ExpenseViewHolder>(options) {

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder called");
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.expenditure_list_content, parent, false);
                return new ExpenseViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ExpenseViewHolder expenseViewHolder, int position, @NonNull final Expense expense) {
                Log.d(TAG, "onBindViewHolder position = " + position);
                expense.setDocumentName(getSnapshots().getSnapshot(position).getId());
                expenseViewHolder.setAmount(expense.getAmount());
                expenseViewHolder.setDescription(expense.getDescription());
                expenseViewHolder.setTimestamp(expense.getTimestamp());
                expenseViewHolder.itemView.setTag(expense);
                expenseViewHolder.itemView.setOnClickListener(expendituresOnClickListener);
            }
        };
    }

    public static CollectionReference monthlySummary(String year, String month) {
        return FirebaseFirestore.
                getInstance().
                collection("users").
                document(getUserId()).
                collection("monthly_summary")
                .document(year)
                .collection(month);
    }

    public static FirestoreRecyclerAdapter getMonthlySummaryRecyclerViewAdapter(Query query) {
        return null;
    }
}
