package com.tadeo.ejemplomap.geofence;

import android.app.Application;
import android.content.Context;

/**
 * Created by Tadeo-developer on 25/12/16.
 */

public class GeofenceApplication extends Application {

    public static Context CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
    }
}