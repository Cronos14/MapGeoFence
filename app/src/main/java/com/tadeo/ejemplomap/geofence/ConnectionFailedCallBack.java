package com.tadeo.ejemplomap.geofence;

import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Tadeo-developer on 25/12/16.
 */

public class ConnectionFailedCallBack implements GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("DEBUG", "CONNECTION FAILED");
    }
}