package uk.ac.abertay.cmp309.assessmentempty;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    private static final int LOCATION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_reminders);

        //Location permission check upon loading application
        int locationPermissionCheck = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        if (locationPermissionCheck != PackageManager.PERMISSION_DENIED)
        {
            Toast.makeText(this, "Location permissions have been granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    RemindersFragment remindersFragment = new RemindersFragment();
    CalendarFragment calendarFragment = new CalendarFragment();
    MapsFragment mapsFragment = new MapsFragment();

    //Navigating through the different fragments using the bottom nav bar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_reminders:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment, remindersFragment).commit();
                return true;

            case R.id.navigation_calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment, calendarFragment).commit();
                return true;

            case R.id.navigation_maps:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment, mapsFragment).commit();
                return true;
        }
        return false;
    }

}