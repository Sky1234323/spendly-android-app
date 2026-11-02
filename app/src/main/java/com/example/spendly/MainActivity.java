package com.example.spendly;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView nav;
    private int currentTab = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            showTab(item.getItemId());
            return true;
        });

        findViewById(R.id.fabAdd).setOnClickListener(v ->
                new AddExpenseSheet().show(getSupportFragmentManager(), "add"));

        if (savedInstanceState == null) {
            showTab(R.id.nav_home);
        } else {
            currentTab = nav.getSelectedItemId();
        }
    }

    private void showTab(int id) {
        currentTab = id;
        Fragment fragment;
        if (id == R.id.nav_history) {
            fragment = new HistoryFragment();
        } else if (id == R.id.nav_insights) {
            fragment = new InsightsFragment();
        } else {
            fragment = new HomeFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commitAllowingStateLoss();
    }

    // Reloads the screen that is open, so new data shows up
    public void refresh() {
        showTab(currentTab);
    }

    public void openHistory() {
        nav.setSelectedItemId(R.id.nav_history);
    }
}