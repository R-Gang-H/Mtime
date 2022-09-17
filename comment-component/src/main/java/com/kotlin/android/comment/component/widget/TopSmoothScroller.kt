package com.kotlin.android.comment.component.widget

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

/**
 * create by lushan on 2020/10/21
 * description:
 */
class TopSmoothScroller(var context: Context):LinearSmoothScroller(context) {
    override fun getHorizontalSnapPreference(): Int {
        return SNAP_TO_START
    }

    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START
    }
}