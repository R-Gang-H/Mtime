package com.kotlin.android.ktx.ext.log

import android.util.Log
import com.kotlin.android.ktx.BuildConfig
import com.kotlin.android.ktx.utils.LogUtils

/**
 * 日志扩展
 *
 * Created on 2020/4/21.
 *
 * @author o.s
 */

fun Any?.v() {
    if (BuildConfig.DEBUG) {
        when (LogManager.LOG_TYPE) {
            LogType.LOG_UTILS -> {
                LogUtils.v(this.toString())
            }
            LogType.LOG -> {
                Log.v(LogManager.LOG_TAG, LogFormat.logFormat(this))
            }
            LogType.LOG_FULL -> {
                LogFormat.logFormatFull(this).forEach {
                    Log.v(LogManager.LOG_TAG, it)
                }
            }
        }
    }
}

fun Any?.d() {
    if (BuildConfig.DEBUG) {
        when (LogManager.LOG_TYPE) {
            LogType.LOG_UTILS -> {
                LogUtils.d(this.toString())
            }
            LogType.LOG -> {
                Log.d(LogManager.LOG_TAG, LogFormat.logFormat(this))
            }
            LogType.LOG_FULL -> {
                LogFormat.logFormatFull(this).forEach {
                    Log.d(LogManager.LOG_TAG, it)
                }
            }
        }
    }
}

fun Any?.i() {
    if (BuildConfig.DEBUG) {
        when (LogManager.LOG_TYPE) {
            LogType.LOG_UTILS -> {
                LogUtils.i(this.toString())
            }
            LogType.LOG -> {
                Log.i(LogManager.LOG_TAG, LogFormat.logFormat(this))
            }
            LogType.LOG_FULL -> {
                LogFormat.logFormatFull(this).forEach {
                    Log.i(LogManager.LOG_TAG, it)
                }
            }
        }
    }
}

fun Any?.w() {
    if (BuildConfig.DEBUG) {
        when (LogManager.LOG_TYPE) {
            LogType.LOG_UTILS -> {
                LogUtils.w(this.toString())
            }
            LogType.LOG -> {
                Log.w(LogManager.LOG_TAG, LogFormat.logFormat(this))
            }
            LogType.LOG_FULL -> {
                LogFormat.logFormatFull(this).forEach {
                    Log.w(LogManager.LOG_TAG, it)
                }
            }
        }
    }
}

fun Any?.e() {
    if (BuildConfig.DEBUG) {
        when (LogManager.LOG_TYPE) {
            LogType.LOG_UTILS -> {
                LogUtils.e(this.toString())
            }
            LogType.LOG -> {
                Log.e(LogManager.LOG_TAG, LogFormat.logFormat(this))
            }
            LogType.LOG_FULL -> {
                LogFormat.logFormatFull(this).forEach {
                    Log.e(LogManager.LOG_TAG, it)
                }
            }
        }
    }
}

fun e(e: () -> Any?) {
    if (BuildConfig.DEBUG) {
        e().e()
    }
}

fun w(w: () -> Any?) {
    if (BuildConfig.DEBUG) {
        w().w()
    }
}

fun i(i: () -> Any?) {
    if (BuildConfig.DEBUG) {
        i().i()
    }
}

fun d(d: () -> Any?) {
    if (BuildConfig.DEBUG) {
        d().d()
    }
}

fun v(v: () -> Any?) {
    if (BuildConfig.DEBUG) {
        v().v()
    }
}

fun Any?.json() {
    LogUtils.json(this.toString())
}