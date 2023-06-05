// ignore_for_file: constant_identifier_names, prefer_interpolation_to_compose_strings, avoid_print, prefer_const_constructors, unused_local_variable

import 'dart:async';
import 'package:flutter/services.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:flutter/material.dart';
import 'package:flutter_webview_plugin/flutter_webview_plugin.dart';
import 'package:http/http.dart' as http;
import 'package:url_launcher/url_launcher.dart';

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
      // setState(() {});

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
