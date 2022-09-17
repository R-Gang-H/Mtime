package com.kotlin.android.publish.component.widget.selector

import com.kotlin.android.ktx.ext.orZero
import java.util.*
object SortUtils {
    /**
     * Sort by the number of files
     *
     * @param imageFolders
     */
    fun sortFolder(imageFolders: List<LocalMediaFolder?>?) {
        Collections.sort(imageFolders) { lhs: LocalMediaFolder?, rhs: LocalMediaFolder? ->
            if (lhs?.data == null || rhs?.data == null) {
                return@sort 0
            }
            val lSize = lhs.folderTotalNum
            val rSize = rhs.folderTotalNum
            rSize.compareTo(lSize)
        }
    }

    /**
     * Sort by the add Time of files
     *
     * @param list
     */
    @JvmStatic
    fun sortLocalMediaAddedTime(list: List<LocalMedia?>?) {
        Collections.sort(list) { lhs: LocalMedia?, rhs: LocalMedia? ->
            val lAddedTime = lhs?.dateAddedTime.orZero()
            val rAddedTime = rhs?.dateAddedTime.orZero()
            rAddedTime.compareTo(lAddedTime)
        }
    }
}