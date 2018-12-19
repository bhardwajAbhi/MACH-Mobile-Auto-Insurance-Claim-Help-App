package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.nearby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class LocationInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{


    private final View window;
    private Context context;

    public LocationInfoWindowAdapter(Context context) {
        this.context = context;
        window = LayoutInflater.from(context).inflate(R.layout.location_info_window, null);
    }


    private void DisplayTextonWindow (Marker marker, View view)
    {
        String title = marker.getTitle();
        TextView location_title = (TextView) view.findViewById(R.id.location_title);

        if(!title.equals(""))
        {
            location_title.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView location_details = (TextView) view.findViewById(R.id.snippet);

        if(!snippet.equals(""))
        {
            location_details.setText(snippet);
        }



    }




    @Override
    public View getInfoWindow(Marker marker) {

        DisplayTextonWindow(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        DisplayTextonWindow(marker, window);
        return window;
    }
}
