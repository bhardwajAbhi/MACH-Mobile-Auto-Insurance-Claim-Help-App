package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.nearby;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenu;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {


    //variables
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;


    private GoogleMap mMap;
    private static final int DEFAULT_ZOOM = 13;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleApiClient mGoogleApiClient, client;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(88, 456));
    private PlaceInfo mPlace;
    private Marker marker;
    LocationRequest request;
    Location currentLocation;


    //widgets
    private AutoCompleteTextView inputSearch;
    private ImageView locationInfo;
    FabSpeedDial fabnearyby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getLocationPermission();



        //search edit text and its listener
        inputSearch = (AutoCompleteTextView) findViewById(R.id.input_search);
        fabnearyby = (FabSpeedDial) findViewById(R.id.fab_nearby);
        locationInfo = (ImageView) findViewById(R.id.location_info);
        locationInfo.setVisibility(View.INVISIBLE);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        inputSearch.setOnItemClickListener(autoCompleteClickListener);

        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);


        inputSearch.setAdapter(placeAutocompleteAdapter);


        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //calling a method geoLocate() to locate the input
                geoLocate();

                return false;
            }
        });


        locationInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (marker.isInfoWindowShown()) {
                        marker.hideInfoWindow();
                    } else {
                        marker.showInfoWindow();
                    }

                } catch (NullPointerException e) {
                    e.getMessage();
                }


            }
        });



        //floating button click event handling
        fabnearyby.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.nearby_hospital :
                    {
                        nearby("hospital");
                        break;
                    }
                    case R.id.nearby_fire :
                    {
                        nearby("fire");
                        break;
                    }

                    case R.id.nearby_gas :
                    {
                        nearby("gas");
                        break;
                    }
                    case R.id.nearby_car :
                    {
                        nearby("car_dealer");
                        break;
                    }
                    default: {
                        return true;
                    }
                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });

    }


    //method to be called inside the switch case
    private void nearby(String keyword)
    {

            Log.d("nearby", "nearby method begin");

            StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            stringBuilder.append("location="+currentLocation.getLatitude()+","+currentLocation.getLongitude());
            stringBuilder.append("&radius="+100000);
            stringBuilder.append("&keyword="+keyword);
            stringBuilder.append("&key="+"AIzaSyDefGeV45m3i8IOdGQ9oUCw6LCNmV3PX58");

            String url = stringBuilder.toString();

            Object dataTransfer[] = new Object[2];
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;

            GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces(MapsActivity.this);
            getNearbyPlaces.execute(dataTransfer);

            Log.d("nearby", "nearby method completed");


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        Toast.makeText(this, "Map is Ready!", Toast.LENGTH_SHORT).show();
        mMap = googleMap;


        //custom map styling
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


        client = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        client.connect();


        getDeviceLocation();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        request = new LocationRequest().create();
        request.setInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);




    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }





    /*-------------------------------------------------------------------------------------------------------------------------------------------------*/
    //explicitly check permissions

    private void initMap()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission()
    {
        String [] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionGranted  = true;
                initMap();

            }
            else
            {
                ActivityCompat.requestPermissions(this, permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this, permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE :
            {
                if(grantResults.length > 0 )
                {

                    for (int i = 0; i < grantResults.length; i ++ )
                    {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            mLocationPermissionGranted = false;
                           return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //everything is OK, and we are ready to initialize the map like this
                    initMap();

                }
            }
        }
    }




    private void geoLocate() {
        String searchString = inputSearch.getText().toString().trim();

        Geocoder geocoder = new Geocoder(MapsActivity.this);

        List<Address> addressList = new ArrayList<>();


        try {
            addressList = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (addressList.size() > 0) {
            Address address = addressList.get(0);

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));


        }


    }


    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.clear();

        mMap.setInfoWindowAdapter(new LocationInfoWindowAdapter(MapsActivity.this));

        if (placeInfo != null) {

            try {

                String snippet = "Address: " + placeInfo.getAddress() + "\n\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n\n" +
                        "Rating: " + placeInfo.getAddress() + "\n\n";


                MarkerOptions options = new MarkerOptions().position(latLng).title(placeInfo.getName()).snippet(snippet);
                marker = mMap.addMarker(options);
                locationInfo.setVisibility(View.VISIBLE);


            } catch (NullPointerException e) {
                e.getMessage();
            }

        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }


    }


    private void moveCamera(LatLng latLng, float zoom, String title) {
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));


        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);

            mMap.addMarker(options);
        }


    }


    private void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()) {
                        currentLocation = (Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");

                    } else {
                        Toast.makeText(MapsActivity.this, "Unable to get Current Location!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

























    /*
     * -----------------------------------------------------------------------google places api autocomplete suggestions -------------------------------------------------------------------------------------------------
     * */


    private AdapterView.OnItemClickListener autoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
            final String placeID = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeID);

            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {

            if (!places.getStatus().isSuccess()) {
                //place query did not complete successfully
                places.release();
                return;
            }


            final Place place = places.get(0);

            mPlace = new PlaceInfo();

            try {

                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name : " + mPlace.getName());

                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: ID : " + mPlace.getId());

                mPlace.setLatLng(place.getLatLng());
                Log.d(TAG, "onResult: LatLng : " + mPlace.getLatLng());

                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating : " + mPlace.getRating());

                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: website : " + mPlace.getWebsiteUri());

                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone : " + mPlace.getPhoneNumber());

                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: addresss : " + mPlace.getAddress());

              /*  mPlace.setAttributions(place.getAttributions().toString());
                Log.d(TAG, "onResult: attributions : " + mPlace.getAttributions());*/

            } catch (NullPointerException e) {
                e.getMessage();
            }


            moveCamera(mPlace.getLatLng(), DEFAULT_ZOOM, mPlace);


            places.release();
        }
    };



    //places key - AIzaSyDuRBcIYAsyfnT-CO5GPrO3aEvtuX8ICmk

}
