import 'query_ad_revenue_config.dart';
import 'query_ad_revenue_platform_interface.dart';

export 'query_ad_revenue_config.dart';

class QueryAdRevenue {
  static final QueryAdRevenue _adRevenue = QueryAdRevenue();
  static QueryAdRevenue get instance => _adRevenue;

  Future<void> initConfig(QueryAdRevenueConfig config) {
    return QueryAdRevenuePlatform.instance.initConfig(config);
  }

  Future<double> getOpenAdRevenue(String adId) {
    return QueryAdRevenuePlatform.instance.getOpenAdRevenue(adId);
  }

  Future<double> getIntAdRevenue(String adId) {
    return QueryAdRevenuePlatform.instance.getIntAdRevenue(adId);
  }

  Future<double> getNativeAdRevenue(String adId) {
    return QueryAdRevenuePlatform.instance.getNativeAdRevenue(adId);
  }
}
