package tv.youi.app;

import android.Manifest;

import android.annotation.SuppressLint;

import android.content.ComponentCallbacks2;
import android.content.Context;

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

    private LocationListener mLocationListener = null;

    public boolean isLocationAvailable() {
        LocationManager locationManager = (LocationManager) getApplicationContext()
          .getSystemService(Context.LOCATION_SERVICE);

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

    public double[] _get()
    {
        List<Double> list = new ArrayList<>();

        try {
            if (!isLocationAvailable()) {
                ActivityCompat.requestPermissions(AppActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(AppActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                if (!isLocationAvailable()) {
                    return new double[0];
                }
            }

            if (mLocationListener == null) {
                mLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(android.location.Location location) {}

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    @Override
                    public void onProviderEnabled(String provider) {}

                    @Override
                    public void onProviderDisabled(String provider) {}
                };
            }

            LocationManager locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(Context.LOCATION_SERVICE);

            if (locationManager != null) {
                List<String> providers = locationManager.getProviders(true);

                Location bestLocation = null;

                for (String provider : providers) {
                    Location location = locationManager.getLastKnownLocation(provider);

                    if (location == null) {
                        //This is needed to prime for location updates in the event that a location was never synced (like on a fresh device with no SIM or GPS available).
                        HandlerThread mLocationLooper = new HandlerThread("LocationHandlerThread");
                        mLocationLooper.start();
                        locationManager.requestSingleUpdate(provider, mLocationListener, mLocationLooper.getLooper());

                        mLocationLooper.join(750);

                        location = locationManager.getLastKnownLocation(provider);
                    }

                    if (location != null && (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy())) {
                        bestLocation = location;
                    }
                }

                if (bestLocation != null) {
                    list.add(bestLocation.getLatitude());
                    list.add(bestLocation.getLongitude());
                    list.add(bestLocation.getAltitude());
                }
            }
        }
        catch(InterruptedException inte) {
            Log.e(LOG_TAG, "Location Services currently not available.\n\n\t" + inte.getMessage());
        }
        catch(SecurityException sec) {
            Log.e(LOG_TAG, "Location Services currently not available.\n\n\t" + sec.getMessage());
        }

        double[] target = new double[list.size()];
        for(int i = target.length; --i >= 0;) {
            target[i] = list.get(i);
        }

        return target;
    }
}
