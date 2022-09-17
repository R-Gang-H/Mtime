package com.kotlin.android.player

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.provider.Settings

class RotationContentObserver(context: Context) : ContentObserver(null) {
    private val mResolver: ContentResolver
    private var mOnSettingChangeListener: OnSettingChangeListener? = null
    fun setOnSettingChangeListener(onSettingChangeListener: OnSettingChangeListener?) {
        mOnSettingChangeListener = onSettingChangeListener
    }

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        mOnSettingChangeListener?.onSettingChange(isAutoRotation(mResolver))
    }

    fun startObserver() {
        mResolver.registerContentObserver(Settings.System
                .getUriFor(Settings.System.ACCELEROMETER_ROTATION), false,
                this)
    }

    fun stopObserver() {
        mResolver.unregisterContentObserver(this)
    }

    interface OnSettingChangeListener {
        fun onSettingChange(autoRotation: Boolean)
    }

    companion object {
        @JvmStatic
        fun isAutoRotation(resolver: ContentResolver?): Boolean {
            return Settings.System.getInt(resolver,
                    Settings.System.ACCELEROMETER_ROTATION, 0) == 1
        }
    }

    /**
     * Creates a content observer.
     *
     * @param context
     * @param handler The handler to run [.onChange] on, or null if none.
     */
    init {
        mResolver = context.contentResolver
    }
}