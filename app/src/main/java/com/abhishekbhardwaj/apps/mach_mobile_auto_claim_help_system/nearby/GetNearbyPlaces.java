package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.nearby;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {

    GoogleMap mMap;
    String url;
    InputStream is;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    String data;
    static Context context;



    //for context purpose
    public GetNearbyPlaces(Context c)
    {
        context = c;
    }

    @Override
    protected String doInBackground(Object... params) {

        mMap = (GoogleMap) params[0];
        url = (String) params[1];


        try {
            URL myUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)  myUrl.openConnection();
            httpURLConnection.connect();
            is = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));

            String line="";
            stringBuilder = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null)
            {
                    stringBuilder.append(line);
            }
            data = stringBuilder.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    protected void onPostExecute(String s) {


        mMap.clear();
        Log.d("nearby", "onPostExecute: inside postexecute");



        try {
            JSONObject parentObject = new JSONObject(s);     Log.d("nearby", "75");
            JSONArray resultsArray = parentObject.getJSONArray("results"); Log.d("nearby", "76");


            if (resultsArray.length() != 0) {


                for (int i = 0; i < resultsArray.length(); i++) {

                    JSONObject jsonObject = resultsArray.getJSONObject(i);
                    Log.d("nearby", "82");
                    JSONObject locationObject = jsonObject.getJSONObject("geometry").getJSONObject("location");
                    Log.d("nearby", "83");

                    String latitude = locationObject.getString("lat");
                    Log.d("nearby", "85");
                    String longitude = locationObject.getString("lng");
                    Log.d("nearby", "86");

                    JSONObject nameObject = resultsArray.getJSONObject(i);
                    Log.d("nearby", "88");

                    String place_name = nameObject.getString("name");
                    Log.d("nearby", "90");
                    String vicinity = nameObject.getString("vicinity");
                    Log.d("nearby", "91");

                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    Log.d("nearby", "93");

                    MarkerOptions markerOptions = new MarkerOptions();
                    Log.d("nearby", "95");
                    markerOptions.title(place_name);
                    Log.d("nearby", "96");
                    markerOptions.snippet(vicinity);
                    Log.d("nearby", "97");
                    markerOptions.position(latLng);
                    Log.d("nearby", "98");

                    mMap.addMarker(markerOptions);
                    Log.d("nearby", "100");

                    Log.d("nearby", "onPostExecute: completed postexecute");
                }
            }
            else
            {
                Toast.makeText(context, "Ooops!! No Places Found Nearby", Toast.LENGTH_SHORT).show();
            }


        }
        catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
