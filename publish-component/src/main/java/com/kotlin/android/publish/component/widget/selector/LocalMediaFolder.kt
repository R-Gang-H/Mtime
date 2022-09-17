package com.kotlin.android.publish.component.widget.selector

import android.os.Parcelable
import android.text.TextUtils
import com.kotlin.android.app.data.ProguardRule
import kotlinx.android.parcel.Parcelize

@Parcelize
class LocalMediaFolder : Parcelable, ProguardRule {
    /**
     * folder bucketId
     */
    var bucketId = -1L

    /**
     * folder name
     */
    var folderName: String = "unknown"



    /**
     * folder first path
     */
    var firstImagePath: String? = null

    /**
     * first data mime type
     */
    var firstMimeType: String? = null

    /**
     * folder total media num
     */
    var folderTotalNum = 0

    /**
     * There are selected resources in the current directory
     */
    var isSelectTag = false

    /**
     * current folder data
     *
     *
     * In isPageStrategy mode, there is no data for the first time
     *
     */
    var data: ArrayList<LocalMedia>? = ArrayList()

    /**
     * # Internal use
     * setCurrentDataPage
     */
    var currentDataPage = 1

    /**
     * # Internal use
     * is load more
     */
    var isHasMore = false



}