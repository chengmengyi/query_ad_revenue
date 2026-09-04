# query_ad_revenue

通过广告在 `google_mobile_ads` 中的 `adId`，查询 Android 原生广告对象的收益值。插件本身不依赖 `google_mobile_ads` 或 Google Mobile Ads Android SDK，相关运行时依赖由宿主 App 提供。

## 1. 申请 SO

申请 SO 时需要提供测试包名、正式包名和 JNI 函数：

```text
[
  'com.xxx.xxxx',                           # 测试包名
  'com.xxxx.xxxxx',                         # 正式包名
  'Java_com_query_ad_revenue_Query_query',  # JNI 函数
]
```

JNI 函数固定为 `Java_com_query_ad_revenue_Query_query`，AdMob 版本固定为 `24.9.0`。

## 2. 放置 SO

把包含 `Query.query` JNI 实现的 SO 放到宿主 App 的 `android/app/src/main/jniLibs/`：

```text
android/app/src/main/jniLibs/
├── arm64-v8a/xxx.so
├── armeabi-v7a/xxx.so
├── x86/xxx.so
└── x86_64/xxx.so
```

目录按实际支持的 ABI 保留即可。`libName` 只填写中间名称，例如文件是 `libb03ad.so` 时填写 `b03ad`，不要包含 `lib` 前缀或 `.so` 后缀。

## 3. 引入插件

```yaml
dependencies:
  query_ad_revenue:
    path: plugins/query_ad_revenue
```

宿主 App 需要自行引入并初始化广告 SDK。插件运行时会根据 `adId` 查找 `google_mobile_ads` 创建的 Android 广告实例。

## 4. 初始化配置

查询收益前必须调用一次 `initConfig`。`enableRevenue`、三个 key 列表和 `libName` 都是必传参数：

```dart
final queryAdRevenue = QueryAdRevenue();

await queryAdRevenue.initConfig(
  const QueryAdRevenueConfig(
    enableRevenue: true,
    openKeyList: <String>[
      // AppOpenAd 对应的 key。
    ],
    intKeyList: <String>[
      // InterstitialAd 对应的 key。
    ],
    nativeKeyList: <String>[
      // NativeAd 对应的 key。
    ],
    libName: 'b03ad',
  ),
);
```

`enableRevenue` 为 `true` 时，`initConfig` 会在 Android 调用 `System.loadLibrary(libName)`，加载失败时会抛出 `PlatformException`。设置为 `false` 时不会初始化 JNI，三个收益查询方法都会直接返回 `0.0`。

## 5. 查询收益

把 `google_mobile_ads` 对应广告对象的 `adId` 转为字符串后，传给相应方法：

```dart
final openRevenue = await queryAdRevenue.getOpenAdRevenue(openAdId.toString());
final intRevenue = await queryAdRevenue.getIntAdRevenue(interstitialAdId.toString());
final nativeRevenue = await queryAdRevenue.getNativeAdRevenue(nativeAdId.toString());
```

Android 会按配置列表顺序调用 `Query.getRevenueInfo(context, ad, key)`，遇到第一个大于等于 `0` 的值就返回。广告不存在、无法取得原生对象或遍历完仍无有效值时返回 `-1.0`。

插件只接收 `adId`，不负责从 Flutter 广告对象生成 `adId`。调用方需要从自己的广告管理层取得该 ID。
