package com.example.expenditure;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                expenseAdapter.notifyItemChanged(position);
                Log.d(TAG, tempExpenses.toString());
                Log.d(TAG, "Save Button: Data saved on index: " + position);
            }
        });
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

        mAuth = FirebaseAuth.getInstance();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
