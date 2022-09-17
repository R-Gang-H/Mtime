# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Youzan SDK
-dontwarn com.youzan.**
-keep class com.youzan.jsbridge.** { *; }
-keep class com.youzan.spiderman.** { *; }
-keep class com.youzan.androidsdk.** { *; }
-keep class com.youzan.x5web.** { *; }
-keep class com.youzan.androidsdkx5.** { *; }
-keep class dalvik.system.VMStack { *; }
-keep class com.tencent.smtt.** { *; }
-dontwarn  com.tencent.smtt.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-keep class okio.**{*;}
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

# IM
-keep class com.youzan.mobile.zanim.model.** { *; }

-dontwarn java.nio.file.*
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement