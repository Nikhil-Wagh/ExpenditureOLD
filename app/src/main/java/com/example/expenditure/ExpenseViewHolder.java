package com.example.expenditure;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.util.Locale;

public class ExpenseViewHolder extends RecyclerView.ViewHolder {
    private String TAG = "ExpenseViewHolder";

    private TextView textView_amount, textView_description, textView_timestamp;

    public ExpenseViewHolder(@NonNull View itemView) {
        super(itemView);

        textView_amount = itemView.findViewById(R.id.textView_amount);
        textView_description = itemView.findViewById(R.id.textView_description);
        textView_timestamp = itemView.findViewById(R.id.textView_timestamp);
    }

    public void setAmount(float amount) {
        textView_amount.setText(String.format(Locale.US,"₹ %.2f", amount));
    }

    public void setDescription(String description) {
        textView_description.setText(description);
    }

//  d MMM yyyy HH:mm:ss
    public void setTimestamp(Timestamp timestamp) {
//        String pattern = "d MMMMM yyyy HH:mm:ss";
        Log.d(TAG, "timestamp = " + timestamp);
        textView_timestamp.setText(DateFormat.getDateTimeInstance().format(timestamp.getSeconds()));
    }

}
