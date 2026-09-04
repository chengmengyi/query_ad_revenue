package com.query.ad.revenue.query_ad_revenue

import android.content.Context
import com.query.ad.revenue.Query
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** QueryAdRevenuePlugin */
class QueryAdRevenuePlugin :
    FlutterPlugin,
    MethodCallHandler {
    // The MethodChannel that will the communication between Flutter and native Android
    //
    // This local reference serves to register the plugin with the Flutter Engine and unregister it
    // when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var applicationContext: Context
    private var flutterPluginBinding: FlutterPlugin.FlutterPluginBinding? = null

    private var openKeyList: List<String>? = null
    private var intKeyList: List<String>? = null
    private var nativeKeyList: List<String>? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        this.flutterPluginBinding = flutterPluginBinding
        applicationContext = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "query_ad_revenue")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(
        call: MethodCall,
        result: Result
    ) {
        when (call.method) {
            "initConfig" -> initConfig(call, result)
            "getOpenAdRevenue" -> queryRevenue(call, openKeyList, result)
            "getIntAdRevenue" -> queryRevenue(call, intKeyList, result)
            "getNativeAdRevenue" -> queryRevenue(call, nativeKeyList, result)
            else -> result.notImplemented()
        }
    }

    private fun initConfig(call: MethodCall, result: Result) {
        val openKeys = call.stringListArgument("openKeyList")
        val intKeys = call.stringListArgument("intKeyList")
        val nativeKeys = call.stringListArgument("nativeKeyList")
        val libName = call.argument<String>("libName")?.trim().orEmpty()
        if (openKeys == null || intKeys == null || nativeKeys == null || libName.isEmpty()) {
            result.error(
                "invalid_config",
                "openKeyList, intKeyList, nativeKeyList and libName are required",
                null,
            )
            return
        }
        runCatching {
            Query.loadLibrary(libName)
        }.onSuccess {
            openKeyList = openKeys
            intKeyList = intKeys
            nativeKeyList = nativeKeys
            result.success(null)
        }.onFailure {
            result.error("load_library_failed", it.message, null)
        }
    }

    private fun MethodCall.stringListArgument(name: String): List<String>? {
        val value = argument<List<*>>(name) ?: return null
        if (value.any { it !is String }) {
            return null
        }
        return value.filterIsInstance<String>()
    }

    private fun queryRevenue(
        call: MethodCall,
        keyList: List<String>?,
        result: Result,
    ) {
        if (keyList == null) {
            result.error("not_initialized", "Call initConfig before querying revenue", null)
            return
        }
        val adId = call.argument<String>("adId")?.toIntOrNull()
        val flutterAd = adId?.let(::findGoogleMobileAdsAd)
        val ad = flutterAd?.let { unwrapGoogleMobileAdsAd(call.method, it) }
        if (ad == null) {
            result.success(-1.0)
            return
        }
        for (key in keyList) {
            val revenue = Query.getRevenueInfo(applicationContext, ad, key)
            if (revenue >= 0) {
                result.success(revenue.toDouble())
                return
            }
        }
        result.success(-1.0)
    }

    private fun unwrapGoogleMobileAdsAd(method: String, flutterAd: Any): Any? {
        return when (method) {
            "getOpenAdRevenue" -> readField(flutterAd, "ad")
            "getIntAdRevenue" -> readField(flutterAd, "ad")
            "getNativeAdRevenue" -> unwrapNativeAd(flutterAd)
            else -> null
        }
    }

    private fun unwrapNativeAd(flutterAd: Any): Any? {
        val nativeAdClass = runCatching {
            Class.forName("com.google.android.gms.ads.nativead.NativeAd")
        }.getOrNull() ?: return null
        val templateView = readField(flutterAd, "templateView")
        val templateNativeAd = templateView?.let { readField(it, "nativeAd") }
        if (templateNativeAd != null && nativeAdClass.isInstance(templateNativeAd)) {
            return templateNativeAd
        }
        val nativeAdView = readField(flutterAd, "nativeAdView") ?: return null
        return findNativeAdField(nativeAdView, nativeAdClass)
    }

    private fun findNativeAdField(instance: Any, nativeAdClass: Class<*>): Any? {
        var currentClass: Class<*>? = instance.javaClass
        while (currentClass != null && currentClass != Any::class.java) {
            val clazz = currentClass
            for (field in clazz.declaredFields) {
                if (!nativeAdClass.isAssignableFrom(field.type)) {
                    continue
                }
                val value = runCatching {
                    field.isAccessible = true
                    field.get(instance)
                }.getOrNull()
                if (value != null && nativeAdClass.isInstance(value)) {
                    return value
                }
            }
            currentClass = clazz.superclass
        }
        return null
    }

    private fun readField(instance: Any, fieldName: String): Any? {
        var currentClass: Class<*>? = instance.javaClass
        while (currentClass != null && currentClass != Any::class.java) {
            val clazz = currentClass
            val value = runCatching {
                clazz.getDeclaredField(fieldName).apply { isAccessible = true }.get(instance)
            }.getOrNull()
            if (value != null) {
                return value
            }
            currentClass = clazz.superclass
        }
        return null
    }

    private fun findGoogleMobileAdsAd(adId: Int): Any? = runCatching {
        val binding = flutterPluginBinding ?: return null
        @Suppress("UNCHECKED_CAST")
        val pluginClass = Class.forName(
            "io.flutter.plugins.googlemobileads.GoogleMobileAdsPlugin",
        ) as Class<out FlutterPlugin>
        val googleMobileAdsPlugin = binding.flutterEngine.plugins
            .get(pluginClass)
            ?: return null
        val instanceManagerField = pluginClass
            .getDeclaredField("instanceManager")
            .apply { isAccessible = true }
        val instanceManager = instanceManagerField.get(googleMobileAdsPlugin) ?: return null
        val adForIdMethod = instanceManager.javaClass
            .getDeclaredMethod("adForId", Int::class.javaPrimitiveType)
            .apply { isAccessible = true }
        adForIdMethod.invoke(instanceManager, adId)
    }.getOrNull()

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        flutterPluginBinding = null
    }
}
