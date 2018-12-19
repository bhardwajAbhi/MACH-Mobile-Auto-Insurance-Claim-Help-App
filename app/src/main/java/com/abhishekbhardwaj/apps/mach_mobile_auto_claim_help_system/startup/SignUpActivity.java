package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.startup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.MainActivity;
import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{


    //variables
    EditText editTextFullName, editTextEmailAddress, editTextPassword, editTextPhone;
    Button buttonRegister;
    TextView textviewLogIN;

    ProgressDialog progressDialog;

    //for fireBase Authentication
    FirebaseAuth firebaseAuth;
    //for fireBase DataBase reference
    DatabaseReference UserDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        UserDetails = FirebaseDatabase.getInstance().getReference("UserDetails");

        progressDialog = new ProgressDialog(this);

        editTextFullName = (EditText) findViewById(R.id.editTextFullName);
        editTextEmailAddress = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);

        textviewLogIN = (TextView) findViewById(R.id.textviewLogIN);

        buttonRegister = (Button) findViewById(R.id.buttonSignUp);

        buttonRegister.setOnClickListener(this);
        textviewLogIN.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        if (v == buttonRegister)
        {
            onRegisterButtonClicked();
        }
        if (v == textviewLogIN)
        {
            startActivity(new Intent (SignUpActivity.this, LoginActivity.class));
            finish();
        }
    }

    public void onRegisterButtonClicked() {

        final String fullName = editTextFullName.getText().toString().trim();
        final String emailAddress = editTextEmailAddress.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();

        //validation check for name
        if(TextUtils.isEmpty(fullName))
        {
            editTextFullName.setError("Enter Full Name");
            return;
        }

        //validation check for emailAddress
        if(!emailAddress.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+"))
        {
            editTextEmailAddress.setError("Invalid Email!");
            return;
        }

        //validation check for password
        if(!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,}$"))
        {
            editTextPassword.setError("Must Contain one Digit, Upper & a Lowercase letter [Min length - 6 ch]");
            return;
        }

        //validation check for phone number
        if(TextUtils.isEmpty(phone) || phone.length() <= 9)  // !android.util.Patterns.PHONE.matcher(phone).matches()
        {
            editTextPhone.setError("Invalid Phone Number");
            return;
        }

        //after validation
        progressDialog.setMessage("Creating Account ...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if(task.isSuccessful())
                {
                    // register account and add basic details to database
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    UserDetails.child(user.getUid()).child("Basic_Details").child("Full_Name").setValue(fullName);
                    UserDetails.child(user.getUid()).child("Basic_Details").child("Email").setValue(emailAddress);
                    UserDetails.child(user.getUid()).child("Basic_Details").child("Phone").setValue(phone);

                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();

                    //and jump to login Activity
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    finish();
                }
                else {
                    // user already exists or there may be internal failure
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Could not Register! Try Again!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });



    }
}
