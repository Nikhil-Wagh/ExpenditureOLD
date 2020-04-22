package com.example.expenditure;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Visibility;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ScrollingActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private Button SaveButton;
    ImageButton EditTimestampButton;
    private EditText AmountEditText, DescriptionEditText;
    private TextView TimestampTextView;

    private FirestoreRecyclerAdapter adapter;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Log.d(TAG, "onCreate: started");


//        final Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

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
//                Log.d(TAG, "onBindViewHolder expense = " + expense.toString());
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
                // TODO: validate inputs
                float amount = Float.valueOf(AmountEditText.getText().toString());
                String description = DescriptionEditText.getText().toString();
                String str_timestamp = TimestampTextView.getText().toString();
                Date timestamp = null;
                try {
                    timestamp = DateFormat.getDateTimeInstance().parse(str_timestamp);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Expense expense = new Expense(amount, description, timestamp);
                Log.d(TAG, "expense = " + expense.toString());
                saveNewExpenseToDB(user_expenses, expense);
                // TODO: Clear responses
            }
        });

        EditTimestampButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateTimeFromPicker();
            }
        });
    }

    public Calendar setDateTimeFromPicker() {
        final Calendar date;
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(ScrollingActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(ScrollingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        TimestampTextView.setText(DateFormat.getDateTimeInstance().format(date.getTime()));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
        return date;
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
        EditTimestampButton = findViewById(R.id.button_EditTimestamp);
        TimestampTextView = findViewById(R.id.textView_Timestamp);
//        TimestampTextView.setText(DateFormat.getDateTimeInstance().format(new Date()));
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
//        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
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
