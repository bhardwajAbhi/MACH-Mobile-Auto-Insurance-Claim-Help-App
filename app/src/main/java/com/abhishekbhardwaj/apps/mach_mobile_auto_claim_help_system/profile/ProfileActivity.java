package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.R;
import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.startup.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    //variables
    private CircleImageView profileImage;
    private TextView textViewUserName;
    private Button signOut;
    private ProgressDialog progressDialog;


    //basic details card
    private TextView customerName, customerEmail, customerPhone, customerAdd;

    //Vehicle details card
    private TextView vehicleMake, vehicleModel, vehicleSubModel, vehicleCubicCapacity, vehicleMfg, vehicleSeating, vehicleBody, vehicleRegistrationNo, vehicleRTO, vehicleFuelType, vehicleChassis, vehicleEngine;

    //Insurance details card
    private TextView insurancePolicyNo, insuranceProposalNo, insuranceProposalDate, insuranceIssuedOn, insurancePolicyPeriod, insuredName, insuredAdd;
    private Button insuranceButtonMoreDetails;



    private static final int GALLERY_INTENT = 2;

    //for firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private DatabaseReference userDetailsReference, userInsuranceDetailsReference;
    private String UserEmail = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);



        //fireBase
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();



        userDetailsReference = FirebaseDatabase.getInstance().getReference("UserDetails");





        userInsuranceDetailsReference = FirebaseDatabase.getInstance().getReference("UserInsuranceDetails");
        storageReference = FirebaseStorage.getInstance().getReference("UserProfilePics");



        signOut = (Button) findViewById(R.id.buttonSignOut) ;
        progressDialog = new ProgressDialog(this);
        //app bar widgets
        profileImage = (CircleImageView) findViewById(R.id.userProfileImage);
        textViewUserName = (TextView) findViewById(R.id.textviewuserName);


        //basic details card
        customerName = (TextView) findViewById(R.id.customerName);
        customerEmail = (TextView) findViewById(R.id.customerEmail);
        customerPhone = (TextView) findViewById(R.id.customerPhone);
        customerAdd = (TextView) findViewById(R.id.customerAdd);

        //vehicle details card
        vehicleMake = (TextView) findViewById(R.id.vehicleCompany);
        vehicleModel = (TextView) findViewById(R.id.vehicleModel);
        vehicleSubModel = (TextView) findViewById(R.id.vehicleSubmodel);
        vehicleCubicCapacity = (TextView) findViewById(R.id.VehicleCubicCapacity);
        vehicleMfg = (TextView) findViewById(R.id.VehicleMfg);
        vehicleSeating = (TextView) findViewById(R.id.vehicleSeating);
        vehicleBody = (TextView) findViewById(R.id.vehicleBody);
        vehicleRegistrationNo = (TextView) findViewById(R.id.vehicleRegistrationNo);
        vehicleRTO = (TextView) findViewById(R.id.vehicleRto);
        vehicleFuelType = (TextView) findViewById(R.id.vehicleFuel);
        vehicleChassis = (TextView) findViewById(R.id.vehicleChassis);
        vehicleEngine = (TextView) findViewById(R.id.VehicleEngine);

        //insurance details card
        insurancePolicyNo = (TextView) findViewById(R.id.insurancePolicyNo);
        insuranceProposalNo = (TextView) findViewById(R.id.insuranceProposalNo);
        insuranceProposalDate = (TextView) findViewById(R.id.insuranceProposalDate);
        insuranceIssuedOn = (TextView) findViewById(R.id.insurancePolicyIssuedOn);
        insurancePolicyPeriod = (TextView) findViewById(R.id.insurancePeriod);
        insuredName = (TextView) findViewById(R.id.insuredName);
        insuredAdd = (TextView) findViewById(R.id.insuredAdd);





        //action listener
        signOut.setOnClickListener(this);
        profileImage.setOnClickListener(this);


        //setting profileImage
        setProfileImage ();
        //setting userName
        setTextViewUserName();

    }



    @Override
    public void onClick(View v) {

        if(v == signOut)
        {
            firebaseAuth.signOut();
            Toast.makeText(ProfileActivity.this, "Logged Out Successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        }

        if(v == profileImage)
        {
            //check or ask for READ_EXTERNAL_STORAGE_PERMISSION firstly
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            //then handle this in onRequestPermissionResult() method
        }

    }



    private void setTextViewUserName()
    {
        userDetailsReference.child(currentUser.getUid()).child("Basic_Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("data", "onDataChange: Datasnapshot" + dataSnapshot);
                textViewUserName.setText(dataSnapshot.child("Full_Name").getValue().toString());
                Log.d("data", "UserName has been set");


                UserEmail = dataSnapshot.child("Email").getValue().toString().trim();
                UserEmail = UserEmail.replace(".","_dot_");
                Log.d("data", "Insurance Key fetched" + UserEmail);


                //after getting email,, we can fill the card view content
                fillBasicDetailsCard(UserEmail);
                fillVehicleDetailsCard(UserEmail);
                fillInsuranceDetailsCard(UserEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setProfileImage () {

        //get the fileReference
        StorageReference filePath = storageReference.child("UserProfilePhotos").child(currentUser.getUid()).child("Profile Picture");

        //add the OnSuccessListener to get the uri of that file
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("data", "onSuccess: setProfile URI: " + uri);


                //then use the picasso to download and set image as profile
                Picasso.get().load(uri).placeholder(R.drawable.default_profile_image).error(R.drawable.profile).into(profileImage);

            }
        });

    }

    private void getProfileImage() {

        Intent selectPhoto = new Intent(Intent.ACTION_PICK);
        selectPhoto.setType("image/*");

        startActivityForResult(selectPhoto, GALLERY_INTENT);
        //then look for onActivityResult() method
    }

    //methods for card views
    private void fillBasicDetailsCard(String userEmail) {

        userInsuranceDetailsReference.child(userEmail).child("BasicDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("data", "User Details DataSnapShot : " + dataSnapshot);
                customerName.setText(dataSnapshot.child("Name").getValue().toString());
                customerEmail.setText(dataSnapshot.child("Email").getValue().toString());
                customerPhone.setText(dataSnapshot.child("Phone").getValue().toString());
                customerAdd.setText(dataSnapshot.child("Address").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fillVehicleDetailsCard(String userEmail) {
        userInsuranceDetailsReference.child(userEmail).child("VehicleDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("data", "Vehicle Details DataSnapShot : " + dataSnapshot);

                //setting data
                vehicleMake.setText(dataSnapshot.child("Make").getValue().toString());
                vehicleModel.setText(dataSnapshot.child("Model").getValue().toString());
                vehicleSubModel.setText(dataSnapshot.child("SubModel").getValue().toString());
                vehicleCubicCapacity.setText(dataSnapshot.child("CC").getValue().toString());
                vehicleMfg.setText(dataSnapshot.child("Mfg").getValue().toString());
                vehicleSeating.setText(dataSnapshot.child("Seating").getValue().toString());
                vehicleBody.setText(dataSnapshot.child("Body").getValue().toString());
                vehicleRegistrationNo.setText(dataSnapshot.child("Registration").getValue().toString());
                vehicleRTO.setText(dataSnapshot.child("RTO").getValue().toString());
                vehicleFuelType.setText(dataSnapshot.child("Fuel").getValue().toString());
                vehicleChassis.setText(dataSnapshot.child("Chassis").getValue().toString());
                vehicleEngine.setText(dataSnapshot.child("Engine").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fillInsuranceDetailsCard(String userEmail) {

        userInsuranceDetailsReference.child(userEmail).child("InsuranceDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("data", "Insurance Details DataSnapShot : " + dataSnapshot);

                //setting fields
                insurancePolicyNo.setText(dataSnapshot.child("PolicyNo").getValue().toString());
                insuranceProposalNo.setText(dataSnapshot.child("ProposalNo").getValue().toString());
                insuranceProposalDate.setText(dataSnapshot.child("ProposalDate").getValue().toString());
                insuranceIssuedOn.setText(dataSnapshot.child("IssuedOn").getValue().toString());
                insurancePolicyPeriod.setText(dataSnapshot.child("PolicyPeriod").getValue().toString());
                insuredName.setText(dataSnapshot.child("InsuredName").getValue().toString());
                insuredAdd.setText(dataSnapshot.child("InsuredAdd").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {

            progressDialog.setMessage("Uploading Picture ...");
            progressDialog.show();

            final Uri uri = data.getData();

            Log.d("data", "onActivityResult: uri data" + uri);

            StorageReference filePath = storageReference.child("UserProfilePhotos").child(currentUser.getUid()).child("Profile Picture");
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "New Profile Pic Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    profileImage.setImageURI(uri);

                }
            });
        }


    }

    //for EXTERNAL_STORAGE permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //permissions are granted,, so call the getImage method
                    getProfileImage();
                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }


    }
}
