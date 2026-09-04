import 'query_ad_revenue_config.dart';
import 'query_ad_revenue_platform_interface.dart';

export 'query_ad_revenue_config.dart';

class QueryAdRevenue {
  Future<void> initConfig(QueryAdRevenueConfig config) {
    return QueryAdRevenuePlatform.instance.initConfig(config);
  }

  Future<double> getOpenAdRevenue(int adId) {
    return QueryAdRevenuePlatform.instance.getOpenAdRevenue(adId);
  }

  Future<double> getIntAdRevenue(int adId) {
    return QueryAdRevenuePlatform.instance.getIntAdRevenue(adId);
  }

  Future<double> getNativeAdRevenue(int adId) {
    return QueryAdRevenuePlatform.instance.getNativeAdRevenue(adId);
  }
}
