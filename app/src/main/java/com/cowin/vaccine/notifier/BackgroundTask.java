package com.cowin.vaccine.notifier;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BackgroundTask {

    public static Exception exception=null;
    public static List<String> available_slot_list = null;
    private static final String COWIN_API_URL =
            "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=%s&date=%s";
    private static final String COWIN_API_URL1 =
            "https://cdn-api.co-vin.in/api//v2/appointment/sessions/public/findByPin?pincode=%s&date=%s";




    public interface AsyncResponse {
        public void playAlarm() throws InterruptedException;
        public void updateUIAndCallAgain() throws InterruptedException;
        public void updateCentersInUI(List<String> available_slot) throws InterruptedException;
    }

    public interface AsyncUpdateUIResponse {
        public void updateUI() throws InterruptedException;
    }

    @SuppressLint("NewApi")
    public static class CallApiTask extends AsyncTask<String, Void, JSONObject> {

        public AsyncResponse delegate = null;//Call back interface

        public CallApiTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;//Assigning call back interfacethrough constructor
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonWeather = null;
            try {
                while (true) {
                    jsonWeather = getCovidVaccineJSON(params[0]);
                    if (checkAvailability(jsonWeather, params[1], params[2], params[3])) {
                        return jsonWeather;
                    }
                    Thread.sleep(7000);
                }
            } catch (Exception e) {
                Log.d("Error", "Cannot process JSON results", e);
            }
            return jsonWeather;
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                //if (exception != null){
                    //delegate.updateUIAndCallAgain();
                //}else{
                    //delegate.playAlarm();
                //}

                //while(true) {
                    delegate.updateCentersInUI(available_slot_list);
                    delegate.playAlarm();
                    //Thread.sleep(20* 1000);
                //}
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




    private static boolean checkAvailability(JSONObject json, String age_group, String dose_type, String vaccine_type) {
        int available_slot = 0;
        available_slot_list = new ArrayList<String>();
        try {
            if (json != null) {
                JSONArray centers = json.getJSONArray("centers");
                int center_length = centers.length();
                for (int i = 0; i < center_length; i++) {

                    JSONObject center_detail = centers.getJSONObject(i);
                    JSONArray sessions = center_detail.getJSONArray("sessions");
                    int session_length = sessions.length();
                    for (int j = 0; j < session_length; j++) {
                        JSONObject session = sessions.getJSONObject(j);

                        if (session.getInt("min_age_limit") == Integer.parseInt(age_group)) {
                            if ("AnyVaccine".equalsIgnoreCase(vaccine_type) || session.getString("vaccine").equalsIgnoreCase(vaccine_type)) {

                                System.out.println("checking for age_group:- " + age_group + " and vaccine_type:- " + vaccine_type);

                                if ("dose1".equalsIgnoreCase(dose_type)) {
                                    int available = session.getInt("available_capacity_dose1");
                                    available_slot += available;
                                    available_slot_list.add(center_detail.getString("name")+ "(available "+available+")");
                                    System.out.println(available_slot + " Vaccine found so far");
                                } else if ("dose2".equalsIgnoreCase(dose_type)) {
                                    int available = session.getInt("available_capacity_dose2");
                                    available_slot += available;
                                    available_slot_list.add(center_detail.getString("name")+ "(available "+available+")");
                                    System.out.println(available_slot + " Vaccine found so far");
                                } else {
                                    int available = session.getInt("available_capacity_dose1") + session.getInt("available_capacity_dose2");
                                    available_slot += available;
                                    available_slot_list.add(center_detail.getString("name")+ "(available "+available+")");
                                    System.out.println(available_slot + " Vaccine found so far");
                                }
                            }

                        }


                    }
                }
                System.out.println(" center_length: " + center_length + ", available_slot: " + available_slot);
                if (available_slot > 0) {
                    return true;
                }
            }
            else{
                return true; // return to show error in Screen
            }
        } catch (JSONException e) {
            return false; //Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return false;
    }

    public static JSONObject getTestCovidVaccineJSON(String pincode) throws JSONException {
        /* Test JSON */
        JSONObject data = new JSONObject(Constants.testJson);
        return data;
    }

        public static JSONObject getCovidVaccineJSON(String pincode) {
        try {
            URL url = new URL(String.format(COWIN_API_URL, pincode, getDate()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36");
            connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            connection.setRequestProperty("authority", "cdn-api.co-vin.in");
            System.out.println("connection:- " + connection);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(40000);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());
            //System.out.println(data);
            return data;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    private static String getDate() {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat tf = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);

        Date timeStamp = new Date(System.currentTimeMillis());
        int compare = timeStamp.compareTo(new Date(timeStamp.getYear(), timeStamp.getMonth(), timeStamp.getDate(), 15, 0));
        if (compare > 0) { //after 3pm
            timeStamp.setDate(timeStamp.getDate() + 1);
        }
        String date = df.format(timeStamp);
        String time = tf.format(timeStamp);
        System.out.println("date " + date + " time " + time);
        return date;

    }


}
