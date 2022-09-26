package uk.ac.abertay.cmp309.assessmentempty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsFragment extends Fragment implements View.OnClickListener{
    private DBManager dbManager;
    String id;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {

            //Getting location

            FusedLocationProviderClient mLocationProvider = LocationServices.getFusedLocationProviderClient(getContext());
            mLocationProvider.getLastLocation()
                    .addOnSuccessListener((Activity) getContext(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null)
                            {
                                double userlat = location.getLatitude();
                                double userlong = location.getLongitude();
                                float zoom = 15;
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userlat, userlong), zoom));

                            }
                            else
                            {
                                Toast.makeText(getContext(), "Location could not be found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            //Obtaining database data for populating map
            Cursor cursor = dbManager.readAllMap();

            if(cursor != null) {
                    while (cursor.moveToNext()) {
                        if (!cursor.getString(2).equals("No location is selected")) {
                            //Iterate over records with locations stored and placing markers on the map containing reminder info
                            String[] coords = cursor.getString(2).split(",");
                            double latitude = Double.parseDouble(coords[0]);
                            double longitude = Double.parseDouble(coords[1]);

                            LatLng location = new LatLng(latitude, longitude);

                            googleMap.addMarker(new MarkerOptions().position(location).title(cursor.getString(1)).snippet(cursor.getString(0)));
                        }
                    }
            }

            //Marker click listener
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {

                    //Setting the id to the snippet from the selected marker

                    id = marker.getSnippet();

                    return false;

                }
            });

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        dbManager = new DBManager(getContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Button "View" listener
        Button btn = (Button) view.findViewById(R.id.ViewButton);
        btn.setOnClickListener(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


    @Override
    public void onClick(View view){
        switch(view.getId()) {
            case R.id.ViewButton:

                //If marker is selected (id != null) and view button is clicked, set up DeleteRecord activity to view more info about record
                if (id != null) {

                    Cursor cursor = dbManager.retrieveSpecific(Integer.parseInt(id));

                    String idf = cursor.getString(0);
                    String type = cursor.getString(1);
                    String info = cursor.getString(2);
                    String date = cursor.getString(3);
                    String time = cursor.getString(4);
                    String location = cursor.getString(5);
                    int notification = cursor.getInt(6);

                    Intent intent = new Intent(getActivity(), DeleteRecord.class);
                    intent.putExtra("id", idf);
                    intent.putExtra("type", type);
                    intent.putExtra("info", info);
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);
                    intent.putExtra("location", location);
                    intent.putExtra("notification", notification);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getContext(), "Please select a marker!", Toast.LENGTH_SHORT).show();
                }
        }
    }

}