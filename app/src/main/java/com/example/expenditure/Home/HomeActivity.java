package com.example.expenditure.Home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expenditure.Firebase.Helpers;
import com.example.expenditure.History.ExpenditureListActivity;
import com.example.expenditure.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: started");

        initViews();
    }

    private void initViews() {
        setupTopAppBar();
        setupBottomNavbar();
    }

    private void setupTopAppBar() {
        MaterialToolbar mToolBar = findViewById(R.id.toolbar);
        mToolBar.setTitle(getString(R.string.greeting_user, Helpers.getUsername()));
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
}
