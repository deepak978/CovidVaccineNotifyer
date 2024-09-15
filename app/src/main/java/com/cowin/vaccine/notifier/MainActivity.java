package com.cowin.vaccine.notifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends Activity {

    public static String pincode;
    public static String age_group;
    public static String dose_type;
    public static String vaccine_type;


    public static AdRequest adRequest;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    loadBannerAdd();
  }

    private void loadBannerAdd() {
        // Test adUnitId="ca-app-pub-3940256099942544/6300978111"
        // my banner adUnitId="ca-app-pub-5673415844958265/5906985847"

       // List<String> testDeviceIds = Arrays.asList("72C61635C6E6271333EB485F3029E7F1");
       // RequestConfiguration configuration =
       //         new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
       // MobileAds.setRequestConfiguration(configuration);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adRequest = new AdRequest.Builder().build();

    }
    public void submitButtonHandler(View view) {
      getUserInputs();
      openAddActivity();

    }

    private void openAddActivity() {
        Intent intent = new Intent(this, StopAlarmActivity.class);
        startActivity(intent);
    }


    private void getUserInputs() {

        EditText pincodeEditText = (EditText) findViewById(R.id.pincode);
        pincode = pincodeEditText.getText().toString();

        EditText age_groupEditText = (EditText) findViewById(R.id.age_group);
        age_group = age_groupEditText.getText().toString();

        EditText dose_typeEditText = (EditText) findViewById(R.id.dose_type);
        dose_type = dose_typeEditText.getText().toString();

        RadioButton radioAnyVaccine = (RadioButton) findViewById(R.id.radioAnyVaccine);
        RadioButton radioCovishield = (RadioButton) findViewById(R.id.radioCovishield);
        RadioButton radioCovaxin = (RadioButton) findViewById(R.id.radioCovaxin);
        RadioButton radioSputnikV = (RadioButton) findViewById(R.id.radioSputnikV);

        if(radioAnyVaccine.isChecked()){
            vaccine_type="AnyVaccine";
        }else if(radioCovishield.isChecked()){
            vaccine_type="Covishield";
        }else if(radioCovaxin.isChecked()){
            vaccine_type="Covaxin";
        }else if(radioSputnikV.isChecked()){
            vaccine_type="Sputnik V";
        }

        Button submitButton = (Button) findViewById(R.id.btn_submit);
        submitButton.setEnabled(false);

    }
}