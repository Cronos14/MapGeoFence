package com.tadeo.ejemplomap.utils;

import com.tadeo.ejemplomap.models.Geo;

import java.util.ArrayList;

/**
 * Created by Tadeo Gonzalez on 27/12/2016.
 */

public class Singleton {
    private static Singleton singleton;

    private ArrayList<Geo> geos;

    private Geo geoSelected;

    private Singleton(){

    }

    public static Singleton getSingleton(){
        if(singleton==null)
            singleton = new Singleton();

        return singleton;
    }

    public ArrayList<Geo> getGeos() {
        return geos;
    }

    public void addGeo(Geo geo){
        if(geos==null)
            geos = new ArrayList<>();

        geos.add(geo);
    }

    public void setGeos(ArrayList<Geo> geos) {
        this.geos = geos;
    }

    public void setGeoSelected(Geo geo){
        geoSelected = geo;
    }

    public Geo getGeoSelected(){
        return geoSelected;
    }
}
