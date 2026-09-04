import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'query_ad_revenue_config.dart';
import 'query_ad_revenue_platform_interface.dart';

/// An implementation of [QueryAdRevenuePlatform] that uses method channels.
class MethodChannelQueryAdRevenue extends QueryAdRevenuePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('query_ad_revenue');

  @override
  Future<void> initConfig(QueryAdRevenueConfig config) {
    return methodChannel.invokeMethod<void>('initConfig', config.toMap());
  }

  @override
  Future<double> getOpenAdRevenue(int adId) {
    return _getAdRevenue('getOpenAdRevenue', adId);
  }

  @override
  Future<double> getIntAdRevenue(int adId) {
    return _getAdRevenue('getIntAdRevenue', adId);
  }

  @override
  Future<double> getNativeAdRevenue(int adId) {
    return _getAdRevenue('getNativeAdRevenue', adId);
  }

  Future<double> _getAdRevenue(String method, int adId) async {
    return await methodChannel.invokeMethod<double>(method, {'adId': adId}) ??
        -1.0;
  }
}
