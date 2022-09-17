import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

/**
 * 作者: zl
 * 创建时间: 2020/9/9 3:49 PM
 * 描述: 新框架依赖项，统一管理，同时也方便其他组件引入
 */

/**
 * App
 */
object App {
    const val applicationId = "com.mtime"
}

/**
 * Android
 */
object Android {
    const val kotlin = "1.6.10"
    const val gradle = "7.0.3"
    const val gradle_versions_plugin = "0.38.0"
    const val compileSdkVersion = 31
    const val buildToolsVersion = "31.0.0"
    const val minSdkVersion = 26
    const val targetSdkVersion = 30
    const val versionCode = 1000
    const val versionName = "10.0.0"
    const val coil = "1.4.0"
    const val room = "2.2.5"
//    const val glide = "4.12.0"
    const val coroutines = "1.4.3"
    const val jpush_vip = "4.0.8"
    const val nav_version = "2.3.1"
}

/**
 * 系统库依赖
 * */
object Support {
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.4"
    const val anko_commons = "org.jetbrains.anko:anko-commons:0.10.8"
    const val appcompat = "androidx.appcompat:appcompat:1.2.0"
    const val core_ktx = "androidx.core:core-ktx:1.3.2"
    const val lifecycle_extensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
    const val lifecycle_viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0"
    const val legacy_support_v4 = "androidx.legacy:legacy-support-v4:1.0.0"
    const val kotlinx_coroutines_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Android.coroutines}"
    const val kotlinx_coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Android.coroutines}"
    const val databinding_runtime = "androidx.databinding:databinding-runtime:${Android.gradle}"
    const val databinding_compiler = "androidx.databinding:databinding-compiler:${Android.gradle}"
    const val fragment_ktx = "androidx.fragment:fragment-ktx:1.3.1"
    const val activity_ktx = "androidx.activity:activity-ktx:1.2.0"
    const val recyclerview = "androidx.recyclerview:recyclerview:1.2.0"
    const val cardview = "androidx.cardview:cardview:1.0.0"
    const val paging_runtime_ktx = "androidx.paging:paging-runtime-ktx:2.1.2"
    const val material = "com.google.android.material:material:1.3.0"
    const val kotlin_stdlib_jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Android.kotlin}"
    const val gridlayout = "androidx.gridlayout:gridlayout:1.0.0"
    const val room_runtime = "androidx.room:room-runtime:${Android.room}"
    const val room_compiler = "androidx.room:room-compiler:${Android.room}"
    const val room_ktx = "androidx.room:room-ktx:${Android.room}"
    const val room_guava = "androidx.room:room-guava:${Android.room}"
    const val room_testing = "androidx.room:room-testing:${Android.room}"
    const val build_gradle = "com.android.tools.build:gradle:${Android.gradle}"
    const val kotlin_gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Android.kotlin}"
    const val kotlin_android_extensions = "org.jetbrains.kotlin:kotlin-android-extensions:${Android.kotlin}"
    const val junit = "junit:junit:4.13.2"
    const val junit_ext = "androidx.test.ext:junit:1.1.2"
    const val espresso_core = "androidx.test.espresso:espresso-core:3.3.0"
    const val gradle_versions_plugin = "com.github.ben-manes:gradle-versions-plugin:${Android.gradle_versions_plugin}"
    const val datastore_preferences = "androidx.datastore:datastore-preferences:1.0.0-alpha01"
    const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
}

/**
 * 第三方库依赖
 * */
object Dependencies {
    const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
    const val converter_gson = "com.squareup.retrofit2:converter-gson:2.9.0"
    const val okhttp3 = "com.squareup.okhttp3:okhttp:4.9.1"
    const val logging_interceptor = "com.squareup.okhttp3:logging-interceptor:4.9.1"
    const val arouter_api = "com.alibaba:arouter-api:1.4.0"
    const val arouter_compiler = "com.alibaba:arouter-compiler:1.2.1"
    const val liveeventbus = "com.jeremyliao:live-event-bus-x:1.7.3"
//    const val glide = "com.github.bumptech.glide:glide:${Android.glide}"
//    const val glide_compiler = "com.github.bumptech.glide:compiler:${Android.glide}"
//    const val glide_transformations = "jp.wasabeef:glide-transformations:4.3.0"
    const val multidex = "com.android.support:multidex:1.0.3"
    const val leakcanary_android = "com.squareup.leakcanary:leakcanary-android:2.5"
    const val refresh_layout = "com.scwang.smart:refresh-layout-kernel:2.0.3"
    const val refresh_header_classics = "com.scwang.smart:refresh-header-classics:2.0.3"
    const val smarttablayout = "com.ogaclejapan.smarttablayout:library:2.0.0"
    const val smarttablayout_utils = "com.ogaclejapan.smarttablayout:utils-v4:2.0.0"
    const val gson = "com.google.code.gson:gson:2.8.6"
    const val startup_runtime = "androidx.startup:startup-runtime:1.0.0"
    const val flexbox = "com.google.android:flexbox:2.0.1"
    const val coil = "io.coil-kt:coil:${Android.coil}"
    const val coil_gif = "io.coil-kt:coil-gif:${Android.coil}"
    const val coil_svg = "io.coil-kt:coil-svg:${Android.coil}"
    const val coil_video = "io.coil-kt:coil-video:${Android.coil}"
    const val gif_drawable = "pl.droidsonroids.gif:android-gif-drawable:1.2.23"
//    com.kk.taurus.playerbase:playerbase:3.4.0 此PlayerBase库在Android11上访问网络状态有bug,未加权限判断   https://github.com/jiajunhui/PlayerBase 待修复后再替换回来，
    const val playerbase = "com.github.lirenxinshangqiu.PlayerBase:playerbase:3.4.0.1"//

    const val butterknife = "com.jakewharton:butterknife-gradle-plugin:10.2.3"

    //    图片文件压缩
    const val compress = "com.github.nanchen2251:CompressHelper:1.0.6"

    //新一代Json解析库Moshi
    const val moshi = "com.squareup.moshi:moshi:1.11.0"

    //    二维码 https://github.com/journeyapps/zxing-android-embedded
    const val qr_code = "com.journeyapps:zxing-android-embedded:4.1.0"
    //zxing核心库
    const val zxing = "com.google.zxing:core:3.4.1"
    const val x5_webview = "com.tencent.tbs.tbssdk:sdk:43986"
// okdownload核心库
    const val okDownload = "com.liulishuo.okdownload:okdownload:1.0.7"
    const val okDownload_okhttp = "com.liulishuo.okdownload:okhttp:1.0.7"
    // sockit.io
    const val socket_io_client = "io.socket:socket.io-client:1.0.0"
//    compile 'com.github.ctiao:DanmakuFlameMaster:0.9.25'
//    compile 'com.github.ctiao:ndkbitmap-armv7a:0.9.21'
//    弹幕库
    const val danmaku = "com.github.ctiao:DanmakuFlameMaster:0.9.25"
    const val danmaku_armv7a = "com.github.ctiao:ndkbitmap-armv7a:0.9.21"

    const val sensors_plugin = "com.sensorsdata.analytics.android:android-gradle-plugin2:3.3.4"
    const val sensors = "com.sensorsdata.analytics.android:SensorsAnalyticsSDK:5.2.1"
    const val lottie = "com.airbnb.android:lottie:4.0.0"
    const val youzanyun = "com.youzanyun.open.mobile:x5sdk:7.1.15"
    const val glide = "com.github.bumptech.glide:glide:4.11.0"
    //腾讯云点播
    const val qcloud_cos_android = "com.qcloud.cos:cos-android-nobeacon:5.8.3"
    const val qcloud_quic = "com.qcloud.cos:quic:1.5.37"

    val addRepos: (handler: RepositoryHandler) -> Unit = {
        it.google()
        it.jcenter()
        it.mavenCentral()
        it.maven { url = URI("https://maven.aliyun.com/repository/gradle-plugin") }
        it.maven { url = URI("https://maven.aliyun.com/repository/public") }
        it.maven { url = URI("https://maven.aliyun.com/repository/central") }
        it.maven { url = URI("https://maven.aliyun.com/repository/google") }
        it.maven { url = URI("https://maven.aliyun.com/repository/jcenter") }
        it.maven { url = URI("https://jitpack.io") }
        it.maven { url = URI("https://developer.huawei.com/repo/") }
        it.maven { url = URI("https://dl.google.com/dl/android/maven2/") }
        it.maven { url = URI("https://maven.youzanyun.com/repository/maven-releases") }
        it.maven { url = URI("https://maven.google.com") }
        it.maven { url = URI("https://dl.bintray.com/thelasterstar/maven/") }
        it.maven { url = URI("https://dl.bintray.com/kotlin/kotlin-eap") }
        it.maven { url = URI("https://dl.bintray.com/umsdk/release") }
    }
}

/**
 * 分享依赖库
 */
object Share {
    const val wechat_sdk = "com.tencent.mm.opensdk:wechat-sdk-android-without-mta:6.6.21"

    /**
     * 微博sdk在线依赖已关闭，暂时改成aar依赖
     */
    const val weibo_sdk = "com.sina.weibo.sdk:core:4.4.3:openDefaultRelease@aar"
}

/**
 * 极光推送
 */
object Push {
    const val jiguang_appkey = "67dc84632360039a2d944bfa"
    const val jiguang_jpush = "cn.jiguang.sdk:jpush:${Android.jpush_vip}"
    const val jiguang_jcore = "cn.jiguang.sdk:jcore:2.8.0"
    const val jiguang_jverification = "cn.jiguang.sdk:jverification:2.7.2"
    const val huawei_agcp = "com.huawei.agconnect:agcp:1.6.0.300"
    const val huawei_hms = "com.huawei.hms:push:6.1.0.300" //5.3.0.304 -> 6.1.0.300
    const val jiguang_xiaomi = "cn.jiguang.sdk.plugin:xiaomi:${Android.jpush_vip}"
    const val jiguang_huawei = "cn.jiguang.sdk.plugin:huawei:${Android.jpush_vip}"
    const val jiguang_meizu = "cn.jiguang.sdk.plugin:meizu:${Android.jpush_vip}"
    const val jiguang_oppo = "cn.jiguang.sdk.plugin:oppo:${Android.jpush_vip}"
    const val jiguang_vivo = "cn.jiguang.sdk.plugin:vivo:${Android.jpush_vip}"
    const val xiaomi_appkey = "MI-5621715865997"
    const val xiaomi_appid = "MI-2882303761517158997"
    const val huawei_appid = "1000160"
    const val meizu_appkey = "MZ-83e7cefe65f3403d96fdc2ccdaaa3f81"
    const val meizu_appid = "MZ-149006"
    const val oppo_appkey = "OP-195WScglWbB4W0owsOo4owccc"
    const val oppo_appid = "OP-5862"
    const val oppo_appsecret = "OP-E583da83170Fd136974ACe6eC4902171"
    const val vivo_appkey = "3f1a6283c828145fc117d62577c9d565"
    const val vivo_appid = "100077972"
}


/**
 * 定位相关
 */
object Location {
    const val baidu_lbs_key = "qXX48DH0ZBkvIx4onBBP8ypV"
}

/**
 * bugly相关
 */
object Bugly {
    const val bugly = "com.tencent.bugly:crashreport:3.3.7"
}
