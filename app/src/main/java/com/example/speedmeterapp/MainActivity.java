package com.example.speedmeterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.capur16.digitspeedviewlib.DigitSpeedView;


public class MainActivity extends AppCompatActivity {

        LocationService Service;
        static DigitSpeedView digitSpeedView;
        static boolean status;
        LocationManager locationManager;
        ImageView image;
        static ProgressDialog locate;
        static int p = 0;
        static TextView speed;
        static int velocidade;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            checkGps();
            startGps();

            speed = (TextView) findViewById(R.id.speedtext);
            image = (ImageView) findViewById(R.id.image);
            digitSpeedView = findViewById(R.id.digitSpeedView);



        }
        private void checkGps () {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


                showGPSDisabledAlertToUser();
            }
        }
        private void startGps ()
            {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    return;
                }
                if (status == false)
                    bindService();
                locate = new ProgressDialog(MainActivity.this);
                locate.setIndeterminate(true);
                locate.setCancelable(false);
                locate.setMessage("Getting Location...");
                locate.show();
            }
        private void stopGps () {
            if (status == true)
                unbindService();
            p = 0;
        }


        private void showGPSDisabledAlertToUser () {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Deseja Ativar o GPS, para usar a aplicação ?")
                        .setCancelable(false)
                        .setPositiveButton("Ativar GPS",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent callGPSSettingIntent = new Intent(
                                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(callGPSSettingIntent);
                                    }
                                });
                alertDialogBuilder.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }

            private ServiceConnection serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
                    Service = binder.getService();
                    status = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    status = false;
                }
            };


            void bindService () {
                if (status == true)
                    return;
                Intent i = new Intent(getApplicationContext(), LocationService.class);
                bindService(i, serviceConnection, BIND_AUTO_CREATE);
                status = true;

            }

            void unbindService () {
                if (status == false)
                    return;
                Intent i = new Intent(getApplicationContext(), LocationService.class);
                unbindService(serviceConnection);
                status = false;
            }
            @Override
            protected void onResume () {
                super.onResume();
            }
            @Override
            protected void onStart () {
                super.onStart();
            }
            @Override
            protected void onDestroy () {
                super.onDestroy();
                if (status == true)
                    unbindService();
            }
            @Override
            public void onBackPressed () {
                if (status == false)
                    super.onBackPressed();
                else
                    moveTaskToBack(true);
            }
        }



