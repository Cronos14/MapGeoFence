package com.tadeo.ejemplomap.geofence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.tadeo.ejemplomap.MapsActivity;
import com.tadeo.ejemplomap.R;
import com.tadeo.ejemplomap.models.Geo;
import com.tadeo.ejemplomap.utils.Singleton;
import com.tadeo.ejemplomap.utils.Utils;

/**
 * Created by Tadeo-developer on 25/12/16.
 */

public class GeoIntentService extends IntentService {


    public interface OnGeoTransitionListener{
        void onGeoTransition(Geo geo,int transition);
    }

    public static OnGeoTransitionListener onGeoTransitionListener;

    private String LOG_TAG = "DEBUG";

    public GeoIntentService() {
        super("GeoIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(LOG_TAG, "onHandleIntent");
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);

        if (event == null) {
            return;
        }

        if (event.hasError()) {
            Log.i(LOG_TAG, "ERROR");
            return;
        }

        int transition = event.getGeofenceTransition();
        double latitud = event.getTriggeringLocation().getLatitude();
        double longitude = event.getTriggeringLocation().getLongitude();

        if(Singleton.getSingleton().getGeos()!=null){
            for(Geo geo : Singleton.getSingleton().getGeos()){
                if(Utils.distanceTwoPointsMts(latitud,longitude,geo.getLatitud(),geo.getLongitud())<=geo.getRadio()){
                    Log.e("GeoIntent","nombre: "+geo.getNombre());
                    Singleton.getSingleton().setGeoSelected(geo);
                    break;
                }

            }
        }

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER){
            Log.i(LOG_TAG, "ENTER");


            if(onGeoTransitionListener!=null){
                onGeoTransitionListener.onGeoTransition(Singleton.getSingleton().getGeoSelected(),transition);
            }
            Log.e("GeoIntentService","LatLng: "+latitud+","+longitude);
            if(Singleton.getSingleton().getGeoSelected()!=null)
                createNotification(getBaseContext(),1,"Entrando al "+Singleton.getSingleton().getGeoSelected().getNombre(),"GeoFence Enter", MapsActivity.class);

        } else if(transition == Geofence.GEOFENCE_TRANSITION_DWELL ){
            Log.i(LOG_TAG, "DWELL");
            Log.e("GeoIntentService","LatLng: "+latitud+","+longitude);
            if(onGeoTransitionListener!=null){
                onGeoTransitionListener.onGeoTransition(Singleton.getSingleton().getGeoSelected(),transition);
            }

            if(Singleton.getSingleton().getGeoSelected()!=null)
                Toast.makeText(getBaseContext(),"Tecnico esperando en "+Singleton.getSingleton().getGeoSelected().getNombre(),Toast.LENGTH_LONG).show();
            //createNotification(getBaseContext(),1,"Esperando en un Servicio LatLng: "+latitud+","+longitude,"GeoFence DWELL", MapsActivity.class);
        }else if(transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i(LOG_TAG, "EXIT");

            if(onGeoTransitionListener!=null){
                onGeoTransitionListener.onGeoTransition(Singleton.getSingleton().getGeoSelected(),transition);
            }
            /*if(GeofenceController.getInstance().getGoogleApiClient()!=null && GeofenceController.getInstance().getGoogleApiClient().isConnected()){
                if(geoSelected!=null){
                    ArrayList<String> ids = new ArrayList<>();
                    ids.add(geoSelected.getId());
                    LocationServices.GeofencingApi.removeGeofences(GeofenceController.getInstance().getGoogleApiClient(),ids);
                }

            }*/

            Log.e("GeoIntentService","LatLng: "+latitud+","+longitude);

            if(Singleton.getSingleton().getGeoSelected()!=null)
                Toast.makeText(getBaseContext(),"Tecnico Saliendo del "+Singleton.getSingleton().getGeoSelected().getNombre(),Toast.LENGTH_LONG).show();
            //createNotification(getBaseContext(),1,"Saliendo de un Servicio LatLng: "+latitud+","+longitude,"GeoFence EXIT", MapsActivity.class);
        }
    }

    public static void createNotification(Context context, Integer id,
                                   String mensaje, String emisor, Class<?> actividad) {



        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Configuración de la Notificación
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(emisor)
                .setContentText(mensaje)
                .setAutoCancel(true)
                .setSound(
                        RingtoneManager
                                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        // Actividad a Abrir
        Intent resultIntent = new Intent(context, actividad);

        // Evita que Cree Nuevamente la Misma Actividad
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(actividad);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Crea la Notificación
        notificationManager.notify(id, builder.build());

    }
}
