# 代码的压缩级别
-optimizationpasses 5
#混淆时类名
-dontusemixedcaseclassnames
#指定不去忽略非公共的库类。
-dontskipnonpubliclibraryclasses
#指定不去忽略包可见的库类的成员
-dontskipnonpubliclibraryclassmembers
#不预校验
-dontpreverify
# 混淆时记录日志
-verbose
#不压缩输入的类文件
-dontshrink
 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes *JavascriptInterface*
#
-dontwarn com.mtime.base.imageload.**
-keep class com.mtime.base.imageload.** {*;}

 # 支付宝支付
 -dontwarn com.alipay.**
-keep class com.alipay.** { *; }
-keep class com.ta.utdid2.** { *; }
-keep class com.ut.device.** { *; }
-keep class org.json.alipay.** { *; }
#v4
-keep class android.support.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep class android.support.v7.widget** { *; }

#tencent
-keep class com.tencent.** { *;}

#mtime
-keep class com.android.volley.** { *;}
-keep class com.mtime.base.** { *;}
-keep class com.mtime.common.** { *;}
-keep class com.mtime.constant.** { *;}
-keep class com.mtime.listener.** { *;}

#uppay
-keep class com.unionpay.** { *;}
-keep class cn.gov.pbc.tsm.client.mobile.android.bank.service.** { *;}
-keep class com.UCMobile.PayPlugin.** { *;}

#sina
-keep class com.sina.** { *;}

#other
-keep class com.nineoldandroids.** { *;}
-keep class com.mobclick.android.** {*;}
-keep class com.mapbar.** {*;}
-keep class com.ut.*
#zxing
-keep class com.google.zxing.** { *; }

#jpush
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-keepclassmembers class com.mtime.bussiness.mall.widget.MallWebView.AndroidMallJSInterface {
   public *;
}


-keep public class * extends android.view.View{
    public *;
}

-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.mtime.mtmovie.widgets.** { *;}
-keep class com.mtime.widgets.** { *;}
 #gson相关不混淆
 -dontwarn com.google.**
 -keep class com.google.gson.** {*;}
 -keep class org.json.** {*;}
 # 用于解析json的bean不混淆
-keep class com.mtime.beans.** {*;}
-keep class com.mtime.beansv2.** {*;}
-keep class * extends com.mtime.base.bean.MBaseBean {*;}

-keep class com.mtime.b2clocaoplayer.bean.** { *; }

#七牛sdk
-keep class com.pili.pldroid.player.** { *; }
-keep class com.qiniu.qplayer.mediaEngine.** {*;}
-keep class com.qiniu.qplayer.mediaEngine.MediaPlayer{*;}
 #基本组建不能混淆
 -keep public class * extends android.support.v4.app.FragmentActivity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.** {*;}
-keep class * extends java.lang.annotation.Annotation { *; }

-keepclasseswithmembernames class * {
public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembernames class * {
public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable{*;}
-keep class * implements java.io.Serializable{*;}
#忽略警告
#-ignorewarning
#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
        native <methods>;
    }

     #不混淆资源类
-keepclassmembers class **.R$* {
            public static <fields>;
        }
-dontwarn com.networkbench.**
-dontwarn com.unionpay.**
-dontwarn com.baidu.frontia.**
-dontwarn com.baidu.android.pushservice.apiproxy.**
-keepattributes Signature

#保持崩溃日志中显示行号
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-dontoptimize

#==================protobuf======================
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}

# 直播
-keep class com.pili.** {*;}
-keep class tv.danmaku.** {*;}
-keep class io.rong.** {*;}
-keep class com.mtime.mlive.** {*;}
-keep class com.common.widgets.** {*;}
-keep class com.mtime.player.** {*;}

#-keep class com.common.** {*;}
-keep class master.** {*;}
-keep class tv.** {*;}
-keep class io.socket.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.bumptech.** {*;}
-keep class jp.wasabeef.** {*;}
-keep class jp.co.** {*;}
-keep class com.qiniu.** {*;}
-keep class com.oqaclejapan.** {*;}
-keep class rx.** {*;}
-keep class com.qithub.** {*;}
-keep class com.bjhl.** {*;}
-keep class net.chilicat.** {*;}

-keep class okio.** {*;}
-keep class okhttp3.** {*;}
-keep class com.squareup.** {*;}

-keep class vic.common.** {*;}

# fastjson
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** {*;}
-keepattributes Signature
-keepattributes *Annotation*

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

#融云
-dontwarn io.rong.**
-keep class io.rong.** {*;}
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class *.R$ { *;}

#RxJava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#网络框架retrofit和glide
-keep class com.mtime.mtmovie.network.** { *; }
-keep class com.bumptech.glide.** { *; }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
      **[] $VALUES;
      public *;
}

#加入直播fragment keep代码
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

#jssdk
-keepclassmembers class com.jssdk.JSInterfaceNative {
   public *;
   private *;
}
-keepclassmembers class com.jssdk.JSCenter {
   public *;
   private *;
}
-keep class com.mtime.bussiness.information.adapter.render.ArticleDetailWebViewRender$CallBackBean { *; }
-keepclassmembers class com.mtime.bussiness.information.adapter.render.ArticleDetailWebViewRender$CallBackBean {
   public *;
   private *;
}
-keep class com.mtime.bussiness.information.adapter.render.ArticleDetailWebViewRender$CallBackBean$Data { *; }
-keepclassmembers class com.mtime.bussiness.information.adapter.render.ArticleDetailWebViewRender$CallBackBean$Data {
   public *;
   private *;
}
-keep class com.jssdk.** { *; }
-keep class com.jssdk.beans.** { *; }
-keep class com.jssdk.listener.** { *; }
-keepclassmembers class com.mtime.bussiness.information.adapter.render.ArticleDetailWebViewRender {
   public *;
   private *;
}
-keepclassmembers class com.mtime.util.HtmlUtil {
   public *;
   private *;
}

#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#litepal数据库
-keep class org.litepal.** {
    *;
}
-keep class * extends org.litepal.crud.DataSupport {
    *;
}

# PlayerBase library
-keep public class * extends android.view.View{*;}
-keep public class * implements com.kk.taurus.playerbase.player.IPlayer{*;}

# draglistview
-keep class com.woxthebox.draglistview.** { *; }

#baidu location2库中已将混淆配置打入ARR包
#-keep class com.baidu.** { *;}
#-keep class vi.com.gdi.bgl.android.**{*;}
#-keep class com.networkbench.** { *;}
#-keep class mapsdkvi.com.** {*;}
#-dontwarn com.baidu.*

#友盟
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#百度统计
-keep class com.baidu.bottom.** { *; }
-keep class com.baidu.mobstat.** { *; }

#银联
-dontwarn org.simalliance.openmobileapi.**

-dontwarn com.bun.supplier.IIdentifierListener
-dontwarn com.bun.miitmdid.core.IIdentifierListener

# Allow R8 to optimize away the FastServiceLoader.
# Together with ServiceLoader optimization in R8
# this results in direct instantiation when loading Dispatchers.Main
-assumenosideeffects class kotlinx.coroutines.internal.MainDispatcherLoader {
    boolean FAST_SERVICE_LOADER_ENABLED return false;
}

-assumenosideeffects class kotlinx.coroutines.internal.FastServiceLoaderKt {
    boolean ANDROID_DETECTED return true;
}

-keep class kotlinx.coroutines.android.AndroidDispatcherFactory {*;}

# Disable support for "Missing Main Dispatcher", since we always have Android main dispatcher
-assumenosideeffects class kotlinx.coroutines.internal.MainDispatchersKt {
    boolean SUPPORT_MISSING return false;
}

# Statically turn off all debugging facilities and assertions
-assumenosideeffects class kotlinx.coroutines.DebugKt {
    boolean getASSERTIONS_ENABLED() return false;
    boolean getDEBUG() return false;
    boolean getRECOVER_STACK_TRACES() return false;
}

#极光推送华为厂商通道
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}