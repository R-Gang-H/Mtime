package com.kotlin.android.ktx.utils

/**
 * Created by Jack on 2017/7/4.
 */
import android.text.TextUtils
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object LogUtils {
    private val MIN_STACK_OFFSET = 3
    val SHARE_TAG = "SHARE_"
    val BRANCH_TAG = "branch"
    val NET_TAG = "NET_"
    val PUSH_TAG = "JIGUANG-"
    val JUMP_TAG = "JUMP-"
    val NET_WEB_TAG = "NET_WEB"
    val DOWNLOAD_TAG = "DownloadTask"

    //    val NET_REQUEST_FORMAT = "request  >>> [ %1\$s ] %2\$s :: UUID = %3\$s\n\t\t\t\tparam = %4\$s\n\n"
    val NET_REQUEST_FORMAT = "API request  >>> [ %s ] %s :: UUID = %s\n\t\t\t\tparam = %s"
    val NET_RESPONSE_FORMAT = "API response <<< [ %s ] \n%s"

    // title, browserUrl, mOverrideSchemePrefix, mUserAgentSuffix, mUserAgent
    val NET_WEB_PARAMS_FORMAT = "web param >>> [ %s :: %s :: %s :: %s :: %s ]"

    /**
     * Drawing toolbox
     */
    private val TOP_LEFT_CORNER = '╔'
    private val BOTTOM_LEFT_CORNER = '╚'
    private val MIDDLE_CORNER = '╟'
    private val HORIZONTAL_DOUBLE_LINE = '║'
    private val DOUBLE_DIVIDER = "═══════════════════════════════════════════════════════════════════════════════════════"
    private val SINGLE_DIVIDER = "───────────────────────────────────────────────────────────────────────────────────────"
    val TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    val BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    val MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER
    var isDebug: Boolean = false

    /**
     * It is used for json pretty print
     */
    val JSON_INDENT = 2

    private fun getStackOffset(trace: Array<StackTraceElement>): Int {
        var i = MIN_STACK_OFFSET
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
            if (name != LogUtils::class.java.name && name != LogUtils::class.java.name) {
                return --i
            }
            i++
        }
        return -1
    }

    enum class LogLevel {
        ERROR {
            override val value: Int
                get() = 0
        },
        WARN {
            override val value: Int
                get() = 1
        },
        INFO {
            override val value: Int
                get() = 2
        },
        DEBUG {
            override val value: Int
                get() = 3
        };

        abstract val value: Int
    }

    private var TAG = "SAF_L"

    var logLevel = LogLevel.DEBUG // 日志的等级，可以进行配置，最好在Application中进行全局的配置

    @JvmStatic
    fun init(clazz: Class<*>) {
        TAG = clazz.simpleName
    }

    /**
     * 支持用户自己传tag，可扩展性更好
     * @param tag
     */
    @JvmStatic
    fun init(tag: String) {
        TAG = tag
    }

    @JvmStatic
    fun v(msg: String) {
        if (LogLevel.ERROR.value <= logLevel.value) {

            if (msg.isNotBlank()) {
                val s = getMethodNames()
                if (isDebug)
                    Log.v(TAG, String.format(s, msg))
            }
        }
    }

    @JvmStatic
    fun e(msg: String) {
        if (LogLevel.ERROR.value <= logLevel.value) {

            if (msg.isNotBlank()) {
                val s = getMethodNames()
                if (isDebug)
                    Log.e(TAG, String.format(s, msg))
            }
        }
    }

    @JvmStatic
    fun w(msg: String) {
        if (LogLevel.WARN.value <= logLevel.value) {
            if (msg.isNotBlank()) {
                val s = getMethodNames()
                if (isDebug)
                    Log.w(TAG, String.format(s, msg))
            }
        }
    }

    @JvmStatic
    fun i(msg: String) {
        if (LogLevel.INFO.value <= logLevel.value) {
            if (msg.isNotBlank()) {
                val s = getMethodNames()
                if (isDebug)
                    Log.i(TAG, String.format(s, msg))
            }
        }
    }

    @JvmStatic
    fun d(msg: String) {
        if (LogLevel.DEBUG.value <= logLevel.value) {
            if (msg.isNotBlank()) {

                val s = getMethodNames()
                if (isDebug)
                    Log.d(TAG, String.format(s, msg))
            }
        }
    }

    @JvmStatic
    fun v(tag: String, msg: String, vararg args: Any) {
        if (LogLevel.WARN.value <= logLevel.value) {
            if (msg.isNotBlank()) {
                val s = getMethodNames(false)
                if (isDebug)
                    Log.v(tag, String.format(s, getVarargString(msg, args)))
            }
        }
    }

    @JvmStatic
    fun e(tag: String, msg: String, vararg args: Any) {
        if (LogLevel.WARN.value <= logLevel.value) {
            if (msg.isNotBlank()) {
                val s = getMethodNames(false)
                if (isDebug)
                    Log.e(tag, String.format(s, getVarargString(msg, args)))
            }
        }
    }

    @JvmStatic
    fun w(tag: String, msg: String, vararg args: Any) {
        if (LogLevel.WARN.value <= logLevel.value) {
            if (msg.isNotBlank()) {
                val s = getMethodNames(false)
                if (isDebug)
                    Log.w(tag, String.format(s, getVarargString(msg, args)))
            }
        }
    }

    @JvmStatic
    fun i(tag: String, msg: String, vararg args: Any) {
        if (LogLevel.WARN.value <= logLevel.value) {
            if (msg.isNotBlank()) {
                val s = getMethodNames(false)
                if (isDebug)
                    Log.i(tag, String.format(s, getVarargString(msg, args)))
            }
        }
    }

    @JvmStatic
    fun d(tag: String, msg: String, vararg args: Any) {
        if (LogLevel.WARN.value <= logLevel.value) {
            if (msg.isNotBlank()) {
                val s = getMethodNames(false)
                if (isDebug)
                    Log.d(tag, String.format(s, getVarargString(msg, args)))
            }
        }
    }

    @JvmStatic
    fun json(j: String) {
        val formatJson = formatJson(j)
        if (TextUtils.isEmpty(formatJson).not()) {
            val s = getMethodNames(false)
            if (isDebug) Log.d(TAG, String.format(s, formatJson))
        }
    }

    @JvmStatic
    fun formatJson(j: String): String {
        var json = j

        if (json.isBlank()) {
            d("Empty/Null json content")
            return ""
        }

        try {
            json = json.trim { it <= ' ' }
            if (json.startsWith("{")) {
                val jsonObject = JSONObject(json)
                var message = jsonObject.toString(JSON_INDENT)
                message = message.replace("\n".toRegex(), "\n║ ")
                return "║ $message"
            }
            if (json.startsWith("[")) {
                val jsonArray = JSONArray(json)
                var message = jsonArray.toString(JSON_INDENT)
                message = message.replace("\n".toRegex(), "\n║ ")
                return "║ $message"
            }
            return ""
        } catch (e: JSONException) {
            return ""
        }
    }

    private fun getMethodNames(flag: Boolean = true): String {
        val sElements = Thread.currentThread().stackTrace

        var stackOffset = getStackOffset(sElements)
        stackOffset++
        stackOffset++
        if (flag) {
            stackOffset++
        }
        val builder = StringBuilder()
        builder.append(" ").append("\n").append(TOP_BORDER).append("\r\n")
                // 添加当前线程名
                //.append("║ " + "Thread: " + Thread.currentThread().name).append("\r\n")
                //.append(MIDDLE_BORDER).append("\r\n")
                // 添加类名、方法名、行数
                .append("Class: ")
                .append(sElements[stackOffset].className)
                .append(".")
                .append(sElements[stackOffset].methodName)
                .append(" ")
                .append(" (")
                .append(sElements[stackOffset].fileName)
                .append(":")
                .append(sElements[stackOffset].lineNumber)
                .append(")")
                //.append("\r\n")
                //.append(MIDDLE_BORDER).append("\r\n")
                // 添加打印的日志信息
                .append("\n").append("Log: ").append("%s").append("\n")
                .append(BOTTOM_BORDER).append("\r\n")
        return builder.toString()
    }

    /**
     * Kotlin可变长参数临时处理方案
     */
    private fun getVarargString(format: String, vararg srcArgs: Any): String {
        val args = srcArgs[0] as Array<*>
        val size = args.size
        if (size == 0) {
            return format
        } else if (size == 1) {
            return String.format(format, args[0])
        } else if (size == 2) {
            return String.format(format, args[0], args[1])
        } else if (size == 3) {
            return String.format(format, args[0], args[1], args[2])
        } else if (size == 4) {
            return String.format(format, args[0], args[1], args[2], args[3])
        } else if (size == 5) {
            return String.format(format, args[0], args[1], args[2], args[3], args[4])
        } else if (size == 6) {
            return String.format(format, args[0], args[1], args[2], args[3], args[4], args[5])
        } else {
            return format
        }
    }

}