package me.madri.vanilai;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class VanilaiLocationService implements LocationListener {
    private final static String TAG = VanilaiLocationService.class.getSimpleName();

    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

    public VanilaiLocationService() {
    }

    public Location getLocation(String provider, LocationManager locationManager, Context context) {
        if (locationManager.isProviderEnabled(provider)) {
            Log.v(TAG, "Access Fine Permission: " + ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION));
            Log.v(TAG, "Access Fine Permission: " + PackageManager.PERMISSION_GRANTED);
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                }
            } else {
                locationManager.requestLocationUpdates(provider,
                        MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                return locationManager.getLastKnownLocation(provider);
            }
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
