package com.example.expenditure;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.expenditure.NewExpense.Expense;
import com.example.expenditure.NewExpense.ExpenseViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.expenditure.dummy.DummyContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

/**
 * An activity representing a list of Expenditures. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ExpenditureDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ExpenditureListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private static String TAG = ExpenditureListActivity.class.getName();
    private boolean mTwoPane;

    private FirebaseAuth mAuth;
    private FirestoreRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.expenditure_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.expenditure_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        Query query = getQuery();

        FirestoreRecyclerOptions<Expense> options = new FirestoreRecyclerOptions.Builder<Expense>()
                .setQuery(query, Expense.class)
                .build();

        final ExpenditureListActivity mParentActivity = this;

        final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Expense item = (Expense) view.getTag();
                Log.d(TAG, "getTag() = " + view.getTag());
                if (item != null)
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ExpenditureDetailFragment.ARG_ITEM_ID, item.id.toString());
                    ExpenditureDetailFragment fragment = new ExpenditureDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.expenditure_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ExpenditureDetailActivity.class);
                    intent.putExtra(ExpenditureDetailFragment.ARG_ITEM_ID, item.id.toString());

                    context.startActivity(intent);
                }
                else {
                    Log.e(TAG, "item is null");
                }
            }
        };

        mAdapter = new FirestoreRecyclerAdapter<Expense, ExpenseViewHolder>(options) {

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.individual_expenditure_layout, parent, false);
                return new ExpenseViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ExpenseViewHolder expenseViewHolder, int position, @NonNull final Expense expense) {
                Log.d(TAG, "onBindViewHolder position = " + position);

                expense.setId(getSnapshots().getSnapshot(position).getReference());
                expenseViewHolder.setAmount(expense.getAmount());
                expenseViewHolder.setDescription(expense.getDescription());
                expenseViewHolder.setTimestamp(expense.getTimestamp());
                expenseViewHolder.button_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = expenseViewHolder.getAdapterPosition();
                        Log.d(TAG, "onBindViewHolder :: onClick :: reference = " + expense.getId().toString());
                        expense.getId().delete();
                    }
                });
                expenseViewHolder.itemView.setTag(expense);
                expenseViewHolder.itemView.setOnClickListener(mOnClickListener);
            }
        };

        recyclerView.setAdapter(mAdapter);
    }

    private Query getQuery() {
        FirebaseFirestore db_instance = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        CollectionReference users_collection = db_instance.collection("users");
        DocumentReference user_doc = users_collection.document(getUserId());
        final CollectionReference user_expenses = user_doc.collection("expenses");
        Query query = user_expenses.orderBy("timestamp", Query.Direction.DESCENDING);
        return query;
    }

    private String getUserId() {
        return mAuth.getCurrentUser().getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
