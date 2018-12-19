package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.startup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    //variables
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewForgotPassword, textViewSignUp;

    private ProgressDialog progressDialog;

    //fireBase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //initiating fireBase variable
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //checking if user is already logged in
        if(user != null)
        {
            //means user is already logged in,, so start main activity
            Toast.makeText(LoginActivity.this, "Welcome " + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        //else create the rest activity

        //initiating variables
        progressDialog = new ProgressDialog(this);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);
        //setting multicolor text for signUp text
        final String first = "New User? ";
        String second = "<font color='##0000FF'>SIGN UP Here!</font>";
        textViewSignUp.setText(Html.fromHtml(first + second));


        buttonLogin = (Button) findViewById(R.id.buttonLogin);



        //attaching onClick listeners
        textViewSignUp.setOnClickListener(this);
        textViewForgotPassword.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if(v == buttonLogin)
        {
            loginUser();
        }

        if(v == textViewSignUp)
        {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        }

        if(v == textViewForgotPassword)
        {
            forgotPassowrdRecover();
        }
    }


    private void loginUser()
    {
        final String emailaddress = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        //validation check Email
        if(TextUtils.isEmpty(emailaddress))
        {
            editTextEmail.setError("Enter Email"); return;
        }

        //validation check Password
        if(TextUtils.isEmpty(password))
        {
            editTextPassword.setError("Enter Password"); return;
        }


        //now display progress dialog
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        //and start the fireBase authentication process
        firebaseAuth.signInWithEmailAndPassword(emailaddress, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                //check if task is successful
                if(task.isSuccessful())
                {
                    //then take user to Home Activity
                    Toast.makeText(LoginActivity.this, "Welcome " + emailaddress, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                }
                else {

                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(LoginActivity.this, "User with this email does not exist.", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(LoginActivity.this, "Could Not Log in! Try Again!", Toast.LENGTH_SHORT).show();

                }
            }
        });







    }

    private void forgotPassowrdRecover()
    {

        if(TextUtils.isEmpty(editTextEmail.getText().toString().trim())) {

            editTextEmail.setError("Enter Email First!");
        }
        else
        {
            progressDialog.setMessage("Sending Reset Email...");
            progressDialog.show();
            firebaseAuth.sendPasswordResetEmail(editTextEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Email Sent!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}
