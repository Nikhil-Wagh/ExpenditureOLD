package com.example.expenditure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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

        Log.d(TAG, "onCreate: started");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        NestedScrollView content_scrolling = findViewById(R.id.content_scrolling);

        tempExpenses = sampleList();

        initRecyclerView();

        initComponents();

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Save Button: Saving data");
                float amount = Float.valueOf(AmountEditText.getText().toString());
                String description = DescriptionEditText.getText().toString();

                Expense expense = new Expense((int)System.currentTimeMillis()/1000, amount, description);
                tempExpenses.add(expense);

                int position = tempExpenses.size() - 1;
                if (position > 0)
                    expenseAdapter.notifyItemChanged(position);

                saveExpenseInDB(expense);
            }
        });
    }

    private void saveExpenseInDB(Expense expense) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("users")
                .document("expenses")
                .collection(expense.getIdToken())
                .add(expense)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e(TAG, "saveExpenseInDB: saved successfully: " + documentReference.getId());
                        Toast.makeText(ScrollingActivity.this, "Your expense saved successfully", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "saveExpenseInDB: saving expense to Firestore failed");
                        Toast.makeText(ScrollingActivity.this, "Some error occurred", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void isUserLoggedIn() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private List<Expense> sampleList() {
        List mList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mList.add(new Expense(0, 20, "Auto"));
            mList.add(new Expense(1, 200, "Food"));
        }
        return mList;
    }

    private void initComponents() {
        Log.d(TAG, "initializing components");
        SaveButton = findViewById(R.id.button_save);
        AmountEditText = findViewById(R.id.editText_Amount);
        DescriptionEditText = findViewById(R.id.editText_Description);
    }

    private void initRecyclerView() {
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
