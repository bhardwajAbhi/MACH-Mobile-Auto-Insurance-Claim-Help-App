
package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.tracking;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class CheckCertainty extends Activity implements LocationListener, OnClickListener{

  //variables
  private LocationManager locationManager;
  private double latitude, longitude;
  private String contact1, contact2;

  private TextView countDown;
  private Button btnYES, btnNO;

  private CountDownTimer countDownTimer = null;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_check_certainty);

    //initiate widgets
    countDown  = (TextView) findViewById(R.id.countDown);
    btnYES = (Button) findViewById(R.id.buttonYes);
    btnNO = (Button) findViewById(R.id.buttonNo);

    btnYES.setOnClickListener(this);
    btnNO.setOnClickListener(this);

    playSound();
    loadEmergencyContacts();
    fetchCurrentLocation();
    generateSMS();

    //countDownTimer
    countDownTimer = new CountDownTimer(16000, 1000) {
      @Override
      public void onTick(long millisUntilFinished) {
        countDown.setText("You have " + millisUntilFinished/1000 + " seconds remaining!");

      }

      @Override
      public void onFinish() {
      }
    }; countDownTimer.start();





  }

  private void playSound() {
  final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.rising_swoops);
    mediaPlayer.setLooping(false);
    mediaPlayer.start();

  }

  private void generateSMS() {

    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        sendMessage();
      }
    }, 15000);


  }

  private void sendMessage()
  {
    final SmsManager sms = SmsManager.getDefault();

    Toast.makeText(this, "SMS sent Successfully!", Toast.LENGTH_SHORT).show();

    sms.sendTextMessage(contact1, null,
        "Help! I've met with an accident at http://maps.google.com/?q=" + String
            .valueOf(latitude) + "," + String.valueOf(longitude), null, null);
    sms.sendTextMessage(contact1, null,
        "Nearby Hospitals http://maps.google.com/maps?q=hospital&mrt=yp&sll=" + String
            .valueOf(latitude) + "," + String.valueOf(longitude) + "&output=kml", null, null);
    sms.sendTextMessage(contact2, null,
        "Help! I've met with an accident at http://maps.google.com/?q=" + String
            .valueOf(latitude) + "," + String.valueOf(longitude), null, null);
    sms.sendTextMessage(contact2, null,
        "Nearby Hospitals http://maps.google.com/maps?q=hospital&mrt=yp&sll=" + String
            .valueOf(latitude) + "," + String.valueOf(longitude) + "&output=kml", null, null);


    System.exit(1);

  }
  private void fetchCurrentLocation() {

    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    //check self permission
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, this);


  }

  private void loadEmergencyContacts() {

    try {
      File myFile = new File("/sdcard/.emergencyNumbers.txt");
      FileInputStream fIn = new FileInputStream(myFile);
      BufferedReader myReader = new BufferedReader(
          new InputStreamReader(fIn));
      contact1 = myReader.readLine();
      contact2 = myReader.readLine();
      myReader.close();
    }


    catch (Exception e)
    {
      Toast.makeText(getBaseContext(), e.getMessage(),
          Toast.LENGTH_SHORT).show();
    }


  }

  @Override
  public void onLocationChanged(Location location) {
    latitude = location.getLatitude();
    longitude = location.getLongitude();
    Toast.makeText(getApplicationContext(), "Location Fetched", Toast.LENGTH_SHORT).show();

  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  @Override
  public void onProviderEnabled(String provider) {

  }

  @Override
  public void onProviderDisabled(String provider) {

  }

  @Override
  public void onClick(View v) {

    if (v == btnNO)
    {
      dismissAlert();
    }

    if (v == btnYES)
    {
      sendMessage();
    }

  }

  private void dismissAlert() {

    System.exit(1);

  }
}
