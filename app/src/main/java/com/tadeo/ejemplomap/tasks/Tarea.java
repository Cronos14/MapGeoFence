package com.tadeo.ejemplomap.tasks;

import android.os.AsyncTask;

import com.tadeo.ejemplomap.interfaces.OnPostExecuteListener;
import com.tadeo.ejemplomap.ws.Request;

import org.json.JSONObject;

import java.util.HashMap;

import static com.tadeo.ejemplomap.ws.Request.IP;

/**
 * Created by Tadeo-developer on 24/12/16.
 */

public class Tarea extends AsyncTask<String,Void,JSONObject> {

    private OnPostExecuteListener onPostExecuteListener;

    private HashMap<String,String> params;

    public Tarea(HashMap<String,String> params){
        this.params = params;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {

        return Request.request(IP,params,true,null,"GET");
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);

        if(onPostExecuteListener!=null){
            onPostExecuteListener.onPostExecute(jsonObject);
        }

    }

    public void setOnPostExecuteListener(OnPostExecuteListener onPostExecuteListener){
        this.onPostExecuteListener = onPostExecuteListener;
    }
}
