package com.tadeo.ejemplomap.geofence;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Tadeo-developer on 25/12/16.
 */

public class StartConnectionCallBack implements GoogleApiClient.ConnectionCallbacks {

    private Location lastLocation;

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("DEBUG", "CONNECTED");
        if (ActivityCompat.checkSelfPermission(GeofenceApplication.CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GeofenceApplication.CONTEXT, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(GeofenceController.getInstance().getGoogleApiClient());
        if (lastLocation != null) {



        }

        //GeofenceController.getInstance().addGeofence(createGeo("Servicio Hitss",19.359679,-99.172304,1000));
        /*for(Geo geo : Singleton.getSingleton().getGeos()){
            GeofenceController.getInstance().addGeofence(geo);
        }*/

        GeofenceController.getInstance().initGeofence();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}