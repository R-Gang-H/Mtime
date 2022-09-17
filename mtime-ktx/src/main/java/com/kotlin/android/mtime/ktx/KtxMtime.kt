package com.kotlin.android.mtime.ktx

import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.core.CoreApp
import com.kotlin.android.core.ext.getSpValue
import com.kotlin.android.core.ext.putSpValue
import com.kotlin.android.ktx.ext.core.getCompatDrawable
import com.kotlin.android.ktx.ext.core.getCompoundDrawable
import com.kotlin.android.ktx.ext.time.TimeExt
import java.text.DecimalFormat

/**
 * 业务层Module
 * MTime项目向的扩展，
 *
 * Created on 2020/6/9.
 *
 * @author o.s
 */

private val sCountFormat: DecimalFormat = DecimalFormat(",###.#")
private val TIME_FORMAT_01 = "%02d:%02d"
private val TIME_FORMAT_02 = "%02d:%02d:%02d"


fun formatTime(time: Long?): String {
    time?.apply {
        var totalSeconds = if (time > 1000000000000L) time / 1000L else time
        if (totalSeconds <= 0) totalSeconds = 0
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600

        return if (totalSeconds >= 60*60) {
            String.format(TIME_FORMAT_02, hours, minutes, seconds)
        } else {
            String.format(TIME_FORMAT_01, minutes, seconds)
        }
    }
    return ""
}

/**
 * 消息数统一格式化
 */
fun formatMsgCount(count: Long): String {
    return if (count > 99) {
        "99+"
    } else {
        count.toString()
    }
}

/**
 * 通用的 格式化count值
 *
 * @param cnt
 * @param zero
 * @return
 */
fun formatCount(cnt: Long, zero: Boolean = true): String {
    if (cnt >= 100000000) {
        val f = cnt / 100000000f
        return sCountFormat.format(f.toDouble()) + "亿"
    }
    if (cnt >= 10000) {
        val f = cnt / 10000f
        return sCountFormat.format(f.toDouble()) + "万"
    }
    return if (cnt <= 0) {
        if (zero) "0" else ""
    } else cnt.toString()
}

/**
 * 通用的 格式化发布时间
 *
 * [0秒~60秒)，显示：刚刚
 * [1分钟~60分钟)，显示：n分钟前
 * [1小时~24小时)，显示：n小时前
 * [1天~7天)，显示：n天前
 * [1周~4周)，显示：n周前
 * [1月~∞)，显示：yyyy-MM-dd
 *
 * @param second 时间/单位秒
 * @return 刚刚/分钟前/小时前/天前/周前/yyyy-MM-dd
 */
fun formatPublishTime(time: Long?): String {
    if (null == time || time <= 0) {
        return ""
    }
    val currentTime = getServerTime() / 1000L //MTimeUtils.getLastDiffServerTime() / 1000
    val publishTime = if (time > 1000000000000L) time / 1000L else time

    // 时间差
    val seconds: Long = currentTime - publishTime
    if (seconds < 0L) {
        return ""
    }
    if (seconds < 60L) {  // 60second
        return "刚刚"
    }
    if (seconds < 3600L) { // *60min
        return "${seconds / 60L}分钟前"
    }
    if (seconds < 86400L) { // *24h
        return "${seconds / 3600L}小时前"
    }
    if (seconds < 604800L) { // *7d
        return "${seconds / 86400L}天前"
    }
    return if (seconds < 2419200L) { // *4w
        "${seconds / 604800L}周前"
    } else TimeExt.millis2String(publishTime * 1000, "yyyy-MM-dd")
}


fun formatFileSize(size: Long): String? {
    val kb = size / 1024f
    val mb = kb / 1024f
    if (mb <= 1) {
        return "1M"
    }
    if (mb < 1024) {
        return String.format("%.1fM", mb)
    }
    val gb = mb / 1024f
    return String.format("%.1fG", gb)
}

fun getColor(@ColorRes resId: Int): Int = CoreApp.instance.getColor(resId)
fun getString(@StringRes resId: Int): String = CoreApp.instance.getString(resId)
fun getString(@StringRes resId: Int, vararg params: Any): String = CoreApp.instance.getString(resId, *params)
fun getDrawable(@DrawableRes resId: Int): Drawable? = ContextCompat.getDrawable(CoreApp.instance, resId)
fun getDimension(@DimenRes resId: Int):Float = CoreApp.instance.resources.getDimension(resId)
fun getDimensionPixelOffset(@DimenRes resId: Int):Int = CoreApp.instance.resources.getDimensionPixelOffset(resId)

fun getDrawableOrNull(@DrawableRes resId: Int?): Drawable? {
    return when (resId) {
        null -> {
            null
        }
        else -> {
            CoreApp.instance.getCompatDrawable(resId)
        }
    }
}
fun getCompoundDrawableOrNull(@DrawableRes resId: Int?): Drawable? {
    return when (resId) {
        null -> {
            null
        }
        else -> {
            CoreApp.instance.getCompoundDrawable(resId)
        }
    }
}

fun getStringOrNull(@StringRes resId: Int?, vararg params: Any): CharSequence? {
    return when {
        resId == null -> {
            null
        }
        params.isNotEmpty() -> {
            CoreApp.instance.getString(resId, *params)
        }
        else -> {
            CoreApp.instance.getString(resId)
        }
    }
}

fun getStringOrDefault(@StringRes resId: Int?, def: CharSequence, vararg params: Any): CharSequence {
    return when {
        resId == null -> {
            def
        }
        params.isNotEmpty() -> {
            CoreApp.instance.getString(resId, *params)
        }
        else -> {
            CoreApp.instance.getString(resId)
        }
    }
}

/**
 * 根据指定的颜色、描边、圆角及方位生成一个Drawable
 */
//fun getShapeDrawable(
//        @ColorRes colorRes: Int = android.R.color.transparent,
//        @ColorRes strokeColorRes: Int = android.R.color.transparent,
//        strokeWidth: Int = 0,
//        cornerRadius: Int = 0,
//        direction: Int = Direction.ALL
//): GradientDrawable = CoreApp.instance.getShapeDrawable(
//        colorRes,
//        strokeColorRes,
//        strokeWidth,
//        cornerRadius,
//        direction)

/**
 * 获取本地缓存的家族帖子数
 */
fun getCacheFamilyPostCount(id: Long): Long {
    return getSpValue("CacheFamilyPostCount$id", 0L)
}

/**
 * 保存家族帖子数到本地缓存
 */
fun saveCacheFamilyPostCount(id: Long, count: Long) {
    putSpValue("CacheFamilyPostCount$id", count)
}


/**
 * 获取服务器当前时间：毫秒（ms）
 */
fun getServerTime(): Long {
    return getSpValue("correct_time", 0L) + System.currentTimeMillis()
}

/**
 * 获取服务器当前时间：秒(s)
 */
fun getServerTimeWithSecond(): Long {
    return getServerTime() / 1000L
}

fun getDeviceToken():String{
    var  androidId= Settings.System.getString(CoreApp.instance.contentResolver, Settings.Secure.ANDROID_ID)
    if (TextUtils.isEmpty(androidId)) {
        androidId = Settings.Secure.getString(CoreApp.instance.contentResolver, Settings.Secure.ANDROID_ID)
    }
    return if (Build.VERSION.SDK_INT >= 29) androidId else androidId + Build.SERIAL

}

fun getRatingLevelHintText(level: Int): String {
    return when (level) {
        Mtime.PUBLISH_SCORE_LEVEL_1,
        Mtime.PUBLISH_SCORE_LEVEL_2 -> getString(R.string.publish_score_level_0_2)
        Mtime.PUBLISH_SCORE_LEVEL_3,
        Mtime.PUBLISH_SCORE_LEVEL_4,
        Mtime.PUBLISH_SCORE_LEVEL_5 -> getString(R.string.publish_score_level_3_5)
        Mtime.PUBLISH_SCORE_LEVEL_6,
        Mtime.PUBLISH_SCORE_LEVEL_7 -> getString(R.string.publish_score_level_6_7)
        Mtime.PUBLISH_SCORE_LEVEL_8 -> getString(R.string.publish_score_level_8)
        Mtime.PUBLISH_SCORE_LEVEL_9,
        Mtime.PUBLISH_SCORE_LEVEL_10 -> getString(R.string.publish_score_level_9_10)
        else -> "" // 空
    }
}
///1"个人", 2"影评人", 3"电影人", 4"机构"
//android:src="@{data.bean.isInstitutionAuthUser ? @drawable/ic_jigourenzheng : @drawable/ic_yingrenrenzheng}"
//android:visibility="@{data.bean.authUser ? View.VISIBLE : View.GONE}"
//是不是机构认证用户
fun isInstitutionAuthUser(authType:Long): Boolean {
    return authType == 4L
}

//是不是认证用户
fun isAuthUser(authType:Long): Boolean {
    return authType > 1L
}

/**
 * 设置用户认证标识图标
 */
fun ImageView.setUserAuthType(userAuthType: Long) {
    when (userAuthType) {
        CommConstant.USER_AUTH_TYPE_REVIEW_PERSON,
        CommConstant.USER_AUTH_TYPE_MOVIE_PERSON -> {
            setImageResource(R.drawable.ic_yingrenrenzheng)
        }
        CommConstant.USER_AUTH_TYPE_ORGANIZATION -> {
            setImageResource(R.drawable.ic_jigourenzheng)
        }
        else -> {
            setImageResource(0)
        }
    }
}