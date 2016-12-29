package com.tadeo.ejemplomap.geofence;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.tadeo.ejemplomap.models.Geo;
import com.tadeo.ejemplomap.utils.Singleton;

import java.util.ArrayList;

/**
 * Created by Tadeo-developer on 25/12/16.
 */

public class GeofenceController {

    private String LOG_TAG = "DEBUG";
    private static GeofenceController instance;
    private GoogleApiClient googleApiClient;

    //new
    private ArrayList<Geo> geos;
    //private ArrayList<Geofence> geofences;

    public static GeofenceController getInstance() {
        if (instance == null) {
            instance = new GeofenceController();
        }
        return instance;
    }

    public void startGoogleApiClient(GoogleApiClient.ConnectionCallbacks callback, ConnectionFailedCallBack connectionFailedListener) {

        if(googleApiClient!=null && googleApiClient.isConnected()){
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
        }else{
            googleApiClient = new GoogleApiClient.Builder(GeofenceApplication.CONTEXT)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(callback)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .build();
            googleApiClient.connect();
        }

    }

    public void stopGoogleApiClient() {
        googleApiClient.disconnect();
    }

    public GoogleApiClient getGoogleApiClient(){
        return googleApiClient;
    }


    public void initGeofence() {

        startGoogleApiClient(new AddGeoConnectionCallBack(), new ConnectionFailedCallBack());
    }

    public GeofencingRequest getAddGeofencingRequest() {
        //List<Geofence> geofencesToAdd = new ArrayList<>();
        //geofencesToAdd.add(geofence);

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        for(Geo geo : Singleton.getSingleton().getGeos()){
            builder.addGeofence(geo.geofence());
            Log.e("StartConectionCallBack","geo: "+geo.getNombre());
        }

        return builder.build();
    }

}
