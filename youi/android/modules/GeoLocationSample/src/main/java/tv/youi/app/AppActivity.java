package tv.youi.app;

import android.Manifest;

import android.annotation.SuppressLint;

import android.content.ComponentCallbacks2;
import android.content.Context;

import android.location.Criteria;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.content.ContextCompat;

import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;

import android.os.Bundle;
import android.os.HandlerThread;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import tv.youi.youiengine.CYIActivity;

public class AppActivity extends CYIActivity {
    private static String LOG_TAG = "AppActivity";

    private static long MIN_INTERVAL_BETWEEN_UPDATES = 300000;   // milliseconds
    private static float MIN_DISTANCE_BETWEEN_UPDATES = 5;      // meters

    private LocationListener locationListener = null;
    private LocationManager locationManager = null;

    private Criteria criteria = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        criteria = new Criteria();

        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setSpeedRequired(true);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(LOG_TAG, "Location changed\n\tLatitude: " +
                        location.getLatitude() + "\n\tLongitude: " +
                        location.getLongitude() + "\n\tAltitude: " +
                        location.getAltitude());

                nativeUpdatedGPSCoordinates(location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        if (!isLocationAvailable()) {
            ActivityCompat.requestPermissions(AppActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "onResume()");

        super.onResume();

        try {
            if (!isLocationAvailable()) {
                ActivityCompat.requestPermissions(AppActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(
                        locationManager.getBestProvider(criteria, true),
                        MIN_INTERVAL_BETWEEN_UPDATES,
                        MIN_DISTANCE_BETWEEN_UPDATES,
                        locationListener);
            }
        }
        catch(SecurityException sec) {
            Log.e(LOG_TAG, "[1] Location Services currently not available.\n\n\t" + sec.getMessage());
        }
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause()");

        super.onPause();

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    public double[] _get()
    {
        Location location = null;

        try {
            if (!isLocationAvailable()) {
                ActivityCompat.requestPermissions(AppActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                if (!isLocationAvailable()) {
                    return new double[0];
                }
            }

            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(criteria, true));
            }
        }
        catch(SecurityException sec) {
            Log.e(LOG_TAG, "[2] Location Services currently not available.\n\n\t" + sec.getMessage());
        }

        if (location != null) {
            return new double [] {location.getLatitude(), location.getLongitude(), location.getAltitude()};
        }

        return new double[0];
    }

    private boolean isLocationAvailable() {
        if (locationManager == null) {
            return false;
        }

        boolean isGPSProviderEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkProviderEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isGPSProviderEnabled || !isNetworkProviderEnabled) {
            return false;
        }

        int hasAccessFineLocation = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasAccessCoarseLocation = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return hasAccessFineLocation == PermissionChecker.PERMISSION_GRANTED &&
               hasAccessCoarseLocation == PermissionChecker.PERMISSION_GRANTED;
    }

    // Location update function used to emit an event with new location data to GeoLocationModule
    protected native void nativeUpdatedGPSCoordinates(double latitude, double longitude, double altitude);
}
