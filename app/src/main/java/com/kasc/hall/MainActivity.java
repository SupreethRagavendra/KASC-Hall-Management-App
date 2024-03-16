package com.kasc.hall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kasc.hall.Booking.ShowBookingHall;
import com.kasc.hall.Booking.ShowBookings;
import com.kasc.hall.feedbacks.FeedbackActivity;
import com.kasc.hall.Facultymanagement.LoginActivity;
import com.kasc.hall.home.Developers;
import com.kasc.hall.home.HomeFragment;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public MeowBottomNavigation bottomNavigationView;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle toggle;
    public NavigationView navigationView;
    public Uri uri;
    ConstraintLayout contentView;
    public static final float END_SCALE = 0.7f;
    public Toolbar toolbar;

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public int checkedItem;
    public String selected;
    public final String CHECKEDITEM  = "checked_item";


    @SuppressLint({"ResourceAsColor", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("notification");
        FirebaseMessaging.getInstance().subscribeToTopic("Book");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);
        contentView = findViewById(R.id.contentView);
        toolbar = findViewById(R.id.appbar);

        sharedPreferences = this.getSharedPreferences("themes", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        switch (getCheckedItem()){
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.start, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("HomeScreen");
        toolbar.setTitleTextAppearance(this, R.style.poppins_bold);

        navigation();
        bottomNavigation();


        bottomNavigationView.setOnClickMenuListener(model -> {
            if (model.getId() == 1) {
                replace(new HomeFragment());
            }
            return null;
        });

        bottomNavigationView.setOnReselectListener(model -> {
            Toast.makeText(MainActivity.this, "Uh Oh! You're already here.", Toast.LENGTH_SHORT).show();
            return null;
        });

    }

    private void bottomNavigation() {
        //bottom nav code for setting id and icons
        bottomNavigationView.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_home_24));

        replace(new HomeFragment());
        bottomNavigationView.show(1, true);

    }

    private void navigation() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        animateNavigationView();
    }

    @SuppressLint("ResourceAsColor")
    private void animateNavigationView() {

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);
                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void replace(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        toggle.onOptionsItemSelected(item);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.navigation_developer:
                startActivity(new Intent(this, Developers.class));
                break;
            case R.id.navigation_theme:
                showdialog();
                break;
            case R.id.nav_exit:
                finishAffinity();
                break;
            case R.id.nav_logout:
                // Handle logout animation here
                animateLogout();
                break;

            case R.id.nav_language_english:
                setLocale("en");
                break;
            case R.id.navigation_book:
                startActivity(new Intent(this, ShowBookingHall.class));
                break;

            case R.id.navigation_booking:
                viewBookings();
                break;

            case R.id.nav_language_tamil:
                setLocale("ta");
                break;
            case R.id.navigation_shareus:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "KASC");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Download KASC - KASC Coimbatore and take charge of your college experience : " + "#");
                    startActivity(Intent.createChooser(shareIntent, "Share via :"));
                } catch (Exception e) {
                    Toast.makeText(this, "Unable to share the application", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.navigation_websites:
                uri = Uri.parse(getString(R.string.vtop_website_link));
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.navigation_feeback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;

        }
        return true;
    }

    private void viewBookings() {

        SharedPreferences preferences = getSharedPreferences("FacultyPrefs", MODE_PRIVATE);
        String facultyId = preferences.getString("facultyId", "");

        Intent intent = new Intent(MainActivity.this, ShowBookings.class);
        intent.putExtra("facultyId", facultyId);
        startActivity(intent);
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        // Restart the activity to apply the language change immediately
        Intent restartIntent = new Intent(this, MainActivity.class);
        restartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(restartIntent);
        finish();
    }

    private void showdialog() {
        String[] themes = this.getResources().getStringArray(R.array.theme);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.AlertDialog);
        builder.setTitle("Select theme");
        builder.setSingleChoiceItems(R.array.theme, getCheckedItem(), (dialog, which) -> {
            selected = themes[which];
            checkedItem = which;
        });
        builder.setPositiveButton("Okay", (dialog, which) -> {
            if (selected == null){
                selected = themes[which];
                checkedItem = which;
            }
            switch (selected){
                case "Default":
                case "இயல்புநிலை":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
                case "Dark":
                case "கருப்பு":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case "Light":
                case "விளக்கு":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
            }
            setCheckedItem(checkedItem);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private int getCheckedItem(){
        return sharedPreferences.getInt(CHECKEDITEM,0);
    }

    private void setCheckedItem(int i){
        editor.putInt(CHECKEDITEM,i);
        editor.apply();
    }

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    private void animateLogout() {
        // Fade out the content view
        contentView.animate().alpha(0).setDuration(500);

        // Slide up the navigation view
        navigationView.animate().translationY(-navigationView.getHeight()).setDuration(500);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

            finish();
        }, 3000);
    }


}
