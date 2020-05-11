package com.example.expenditure;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.expenditure.Firebase.Helpers;
import com.example.expenditure.NewExpense.Expense;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.text.DateFormat;


/**
 * A fragment representing a single Expenditure detail screen.
 * This fragment is either contained in a {@link ExpenditureListActivity}
 * in two-pane mode (on tablets) or a {@link ExpenditureDetailActivity}
 * on handsets.
 */
public class ExpenditureDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "document_id";
    public static final String TIMESTAMP = "timestamp";
    public static final String AMOUNT = "amount";
    public static final String DESCRIPTION = "description";
    private static final String TAG = ExpenditureDetailFragment.class.getName();

    /**
     * The dummy content this fragment is presenting.
     */
    private Expense mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExpenditureDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, String.valueOf(getArguments()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.expenditure_detail, container, false);

        if (getArguments().containsKey(DESCRIPTION)) {
            ((TextView) rootView.findViewById(R.id.et_amount)).setText(getArguments().getString(DESCRIPTION));
        }
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            Helpers.
                    getExpenseReference(getArguments().getString(ARG_ITEM_ID)).
                    get(Source.CACHE).
                    addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            mItem = documentSnapshot.toObject(Expense.class);
                            ((EditText) rootView.findViewById(R.id.et_amount)).setText(String.valueOf(mItem.getAmount()));
                            ((EditText) rootView.findViewById(R.id.et_description)).setText(mItem.getDescription());
                            ((TextView) rootView.findViewById(R.id.tv_timestamp)).setText(DateFormat.getDateTimeInstance().format(mItem.getTimestamp().toDate()));
                            ((TextView) rootView.findViewById(R.id.tv_document_id)).setText(documentSnapshot.getId());
//                            ((TextView) rootView.findViewById(R.id.tv_month)).setText(mItem.getTimestamp().().toString());
                            Log.d(TAG, mItem.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error occurred while fetching documents.");
                        }
                    });
        }

        String[] paymentModes = getResources().getStringArray(R.array.payment_modes);
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), R.layout.dropdown_listitem, paymentModes);
        AutoCompleteTextView editTextPaymentMode = rootView.findViewById(R.id.tv_autocomplete);
        editTextPaymentMode.setText(paymentModes[0]);
        editTextPaymentMode.setAdapter(adapter);

        return rootView;
    }
}
