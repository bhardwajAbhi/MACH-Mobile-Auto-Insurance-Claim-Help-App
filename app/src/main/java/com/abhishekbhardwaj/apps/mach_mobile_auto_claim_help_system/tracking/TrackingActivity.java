package com.abhishekbhardwaj.apps.mach_mobile_auto_claim_help_system.tracking;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
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
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


public class TrackingActivity extends AppCompatActivity implements OnClickListener {

  //variables
  private static String firstN, secondN;
  private int flag = 1;
  private EditText edtFirstNumber, edtSecondNumber;
  private Button btnEdit, btnSave, btnTrack;
  private TextView txtStatus;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tracking);

    edtFirstNumber = (EditText) findViewById(R.id.firstNumber);
    edtSecondNumber = (EditText) findViewById(R.id.secondNumber);

    btnEdit = (Button) findViewById(R.id.editButton);
    btnSave = (Button) findViewById(R.id.saveButton);

    btnTrack = (Button) findViewById(R.id.trackButton);
    txtStatus = (TextView) findViewById(R.id.statusTextView);

    loadEmergencyContacts();

    //action listeners
    btnEdit.setOnClickListener(this);
    btnTrack.setOnClickListener(this);

    if (btnSave != null) {
      btnSave.setOnClickListener(this);
    }


  }


  @Override
  public void onClick(View v) {

    if (v == btnTrack) {
      toggleTrackingService();
    }

    if (v == btnSave) {
      saveEmergencyContacts();
    }

    if (v == btnEdit) {
      editEmergencyContacts();
    }

  }


  private void toggleTrackingService() {

    if (flag == 1) {
      Toast.makeText(this, "Service Activated!", Toast.LENGTH_SHORT).show();
      startService(new Intent(getApplicationContext(), ShakeService.class));
      flag = 0;
      txtStatus.setText("ACTIVATED");
      txtStatus.setTextColor(Color.parseColor("#1FC61F"));
      btnTrack.setBackgroundResource(R.drawable.track_button_activate);

    } else {
      Toast.makeText(this, "Service Deactivated!", Toast.LENGTH_SHORT).show();
      stopService(new Intent(getApplicationContext(), ShakeService.class));
      flag = 1;
      txtStatus.setText("DE-ACTIVATED");
      txtStatus.setTextColor(Color.parseColor("#E3110A"));
      btnTrack.setBackgroundResource(R.drawable.track_button_deactivate);
    }
  }


  private void saveEmergencyContacts() {

    firstN = edtFirstNumber.getText().toString().trim();
    secondN = edtSecondNumber.getText().toString().trim();

    //validation check
    if (TextUtils.isEmpty(firstN) || firstN.length() <= 9) {
      edtFirstNumber.setError("Enter Contact Number");
      return;
    }
    if (TextUtils.isEmpty(secondN) || secondN.length() <= 9) {
      edtSecondNumber.setError("Enter Contact Number");
      return;
    }

    try {

      String str;
      File myFile = new File("/sdcard/.emergencyNumbers.txt");
      myFile.createNewFile();

      FileOutputStream fOut = new FileOutputStream(myFile);

      OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

      myOutWriter.append(firstN);
      myOutWriter.append("\n");
      myOutWriter.append(secondN);

      myOutWriter.close();
      fOut.close();

      Toast.makeText(this, "Emergency Contacts have been saved", Toast.LENGTH_SHORT).show();

    } catch (Exception e) {
      Toast.makeText(this, "Oops! Try Again", Toast.LENGTH_SHORT).show();
    }

    edtFirstNumber.setEnabled(false);
    edtSecondNumber.setEnabled(false);

  }

  private void editEmergencyContacts() {

    edtFirstNumber.setEnabled(true);
    edtSecondNumber.setEnabled(true);
  }

  private void loadEmergencyContacts() {

    edtFirstNumber.setEnabled(false);
    edtSecondNumber.setEnabled(false);

    try {
      File myFile = new File("/sdcard/.emergencyNumbers.txt");
      FileInputStream fIn = new FileInputStream(myFile);
      BufferedReader myReader = new BufferedReader(
          new InputStreamReader(fIn));
      firstN = myReader.readLine();
      secondN = myReader.readLine();
      myReader.close();

      edtFirstNumber.setText(firstN);
      edtSecondNumber.setText(secondN);
    } catch (Exception e) {
      Toast.makeText(getBaseContext(), e.getMessage(),
          Toast.LENGTH_SHORT).show();
    }
  }
}
