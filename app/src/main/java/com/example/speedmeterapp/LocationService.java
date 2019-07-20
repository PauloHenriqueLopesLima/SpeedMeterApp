package com.example.speedmeterapp;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
 
public class LocationService extends Service implements LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
 
    private static final long INTERVAL = 0;
    private static final long FASTEST_INTERVAL = 0;
    private LocationRequest LocationRequest;
    private GoogleApiClient GoogleApiClient;
    private Location CurrentLocation, lStart, lEnd;
    static double distance = 0;
    double speed;

    private final IBinder mBinder = new LocalBinder();
 
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createLocationRequest();
        GoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        GoogleApiClient.connect();
        return mBinder;
    }
 
    protected void createLocationRequest() {
        LocationRequest = new LocationRequest();
        LocationRequest.setInterval(INTERVAL);
        LocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         return super.onStartCommand(intent, flags, startId);
    }
 
     @Override
    public void onConnected(Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    GoogleApiClient, LocationRequest, this);
        } catch (SecurityException e) {
        }
    }
     @Override
    public void onConnectionSuspended(int i) {
     }

    @Override
    public void onLocationChanged(Location location) {
        MainActivity.locate.dismiss();
        CurrentLocation = location;
        if (lStart == null) {
            lStart = CurrentLocation;
            lEnd = CurrentLocation;
        } else
            lEnd = CurrentLocation;
 
        atualizarUI();

        speed = location.getSpeed() ;
     }
 
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
     }
 
    public class LocalBinder extends Binder {
         public LocationService getService() {
            return LocationService.this;
        }
     }
 
    private void atualizarUI() {
        if (MainActivity.p == 0) {
            distance = distance + (lStart.distanceTo(lEnd) / 1000.00);
            if (speed > 1)
                MainActivity.speed.setText("Sua velocidade" +speed + " km/hr");
            MainActivity.digitSpeedView.updateSpeed((int) speed);

        }

            else {
            MainActivity.speed.setText("parado");

            lStart = lEnd;
        }
         }

    @Override
    public boolean onUnbind(Intent intent) {
         if (GoogleApiClient.isConnected())
            GoogleApiClient.disconnect();
        lStart = null;
        lEnd = null;
        return super.onUnbind(intent);
    }
}