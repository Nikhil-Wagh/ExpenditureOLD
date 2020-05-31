package com.example.expenditure.History;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.expenditure.Firebase.Helpers;
import com.example.expenditure.NewExpense.Expense;
import com.example.expenditure.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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

    private TextView mTimestampTextView, mModeTextView;
    private EditText mAmountEditText, mDescriptionEditText, mTimestampEditText;
    private Button mSaveButton;

    private ProgressBar mProgressBar;

    private View.OnClickListener mTimestampOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            setDateTimeFromPicker();
        }
    };

    private View.OnClickListener mSaveButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Float amount;
//            try {
//                amount = getAmount();
//            } catch(IllegalArgumentException e){
//
//            }
            Map<String, Object> updates = new HashMap<>();
            float amount;
            String stramount = mAmountEditText.getText().toString();
            if (stramount.length() > 0 && !stramount.matches("[a-zA-Z]"))
                amount = Float.valueOf(stramount);
            else {
                mAmountEditText.requestFocus();
                mAmountEditText.setError("Not a valid number");
                return;
            }
            updates.put("amount", amount);


            String description = mDescriptionEditText.getText().toString();
            if (description.length() <= 0) {
                mDescriptionEditText.requestFocus();
                mDescriptionEditText.setError("Not a valid description");
                return;
            }
            updates.put("description", description);


            String mode = mModeTextView.getText().toString();
            if (mode.length() <= 0 || !isValidMode(mode)) {
                mModeTextView.requestFocus();
                mModeTextView.setError("Please select from the list");
                return;
            }
            updates.put("mode", mode);


            String str_timestamp = mTimestampTextView.getText().toString();
            Date timestamp;
            try {
                timestamp = DateFormat.getDateTimeInstance().parse(str_timestamp);
            } catch (ParseException e) {
                e.printStackTrace();
                timestamp = null;
            }
            updates.put("timestamp", timestamp);

            Log.d(TAG, "documentName=" + mItem.getDocumentName());

            mProgressBar.setVisibility(View.VISIBLE);

            Helpers.
                    getExpenseReference(mItem.getDocumentName()).
                    update(updates).
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Document udpated successfully");
                            startActivity(new Intent(getActivity(), ExpenditureListActivity.class));
                        }
                    }).
                    addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Document could not be updated", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    };

    private boolean isValidMode(String mode) {
        String[] paymentModes = getResources().getStringArray(R.array.payment_modes);
        for (String x : paymentModes) {
            if (x.equals(mode)) return true;
        }
        return false;
    }


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

        initInputFields(rootView);

        if (getArguments().containsKey(DESCRIPTION)) {
            ((TextView) rootView.findViewById(R.id.et_amount)).setText(getArguments().getString(DESCRIPTION));
        }
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            final String doc_name = getArguments().getString(ARG_ITEM_ID);
            Helpers.
                    getExpenseReference(doc_name).
                    get(Source.CACHE).
                    addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            mItem = documentSnapshot.toObject(Expense.class);
                            ((EditText) rootView.findViewById(R.id.et_amount)).setText(String.valueOf(mItem.getAmount()));
                            ((EditText) rootView.findViewById(R.id.et_description)).setText(mItem.getDescription());
//                            ((TextView) rootView.findViewById(R.id.et_timestamp)).setText(DateFormat.getDateTimeInstance().format(mItem.getTimestamp().toDate()));
                            ((TextView) rootView.findViewById(R.id.et_timestamp)).setText(DateFormat.getDateTimeInstance().format(mItem.getTimestamp().toDate()));
                            ((TextView) rootView.findViewById(R.id.tv_document_id)).setText(documentSnapshot.getId());
                            ((TextView) rootView.findViewById(R.id.tv_payment_mode)).setText(mItem.getMode());
                            setDropDownAdapter(rootView);
//                            ((TextView) rootView.findViewById(R.id.tv_month)).setText(mItem.getTimestamp().().toString());
                            mItem.setDocumentName(doc_name);
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
        return rootView;
    }

    private void setDropDownAdapter(View root) {
        String[] paymentModes = getResources().getStringArray(R.array.payment_modes);
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), R.layout.dropdown_listitem, paymentModes);
        AutoCompleteTextView editTextPaymentMode = root.findViewById(R.id.tv_payment_mode);
        editTextPaymentMode.setAdapter(adapter);
    }

    public Calendar setDateTimeFromPicker() {
        final Calendar date;
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        mTimestampEditText.setText(DateFormat.getDateTimeInstance().format(date.getTime()));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
        return date;
    }

    private void initInputFields(View root) {
        mAmountEditText = root.findViewById(R.id.et_amount);
        mDescriptionEditText = root.findViewById(R.id.et_description);
        mModeTextView = root.findViewById(R.id.tv_payment_mode);
        mTimestampEditText = root.findViewById(R.id.et_timestamp);

        mProgressBar = root.findViewById(R.id.indeterminate_progressbar);

        mSaveButton = root.findViewById(R.id.button_save);
        mSaveButton.setOnClickListener(mSaveButtonOnClickListener);

        root.findViewById(R.id.et_timestamp).setOnClickListener(mTimestampOnClickListener);
    }
}
