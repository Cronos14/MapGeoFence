package com.tadeo.ejemplomap.models;

import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.Circle;

import java.util.UUID;

/**
 * Created by Tadeo-developer on 25/12/16.
 */

public class Geo {

    public String id;
    public String nombre;
    public double latitud;
    public double longitud;
    public int radio;
    private Circle circle;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        this.radio = radio;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public Geofence geofence() {
        Log.e("Geo","Agregando geo fence: "+ nombre);
        id = UUID.randomUUID().toString();
        return new Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(1000)
                .setCircularRegion(latitud, longitud, radio)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }


}