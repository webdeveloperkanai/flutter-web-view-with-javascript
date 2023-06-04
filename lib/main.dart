// ignore_for_file: constant_identifier_names, prefer_interpolation_to_compose_strings, avoid_print, prefer_const_constructors, unused_local_variable

import 'dart:async';
import 'package:flutter/services.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:flutter/material.dart';
import 'package:flutter_webview_plugin/flutter_webview_plugin.dart';
import 'package:http/http.dart' as http;
import 'package:url_launcher/url_launcher.dart';
import 'package:onesignal_flutter/onesignal_flutter.dart';

const APP_API = "https://color.devsecit.com/api.php";
const APP_UPDATE = "https://color.devsecit.com/COLOR.apk";
const APP_NAME = "Devil Win";

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool _enableConsentButton = false;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    if (!mounted) return;

    OneSignal.shared.setLogLevel(OSLogLevel.verbose, OSLogLevel.none);

    OneSignal.shared.setRequiresUserPrivacyConsent(false);

    OneSignal.shared
        .setNotificationOpenedHandler((OSNotificationOpenedResult result) {
      print('NOTIFICATION OPENED HANDLER CALLED WITH: ${result}');
      setState(() {});
    });

    OneSignal.shared.setNotificationWillShowInForegroundHandler(
        (OSNotificationReceivedEvent event) {
      print('FOREGROUND HANDLER CALLED WITH: ${event}');

      /// Display Notification, send null to not display
      event.complete(null);
    });

    OneSignal.shared
        .setInAppMessageClickedHandler((OSInAppMessageAction action) {});

    OneSignal.shared
        .setSubscriptionObserver((OSSubscriptionStateChanges changes) {
      print("SUBSCRIPTION STATE CHANGED: ${changes.jsonRepresentation()}");
    });

    OneSignal.shared.setPermissionObserver((OSPermissionStateChanges changes) {
      print("PERMISSION STATE CHANGED: ${changes.jsonRepresentation()}");
    });

    OneSignal.shared.setEmailSubscriptionObserver(
        (OSEmailSubscriptionStateChanges changes) {
      print("EMAIL SUBSCRIPTION STATE CHANGED ${changes.jsonRepresentation()}");
    });

    OneSignal.shared
        .setSMSSubscriptionObserver((OSSMSSubscriptionStateChanges changes) {
      print("SMS SUBSCRIPTION STATE CHANGED ${changes.jsonRepresentation()}");
    });

    OneSignal.shared.setOnWillDisplayInAppMessageHandler((message) {
      print("ON WILL DISPLAY IN APP MESSAGE ${message.messageId}");
    });

    OneSignal.shared.setOnDidDisplayInAppMessageHandler((message) {
      print("ON DID DISPLAY IN APP MESSAGE ${message.messageId}");
    });

    OneSignal.shared.setOnWillDismissInAppMessageHandler((message) {
      print("ON WILL DISMISS IN APP MESSAGE ${message.messageId}");
    });

    OneSignal.shared.setOnDidDismissInAppMessageHandler((message) {
      print("ON DID DISMISS IN APP MESSAGE ${message.messageId}");
    });

    // NOTE: Replace with your own app ID from https://www.onesignal.com
    await OneSignal.shared.setAppId("50eefdeb-38d7-43be-a5ba-86237caeb417");

    // iOS-only method to open launch URLs in Safari when set to false
    OneSignal.shared.setLaunchURLsInApp(false);

    bool requiresConsent = await OneSignal.shared.requiresUserPrivacyConsent();

    setState(() {
      _enableConsentButton = requiresConsent;
    });

    // Some examples of how to use In App Messaging public methods with OneSignal SDK
    oneSignalInAppMessagingTriggerExamples();

    OneSignal.shared.disablePush(false);

    // Some examples of how to use Outcome Events public methods with OneSignal SDK
    oneSignalOutcomeEventsExamples();

    bool userProvidedPrivacyConsent =
        await OneSignal.shared.userProvidedPrivacyConsent();
    print("USER PROVIDED PRIVACY CONSENT: $userProvidedPrivacyConsent");
  }

  oneSignalInAppMessagingTriggerExamples() async {
    OneSignal.shared.addTrigger("trigger_1", "one");

    Map<String, Object> triggers = new Map<String, Object>();
    triggers["trigger_2"] = "two";
    triggers["trigger_3"] = "three";
    OneSignal.shared.addTriggers(triggers);

    OneSignal.shared.removeTriggerForKey("trigger_2");

    // Get the value for a trigger by its key
    Object? triggerValue =
        await OneSignal.shared.getTriggerValueForKey("trigger_3");
    print("'trigger_3' key trigger value: ${triggerValue?.toString()}");

    // Create a list and bulk remove triggers based on keys supplied
    List<String> keys = ["trigger_1", "trigger_3"];
    OneSignal.shared.removeTriggersForKeys(keys);

    // Toggle pausing (displaying or not) of IAMs
    OneSignal.shared.pauseInAppMessages(false);
  }

  oneSignalOutcomeEventsExamples() async {
    // Await example for sending outcomes
    outcomeAwaitExample();

    // Send a normal outcome and get a reply with the name of the outcome
    OneSignal.shared.sendOutcome("normal_1");
    OneSignal.shared.sendOutcome("normal_2").then((outcomeEvent) {
      print(outcomeEvent.jsonRepresentation());
    });

    // Send a unique outcome and get a reply with the name of the outcome
    OneSignal.shared.sendUniqueOutcome("unique_1");
    OneSignal.shared.sendUniqueOutcome("unique_2").then((outcomeEvent) {
      print(outcomeEvent.jsonRepresentation());
    });

    // Send an outcome with a value and get a reply with the name of the outcome
    OneSignal.shared.sendOutcomeWithValue("value_1", 3.2);
    OneSignal.shared.sendOutcomeWithValue("value_2", 3.9).then((outcomeEvent) {
      print(outcomeEvent.jsonRepresentation());
    });
  }

  Future<void> outcomeAwaitExample() async {
    var outcomeEvent = await OneSignal.shared.sendOutcome("await_normal_1");
    print(outcomeEvent.jsonRepresentation());
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: APP_NAME,
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const SplashScreen(),
    );
  }
}

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  late bool isUpdateAvailable = false;

  updateCheck() async {
    PackageInfo packageInfo = await PackageInfo.fromPlatform();

    String appName = packageInfo.appName;
    String packageName = packageInfo.packageName;
    String version = packageInfo.version;
    String buildNumber = packageInfo.buildNumber;
    http.Response v =
        await http.post(Uri.parse(APP_API), body: {"version": "true"});
    print("SERVER V " + v.body.toString());
    if (int.parse(v.body.replaceAll(".", "")) >
        int.parse(version.replaceAll(".", ""))) {
      print("Update available");
      isUpdateAvailable = true;
      setState(() {});
      return "true";
    } else {
      isUpdateAvailable = false;
      setState(() {});

      print("App Version " + version.toString().replaceAll(".", ""));
      return "false";
    }
  }

  @override
  void initState() {
    updateCheck();
    super.initState();
    Timer(Duration(seconds: 3), () {
      Navigator.pushReplacement(
          context,
          MaterialPageRoute(
              builder: (_) =>
                  isUpdateAvailable ? const UpdatePage() : WebPageScreen()));
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        width: MediaQuery.of(context).size.width,
        height: MediaQuery.of(context).size.height,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Image.asset(
              "assets/logo.png",
              height: 230,
            ),
            SizedBox(
              height: MediaQuery.of(context).size.height / 5,
            ),
            CircularProgressIndicator(),
          ],
        ),
      ),
    );
  }
}

class WebPageScreen extends StatefulWidget {
  const WebPageScreen({super.key});

  @override
  State<WebPageScreen> createState() => _WebPageScreenState();
}

class _WebPageScreenState extends State<WebPageScreen> {
  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () async {
        showDialog(
            context: context,
            builder: (_) => AlertDialog(
                  content: Text("Do you want to exit?"),
                  actions: [
                    TextButton(
                        onPressed: () {
                          Navigator.pop(context);
                          SystemNavigator.pop();
                        },
                        child: Text("Yes")),
                    TextButton(
                        onPressed: () {
                          Navigator.pop(context);
                        },
                        child: Text("No")),
                  ],
                ));
        return false;
      },
      child: const SafeArea(
        child: WebviewScaffold(
          url: "https://color.devsecit.com/",
          withJavascript: true,
          withLocalStorage: true,
          withLocalUrl: true,
          withZoom: false,
          allowFileURLs: true,
          appCacheEnabled: true,
          displayZoomControls: false,
          userAgent: "random",
          geolocationEnabled: true,
          resizeToAvoidBottomInset: true,
          scrollBar: false,
          supportMultipleWindows: true,
          ignoreSSLErrors: true,
          debuggingEnabled: false,
        ),
      ),
    );
  }
}

class UpdatePage extends StatelessWidget {
  const UpdatePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("New update available"),
        centerTitle: true,
        elevation: 1,
      ),
      body: Center(
        child: ElevatedButton(
            onPressed: () async {
              await launchUrl(Uri.parse(APP_UPDATE),
                  mode: LaunchMode.externalApplication);
            },
            child: const Text("Update Now")),
      ),
    );
  }
}
