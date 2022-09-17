package com.kotlin.android.publish.component.widget.selector

object SelectMimeType {
    /**
     * GET image or video only
     *
     *
     * excluding Audio
     *
     */
    fun ofAll(): Int {
        return TYPE_ALL
    }

    /**
     * GET image only
     */
    @JvmStatic
    fun ofImage(): Int {
        return TYPE_IMAGE
    }

    /**
     * GET video only
     */
    @JvmStatic
    fun ofVideo(): Int {
        return TYPE_VIDEO
    }

    /**
     * GET audio only
     *
     *
     * # No longer maintain audio related functions,
     * but can continue to use but there will be phone compatibility issues
     *
     *
     * 不再维护音频相关功能，但可以继续使用但会有机型兼容性问题
     */
    @JvmStatic
    fun ofAudio(): Int {
        return TYPE_AUDIO
    }

    const val TYPE_ALL = 0
    const val TYPE_IMAGE = 1
    const val TYPE_VIDEO = 2
    const val TYPE_AUDIO = 3

    /**
     * System all image or video album
     */
    const val SYSTEM_ALL = "image/*,video/*"

    /**
     * System image album
     */
    const val SYSTEM_IMAGE = "image/*"

    /**
     * System video album
     */
    const val SYSTEM_VIDEO = "video/*"

    /**
     * System audio album
     */
    const val SYSTEM_AUDIO = "audio/*"
}