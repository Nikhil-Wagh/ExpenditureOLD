package com.example.expenditure;

import android.app.Activity;
import android.os.Bundle;

import com.example.expenditure.NewExpense.Expense;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.expenditure.dummy.DummyContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firestore.v1.Document;

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
    public static final String ARG_ITEM_ID = "item_id";
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

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            FirebaseFirestore db_instance = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            CollectionReference users_collection = db_instance.collection("users");
            DocumentReference user_doc = users_collection.document(mAuth.getCurrentUser().getUid());
            CollectionReference user_expenses = user_doc.collection("expenses");
//            Query query = user_expenses.orderBy("timestamp", Query.Direction.DESCENDING);
            DocumentReference doc_ref = user_expenses.document(getArguments().getString(ARG_ITEM_ID));
            Document doc = doc_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d(TAG, "documentSnapshot=" + documentSnapshot.toString());
                }
            })

            Log.d(TAG,  "ARG_ITEM_ID="+getArguments().getString(ARG_ITEM_ID));
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
//
//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.content);
//            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.expenditure_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.expenditure_detail)).setText(mItem.getDescription());
        }

        return rootView;
    }
}
