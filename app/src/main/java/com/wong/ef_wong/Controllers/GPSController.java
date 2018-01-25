package com.wong.ef_wong.Controllers;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.wong.ef_wong.Activities.PublicarActivity;
import com.wong.ef_wong.R;

/**
 * Created by Leo on 03/12/2016.
 */

public class GPSController implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    public static GoogleApiClient mApiClient;
    private Location mLocation;
    private Activity activity;

    public GPSController(Activity activity) {
        super();
        this.activity = activity;
        mApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    public Location getLocation() {
        return mLocation;
    }

    @Override
    public void onConnected(@NonNull Bundle bundle) {
        startLocationUpdates();
    }


    public void startLocationUpdates() {
        LocationRequest locationRequest = createLocationRequest();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
                Toast.makeText(activity, "No puede realizar actualizaciones de Localizacion (permisos)",Toast.LENGTH_LONG).show();
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, locationRequest, this);
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(activity, "Error en la conexión de google", Toast.LENGTH_SHORT).show();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation=location;
        if(activity instanceof PublicarActivity) {
            ((TextView) activity.findViewById(R.id.idLongitud)).setText(String.valueOf(mLocation.getLongitude()));
            ((TextView) activity.findViewById(R.id.idLatitud)).setText(String.valueOf(mLocation.getLatitude()));
            if (((PublicarActivity) activity).bool[0]==false) ;
                ((PublicarActivity) activity).validarUbicacion();
            stopLocationUpdates();
        }
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);
    }

}
