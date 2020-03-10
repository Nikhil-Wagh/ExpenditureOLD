package com.example.expenditure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScrollingActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private Button SaveButton;
    private EditText AmountEditText, DescriptionEditText;
    private List<Expense> tempExpenses;
    private ExpenseAdapter expenseAdapter;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        isUserLoggedIn();

        Log.d(TAG, "onCreate: started");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tempExpenses = loadList();

        initRecyclerView(tempExpenses);

        initComponents();

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Save Button: Saving data");
                float amount = Float.valueOf(AmountEditText.getText().toString());
                String description = DescriptionEditText.getText().toString();

                Expense expense = new Expense((int) System.currentTimeMillis(), amount, description);
                tempExpenses.add(expense);

                int position = 0; //tempExpenses.size() - 1;
                expenseAdapter.notifyItemChanged(position);
                Log.d(TAG, tempExpenses.toString());
                Log.d(TAG, "Save Button: Data saved on index: " + position);

                saveNewExpense(expense);
            }
        });
    }

    private List<Expense> loadList() {
        final List<Expense> mList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("expenses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Map<String, Object> data = documentSnapshot.getData();
                                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                Expense e = new Expense(Integer.parseInt(String.valueOf(data.get("id"))), Float.parseFloat(String.valueOf(data.get("amount"))), data.get("description").toString());
                                mList.add(e);
                            }
                        }
                    }
                });
        Log.d(TAG, "mList : " + mList.toString());
        return mList;
    }

    private void saveNewExpense(Expense expense) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("expenses")
                .add(expense)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "saveNewExpense: saved successfully");
                        } else {
                            Log.d(TAG, "saveNewExpense: failed");
                        }
                        if (task.isCanceled()) {
                            Log.d(TAG, "saveNewExpense: cancelled");
                        }
                    }
                });
    }

    private void isUserLoggedIn() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void initComponents() {
        Log.d(TAG, "initializing components");
        SaveButton = findViewById(R.id.button_save);
        AmountEditText = findViewById(R.id.editText_Amount);
        DescriptionEditText = findViewById(R.id.editText_Description);
    }

    private void initRecyclerView(List<Expense> tempExpenses) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_Content);
        recyclerView.setHasFixedSize(true);

        expenseAdapter = new ExpenseAdapter(this, tempExpenses);
        recyclerView.setAdapter(expenseAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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

        isUserLoggedIn();
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
