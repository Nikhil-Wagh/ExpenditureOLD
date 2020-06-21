package com.example.expenditure.Home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenditure.DayViewContainer;
import com.example.expenditure.History.ExpenditureListActivity;
import com.example.expenditure.NewExpense.Expense;
import com.example.expenditure.NewExpense.ExpenseViewHolder;
import com.example.expenditure.R;
import com.example.expenditure.Utility.Firebase;
import com.example.expenditure.Utility.Helpers;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.Query;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.DayBinder;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;
import org.threeten.bp.temporal.WeekFields;

import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private FirestoreRecyclerAdapter dailyExpendituresRecyclerViewAdapter;
    private RecyclerView dailyExpendituresRecyclerView;

    private TextView selectedDateTextView;
    private TextView noDataTextView;
    private TextView tvMonthHeader;

    private CalendarView calendarView;

    private LocalDate selectedDate = null;
    private LocalDate oldSelectedDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: started");

        AndroidThreeTen.init(this);

        initViews();
//        selectToday();
    }

//    private void selectToday() {
//        LocalDate today = LocalDate.now();
//        Log.d(TAG, "selectToday called");
//        selectDate(today);
//    }

    private void initViews() {
        setupViews();
        setupTopAppBar();
        setupRecyclerView();
        setupCalendar();
//        Need to improve this
//        setupBottomNavbar();
    }

    private void setupViews() {
//        selectedDateTextView = findViewById(R.id.tv_selected_date);
        noDataTextView = findViewById(R.id.tv_no_data);
        tvMonthHeader = findViewById(R.id.calendarMonthText);
    }

    private void setupRecyclerView() {
        dailyExpendituresRecyclerView = findViewById(R.id.rw_monthly_expenditures);
    }

    private void setupCalendar() {
        final LocalDate today = LocalDate.now();
        calendarView = findViewById(R.id.calendarView);
        calendarView.setDayBinder(new DayBinder<DayViewContainer>() {

            @Override
            public DayViewContainer create(final View view) {
                final DayViewContainer viewContainer = new DayViewContainer(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewContainer.getOwner() == DayOwner.THIS_MONTH)
                            selectDate(viewContainer.getDay().getDate());
                    }
                });
                return viewContainer;
            }

            @Override
            public void bind(DayViewContainer viewContainer, CalendarDay calendarDay) {
                viewContainer.setDay(calendarDay);
                viewContainer.setDateTextView(calendarDay.getDate().getDayOfMonth());
//
                if (calendarDay.getOwner() == DayOwner.THIS_MONTH) {
                    if (calendarDay.getDate().equals(today)) {
                        viewContainer.updateUI(getResources().getColor(R.color.white), getResources().getColor(R.color.color_today));
//                    }
                    } else if (calendarDay.getDate() == selectedDate) {
                        viewContainer.updateUI(getResources().getColor(R.color.white), getResources().getColor(R.color.color_selected_bg));
                    } else {
                        viewContainer.updateUI(getResources().getColor(R.color.black), getResources().getColor(R.color.white));
                    }
                } else {
                    viewContainer.updateUI(getResources().getColor(R.color.gray), getResources().getColor(R.color.white));
                }
            }
        });

        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(10);
        YearMonth endMonth = currentMonth.plusMonths(10);
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        calendarView.setup(startMonth, endMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currentMonth);

        calendarView.setMonthScrollListener(new Function1<CalendarMonth, Unit>() {
            @Override
            public Unit invoke(CalendarMonth calendarMonth) {
//                Log.d(TAG, "monthScrollListener: " + calendarMonth);
                updateMonthHeader(calendarMonth);
                return Unit.INSTANCE;
            }
        });

        selectDate(today);
    }

    private void selectDate(LocalDate date) {
        Log.d(TAG, "selectDate :: date = " + date);
        Log.d(TAG, "selectDate :: oldSelectedDate = " + oldSelectedDate);
        if (date != oldSelectedDate) {
            oldSelectedDate = selectedDate;
            selectedDate = date;
            updateDateHeader(selectedDate);
            if (oldSelectedDate != null)
                calendarView.notifyDateChanged(oldSelectedDate);
            calendarView.notifyDateChanged(selectedDate);
            updateExpensesForDate(date);
            Log.d(TAG, "selectDate :: inside if");
        }
        Log.d(TAG, "selectedDate=" + selectedDate);
    }

    private void updateDateHeader(LocalDate date) {
        Log.d(TAG, "updateDateHeader :: date = " + date);
        Log.d(TAG, "Old text = " + tvMonthHeader.getText().toString());
        tvMonthHeader.setText(Helpers.localDateToStringDate(date));
    }

    private void updateMonthHeader(CalendarMonth calendarMonth) {
        TextView tvMonthHeader = findViewById(R.id.calendarMonthText);
        String[] months = getResources().getStringArray(R.array.months);
        String header = getYearAndMonthHeader(months[calendarMonth.getMonth() - 1], calendarMonth.getYear());
        tvMonthHeader.setText(header);
        Log.d(TAG, "updateMonthHeader :: header = " + header);
    }

    private String getYearAndMonthHeader(String month, int year) {
        return month + ", " + year;
    }

    private void updateExpensesForDate(LocalDate date) {
        Query query = Firebase.selectSummaryForDate(date);

        if (dailyExpendituresRecyclerViewAdapter != null)
            dailyExpendituresRecyclerViewAdapter.stopListening();

        dailyExpendituresRecyclerViewAdapter = new FirestoreRecyclerAdapter<Expense, ExpenseViewHolder>(Firebase.getOptions(query)) {

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder called");
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.expenditure_list_content, parent, false);
                return new ExpenseViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ExpenseViewHolder expenseViewHolder, int position, @NonNull final Expense expense) {
                Log.d(TAG, "onBindViewHolder position = " + position);
                expense.setDocumentName(getSnapshots().getSnapshot(position).getId());
                expenseViewHolder.setAmount(expense.getAmount());
                expenseViewHolder.setDescription(expense.getDescription());
                expenseViewHolder.setTimestamp(expense.getTimestamp());
                expenseViewHolder.itemView.setTag(expense);
                expenseViewHolder.itemView.setOnClickListener(Firebase.expendituresOnClickListener());
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                noDataTextView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };

        dailyExpendituresRecyclerView.setAdapter(dailyExpendituresRecyclerViewAdapter);
        dailyExpendituresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dailyExpendituresRecyclerViewAdapter.startListening();
        Log.d(TAG, "recycler View count = " + dailyExpendituresRecyclerView.getChildCount());
    }

    private void setupTopAppBar() {
        MaterialToolbar mToolBar = findViewById(R.id.toolbar);
        mToolBar.setTitle(getString(R.string.greeting_user, Firebase.getUsername()));

        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_notifications: {
                        // TODO: implement me
                    }
                    case R.id.item_search: {
                        // TODO: implement me
                    }
                }
                return true;
            }
        });
    }

    private void setupBottomNavbar() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_home: {
                        break;
                    }
                    case R.id.page_history: {
                        Intent intent = new Intent(
                                HomeActivity.this,
                                ExpenditureListActivity.class);
                        startActivity(intent);
                        break;
                    }
//                    case R.id.page_add_new: {
//                        Intent intent = new Intent(HomeActivity.this, AddExpenseActivity.class);
//                        startActivity(intent);
//                        break;
//                    }
//                    case R.id.page_analytics: {
//                        Intent intent = new Intent(HomeActivity.this, AnalyticsActivity.class);
//                        startActivity(intent);
//                        break;
//                    }
//                    case R.id.page_settings: {
//                        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
//                        startActivity(intent);
//                        break;
//                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        dailyExpendituresRecyclerViewAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
