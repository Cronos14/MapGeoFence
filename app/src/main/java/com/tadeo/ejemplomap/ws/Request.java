package com.tadeo.ejemplomap.ws;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Tadeo-developer on 22/11/16.
 */

public class Request {

    public static final String IP = "https://maps.googleapis.com/maps/api/directions/json";

    private static JSONObject jObj 	= null;
    private static String json 		= "";


    public static JSONObject request(String requestURL, HashMap<String,String> params, boolean contentTypeJson, HashMap<String,String> headers, String method){


        URL url;
        String response = "";
        try {
            Log.i("WebServices", "URL: "+requestURL);

            if(method.equalsIgnoreCase("GET")){
                if(params != null && params.size()>0){

                    requestURL+="?"+convertParametersToGet(params);
                }
            }

            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(method);

            if(headers!=null){
                String txtHeaders = "[";
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    txtHeaders+=key+":"+value+",";
                    conn.setRequestProperty(key,value);

                }
                txtHeaders+="]";

                Log.i("WebServices", "headers: "+txtHeaders);
            }

            if(contentTypeJson)conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            if(params!=null)
                Log.i("WebServices", "request: " + params.toString());

            if(method.equalsIgnoreCase("POST")){
                conn.setDoOutput(true);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                bw.write(params.toString());

                bw.flush();
                bw.close();
            }



            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED ||
                    responseCode == HttpsURLConnection.HTTP_CONFLICT) {

                BufferedReader br;
                if(responseCode == HttpsURLConnection.HTTP_OK){

                    br=new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                }else{
                    br=new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                }
                String line;
                while ((line=br.readLine()) != null) {
                    response+=line;
                }

            }else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            json = response;
            Log.i("WebServices ", "response txt: "+json);
            jObj = new JSONObject(json);
            Log.i("WebServices ", "response: " + jObj.toString());

        } catch (JSONException e) {

            try{
                Log.e("WebServices ","http json object exception: " + e);
                Log.e("WebServices ","convert to JsonArray: "+json);
                JSONArray jsonArray =  new JSONArray(json);
                JSONObject jsonObjectAux = new JSONObject();
                jsonObjectAux.accumulate("values",jsonArray);
                jObj = jsonObjectAux;
            }catch (JSONException e1){
                Log.e("WebServices ","http json object exception to Array: " + e);
                jObj = null;
            }


        }
        return jObj;
    }

    public static JSONObject request(String requestURL,
                                     HashMap<String,String> params, boolean contentTypeJson, String method) {

        return request(requestURL,params,contentTypeJson,null,method);


    }

    private static String convertParametersToGet(HashMap<String,String> params){
        String paramsGet = "";

        for (String key : params.keySet()) {
            paramsGet+=key+"="+params.get(key).replace(" ","+")+"&";
        }

        if(paramsGet.length()>0){
            paramsGet = paramsGet.substring(0,paramsGet.length()-1);
        }

        return paramsGet;

    }
}
