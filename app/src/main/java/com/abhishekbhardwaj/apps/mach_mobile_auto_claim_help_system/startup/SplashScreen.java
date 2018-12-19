package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.startup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.MainActivity;
import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.R;


public class SplashScreen extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        final String PREFS_NAME = "MyPrefsFile";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

                if (settings.getBoolean("my_first_time", true)) {

                    //the app is being launched for first time, do something
                    Intent i = new Intent(SplashScreen.this, WelcomeScreen.class);
                    startActivity(i);
                    finish();


                    // also record the fact that the app has been started at least once
                    settings.edit().putBoolean("my_first_time", false).apply();
                }
                else
                {
                    Intent i = new Intent(SplashScreen.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        },3000);




    }
}
