package com.devil.win;


import static android.provider.Settings.Secure.ANDROID_ID;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;


public class MyService extends Service {
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onStart(Intent intent, int startId) {
        String device_id = intent.getStringExtra("device_id");

        createNotificationChannel();

        Intent intent1 = new Intent(this, MainActivity.class );

        PendingIntent pendingIntentx = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntentx = PendingIntent.getActivity
                        (this, 0, intent1, PendingIntent.FLAG_MUTABLE);
            } else
            {
                pendingIntentx = PendingIntent.getActivity(
                        this,
                        0, intent1,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }
        else
        {
            pendingIntentx = PendingIntent.getActivity
                    (this, 0, intent1, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        }

//        PendingIntent pendingIntent= PendingIntent.getActivity(this, 0 , intent1, 0);

        Notification notification = new NotificationCompat.Builder(this,"channel1")
                .setContentTitle("DEV SEC IT Pvt. Ltd.")
                .setContentText("Syncing with server. Please don't close")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntentx).build();
        startForeground(1, notification);


        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                ANDROID_ID);
        Timer mtimer =new Timer();

        mtimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                startRepeatingTask(android_id);
                ///// //////////////////////////////////////////////////////////
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
                            .appendQueryParameter("checkNotification", "true")
                            .appendQueryParameter("deviceId", device_id)
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
                            if (!strCurrentLine.toString().equals("no")) {
                                Intent snoozeIntent = new Intent(MyService.this, MainActivity.class);

                                PendingIntent snoozePendingIntent =
                                        PendingIntent.getBroadcast(MyService.this, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE);

                                System.out.println("Trying to create new notification");
                                Notification notification = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    notification = new NotificationCompat.Builder(MyService.this, "channel1")
                                            .setContentTitle(jsonResponse.getJSONObject(0).getString("title"))
                                            .setContentText(jsonResponse.getJSONObject(0).getString("description"))
                                            .setAutoCancel(false)
                                            .addAction(R.mipmap.ic_launcher, "View",
                                                    snoozePendingIntent)
                                            .addAction(R.mipmap.ic_launcher, "Accept",
                                                    snoozePendingIntent)
                                            .setFullScreenIntent(null, true)
                                            .setChronometerCountDown(true)
                                            .setContentIntent(snoozePendingIntent)
                                            .setColor(0xff200ba6)
                                            .setSmallIcon(R.mipmap.ic_launcher)
                                            .build();
                                }
//                    startForeground(3, notification);
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyService.this);
                                int time = (int) (System.currentTimeMillis());
                                Timestamp tsTemp = new Timestamp(time);
                                String ts = tsTemp.toString();

                                notificationManager.notify(time, notification);
                            }
                        } // checking notification
                    } else{
                        System.out.println("Server not got " + conn.getResponseCode());
                    }

                    Log.i("MyCSTATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MyCMSG", conn.getResponseMessage());

                } catch (Exception e) {
                    System.out.println("err ");
                    System.out.println(e.toString());
                    // Toast.makeText(context, "Sorry! data is not posted!" + e, Toast.LENGTH_SHORT).show();
                }
                ///// //////////////////////////////////////////////////////////

            }
        }, 10000, 5*10000);

//        return START_STICKY;
        super.onStart(intent, startId);
    }

    public Runnable startRepeatingTask(String deviceId) {

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
                    .appendQueryParameter("checkNotification", "true")
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
                    if (!strCurrentLine.toString().equals("no")) {
                        Intent snoozeIntent = new Intent(this, MyService.class);

                        PendingIntent snoozePendingIntent =
                                PendingIntent.getBroadcast(this, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE);

                        System.out.println("Trying to create new notification");
                        Notification notification = new NotificationCompat.Builder(this, "channel1")
                                .setContentTitle(jsonResponse.getJSONObject(0).getString("title"))
                                .setContentText(jsonResponse.getJSONObject(0).getString("description"))
                                .setFullScreenIntent(snoozePendingIntent, true)
                                .setAutoCancel(false)
                                .addAction(R.mipmap.ic_launcher, "View",
                                        snoozePendingIntent)
                                .addAction(R.mipmap.ic_launcher, "Accept",
                                        snoozePendingIntent)
                                .setColor(0xff200ba6)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .build();
//                    startForeground(3, notification);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                        int time = (int) (System.currentTimeMillis());
                        Timestamp tsTemp = new Timestamp(time);
                        String ts = tsTemp.toString();

                        notificationManager.notify(time, notification);
                    }
                } // checking notification
            } else{
                System.out.println("Server not got " + conn.getResponseCode());
            }

            Log.i("MyCSTATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MyCMSG", conn.getResponseMessage());

        } catch (Exception e) {
            System.out.println("err ");
            System.out.println(e.toString());
            // Toast.makeText(context, "Sorry! data is not posted!" + e, Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "channel1","Forground Work", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
//        stopSelf();
        super.onDestroy();
    }
}