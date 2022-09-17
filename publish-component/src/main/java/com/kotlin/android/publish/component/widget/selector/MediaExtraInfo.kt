package com.kotlin.android.publish.component.widget.selector

import com.kotlin.android.app.data.ProguardRule

class MediaExtraInfo :ProguardRule{
    /**
     * videoThumbnail
     */
    var videoThumbnail: String? = null

    /**
     * width
     */
    var width = 0

    /**
     * height
     */
    var height = 0

    /**
     * duration
     */
    var duration: Long = 0

    /**
     * orientation
     */
    var orientation: String? = null
    override fun toString(): String {
        return "MediaExtraInfo{" +
                "videoThumbnail='" + videoThumbnail + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", duration=" + duration +
                ", orientation='" + orientation + '\'' +
                '}'
    }
}