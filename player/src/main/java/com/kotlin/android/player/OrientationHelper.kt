package com.kotlin.android.player

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.kotlin.android.player.RotationContentObserver.Companion.isAutoRotation
class OrientationHelper(var activity: Activity) {
    private val MSG_SENSOR = 888
    private var mActivity:Activity? = null
    // 是否是竖屏
    var isPortrait = true
        private set
    private var mRotation = 0
    private var sm: SensorManager?
    private val listener: OrientationSensorListener
    private var sensor: Sensor?
    private var sm1: SensorManager?
    private var sensor1: Sensor?
    private val listener1: OrientationSensorListener1
    private val mRotationContentObserver: RotationContentObserver
    private var mAutoRotation: Boolean
    private var openSensor:Boolean = true//是否开启重力感应
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_SENSOR -> {
                    val orientation = msg.arg1
                    if (orientation in 46..134) {
                        if (isPortrait) {
                            if (isLiving()) return
                            //切换成横屏反向：ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                            mActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                            isPortrait = false
                        } else if (mRotation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                            mActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        }
                        mRotation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    } else if (orientation in 136..224) {
                        if (!isPortrait) {
                            /*
                             * 切换成竖屏反向：ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT(9),
                             * ActivityInfo.SCREEN_ORIENTATION_SENSOR:根据重力感应自动旋转
                             * 此处正常应该是上面第一个属性，但是在真机测试时显示为竖屏正向，所以用第二个替代
                             */
                            mActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                            isPortrait = true
                        }
                        mRotation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    } else if (orientation in 226..314) {
                        if (isPortrait) {
                            if (isLiving()) return
                            //切换成横屏：ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            mActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            isPortrait = false
                        } else if (mRotation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                            mActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }
                        mRotation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    } else if (orientation in 316..359 || orientation in 1..44) {
                        if (isLiving()) return
                        if (!isPortrait) {
                            //切换成竖屏ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            mActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            isPortrait = true
                        }
                        mRotation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                }
                else -> {
                }
            }
        }
    }
    private val onSettingChangeListener: RotationContentObserver.OnSettingChangeListener = object : RotationContentObserver.OnSettingChangeListener {
        override fun onSettingChange(autoRotation: Boolean) {
            mAutoRotation = autoRotation
            if (openSensor.not()) return
            if (mAutoRotation) {
                enable()
            } else {
                disable()
            }
        }
    }

    private fun isLiving() = PlayerConfig.liveStatus == LiveStatus.LIVING

    fun sensorEnable(isEnable:Boolean){
        openSensor = isEnable
    }


    /**
     * 更新视频播放器位置
     */
     fun updateContentContainerLocation(view:ViewGroup) {
        val layoutParams: ViewGroup.LayoutParams = view.layoutParams
        val contentLayoutParams: ViewGroup.LayoutParams = view.layoutParams
        if (contentLayoutParams is RelativeLayout.LayoutParams) {
            contentLayoutParams.topMargin = layoutParams.height
        } else if (contentLayoutParams is LinearLayout.LayoutParams) {
            contentLayoutParams.topMargin = layoutParams.height
        }
         view.layoutParams = contentLayoutParams
    }
    fun keepOnScreen(context: Activity){
        context.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * 手动横竖屏切换方向
     */
    fun toggleScreen() {
        sm?.unregisterListener(listener)
        if (mAutoRotation) {
            sm1?.registerListener(listener1, sensor1, SensorManager.SENSOR_DELAY_UI)
        }
        if (isPortrait) {
            isPortrait = false
            // 切换成横屏
            mActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            isPortrait = true
            // 切换成竖屏
            mActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    fun enable() {
        if (mAutoRotation) {
            sm?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun disable() {
        sm?.unregisterListener(listener)
        sm1?.unregisterListener(listener1)
    }

    fun destroy() {
        disable()
        mRotationContentObserver.stopObserver()
        mActivity = null
    }

    fun quitFullScreen(activity: Activity) {
        val attrs = activity.window.attributes
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        activity.window.attributes = attrs
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    /**
     * 重力感应监听者
     */
    inner class OrientationSensorListener(private val rotateHandler: Handler?) : SensorEventListener {
        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            val values = event.values
            var orientation = Companion.ORIENTATION_UNKNOWN
            val X = -values[Companion._DATA_X]
            val Y = -values[Companion._DATA_Y]
            val Z = -values[Companion._DATA_Z]
            val magnitude = X * X + Y * Y
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                val OneEightyOverPi = 57.29577957855f
                val angle = Math.atan2(-Y.toDouble(), X.toDouble()).toFloat() * OneEightyOverPi
                orientation = 90 - Math.round(angle)
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360
                }
                while (orientation < 0) {
                    orientation += 360
                }
            }
            if (openSensor.not()) return
            rotateHandler?.obtainMessage(MSG_SENSOR, orientation, 0)?.sendToTarget()
        }

    }



    inner class OrientationSensorListener1 : SensorEventListener {

        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            val values = event.values
            var orientation = Companion.ORIENTATION_UNKNOWN
            val X = -values[Companion._DATA_X]
            val Y = -values[Companion._DATA_Y]
            val Z = -values[Companion._DATA_Z]
            val magnitude = X * X + Y * Y
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                val OneEightyOverPi = 57.29577957855f
                val angle = Math.atan2(-Y.toDouble(), X.toDouble()).toFloat() * OneEightyOverPi
                orientation = 90 - Math.round(angle)
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360
                }
                while (orientation < 0) {
                    orientation += 360
                }
            }
            if (openSensor.not()) return
            if (orientation in 226..314) { // 检测到当前实际是横屏
                if (!isPortrait) {
                    sm?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
                    sm1?.unregisterListener(listener1)
                }
            } else if (orientation in 316..359 || orientation in 1..44) { // 检测到当前实际是竖屏
                if (isPortrait) {
                    sm?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
                    sm1?.unregisterListener(listener1)
                }
            }
        }

    }
    companion object {
        private const val _DATA_X = 0
        private const val _DATA_Y = 1
        private const val _DATA_Z = 2
        const val ORIENTATION_UNKNOWN = -1
    }

    init {
        mActivity = activity
        val context = activity.applicationContext
        mAutoRotation = isAutoRotation(context.contentResolver)
        mRotationContentObserver = RotationContentObserver(context)
        mRotationContentObserver.setOnSettingChangeListener(onSettingChangeListener)
        mRotationContentObserver.startObserver()

        // 注册重力感应器,监听屏幕旋转
        sm = mActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sm?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        listener = OrientationSensorListener(mHandler)

        // 根据 旋转之后/点击全屏之后 两者方向一致,激活sm.
        sm1 = mActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor1 = sm1?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        listener1 = OrientationSensorListener1()
    }
}

