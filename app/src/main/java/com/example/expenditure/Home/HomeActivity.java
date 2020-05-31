package com.example.expenditure.Home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.expenditure.History.ExpenditureListActivity;
import com.example.expenditure.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: started");

        initViews();

//        mAppBarLayout.addOnOffsetChangedListener(this);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    }

    private void initViews() {
        bindActivity();
        setupBottomNavbar();
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
                        Intent intent = new Intent(HomeActivity.this, ExpenditureListActivity.class);
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

    private void bindActivity() {
        mToolbar = findViewById(R.id.toolbar);
        mTitle = findViewById(R.id.main_textview_title);
        mTitleContainer = findViewById(R.id.main_linearlayout_title);
        mAppBarLayout = findViewById(R.id.app_bar);
    }
}
