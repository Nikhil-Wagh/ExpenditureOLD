package com.example.expenditure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ScrollingActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private Button SaveButton;
    private EditText AmountEditText, DescriptionEditText;

    private FirestoreRecyclerAdapter adapter;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Log.d(TAG, "onCreate: started");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initComponents();

        RecyclerView recyclerView = findViewById(R.id.expense_list);

        FirebaseFirestore db_instance = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        CollectionReference users_collection = db_instance.collection("users");
        DocumentReference user_doc = users_collection.document(getUserId());
        final CollectionReference user_expenses = user_doc.collection("expenses");
        Query query = user_expenses.orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Expense> options = new FirestoreRecyclerOptions.Builder<Expense>()
                .setQuery(query, Expense.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Expense, ExpenseViewHolder>(options) {
            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.individual_expenditure_layout, parent, false);
                return new ExpenseViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder expenseViewHolder, int position, @NonNull Expense expense) {
                Log.d(TAG, "onBindViewHolder expense = " + expense.toString());
                expenseViewHolder.setAmount(expense.getAmount());
                expenseViewHolder.setDescription(expense.getDescription());
                expenseViewHolder.setTimestamp(expense.getTimestamp());
            }
        };

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float amount = Float.valueOf(AmountEditText.getText().toString());
                String description = DescriptionEditText.getText().toString();
                Expense expense = new Expense(amount, description);
                saveNewExpenseToDB(user_expenses, expense);
            }
        });
    }

    private void saveNewExpenseToDB(CollectionReference user_expenses, Expense expense) {
        user_expenses.add(expense)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i(TAG, "Document saved successfully, document reference = " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Show proper alert
                        Toast.makeText(ScrollingActivity.this, "Error while saving expense", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void initComponents() {
        SaveButton = findViewById(R.id.button_save);
        AmountEditText = findViewById(R.id.editText_Amount);
        DescriptionEditText = findViewById(R.id.editText_Description);
    }

    private String getUserId() {
        return mAuth.getCurrentUser().getUid();
    }

    private void isUserLoggedIn() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            signOutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        updateUI(null);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        isUserLoggedIn();
        adapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            Log.d(TAG, "onStart: no User");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
