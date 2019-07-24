package com.example.speedmeterapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static LocationService locationService;
    static DigitSpeedView digitSpeedView;
    static boolean status;
    ImageView image;
    static int p = 0;
    Button sinalButton;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

   String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        String rationale = "Para usar este App é necessario permitir a sua localização. ...";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Serviço de localização")
                .setSettingsDialogTitle("Localização");


       Permissions.check(this, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {

                ativarGps();
                verificarLocalizacao();
                ativarGps();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                finish();
            }
        });

        image = findViewById(R.id.image);
        digitSpeedView = findViewById(R.id.digitSpeedView);
        sinalButton = findViewById(R.id.sinal_button);
        ativarGps();
    }


    private void verificarLocalizacao() {

            LocationManager locationManager = null;
            boolean gps_enabled = false;
            boolean network_enabled = false;
            if ( locationManager == null ) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            }
            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex){}
            try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex){}
            if ( !gps_enabled && !network_enabled ){
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage("A função de localização, não esta ativada.");
                dialog.setPositiveButton("Ativar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        ativarGps();
                    }
                });

                AlertDialog alert = dialog.create();
                alert.show();
                ativarGps();
            }

        ativarGps();
        }
            private ServiceConnection serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
                    locationService = binder.getService();
                    status = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    status = false;
                }
            };

            void ativarGps() {
                if (status)
                    return;
                Intent i = new Intent(getApplicationContext(), LocationService.class);
                bindService(i, serviceConnection, BIND_AUTO_CREATE);
                status = true;
            }

            void desativarGps() {
                if (!status)
                    return;
                Intent i = new Intent(getApplicationContext(), LocationService.class);
                unbindService(serviceConnection);
                status = false;
            }

    @Override
    protected void onPause() {
        super.onPause();
        ativarGps();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ativarGps();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ativarGps();
    }

    @Override
            protected void onResume () {
                super.onResume();
                ativarGps();
            }
            @Override
            protected void onStart () {
                super.onStart();
                ativarGps();
            }
            @Override
            protected void onDestroy () {
                super.onDestroy();
                if (status)
                    desativarGps();
            }
            @Override
            public void onBackPressed () {
                if (!status)
                    super.onBackPressed();
                else
                    moveTaskToBack(true);
            }



}
