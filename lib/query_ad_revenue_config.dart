class QueryAdRevenueConfig {
  const QueryAdRevenueConfig({
    required this.openKeyList,
    required this.intKeyList,
    required this.nativeKeyList,
    required this.libName,
  });

  final List<String> openKeyList;
  final List<String> intKeyList;
  final List<String> nativeKeyList;
  final String libName;

  Map<String, Object> toMap() => <String, Object>{
    'openKeyList': openKeyList,
    'intKeyList': intKeyList,
    'nativeKeyList': nativeKeyList,
    'libName': libName,
  };
}
