package com.example.expenditure.History;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenditure.Home.HomeActivity;
import com.example.expenditure.NewExpense.Expense;
import com.example.expenditure.NewExpense.ExpenseViewHolder;
import com.example.expenditure.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        setupBottomNavbar();

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

        Log.d(TAG, "mTwoPane=" + mTwoPane);

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
                if (item != null) {
                    if (mTwoPane) {
                        Toast.makeText(mParentActivity, "Hello World", Toast.LENGTH_SHORT).show();
                        Bundle arguments = new Bundle();
                        arguments.putSerializable(ExpenditureDetailFragment.ARG_ITEM_ID, item.getDocumentName());
                        ExpenditureDetailFragment fragment = new ExpenditureDetailFragment();
                        fragment.setArguments(arguments);
                        mParentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.expenditure_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = view.getContext();
                        Intent intent = new Intent(context, ExpenditureDetailActivity.class);
                        intent.putExtra(ExpenditureDetailFragment.ARG_ITEM_ID, item.getDocumentName());
                        context.startActivity(intent);
                    }
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
                View view = layoutInflater.inflate(R.layout.expenditure_list_content, parent, false);
                return new ExpenseViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ExpenseViewHolder expenseViewHolder, int position, @NonNull final Expense expense) {
                Log.d(TAG, "onBindViewHolder position = " + position);

//                Log.d(TAG, "SEE THIS"+getSnapshots().getSnapshot(position).getId());

                expense.setDocumentName(getSnapshots().getSnapshot(position).getId());
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

    private void setupBottomNavbar() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_history);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_home: {
                        Intent intent = new Intent(ExpenditureListActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case R.id.page_history: {
                        break;
                    }
//                    case R.id.page_add_new: {
//                        Intent intent = new Intent(HomeActivity.this, AddExpenseActivity.class);
//                        startActivity(intent);
//                        break;
//                    }
//                    case R.id.page_analytics: {
//                        Intent intent = new Intent(HomeActivity.this, AnalyticsActivity.class);
//                        startActivity(intent);
//                        break;
//                    }
//                    case R.id.page_settings: {
//                        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
//                        startActivity(intent);
//                        break;
//                    }
                }
                return true;
            }
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            navigateUpTo(new Intent(this, HomeActivity.class));
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

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
