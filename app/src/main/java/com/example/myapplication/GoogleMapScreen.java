package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class GoogleMapScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PlacesClient placesClient;

    Double lati;
    Double longi;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_screen);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        enableMyLocation();

        // redirectToMapApplication(37.7749, -122.4194);
        // Initialize Places SDK
        Places.initialize(getApplicationContext(), "AIzaSyBW5VhijEa6_ROXR8go0BnM2hq5LuixtUA");
        placesClient = Places.createClient(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        enableMyLocation();

    }

    private void enableMyLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        }
        else {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    mMap = googleMap;
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);

                    FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(GoogleMapScreen.this);
                    fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Location location = task.getResult();
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));

                                redirectToMapApplication(location.getLatitude(), location.getLongitude());
                            }
                        }
                    });
                }
            });
        }
    }

    private void redirectToMapApplication(double latitude, double longitude) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Uri mapUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=skin + specialist +doctor +or +hospital");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                mapIntent.setPackage("com.google.android.apps.maps"); // Specify the package name of the map application
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(GoogleMapScreen.this, "No map application found.", Toast.LENGTH_SHORT).show();
                }
            }
        }, 4*1000);
    }


    private void searchPlace(String placeName) {
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG))
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<FindCurrentPlaceResponse> placeResponseTask = placesClient.findCurrentPlace(request);
        placeResponseTask.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    if (response != null) {
                        List<PlaceLikelihood> placeLikelihoods = response.getPlaceLikelihoods();
                        if (!placeLikelihoods.isEmpty()) {
                            PlaceLikelihood likelihood = placeLikelihoods.get(0);
                            Place place = likelihood.getPlace();
                            LatLng placeLocation = place.getLatLng();
                            // Redirect to the map application
                            lati=placeLocation.latitude;
                            longi= placeLocation.longitude;
                            redirectToMapApplication(placeLocation.latitude, placeLocation.longitude);
                        } else {
                            // No nearby places found
                            Toast.makeText(GoogleMapScreen.this, "No nearby places found", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Failed to fetch places
                    Toast.makeText(GoogleMapScreen.this, "Failed to fetch places", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onSearchButtonClick(View view) {
        String placeName = ""; // Get the place name from your search view
        searchPlace(placeName);
    }
}
