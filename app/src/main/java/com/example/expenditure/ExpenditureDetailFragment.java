package com.example.expenditure;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.expenditure.Firebase.Helpers;
import com.example.expenditure.NewExpense.Expense;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Source;


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
                            ((TextView) rootView.findViewById(R.id.et_amount)).setText(mItem.getDescription());
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
        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.expenditure_detail)).setText(mItem.getDescription());
//        } else {
//            Log.e(TAG, "mItem is null");
//        }

        return rootView;
    }
}
