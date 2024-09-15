package com.cowin.vaccine.notifier;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.util.List;

public class StopAlarmActivity extends Activity {

    BackgroundTask.CallApiTask asyncTask = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop_activity);
       //loadAdd();


        AdView mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(MainActivity.adRequest);
        if(MainActivity.adRequest.isTestDevice(this)){
            System.out.println("This is test Device");
        }
    }



    /*
    private void loadAdd() {
        MobileAds.initialize(this, null);
        AdRequest adRequest = new AdRequest.Builder().build();

        // Prepare the Interstitial Ad
        interstitial = new InterstitialAd(StopAlarmActivity.this);
        // Insert the Ad Unit ID
        interstitial.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        interstitial.loadAd(adRequest);
        // Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Call displayInterstitial() function
                displayInterstitial();
            }
        });
    }  */

    /*public void displayInterstitial() {
        // If Ads are loaded, show Interstitial else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }*/

    private void callApiInBackGround() {

        asyncTask =new BackgroundTask.CallApiTask(new BackgroundTask.AsyncResponse() {
            public void playAlarm()  {
                int duration= 20;
                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                if(alert == null){
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    if(alert == null) {
                        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        duration = 3;
                    }
                }
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alert);
                //while(true) {
                    //try {
                        r.play();
                        //Thread.sleep(duration * 1000);

                    //} catch (InterruptedException e) {
                        //break;
                    //}
                //}
            }

            public void updateUIAndCallAgain(){
                TextView text_view_status = (TextView) findViewById(R.id.text_view_status);
                text_view_status.setText(BackgroundTask.exception.getMessage());
                callApiInBackGround();
            }

            @Override
            public void updateCentersInUI(List<String> available_slot) throws InterruptedException {
                String[] available_slot_arr =  new String[available_slot.size()];
                available_slot.toArray(available_slot_arr);
                ListView list = (ListView)findViewById(R.id.center_list);
                list.setAdapter(new ArrayAdapter<String>(StopAlarmActivity.this, android.R.layout.simple_list_item_1,android.R.id.text1,available_slot_arr));

            }

        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            asyncTask.execute(MainActivity.pincode, MainActivity.age_group, MainActivity.dose_type, MainActivity.vaccine_type);
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void stopAlarmButtonHandler(View view) {
         boolean cancel = asyncTask.cancel(true);

    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void notifyMeButtonHandler(View view) {
        callApiInBackGround();

    }
}
