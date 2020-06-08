package com.example.expenditure.Home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.expenditure.DayViewContainer;
import com.example.expenditure.History.ExpenditureListActivity;
import com.example.expenditure.R;
import com.example.expenditure.Utility.Firebase;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

    private FirestoreRecyclerAdapter recentExpendituresRecyclerViewAdapter;
    private FirestoreRecyclerAdapter monthlyExpendituresRecyclerViewAdapter;


    private CalendarView calendarView;
    private LocalDate selectedDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: started");

        AndroidThreeTen.init(this);

        initViews();
    }

    private void initViews() {
        setupTopAppBar();
//        setupRecyclerViews();
        setupCalendar();
//        setupBottomNavbar();
    }

    private void setupCalendar() {
        final LocalDate today = LocalDate.now();
//        Log.d(TAG, "today="+today);
//        selectDate(today);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setDayBinder(new DayBinder<DayViewContainer>() {

            @Override
            public DayViewContainer create(final View view) {
//                return new DayViewContainer(view);
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

                if (calendarDay.getOwner() == DayOwner.THIS_MONTH) {
                    viewContainer.makeTextViewVisible();
//                    Log.d(TAG, "calendarDay="+calendarDay.getDate());
                    if (calendarDay.getDate().equals(today)) {
                        viewContainer.updateUI(getResources().getColor(R.color.white));
                    } else if (calendarDay.getDate() == selectedDate) {
                        viewContainer.updateUI(getResources().getColor(R.color.black));
                    } else {
                        viewContainer.updateUI(getResources().getColor(R.color.black));
                    }
                } else {
                    viewContainer.setTextColor(getResources().getColor(R.color.gray));
                }
            }
        });

        YearMonth currentMonth = YearMonth.now();
//        final String[] daysOfWeek = getResources().getStringArray(R.array.week_days);
        YearMonth startMonth = currentMonth.minusMonths(10);
        YearMonth endMonth = currentMonth.plusMonths(10);
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        calendarView.setup(startMonth, endMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currentMonth);

//        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthlyViewContainer>() {
//            @Override
//            public MonthlyViewContainer create(View view) {
//                return new MonthlyViewContainer(view);
//            }
//
//            @Override
//            public void bind(MonthlyViewContainer container, CalendarMonth calendarMonth) {
//                if (container.legendLayout.getTag() == null) {
//                    container.legendLayout.setTag(calendarMonth.getYearMonth());
//                    for (int i = 0; i < container.legendLayout.getChildCount(); i++) {
//                        TextView child = (TextView) container.legendLayout.getChildAt(i);
//                        child.setText(daysOfWeek[i]);
//                        child.setTextColor(getResources().getColor(R.color.black));
//                    }
//                }
//            }
//        });


        calendarView.setMonthScrollListener(new Function1<CalendarMonth, Unit>() {
            @Override
            public Unit invoke(CalendarMonth calendarMonth) {
                Log.d(TAG, "monthScrollListener: " + calendarMonth);
                selectDate(calendarMonth.getYearMonth().atDay(1));
                updateMonthHeader(calendarMonth);
                return Unit.INSTANCE;
            }
        });

//        selectDate(today);
    }

    private void updateMonthHeader(CalendarMonth calendarMonth) {
        TextView tvMonthHeader = findViewById(R.id.calendarMonthText);
        String[] months = getResources().getStringArray(R.array.months);
        Log.d(TAG, "calendarMonth=" + calendarMonth.getMonth());
        tvMonthHeader.setText(months[calendarMonth.getMonth() - 1]);
    }

    private void monthScrollListener() {

    }

    private void selectDate(LocalDate date) {
        if (selectedDate != date) {
            calendarView.notifyDateChanged(date);
            updateAdapterForDate(date);
            selectedDate = date;
        }
        Log.d(TAG, "selectedDate=" + selectedDate);
    }

    private void updateAdapterForDate(LocalDate date) {
    }

//    private void setupMonthylExpendituresRecylerView() {
//        RecyclerView recyclerView = findViewById(R.id.rw_monthly_expenditures);
//        Query query = Firebase.monthlySummary("2020", "January");
//        monthlyExpendituresRecyclerViewAdapter = Firebase.getMonthlySummaryRecyclerViewAdapter(query);
//        recyclerView.setAdapter(monthlyExpendituresRecyclerViewAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//    }

//    private void setupRecentExpendituresRecyclerView() {
//        RecyclerView recyclerView = findViewById(R.id.rw_recent_expenses);
//        Query query = Firebase.expenses().orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
//        recentExpendituresRecyclerViewAdapter = Firebase.getExpensesRecyclerViewAdapter(query);
//        recyclerView.setAdapter(recentExpendituresRecyclerViewAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }

    private void setupRecyclerViews() {
//        setupRecentExpendituresRecyclerView();
//        setupMonthylExpendituresRecylerView();
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
//        recentExpendituresRecyclerViewAdapter.startListening();
//        monthlyExpendituresRecyclerViewAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        recentExpendituresRecyclerViewAdapter.stopListening();
//        monthlyExpendituresRecyclerViewAdapter.stopListening();
    }
}
