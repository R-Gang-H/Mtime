package com.kotlin.android.player

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.kotlin.android.core.ext.getSpValue
import com.kotlin.android.core.ext.putSpValue
import com.kotlin.android.app.data.entity.video.VideoPlayUrl
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.ktx.ext.core.activityManager
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.ktx.ext.dimension.screenWidth

/**
 * create by lushan on 2020/8/19
 * description: 视频播放类
 */
object PlayerHelper {
    const val KEY_VIDEO_DEFINITION = "video_definition"
    const val KEY_PLAYER_BRIGHTNESS = "player_brightness"

    fun setSystemBarVisibility(context: Activity?,isShow:Boolean){
        if (isShow){
            context?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }else{
            context?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    fun setSystemUIVisible(context: Activity, show: Boolean) {
        if (show) {
            val decorView = context.window.decorView
            val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            decorView.systemUiVisibility = option
            if (Build.VERSION.SDK_INT >= 21) {
                context.window.statusBarColor = Color.TRANSPARENT
                return
            }
            if (Build.VERSION.SDK_INT >= 19) {
                context.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        } else {
            var uiFlags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
            uiFlags = uiFlags or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            context.window.decorView.systemUiVisibility = uiFlags
        }
    }

    fun portraitMatchWidth_16_9(context: Context?, container: ViewGroup, params: Params?) {
        portraitMatchWidthWithHeight(context,container,params,screenWidth * 9 / 16)
    }

    fun portraitMatchWidthWithHeight(context: Context?, container: ViewGroup, params: Params?,height:Int) {
        val layoutParams = container.layoutParams
        layoutParams.width = -1
        layoutParams.height = height
        if (params != null) {
            if (layoutParams is RelativeLayout.LayoutParams) {
                layoutParams
                    .setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin)
            } else if (layoutParams is FrameLayout.LayoutParams) {
                layoutParams
                    .setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin)
            }
            container.setPadding(params.leftPadding, params.topPadding, params.rightPadding, params.bottomPadding)
        }
        container.layoutParams = layoutParams
    }


    fun landscapeMatchWidthHeight(context: Context?, container: ViewGroup, params: Params?) {
        val layoutParams = container.layoutParams
        layoutParams.width = screenWidth
        layoutParams.height = screenHeight
        if (params != null) {
            if (layoutParams is RelativeLayout.LayoutParams) {
                layoutParams
                        .setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.topMargin)
            } else if (layoutParams is FrameLayout.LayoutParams) {
                layoutParams
                        .setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.topMargin)
            }
            container.setPadding(params.leftPadding, params.topPadding, params.rightPadding, params.bottomPadding)
        }
        container.layoutParams = layoutParams
    }


    fun recordDefinition(definition: String?) {
        putSpValue(KEY_VIDEO_DEFINITION, definition.orEmpty())
    }

    fun isTopActivity(activity: Activity?): Boolean {
        return activity != null && isForeground(activity, activity.javaClass.name)
    }

    fun isForeground(context: Context?, className: String): Boolean {
        if (context == null || TextUtils.isEmpty(className)) {
            return false
        }
        val am = context.activityManager
        val list = am?.getRunningTasks(1)
        list?.take(1)?.forEach {
            if (className == it.topActivity?.className) {
                return true
            }
        }
        return false
    }

    /**
     * 获取视频历史清晰度
     */
    fun getHistoryDefinition(): String? {
        return getSpValue(KEY_VIDEO_DEFINITION, "")
    }

    /**
     * 获取高清晰度
     */
    fun getHighDefinition(list: ArrayList<VideoPlayUrl>): VideoPlayUrl? {
        if (list.isEmpty()) {
            return null
        }
        val videoPlayUrl = list[list.size - 1]
        var maxDefinitionCode = videoPlayUrl.resolutionType
        var maxBean = videoPlayUrl
        list.forEach {
            val code = it.resolutionType
            if (code > maxDefinitionCode) {
                maxBean = it
                maxDefinitionCode = code
            }
        }
        return maxBean
    }
}


data class Params(var leftMargin: Int = 0,
                  var topMargin: Int = 0,
                  var rightMargin: Int = 0,
                  var bottomMargin: Int = 0,
                  var leftPadding: Int = 0,
                  var topPadding: Int = 0,
                  var rightPadding: Int = 0,
                  var bottomPadding: Int = 0) : ProguardRule
