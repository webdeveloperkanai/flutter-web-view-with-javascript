package com.devil.win;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MYBroadCaster extends BroadcastReceiver {

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    public static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {

//        Toast.makeText(context.getApplicationContext(), "Me jinda hu...", Toast.LENGTH_SHORT).show();
        startRepeatingTask(context);

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent service = new Intent(context, MyService.class);
            context.startService(service);
        }


        mHandler = new Handler() {
            @Override
            public void publish(LogRecord logRecord) {
//                startRepeatingTask(context);
            }

            @Override
            public void flush() {
//                startRepeatingTask(context);
            }

            @Override
            public void close() throws SecurityException {

            }

        };




    }




    private void startRepeatingTask(Context context  ) {


        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String  deviceId = pref.getString("device_id", null); // getting String
        String  phone = pref.getString("phone", null); // getting String

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL urlx = new URL(MyConst.APP_API);
            HttpURLConnection conn = (HttpURLConnection) urlx.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("version", "true")
                    .appendQueryParameter("deviceId", deviceId)
                    ;
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            conn.getResponseCode();

            BufferedReader br = null;
            if (conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String strCurrentLine;
                while ((strCurrentLine = br.readLine()) != null) {
                    System.out.println("Server Response ::: " + strCurrentLine.toString());
                    JSONArray jsonResponse = new JSONArray(strCurrentLine);
                    System.out.println(jsonResponse);


//                 Toast.makeText(context, "Name :: "+ jsonResponse.getJSONObject(0).getString("id"), Toast.LENGTH_SHORT).show();
                }
            } else {
                System.out.println("Server not got " + conn.getResponseCode());
            }

            Log.i("MyCSTATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MyCMSG" , conn.getResponseMessage());

        } catch (Exception e) {
            // Toast.makeText(context, "Sorry! data is not posted!" + e, Toast.LENGTH_SHORT).show();
        }

        // Toast.makeText(context.getApplicationContext(), "Me jinda hu with cron jobs Java...", Toast.LENGTH_SHORT).show();
    }

}