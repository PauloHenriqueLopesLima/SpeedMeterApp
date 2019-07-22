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

public class LocationService extends Service implements LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
 
    private static final long INTERVAL = 500;
    private static final long FASTEST_INTERVAL = 500;
    private LocationRequest LocationRequest;
    private GoogleApiClient GoogleApiClient;
    private Location location, pontoA, pontoB;
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
                .addOnConnectionFailedListener(this)
                .build();
        GoogleApiClient.connect();
        return binder;
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
        this.location = location;
        if (pontoA == null) {
            pontoA = this.location;
            pontoB = this.location;
        } else
            pontoB = this.location;
 
        atualizarUI();

        velocidade = location.getSpeed() * 18 / 5 ;
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
            distancia = distancia + (pontoA.distanceTo(pontoB) / 1000.00);
            if (velocidade > 0)
                MainActivity.speed.setText("Sua velocidade"+ new DecimalFormat("##,##").format(velocidade)  + " km/hr");
            MainActivity.digitSpeedView.updateSpeed((int) velocidade);

        }

            else {
            MainActivity.speed.setText("parado");

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