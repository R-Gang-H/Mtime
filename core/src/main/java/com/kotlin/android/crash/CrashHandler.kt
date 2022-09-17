package com.kotlin.android.crash

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.os.Process
import androidx.collection.arrayMapOf
import com.kotlin.android.core.ext.exitApp
import com.kotlin.android.ktx.ext.io.makeSDDir
import com.kotlin.android.ktx.ext.io.safeClose
import com.kotlin.android.ktx.utils.LogUtils.e
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

/**
 * Created by zl on 17/8/30.
 * 保证只有一个CrashHandler实例
 */
object CrashHandler : Thread.UncaughtExceptionHandler {
    private const val TAG = "崩溃日志"
    private var logPath = Environment.getExternalStorageDirectory().path + File.separator + Environment.DIRECTORY_DOWNLOADS + "/MTime/log"

    private var mDefaultHandler // 系统默认的UncaughtException处理类
            : Thread.UncaughtExceptionHandler? = null
    private var mContext // 程序的Context对象
            : Context? = null
    private val info: MutableMap<String, Any> = arrayMapOf() // 用来存储设备信息和异常信息
    private val format = SimpleDateFormat(
            "yyyy-MM-dd-HH:mm:ss", Locale.CHINA) // 用于格式化日期,作为日志文件名的一部分
    private var time //发生crash的具体时间
            : String? = null

    /**
     * 初始化
     *
     * @param context
     */
    fun init(context: Context?) {
        mContext = context
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler() // 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this) // 设置该CrashHandler为程序的默认处理器
    }

    /**
     * 当UncaughtException发生时会转入该重写的方法来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (handleException(ex) && mDefaultHandler != null) {
            // 如果自定义的没有处理则让系统默认的异常处理器来处理
            mDefaultHandler?.uncaughtException(thread, ex)
        } else {
            try {
                Thread.sleep(1000) // 如果处理了，让程序继续运行1秒再退出，保证文件保存并上传到服务器
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                // 退出程序
                exitApp()
                Process.killProcess(Process.myPid())
                exitProcess(1)
            }
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 异常信息
     * @return true 如果处理了该异常信息;否则返回false.
     */
    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        thread(start = true) {
            Looper.prepare()
            //Toast.makeText(mContext, "出现闪退了正在把日志保存到sdcard log目录下", Toast.LENGTH_SHORT).show();
            Looper.loop()
        }
        // 收集设备参数信息
        collectDeviceInfo(mContext)
        // 保存日志文件
        saveCrashInfo2File(ex)
        return true
    }

    /**
     * 收集设备参数信息
     *
     * @param context
     */
    private fun collectDeviceInfo(context: Context?) {
        try {
            val pm = context?.packageManager // 获得包管理器
            val pi = pm?.getPackageInfo(context.packageName,
                    PackageManager.GET_ACTIVITIES) // 得到该应用的信息，即主Activity
            if (pi != null) {
                info["versionName"] = pi.versionName
                info["versionCode"] = pi.versionCode
                val time = format.format(Date())
                info["logTime"] = time
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e(TAG, "collectDeviceInfo-----> $e")
        }
        val fields = Build::class.java.declaredFields // 反射机制
        for (field in fields) {
            try {
                field.isAccessible = true
                info[field.name] = "${field[""]}"
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveCrashInfo2File(ex: Throwable): String {
        val sb = StringBuffer()
        for ((key, value) in info) {
            sb.append("$key=$value\r\n")
        }
        val writer: Writer = StringWriter()
        val pw = PrintWriter(writer)
        ex.printStackTrace(pw)
        var cause = ex.cause
        // 循环把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(pw)
            cause = cause.cause
        }
        pw.close() // 记得关闭
        val result = writer.toString()
        sb.append(result)
        e(TAG, "crash-----> $result")
        // 保存文件
        val timestamp = System.currentTimeMillis()
        time = format.format(Date())
        val fileName = "$time-$timestamp.log"
        var fos: FileOutputStream? = null
        try {
            val dir = makeSDDir(logPath)
            if (dir != null) {
                fos = FileOutputStream(File(dir, fileName))
                fos.write(sb.toString().toByteArray())
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fos.safeClose()
        }
        return fileName
    }


}