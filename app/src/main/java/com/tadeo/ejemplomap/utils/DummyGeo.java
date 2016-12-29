package com.tadeo.ejemplomap.utils;

import com.tadeo.ejemplomap.models.Geo;

/**
 * Created by Tadeo Gonzalez on 27/12/2016.
 */

public class DummyGeo {

    public static Geo createGeo(double latitud, double longitud, String nombre, int radio){
        Geo geo = new Geo();
        geo.setLatitud(latitud);
        geo.setLongitud(longitud);
        geo.setNombre(nombre);
        geo.setRadio(radio);
        return geo;
    }
}
