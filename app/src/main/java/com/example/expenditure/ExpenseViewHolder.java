package com.example.expenditure;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class ExpenseViewHolder extends RecyclerView.ViewHolder {

    private TextView textView_amount, textView_description;

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
