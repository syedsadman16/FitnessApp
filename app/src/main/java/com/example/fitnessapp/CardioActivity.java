package com.example.fitnessapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.TimeUnit;


public class CardioActivity extends AppCompatActivity {

    private MapView mapView;
    private GoogleMap googleMap;
    private int locationMinDistance = 20;
    private int locationMinTime = 4000;
    private LatLng latLng;
    private LocationManager locationManager;
    Location locationA = new Location("A");
    Location locationB = new Location("B");
    TextView distanceDisplay, timeDisplay;
    double totalDistanceFeet, totalDistanceMiles;
    Button startBtn,endBtn;
    long totalSeconds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        enableLocation();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            //provides instance of map
            public void onMapReady(GoogleMap googleMap) {
                mapViewReady(googleMap);
            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        initMap();
        getCurrentLocation();

        startBtn = findViewById(R.id.startBtn);
        endBtn = findViewById(R.id.endBtn);
        endBtn.setEnabled(false);

    }


    //When location has changed
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            findUser(location);
            locationManager.removeUpdates(locationListener);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }
        @Override
        public void onProviderEnabled(String provider) { }
        @Override
        public void onProviderDisabled(String provider) { }
    };

    //Prompts user to enable location
    private void enableLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // If GPS and NETWORK are disabled
        if ( (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) || (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) ) {
            //Redirects user to location settings
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Location Permission");
            alertDialog.setMessage("Please enable location services");
            //If User denies location, take them back
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(i);
                }
            });
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.show();
        }
    }


    //Set this map
    public void mapViewReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    /*
    /** Called by Android
    */

    //When user receives a small event, application is put on pause
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        getCurrentLocation();
    }

    //Setup map
    private void initMap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setAllGesturesEnabled(true);

            }
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Allows API to get most precise location from GPS,Wifi and mobile data.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            }

        }

    }


    private void getCurrentLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            boolean GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean NetworkEnabled = locationManager.isProviderEnabled((LocationManager.NETWORK_PROVIDER));

            Location location = null;

            //If there is a connection, get the users location
            if (!GPSEnabled && !NetworkEnabled) {
                Toast.makeText(getApplicationContext(), "Location is turned off", Toast.LENGTH_SHORT).show();
            } else {

                if (GPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locationMinTime, locationMinDistance, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }

                if (NetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, locationMinTime, locationMinDistance, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }

            //Once there is a location,, find the user
            if (location != null) {
                findUser(location);
            }

        }
    }


    //When user is ready to start run
    public void startRun(View view) {

        Location location = null;
        distanceDisplay = findViewById(R.id.distanceDisplay);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        }

        //Retrieve current location
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, locationMinTime, locationMinDistance, locationListener);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //Get the long and lat for starting position
        locationA.setLongitude(latLng.longitude);
        locationA.setLatitude(latLng.latitude);


        startBtn.setEnabled(false);
        endBtn.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Start Run", Toast.LENGTH_SHORT).show();

    }

    //When the user hits the stop button
    public void endRun(View view) {
        Location location = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        }

        //Get the ending location
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, locationMinTime, locationMinDistance, locationListener);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        locationB.setLatitude(latLng.latitude);
        locationB.setLongitude(latLng.longitude);

        //Conversions
        double distanceMiles = (locationA.distanceTo(locationB) / 1000) * 0.6213;
        double distanceFeet = (locationA.distanceTo(locationB) / 1000) * 3290;

        //Print the distance from starting to ending
        distanceDisplay.setText("Miles: "+ String.format("%.2f", distanceMiles) + "\n" +
                "Feet" + String.format("%.2f",distanceFeet));

        //Update Total distance
        totalDistanceMiles += distanceMiles;
        totalDistanceFeet += distanceMiles;

        startBtn.setEnabled(true);
        endBtn.setEnabled(false);
        Toast.makeText(getApplicationContext(), "End Run", Toast.LENGTH_SHORT).show();
        Log.i("LocationA", String.valueOf(locationA.getLatitude()));
        Log.i("Distance Test",Double.toString(distanceMiles));
    }

    private void findUser(Location location) {

        if (googleMap != null) {

            //Make sure to get permission if needed
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            }

            //Get coordinates
            latLng = new LatLng(location.getLatitude(), location.getLongitude());

            //Zoom in once user is found
            googleMap.setMyLocationEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
    }



}
