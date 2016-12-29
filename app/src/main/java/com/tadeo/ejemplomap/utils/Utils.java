package com.tadeo.ejemplomap.utils;

/**
 * Created by Tadeo Gonzalez on 28/12/2016.
 */

public class Utils{

    public static double distanceTwoPointsKm(double lat1,double lon1, double lat2, double lon2){

        final int R = 6371; // Radious of the earth
        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        System.out.println("latDistance: "+latDistance);
        System.out.println("lonDistance: "+lonDistance);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = R * c;
        System.out.println("diferenceDistance: "+distance);
        return distance;

    }

    public static double distanceTwoPointsKm(String lat1T,String lon1T, String lat2T, String lon2T){
        return distanceTwoPointsKm(Double.parseDouble(lat1T),Double.parseDouble(lon1T),Double.parseDouble(lat2T),Double.parseDouble(lon2T));
    }

    public static double distanceTwoPointsMts(double lat1,double lon1, double lat2, double lon2){
        return distanceTwoPointsKm(lat1,lon1,lat2,lon2)*1000;
    }

    public static double distanceTwoPointsMts(String lat1T,String lon1T, String lat2T, String lon2T){
        return distanceTwoPointsKm(lat1T,lon1T,lat2T,lon2T)*1000;
    }



    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }
}
