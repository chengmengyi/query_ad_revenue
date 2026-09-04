import 'query_ad_revenue_config.dart';
import 'query_ad_revenue_platform_interface.dart';

export 'query_ad_revenue_config.dart';

class QueryAdRevenue {
  static final QueryAdRevenue _adRevenue = QueryAdRevenue();
  static QueryAdRevenue get instance => _adRevenue;

  bool? _enableRevenue;

  Future<void> initConfig({required QueryAdRevenueConfig config}) {
    _enableRevenue = config.enableRevenue;
    if (!config.enableRevenue) {
      return Future<void>.value();
    }
    return QueryAdRevenuePlatform.instance.initConfig(config);
  }

  Future<double> getOpenAdRevenue(String adId) {
    if (_enableRevenue == false) {
      return Future<double>.value(0.0);
    }
    return QueryAdRevenuePlatform.instance.getOpenAdRevenue(adId);
  }

  Future<double> getIntAdRevenue(String adId) {
    if (_enableRevenue == false) {
      return Future<double>.value(0.0);
    }
    return QueryAdRevenuePlatform.instance.getIntAdRevenue(adId);
  }

  Future<double> getNativeAdRevenue(String adId) {
    if (_enableRevenue == false) {
      return Future<double>.value(0.0);
    }
    return QueryAdRevenuePlatform.instance.getNativeAdRevenue(adId);
  }
}
