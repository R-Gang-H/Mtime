package com.kotlin.android.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlin.android.core.ext.versionName
import com.kotlin.android.crash.CrashHandler
import com.kotlin.android.ktx.ext.io.readFile
import com.kotlin.android.ktx.ext.core.setWebViewDataDirectory
import com.kotlin.android.ktx.ext.io.makeSDDir
import com.kotlin.android.ktx.ext.log.LogManager
import com.kotlin.android.ktx.ext.log.LogType
import com.kotlin.android.ktx.utils.LogUtils
import java.util.*
import kotlin.concurrent.thread
import kotlin.properties.Delegates


/**
 *
 * Created on 2020/4/21.
 *
 * @author o.s
 */
open class CoreApp : Application() {
    companion object {
        const val TEST_PATH = "/时光网/test/"

        /**
         * 本地sd卡json测试开关
         */
        var testFlag: Boolean = false
        var instance: Context by Delegates.notNull()
        var testApiContents: MutableList<JsonObject> = ArrayList()
    }

    private var activityList: MutableList<Activity> = mutableListOf()

    override fun onCreate() {
        super.onCreate()
        instance = applicationContext
//        initRouter()
        /**
         * 子线程处理，减轻onCreate负担
         */
        thread(start = true) {
            initLogManager()
            initStetho()
            //错误处理器
            CrashHandler.init(this)
            initTestFile()
            setWebViewDataDirectory()
        }

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activityList.add(activity)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                activityList.remove(activity)
            }
        })
    }

    fun getTopActivity(): Activity? {
        return if (activityList.isEmpty()) {
            null
        } else {
            activityList[activityList.size - 1]
        }
    }

    /**
     * 本地创建测试文件夹，方便调试
     */
    private fun initTestFile() {
        if (!BuildConfig.DEBUG) {
            return
        }
        /**
         * 创建test文件夹
         */
        makeSDDir(TEST_PATH)
        val testFile = readFile(TEST_PATH)
        LogUtils.d("testFile-> $testFile")
        if (testFile.isEmpty()) {
            return
        }
        val fromJson = JsonParser.parseString(testFile)
        fromJson?.apply {
            val obj = this.asJsonObject
            val testApiContent = obj.get("testApiContent")
            testApiContent?.apply {
                val contents = this.asJsonArray
                contents?.forEach {
                    testApiContents.add(it.asJsonObject)
                }
            }
        }
    }

    /**
     * 初始化isDebug开关配置
     */
    private fun initLogManager() {
        if (BuildConfig.DEBUG) {
            LogUtils.isDebug = BuildConfig.DEBUG
            LogUtils.init("时光网$versionName")
            LogManager.initGlobal(tag = versionName, logType = LogType.LOG_FULL)
        }
    }

    /**
     * 初始化路由组件
     */
//    private fun initRouter() {
//        if (BuildConfig.DEBUG) {
//            ARouter.openLog()
//            ARouter.openDebug()
//        }
//        ARouter.init(this)
//    }

    /**
     * Facebook/Stetho——Android开发调试神器  可以进行抓包和db/sp查看及修改  https://www.jianshu.com/p/804a2f9c8897
     */
    private fun initStetho() {
        if (BuildConfig.DEBUG) {
//            Stetho.initializeWithDefaults(this)
        }
    }


}