package com.tadeo.ejemplomap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tadeo.ejemplomap.geofence.ConnectionFailedCallBack;
import com.tadeo.ejemplomap.geofence.GeoIntentService;
import com.tadeo.ejemplomap.geofence.GeofenceController;
import com.tadeo.ejemplomap.geofence.StartConnectionCallBack;
import com.tadeo.ejemplomap.interfaces.OnPostExecuteListener;
import com.tadeo.ejemplomap.models.Geo;
import com.tadeo.ejemplomap.tasks.Tarea;
import com.tadeo.ejemplomap.utils.DummyGeo;
import com.tadeo.ejemplomap.utils.Singleton;
import com.tadeo.ejemplomap.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.tadeo.ejemplomap.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private static final int PETICION_CONFIG_UBICACION = 1;
    private static final String LOGTAG = "MapsActivity";

    private final String STATUS_SERVICIO_INICIO = "Iniciar Servicio";
    private final String STATUS_SERVICIO_SUSPENDER = "Suspender Servicio";
    private final String STATUS_SERVICIO_TERMINAR = "Terminar Servicio";

    private TextView tvDistancia;
    private TextView tvTiempo;
    private Button btnStatus;
    private GoogleMap mMap;
    private ArrayList<Polyline> mPolyLines;

    //hitss
    //LatLng latLngTecnico = new LatLng(19.359413, -99.172251);

    //parque via
    LatLng latLngTecnico = new LatLng(19.435202, -99.167247);

    //casa
    //LatLng latLngTecnico = new LatLng(19.658348, -99.089120);

    private static final int LOCATION_REQUEST_CODE = 1;

    private LocationRequest locRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        tvDistancia = (TextView) findViewById(R.id.tv_distancia);
        tvTiempo = (TextView) findViewById(R.id.tv_tiempo);
        btnStatus = (Button) findViewById(R.id.btn_terminar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        mPolyLines = new ArrayList<>();

        //cerca hitss
        /*Singleton.getSingleton().addGeo(DummyGeo.createGeo(19.363389,-99.172921,"Servicio 1",100));
        Singleton.getSingleton().addGeo(DummyGeo.createGeo(19.360534,-99.163501,"Servicio 2",100));
        Singleton.getSingleton().addGeo(DummyGeo.createGeo(19.363227,-99.179637,"Servicio 3",100));
        Singleton.getSingleton().addGeo(DummyGeo.createGeo(19.359160,-99.171728,"Servicio 4",100));*/


        //cerca parque via
        Singleton.getSingleton().addGeo(DummyGeo.createGeo(19.440983, -99.167189,"Servicio 1",100));
        Singleton.getSingleton().addGeo(DummyGeo.createGeo(19.439872, -99.155487,"Servicio 2",100));
        Singleton.getSingleton().addGeo(DummyGeo.createGeo(19.433819, -99.162561,"Servicio 3",100));
        Singleton.getSingleton().addGeo(DummyGeo.createGeo(19.418788, -99.164351,"Servicio 4",100));

        //cerca de casa
        /*Singleton.getSingleton().addGeo(DummyGeo.createGeo(19.655923, -99.094957, "Servicio 1", 100));
        Singleton.getSingleton().addGeo(DummyGeo.createGeo(19.655115, -99.090558, "Servicio 2", 100));
        Singleton.getSingleton().addGeo(DummyGeo.createGeo(19.654589, -99.084356, "Servicio 3", 100));
        //Singleton.getSingleton().addGeo(DummyGeo.createGeo(19.418788, -99.164351,"Servicio 4",100));*/


        Collections.sort(Singleton.getSingleton().getGeos(), new Comparator<Geo>() {


            @Override
            public int compare(Geo o1, Geo o2) {

                double distancia = Utils.distanceTwoPointsMts(o1.getLatitud(), o1.getLongitud(), latLngTecnico.latitude, latLngTecnico.longitude);

                double distancia2 = Utils.distanceTwoPointsMts(o2.getLatitud(), o2.getLongitud(), latLngTecnico.latitude, latLngTecnico.longitude);


                if (distancia > distancia2) {
                    return -1;
                } else if (distancia < distancia2) {
                    return 1;
                } else {
                    return 0;
                }

            }
        });

        for (Geo geo : Singleton.getSingleton().getGeos()) {
            Log.e("MapsActivity", "servicio orden: " + geo.getNombre());
        }


        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(btnStatus.getText().toString().equalsIgnoreCase(STATUS_SERVICIO_INICIO)){
                    btnStatus.setText(STATUS_SERVICIO_SUSPENDER);

                    Location lastLocation = getLastLocation();

                    if(lastLocation!=null){
                            CameraPosition.Builder cameraPosition = new CameraPosition.Builder();
                            cameraPosition.target(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
                            cameraPosition.zoom(18);
                            //cameraPosition.bearing(20);//120
                            cameraPosition.tilt(90);
                            CameraPosition newCamera = cameraPosition.build();

                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamera));


                    }




                }else if(btnStatus.getText().toString().equalsIgnoreCase(STATUS_SERVICIO_SUSPENDER)){
                    btnStatus.setText(STATUS_SERVICIO_INICIO);

                    Location lastLocation = getLastLocation();

                    if(lastLocation!=null){
                        CameraPosition.Builder cameraPosition = new CameraPosition.Builder();
                        cameraPosition.target(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
                        cameraPosition.zoom(20);
                        //cameraPosition.bearing(0);//120
                        //cameraPosition.tilt(90);
                        CameraPosition newCamera = cameraPosition.build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamera));

                    }
                }else if(btnStatus.getText().toString().equalsIgnoreCase(STATUS_SERVICIO_TERMINAR)){
                    btnStatus.setText(STATUS_SERVICIO_INICIO);
                    Location lastLocation = getLastLocation();

                    if(lastLocation!=null){
                        CameraPosition.Builder cameraPosition = new CameraPosition.Builder();
                        cameraPosition.target(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
                        cameraPosition.zoom(15);
                        //cameraPosition.bearing(0);//120
                        //cameraPosition.tilt(90);
                        CameraPosition newCamera = cameraPosition.build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamera));

                        for(Geo geoAux : Singleton.getSingleton().getGeos()){
                            if(geoAux.getNombre().equalsIgnoreCase(Singleton.getSingleton().getGeoSelected().getNombre())){
                                if(Singleton.getSingleton().getGeoSelected().getCircle()!=null){
                                    Singleton.getSingleton().getGeoSelected().getCircle().setFillColor(Color.argb(32,86,222,52));
                                    Singleton.getSingleton().getGeoSelected().getCircle().setStrokeColor(Color.GREEN);
                                }
                            }
                        }


                        Singleton.getSingleton().getGeos().remove(Singleton.getSingleton().getGeos().size()-1);
                        if(Singleton.getSingleton().getGeos().size()>0){
                            Geo geoCercano = Singleton.getSingleton().getGeos().get(Singleton.getSingleton().getGeos().size()-1);
                            addRute(mMap,geoCercano,new LatLng(Singleton.getSingleton().getGeoSelected().getLatitud(),Singleton.getSingleton().getGeoSelected().getLongitud()));

                            geoCercano.setCircle(mMap.addCircle(createCircle(new LatLng(geoCercano.getLatitud(),geoCercano.getLongitud()),geoCercano.getRadio())));

                        }

                    }
                }





            }
        });


        GeoIntentService.onGeoTransitionListener = new GeoIntentService.OnGeoTransitionListener() {
            @Override
            public void onGeoTransition(final Geo geo, int transition) {
                if(transition== Geofence.GEOFENCE_TRANSITION_ENTER){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnStatus.setText(STATUS_SERVICIO_TERMINAR);

                            for(Geo geoAux : Singleton.getSingleton().getGeos()){
                                if(geoAux!=null && geo!=null) {
                                    if (geoAux.getNombre().equalsIgnoreCase(geo.getNombre())) {
                                        if (geo.getCircle() != null) {
                                            geo.getCircle().setFillColor(Color.argb(32, 240, 214, 43));
                                            geo.getCircle().setStrokeColor(Color.YELLOW);
                                        }
                                    }
                                }
                            }

                        }


                    });


                }else if(transition==Geofence.GEOFENCE_TRANSITION_DWELL){

                }else if(transition==Geofence.GEOFENCE_TRANSITION_EXIT){

                }
            }
        };
    }

    private Location getLastLocation(){
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(GeofenceController.getInstance().getGoogleApiClient());

        return lastLocation;
    }

    protected void onStart() {
        super.onStart();

        GeofenceController.getInstance().startGoogleApiClient(new StartConnectionCallBack(), new ConnectionFailedCallBack());
    }

    protected void onStop() {
        super.onStop();
        GeofenceController.getInstance().stopGoogleApiClient();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings settingsApi = mMap.getUiSettings();
        settingsApi.setZoomControlsEnabled(true);

        // Controles UI
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }

        for(Geo geo : Singleton.getSingleton().getGeos()){
            //mMap.addCircle(createCircle(new LatLng(geo.getLatitud(),geo.getLongitud()),geo.getRadio()));
            mMap.addMarker(new MarkerOptions().position(new LatLng(geo.getLatitud(),geo.getLongitud())).title(geo.getNombre()));
        }


        CameraPosition.Builder cameraPosition = new CameraPosition.Builder();
        cameraPosition.target(latLngTecnico);
        cameraPosition.zoom(20);
        /*cameraPosition.bearing(0);//120
        cameraPosition.tilt(90);*/
        CameraPosition newCamera = cameraPosition.build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamera));


        if(Singleton.getSingleton().getGeos().size()>0){
            Geo geoCercano = Singleton.getSingleton().getGeos().get(Singleton.getSingleton().getGeos().size()-1);
            addRute(mMap,geoCercano,latLngTecnico);
            geoCercano.setCircle(mMap.addCircle(createCircle(new LatLng(geoCercano.getLatitud(),geoCercano.getLongitud()),geoCercano.getRadio())));

        }




        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                //Log.e("MapsActivity","onCameraMove");
            }
        });

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                Log.e("MapsActivity","onCameraMoveStarted");
            }
        });

        //enableLocationUpdates();


    }

    public void addRute(final GoogleMap mMap, Geo geoCercano, LatLng latLngTecnico){

        if(mPolyLines!=null && !mPolyLines.isEmpty()){
            for (Polyline polyline : mPolyLines){
                polyline.remove();
            }
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("origin", latLngTecnico.latitude+","+ latLngTecnico.longitude);
        params.put("destination",geoCercano.getLatitud()+","+geoCercano.getLongitud());
        params.put("alternatives", "true");
        params.put("key", "AIzaSyBmExsWy6lJgVHr_IPd8tps955-NZWKWus");
        Tarea tarea = new Tarea(params);
        tarea.setOnPostExecuteListener(new OnPostExecuteListener() {
            @Override
            public void onPostExecute(JSONObject jsonObject) {


                if (jsonObject != null) {
                    ArrayList<Integer> colors = new ArrayList<Integer>();
                    colors.add(Color.BLUE);
                    colors.add(Color.GREEN);
                    colors.add(Color.GRAY);
                    colors.add(Color.MAGENTA);
                    colors.add(Color.RED);
                    JSONArray jsonArray = jsonObject.optJSONArray("routes");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject overviewPolyline = jsonArray.optJSONObject(i).optJSONObject("overview_polyline");
                            JSONArray jsonLegs = jsonArray.optJSONObject(0).optJSONArray("legs");
                            JSONObject jsonStartLocation = jsonLegs.optJSONObject(0).optJSONObject("start_location");
                            JSONObject jsonEndLocation = jsonLegs.optJSONObject(0).optJSONObject("end_location");

                            JSONObject jsonDistance = jsonLegs.optJSONObject(0).optJSONObject("distance");
                            JSONObject jsonDuration = jsonLegs.optJSONObject(0).optJSONObject("duration");

                            tvDistancia.setText("Distancia: "+jsonDistance.optString("text"));
                            tvTiempo.setText("Tiempo: "+jsonDuration.optString("text"));

                            LatLng startLocation = new LatLng(jsonStartLocation.optDouble("lat"), jsonStartLocation.optDouble("lng"));
                            LatLng endLocation = new LatLng(jsonEndLocation.optDouble("lat"), jsonEndLocation.optDouble("lng"));


                            List<LatLng> points = decodePoly(overviewPolyline.optString("points"));

                            PolylineOptions polylineOptions = new PolylineOptions();
                            polylineOptions.geodesic(true);
                            if (colors.size() < i) {
                                polylineOptions.color(Color.BLUE);
                            } else {
                                polylineOptions.color(colors.get(i));
                            }

                            polylineOptions.width(20);

                            polylineOptions.add(startLocation);

                            for (int j = 0; j < points.size(); j++) {
                                polylineOptions.add(points.get(j));
                            }

                            polylineOptions.add(endLocation);

                            Polyline polyline = mMap.addPolyline(polylineOptions);
                            polyline.setClickable(true);
                            mPolyLines.add(polyline);
                            mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                                @Override
                                public void onPolylineClick(Polyline polyline) {
                                    //polyline.setColor(Color.BLACK);
                                }
                            });
                        }
                    }


                }
            }
        });
        tarea.execute();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            // ¿Permisos asignados?
            if (permissions.length > 0 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Error de permisos", Toast.LENGTH_LONG).show();
            }

        }
    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public CircleOptions createCircle(LatLng latLng, int radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.BLUE);
        circleOptions.strokeWidth(4);
        circleOptions.fillColor(Color.argb(32,33,150,243));
        return circleOptions;
    }

    private void enableLocationUpdates() {

        locRequest = new LocationRequest();
        locRequest.setInterval(2000);
        locRequest.setFastestInterval(1000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest locSettingsRequest =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locRequest)
                        .build();

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        GeofenceController.getInstance().getGoogleApiClient(), locSettingsRequest);

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        Log.i("MapsActivity", "Configuración correcta");
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.i("MapsActivity", "Se requiere actuación del usuario");
                            status.startResolutionForResult(MapsActivity.this, PETICION_CONFIG_UBICACION);
                        } catch (IntentSender.SendIntentException e) {
                            //btnActualizar.setChecked(false);
                            Log.i("MapsActivity", "Error al intentar solucionar configuración de ubicación");
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("MapsActivity", "No se puede cumplir la configuración de ubicación necesaria");
                        //btnActualizar.setChecked(false);
                        break;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PETICION_CONFIG_UBICACION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("MapsActivity", "El usuario no ha realizado los cambios de configuración necesarios");
                        //btnActualizar.setChecked(false);
                        break;
                }
                break;
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(GeofenceController.getInstance().getGoogleApiClient()
                    , locRequest, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.e("MapsActivity","Gps LatLng: "+location.getLatitude()+","+location.getLongitude());
                        }
                    });


        }
    }


    private void updateUI(Location loc) {

    }

}
