package com.example.expensetracker;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    private com.example.expensetracker.DashBoardFragment dashBoardFragment;
    private IncomeFragment incomeFragment;
    private com.example.expensetracker.ExpenseFragment expenseFragment;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Expense Tracker");
        setSupportActionBar(toolbar);

        LottieAnimationView animationView = findViewById(R.id.lottieAnimationView);

        // Start animation programmatically
        animationView.playAnimation();

        // Stop animation
        animationView.cancelAnimation();

        // Control looping
        animationView.setRepeatCount(LottieDrawable.INFINITE); // Lo

        mAuth= FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNavigationbar);
        frameLayout = findViewById(R.id.main_frame);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.naView);
        navigationView.setNavigationItemSelectedListener(this);

        dashBoardFragment = new com.example.expensetracker.DashBoardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new com.example.expensetracker.ExpenseFragment();

        setFragment(dashBoardFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.dashboard) {
                    setFragment(dashBoardFragment);
                    bottomNavigationView.setItemBackgroundResource(R.color.dashboard_color);
                    return true;
                } else if (item.getItemId() == R.id.income) {
                    setFragment(incomeFragment);
                    bottomNavigationView.setItemBackgroundResource(R.color.income_color);
                    return true;
                } else if (item.getItemId() == R.id.expense) {
                    setFragment(expenseFragment);
                    bottomNavigationView.setItemBackgroundResource(R.color.expense_color);
                    return true;
                }  else {
                    return false;
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    public void displaySelectedListner(int itemId) {
        Fragment fragment = null;

        if (itemId == R.id.dashboard) {
            fragment = new com.example.expensetracker.DashBoardFragment();
        } else if (itemId == R.id.income) {
            fragment = new IncomeFragment();
        } else if (itemId == R.id.expense) {
            fragment = new com.example.expensetracker.ExpenseFragment();
        } else if (itemId == R.id.logout) {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListner(item.getItemId());
        return true;
    }
}




