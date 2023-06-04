package com.devil.win
import com.onesignal.OneSignal
const val ONESIGNAL_APP_ID = "50eefdeb-38d7-43be-a5ba-86237caeb417"

import io.flutter.embedding.android.FlutterActivity

class MainActivity: FlutterActivity() {

    OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
      
      // OneSignal Initialization
      OneSignal.initWithContext(this)
      OneSignal.setAppId(ONESIGNAL_APP_ID)
        
      // promptForPushNotifications will show the native Android notification permission prompt.
      // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
      OneSignal.promptForPushNotifications();
}
