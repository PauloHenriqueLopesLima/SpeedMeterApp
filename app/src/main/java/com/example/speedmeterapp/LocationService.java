package com.example.speedmeterapp;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;



import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

public class LocationService extends Service implements LocationListener,GoogleApiClient.ConnectionCallbacks {
 
    private static final long INTERVAL = 500;
    private static final long FASTEST_INTERVAL = 500;
    private LocationRequest LocationRequest;
    private GoogleApiClient GoogleApiClient;
    private Location pontoA;
    private Location pontoB;
    static double distancia = 0;
    double velocidade;


    private final IBinder binder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createLocationRequest();
        GoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        GoogleApiClient.connect();
        return binder;
    }
 
    protected void createLocationRequest() {
        LocationRequest = new LocationRequest();
        LocationRequest.setInterval(INTERVAL);
        LocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
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
            e.printStackTrace();
        }
    }
     @Override
    public void onConnectionSuspended(int i) {
     }

    @Override
    public void onLocationChanged(Location location) {

        if (pontoA == null) {
            pontoA = location;
            pontoB = location;
        } else
            pontoB = location;
         atualizarUI();
        velocidade = location.getSpeed() * 3.6 ;
     }
    class LocalBinder extends Binder {

         LocationService getService() {
            return LocationService.this;
        }
     }

    private void atualizarUI() {
        if (MainActivity.p == 0) {
            distancia = distancia + (pontoA.distanceTo(pontoB) / 1000.00);
            if (velocidade > 0)
            MainActivity.digitSpeedView.updateSpeed((int) velocidade);
        }
            else {
            pontoA = pontoB;
        }
    }
    @Override
    public boolean onUnbind(Intent intent) {
         if (GoogleApiClient.isConnected())
            GoogleApiClient.disconnect();
        pontoA = null;
        pontoB = null;
        return super.onUnbind(intent);
    }
}