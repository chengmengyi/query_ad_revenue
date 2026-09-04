-keep class com.query.ad.revenue.Query {
    *;
}

-keepnames class io.flutter.plugins.googlemobileads.GoogleMobileAdsPlugin
-keepclassmembers class io.flutter.plugins.googlemobileads.GoogleMobileAdsPlugin {
    *** instanceManager;
}
-keepclassmembers class io.flutter.plugins.googlemobileads.AdInstanceManager {
    *** adForId(int);
}
-keepclassmembers class io.flutter.plugins.googlemobileads.FlutterAppOpenAd {
    *** ad;
}
-keepclassmembers class io.flutter.plugins.googlemobileads.FlutterInterstitialAd {
    *** ad;
}
-keepclassmembers class io.flutter.plugins.googlemobileads.FlutterNativeAd {
    *** templateView;
    *** nativeAdView;
}
-keepclassmembers class com.google.android.ads.nativetemplates.TemplateView {
    *** nativeAd;
}
