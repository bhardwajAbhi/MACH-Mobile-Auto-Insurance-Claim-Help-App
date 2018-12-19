package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.claim;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import android.widget.ToggleButton;
import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.R;
import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.nearby.MapsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;

public class ClaimActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener{

    //variables for current location
    private static final String TAG = "ClaimActivity";
    private MapView mapView;
    private GoogleMap gMap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Location currentLocation;

    Button btn_CurrentLocation;

    //variables for camera
    private ArrayList<MyImage> images;
    private ImageAdapter imageAdapter;
    private GridView gridView;
    private Uri mCapturedImageURI;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Button btn_addPhoto, btnRequestClaim;
    
    private String message;
    private ToggleButton driveStatus;

    private CheckBox car_rearWindow, car_rightHeadLight, car_leftBumper, car_frontRightDoor, car_rightBumper, car_rearRightDoor, car_leftFrontDoor, car_leftRearDoor, car_leftBackLight, car_rightBackLight, car_Roof,  car_Hood, car_leftHeadLight;

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    //array list to store image path
    ArrayList<Uri> imagePathList;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_activity);

        //widgets=======
        btn_CurrentLocation = (Button) findViewById(R.id.Btn_useCurrentLocation);
        btn_CurrentLocation.setOnClickListener(this);

        btn_addPhoto = (Button) findViewById(R.id.addPhoto);
        btn_addPhoto.setOnClickListener(this);
        
        btnRequestClaim = (Button) findViewById(R.id.btnRequestClaim);
        btnRequestClaim.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        imagePathList = new ArrayList<Uri>();

        initializeCheckBoxes();


        //================================================================mapView======================================================================
        //map view bundle key
        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
        {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        //mapView initialization
        mapView = (MapView) findViewById(R.id.claim_MapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //===================================================================================================================================================


        //============================================================addPhotos===========================================================================
        images = new ArrayList();
        imageAdapter = new ImageAdapter(this, images);
        gridView = (GridView) findViewById(R.id.main_grid_view);
        //gridView.setAdapter(imageAdapter);


    }

    private void initializeCheckBoxes() {

        driveStatus = (ToggleButton) findViewById(R.id.toggleButton);

        car_frontRightDoor = (CheckBox) findViewById(R.id.car_front_right_door);
        car_Hood = (CheckBox) findViewById(R.id.car_hood);
        car_leftBackLight = (CheckBox) findViewById(R.id.car_left_back_light);
        car_leftBumper = (CheckBox) findViewById(R.id.car_left_bumper);
        car_leftFrontDoor = (CheckBox) findViewById(R.id.car_left_front_door);
        car_leftHeadLight = (CheckBox) findViewById(R.id.car_left_headlight);
        car_leftRearDoor = (CheckBox) findViewById(R.id.car_left_rear_door);
        car_rightHeadLight = (CheckBox) findViewById(R.id.car_right_headlight);
        car_rearRightDoor = (CheckBox) findViewById(R.id.car_right_rear_door);
        car_rearWindow = (CheckBox) findViewById(R.id.car_rearWindow);
        car_rightBackLight = (CheckBox) findViewById(R.id.car_right_back_light);
        car_rightBumper = (CheckBox) findViewById(R.id.car_right_bumper);
        car_rightHeadLight = (CheckBox) findViewById(R.id.car_right_headlight);
        car_Roof = (CheckBox) findViewById(R.id.car_roof);


    }


    //==========================================================ActionListeners====================================================================================
    @Override
    public void onClick(View v) {

        if(v == btn_CurrentLocation)
        {
            Log.d(TAG, "onComplete: current location -- " + currentLocation);
        }

        if(v == btn_addPhoto)
        {
            selectImage();
        }
        
        if (v == btnRequestClaim)
        {
            generateMessage();
            sendEmail();
        }

    }

    private void generateMessage() {

        //add user email
        message = "Claim Request from user =  " + user.getEmail();

        //add user ID
        message = message.concat("\nUser ID = " + user.getUid());

        //add user location
        message = message.concat("\nClaim Request Location = http://maps.google.com/?q=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude());

        //add drive status
        if (driveStatus.isChecked())
        {
            message = message.concat("\nDrive Status = DRIVABLE");
        }
        else
        {
            message = message.concat("\nDrive Status = NON - DRIVABLE");
        }


        //add damage spots information
        message = message.concat("\n\nDamage highlighted from Impact spots = ");

        if(car_Hood.isChecked())
        {
            message = message.concat("\n-Hood");
        }
        if (car_rightHeadLight.isChecked())
        {
            message = message.concat("\n-Right Head Light");
        }
        if (car_rightBumper.isChecked())
        {
            message = message.concat("\n-Right Bumper");
        }
        if (car_rightBackLight.isChecked())
        {
            message = message.concat("\n-Right Tail Light");
        }
        if (car_rearWindow.isChecked())
        {
            message = message.concat("\n-Rear Wind Shield");
        }
        if (car_rearRightDoor.isChecked())
        {
            message = message.concat("\n-Rear Right Door");
        }
        if (car_leftRearDoor.isChecked())
        {
            message = message.concat("\n-Rear Left Door");
        }
        if (car_leftHeadLight.isChecked())
        {
            message = message.concat("\n-Left Head Light");
        }
        if (car_leftFrontDoor.isChecked())
        {
            message = message.concat("\n-Front Left Door");
        }
        if (car_leftBumper.isChecked())
        {
            message = message.concat("\n-Left Bumper");
        }
        if (car_leftBackLight.isChecked())
        {
            message = message.concat("\n-Left Tail Light");
        }
        if (car_frontRightDoor.isChecked())
        {
            message = message.concat("\n-Front Right Door");
        }
        if (car_Roof.isChecked())
        {
            message = message.concat("\n-Roof");
        }


        Log.d("message", "generated message === \n" + message);




    }

    private void sendEmail() {


        Log.d("imagePathList", imagePathList.toString());

        Intent sendMail = new Intent(Intent.ACTION_SEND);
        sendMail.putExtra(Intent.EXTRA_EMAIL, new String[] {"abhishekbhardwaj090@gmail.com", "ashishchoudhary956@gmail.com"});
        sendMail.putExtra(Intent.EXTRA_SUBJECT, "Claim Report from user - " + user.getEmail());
        sendMail.putExtra(Intent.EXTRA_TEXT, message);

        if (imagePathList != null)
        {
            for (int i = 0; i < imagePathList.size(); i ++)
            {
                sendMail.putExtra(Intent.EXTRA_STREAM, imagePathList.get(i));
            }
        }


        sendMail.setType("message/rfc822");

        startActivity(Intent.createChooser(sendMail, "Choose an Email Client :"));

    }


    //=========================================================================Map View Logic Begin=================================================================
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        gMap.clear();
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);

        gMap.addMarker(options);

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
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15, "My Location");

                    } else {
                        Toast.makeText(ClaimActivity.this, "Unable to get Current Location!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        getDeviceLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMyLocationEnabled(true);

    }
    //========================================================================MAP view End=========================================================================


    //========================================================================Add Photos Logic Start===============================================================
    private void selectImage()
    {
        final CharSequence[] items = {"Take a new photo", "Choose from gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ClaimActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                if(items[i].equals("Take a new photo"))
                {
                    //start camera intent
                    startCamera();

                }
                else if(items[i].equals("Choose from gallery"))
                {
                    //start gallery intent
                    openGallery();

                }
                else if(items[i].equals("Cancel"))
                {
                    //dismiss the alert dialog
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    private void startCamera()
    {
        // for android version Nougat 27 and above,,, if not given below two lines; the app with crash because of exposed beyond app through ClipData.Item.getUri() at android.os.StrictMode.onFileUriExposed(
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String fileName = "temp.jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery()
    {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case RESULT_LOAD_IMAGE: {
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    MyImage image = new MyImage();
                    image.setTitle("Test");
                    image.setPath(picturePath); Log.d("ClaimActivity", "Image Path : " + image.getPath());

                    imagePathList.add(Uri.parse("file://" + picturePath));

                    images.add(image);
                    gridView.setAdapter(imageAdapter);
                    Log.d("ClaimActivity", "Image array list length : " + images.size());
                    Log.d("ClaimActivity", "IMage from gallery Successfull");
                }
            }
            case REQUEST_IMAGE_CAPTURE: {
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null);
                    int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String picturePath = cursor.getString(column_index_data);
                    MyImage image = new MyImage();
                    image.setTitle("Test");
                    image.setPath(picturePath); Log.d("ClaimActivity", "Image Path : " + image.getPath());
                    imagePathList.add(Uri.parse("file://" + picturePath));
                    images.add(image);
                    gridView.setAdapter(imageAdapter);

                    Log.d("ClaimActivity", "IMAGE CAPTURE Successfull");
                }
            }
        }

    }
}
