import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'query_ad_revenue_config.dart';
import 'query_ad_revenue_method_channel.dart';

abstract class QueryAdRevenuePlatform extends PlatformInterface {
  /// Constructs a QueryAdRevenuePlatform.
  QueryAdRevenuePlatform() : super(token: _token);

  static final Object _token = Object();

  static QueryAdRevenuePlatform _instance = MethodChannelQueryAdRevenue();

  /// The default instance of [QueryAdRevenuePlatform] to use.
  ///
  /// Defaults to [MethodChannelQueryAdRevenue].
  static QueryAdRevenuePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [QueryAdRevenuePlatform] when
  /// they register themselves.
  static set instance(QueryAdRevenuePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<void> initConfig(QueryAdRevenueConfig config) {
    throw UnimplementedError('initConfig() has not been implemented.');
  }

  Future<double> getOpenAdRevenue(String adId) {
    throw UnimplementedError('getOpenAdRevenue() has not been implemented.');
  }

  Future<double> getIntAdRevenue(String adId) {
    throw UnimplementedError('getIntAdRevenue() has not been implemented.');
  }

  Future<double> getNativeAdRevenue(String adId) {
    throw UnimplementedError('getNativeAdRevenue() has not been implemented.');
  }
}
