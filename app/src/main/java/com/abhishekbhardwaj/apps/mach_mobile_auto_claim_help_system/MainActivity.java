package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system;


import android.Manifest.permission;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.claim.ClaimActivity;
import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.nearby.MapsActivity;
import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.profile.ProfileActivity;
import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.tracking.TrackingActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if the application is running above or on android 6.0
        if (VERSION.SDK_INT < 23)
        {
            // Do not need to check the permission
            setContentView(R.layout.activity_main);
        }
        else
        {
            if (checkAndRequestPermission()) {
                // if already permitted the permission
                setContentView(R.layout.activity_main);
            }
        }

    }

    private boolean checkAndRequestPermission() {

        int permissionWriteExternalStorage = ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE);

        int permissionReadExternalStorage = ContextCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE);

        // int permissionAccessCoarseLocation = ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION);

        // int permissionAccessFineLocation = ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION);

        int permissionCAMERA = ContextCompat.checkSelfPermission(this, permission.CAMERA);

        int permissionSendSMS = ContextCompat.checkSelfPermission(this, permission.SEND_SMS);

        int permissionVibrate = ContextCompat.checkSelfPermission(this, permission.VIBRATE);

        int permissionAccessNetworkState = ContextCompat.checkSelfPermission(this, permission.ACCESS_NETWORK_STATE);

        List<String> listPermissionNeeded = new ArrayList<>();

        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED)
            listPermissionNeeded.add(permission.READ_EXTERNAL_STORAGE);

        if (permissionWriteExternalStorage != PackageManager.PERMISSION_GRANTED)
            listPermissionNeeded.add(permission.WRITE_EXTERNAL_STORAGE);

        /*if (permissionAccessCoarseLocation != PackageManager.PERMISSION_GRANTED)
            listPermissionNeeded.add(permission.ACCESS_COARSE_LOCATION);

        if (permissionAccessFineLocation != PackageManager.PERMISSION_GRANTED)
            listPermissionNeeded.add(permission.ACCESS_FINE_LOCATION);*/

        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED)
            listPermissionNeeded.add(permission.SEND_SMS);

      if (permissionCAMERA != PackageManager.PERMISSION_GRANTED)
        listPermissionNeeded.add(permission.CAMERA);

        if (permissionVibrate != PackageManager.PERMISSION_GRANTED)
            listPermissionNeeded.add(permission.VIBRATE);

        if (permissionAccessNetworkState != PackageManager.PERMISSION_GRANTED)
            listPermissionNeeded.add(permission.ACCESS_NETWORK_STATE);

        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),
                MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }

        return true;
    }

    //check if play services is present on user's device
    public boolean isServiceOK()
    {
        int availabe = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(availabe == ConnectionResult.SUCCESS)
        {
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(availabe))
        {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, availabe, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
        {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_LONG).show();
        }
        return false;
    }



        //action listener for card view
    public void onCardClicked(View view) {


        Intent i;

        switch (view.getId()) {

            case R.id.near_by_card:
            {

                if(isServiceOK())
                {
                    i = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(i);
                }

                break;
            }

            case R.id.tracking_card:
            {
                startActivity(new Intent(MainActivity.this, TrackingActivity.class));
                break;
            }

            case R.id.claim_card:
            {
                startActivity(new Intent(MainActivity.this, ClaimActivity.class));
                break;
            }

            case R.id.profile_card:
            {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            }

            default:
            {
                break;
            }


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    setContentView(R.layout.activity_main);
                }
                else {
                    Toast.makeText(MainActivity.this, "You did not accept the request! Cannot use the Functionality ", Toast.LENGTH_SHORT).show();
                    closeNow();
                }
            }
        }


    }

    private void closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            finishAffinity();
        }

        else
        {
            finish();
        }
    }
}
