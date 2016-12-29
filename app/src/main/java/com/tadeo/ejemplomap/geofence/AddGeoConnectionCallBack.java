package com.tadeo.ejemplomap.geofence;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Tadeo-developer on 25/12/16.
 */

public class AddGeoConnectionCallBack implements GoogleApiClient.ConnectionCallbacks {

    private String LOG_TAG = "DEBUG";

    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(GeofenceApplication.CONTEXT, GeoIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(GeofenceApplication.CONTEXT, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (ActivityCompat.checkSelfPermission(GeofenceApplication.CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> result = LocationServices.GeofencingApi.addGeofences(GeofenceController.getInstance().getGoogleApiClient(), GeofenceController.getInstance().getAddGeofencingRequest(), pendingIntent);
        result.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.e(LOG_TAG, "SUCCESS" + status);
                } else {
                    Log.e(LOG_TAG, "FAILED: " + status.getStatusMessage() + " : " + status.getStatusCode());

                 }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}