package uk.ac.abertay.cmp309.assessmentempty;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import uk.ac.abertay.cmp309.assessmentempty.databinding.ActivityMarkMapBinding;

public class MarkMapActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private String info, date, time;
    private int type;
    private LatLng location;
    MarkerOptions markerOptions = new MarkerOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uk.ac.abertay.cmp309.assessmentempty.databinding.ActivityMarkMapBinding binding = ActivityMarkMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Getting extras from AddReminder activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getInt("type");
            Log.i("tag", "the type is " + type);
            info = extras.getString("info");
            Log.i("tag", "the info is " + info);
            date = extras.getString("date");
            Log.i("tag", "the date is " + date);
            time = extras.getString("time");
            Log.i("tag", "the time is " + time);
        }

        //Button listeners
        Button btnconfirm = findViewById(R.id.confirmButton);
        btnconfirm.setOnClickListener(this);
        Button btncancel = findViewById(R.id.cancelmapButton);
        btncancel.setOnClickListener(this);
        Button btnremove = findViewById(R.id.removemarkerButton);
        btnremove.setOnClickListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //On map ready
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;

        //Get users location and set map view to this

        FusedLocationProviderClient mLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        mLocationProvider.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //null if location turned off or not recorded
                        if (location != null)
                        {
                            double userlat = location.getLatitude();
                            double userlong = location.getLongitude();
                            float zoom = 15;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userlat, userlong), zoom));

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Location could not be found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        //When the map is clicked, add a marker.

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                location = latLng;

                markerOptions.position(latLng);

                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);
            }
        });
    }


    @Override
    public void onClick(View view){
        switch(view.getId()) {
            case R.id.confirmButton:

                //Once confirm button is clicked, send location data back to AddReminders activity, as well as saved inputs from before going to the current activity
                if (location != null) {
                    double flatitude = location.latitude;
                    double flongitude = location.longitude;
                    String coords = flatitude + ", " + flongitude;
                    Intent add = new Intent(MarkMapActivity.this, AddReminder.class);
                    add.putExtra("location", coords);
                    add.putExtra("type", type);
                    add.putExtra("info", info);
                    add.putExtra("date", date);
                    add.putExtra("time", time);
                    startActivity(add);
                }
                else
                {
                    Toast.makeText(this, "Please choose a location", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancelmapButton:
                //Return to add reminder activity without a location
                Intent cancel = new Intent(MarkMapActivity.this, AddReminder.class);
                cancel.putExtra("type", type);
                cancel.putExtra("info", info);
                cancel.putExtra("date", date);
                cancel.putExtra("time", time);
                startActivity(cancel);
                break;
            case R.id.removemarkerButton:
                //Remove currently placed marker
                this.mMap.clear();

        }
    }



}