package com.example.expenditure;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final String TAG = "ExpenseAdapter";
    private Context context;
    private List<Expense> expenses;

    public ExpenseAdapter(Context context, List<Expense> expenses){
        this.context = context;
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.individual_expenditure_layout, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseViewHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder: called");
        Expense expense = expenses.get(position);

        holder.setAmount(expense.getAmount());
        holder.setDescription(expense.getDescription());
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }


    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView textView_amount, textView_description;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_amount = itemView.findViewById(R.id.textView_Amount);
            textView_description = itemView.findViewById(R.id.textView_Description);
        }

        public void setAmount(float amount) {
            textView_amount.setText(String.format(Locale.US,"₹ %.2f", amount));
        }

        public void setDescription(String description) {
            textView_description.setText(description);
        }
    }
}
