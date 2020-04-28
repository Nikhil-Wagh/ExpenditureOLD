package com.example.expenditure.Home;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import com.example.expenditure.Login.LoginActivity;
import com.example.expenditure.NewExpense.Expense;
import com.example.expenditure.NewExpense.ExpenseViewHolder;
import com.example.expenditure.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class HomeActivity extends AppCompatActivity {

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


        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        final View expenseForm = getLayoutInflater().inflate(R.layout.add_expense_form, toolbarLayout, false);
        toolbarLayout.addView(expenseForm);


        initComponents();

        final RecyclerView recyclerView = findViewById(R.id.expense_list);

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
            protected void onBindViewHolder(@NonNull final ExpenseViewHolder expenseViewHolder, int position, @NonNull final Expense expense) {
                Log.d(TAG, "onBindViewHolder position = " + position);
                expenseViewHolder.setAmount(expense.getAmount());
                expenseViewHolder.setDescription(expense.getDescription());
                expenseViewHolder.setTimestamp(expense.getTimestamp());
                expenseViewHolder.button_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = expenseViewHolder.getAdapterPosition();
                        Log.d(TAG, "onBindViewHolder :: onClick :: position = " + position);
                        getSnapshots().getSnapshot(position).getReference().delete();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float amount;
                String stramount = AmountEditText.getText().toString();
                if (stramount.length() > 0 && !stramount.matches("[a-zA-Z]"))
                    amount = Float.valueOf(stramount);
                else {
                    AmountEditText.requestFocus();
                    AmountEditText.setError("Not a valid number");
                    return;
                }

                String description = DescriptionEditText.getText().toString();
                if (description.length() <= 0) {
                    DescriptionEditText.requestFocus();
                    DescriptionEditText.setError("Not a valid description");
                    return;
                }

                String str_timestamp = TimestampTextView.getText().toString();
                Date timestamp;
                try {
                    timestamp = DateFormat.getDateTimeInstance().parse(str_timestamp);
                } catch (ParseException e) {
                    e.printStackTrace();
                    timestamp = null;
                }
                Expense expense = new Expense(amount, description, timestamp);
                Log.d(TAG, "expense = " + expense.toString());

                clearResponses();
                saveNewExpenseToDB(user_expenses, expense);
            }
        });

        EditTimestampButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateTimeFromPicker();
            }
        });
    }

    private void clearResponses() {
        closeKeyboard();
        AmountEditText.setText(null);
        DescriptionEditText.setText(null);
        TimestampTextView.setText(now());
    }

    public Calendar setDateTimeFromPicker() {
        final Calendar date;
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(HomeActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(HomeActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        // TODO: Show proper alert
                        Toast.makeText(HomeActivity.this, "Error while saving expense", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void initComponents() {
        SaveButton = findViewById(R.id.button_save);
        AmountEditText = findViewById(R.id.editText_Amount);
        DescriptionEditText = findViewById(R.id.editText_Description);
        EditTimestampButton = findViewById(R.id.button_EditTimestamp);
        TimestampTextView = findViewById(R.id.textView_Timestamp);
        TimestampTextView.setText(now());
    }

    private String now() {
        return DateFormat.getDateTimeInstance().format(new Date());
    }

    private String getUserId() {
        return mAuth.getCurrentUser().getUid();
    }

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        updateUI(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
