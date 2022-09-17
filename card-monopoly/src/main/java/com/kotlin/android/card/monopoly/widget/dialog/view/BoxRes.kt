package com.kotlin.android.card.monopoly.widget.dialog.view

import androidx.core.graphics.drawable.toBitmap
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.mtime.ktx.getDrawable

/**
 * 宝箱资源
 *
 * Created on 2021/5/19.
 *
 * @author o.s
 */
class BoxRes(val boxType: BoxType) {
    val bitmap0 by lazy {
        when (boxType) {
            BoxType.COPPER -> {
                getDrawable(R.drawable.img_copper_0)?.toBitmap()
            }
            BoxType.SILVER -> {
                getDrawable(R.drawable.img_silver_0)?.toBitmap()
            }
            BoxType.GOLD -> {
                getDrawable(R.drawable.img_gold_0)?.toBitmap()
            }
            BoxType.PLATINUM -> {
                getDrawable(R.drawable.img_platinum_0)?.toBitmap()
            }
            BoxType.DIAMONDS -> {
                getDrawable(R.drawable.img_diamond_0)?.toBitmap()
            }
            BoxType.LIMIT -> {
                getDrawable(R.drawable.img_limit_0)?.toBitmap()
            }
            BoxType.ACTIVITY -> {
                getDrawable(R.drawable.img_limit_0)?.toBitmap()
            }
        }
    }
    val bitmap1 by lazy {
        when (boxType) {
            BoxType.COPPER -> {
                getDrawable(R.drawable.img_copper_1)?.toBitmap()
            }
            BoxType.SILVER -> {
                getDrawable(R.drawable.img_silver_1)?.toBitmap()
            }
            BoxType.GOLD -> {
                getDrawable(R.drawable.img_gold_1)?.toBitmap()
            }
            BoxType.PLATINUM -> {
                getDrawable(R.drawable.img_platinum_1)?.toBitmap()
            }
            BoxType.DIAMONDS -> {
                getDrawable(R.drawable.img_diamond_1)?.toBitmap()
            }
            BoxType.LIMIT -> {
                getDrawable(R.drawable.img_limit_1)?.toBitmap()
            }
            BoxType.ACTIVITY -> {
                getDrawable(R.drawable.img_limit_1)?.toBitmap()
            }
        }
    }
    val bitmap4 by lazy {
        when (boxType) {
            BoxType.COPPER -> {
                getDrawable(R.drawable.img_copper_4)?.toBitmap()
            }
            BoxType.SILVER -> {
                getDrawable(R.drawable.img_silver_4)?.toBitmap()
            }
            BoxType.GOLD -> {
                getDrawable(R.drawable.img_gold_4)?.toBitmap()
            }
            BoxType.PLATINUM -> {
                getDrawable(R.drawable.img_platinum_4)?.toBitmap()
            }
            BoxType.DIAMONDS -> {
                getDrawable(R.drawable.img_diamond_4)?.toBitmap()
            }
            BoxType.LIMIT -> {
                getDrawable(R.drawable.img_limit_4)?.toBitmap()
            }
            BoxType.ACTIVITY -> {
                getDrawable(R.drawable.img_limit_4)?.toBitmap()
            }
        }
    }
    val bitmap5 by lazy {
        when (boxType) {
            BoxType.COPPER -> {
                getDrawable(R.drawable.img_copper_5)?.toBitmap()
            }
            BoxType.SILVER -> {
                getDrawable(R.drawable.img_silver_5)?.toBitmap()
            }
            BoxType.GOLD -> {
                getDrawable(R.drawable.img_gold_5)?.toBitmap()
            }
            BoxType.PLATINUM -> {
                getDrawable(R.drawable.img_platinum_5)?.toBitmap()
            }
            BoxType.DIAMONDS -> {
                getDrawable(R.drawable.img_diamond_5)?.toBitmap()
            }
            BoxType.LIMIT -> {
                getDrawable(R.drawable.img_limit_5)?.toBitmap()
            }
            BoxType.ACTIVITY -> {
                getDrawable(R.drawable.img_limit_5)?.toBitmap()
            }
        }
    }
    val bitmap6 by lazy {
        when (boxType) {
            BoxType.COPPER -> {
                getDrawable(R.drawable.img_copper_6)?.toBitmap()
            }
            BoxType.SILVER -> {
                getDrawable(R.drawable.img_silver_6)?.toBitmap()
            }
            BoxType.GOLD -> {
                getDrawable(R.drawable.img_gold_6)?.toBitmap()
            }
            BoxType.PLATINUM -> {
                getDrawable(R.drawable.img_platinum_6)?.toBitmap()
            }
            BoxType.DIAMONDS -> {
                getDrawable(R.drawable.img_diamond_6)?.toBitmap()
            }
            BoxType.LIMIT -> {
                getDrawable(R.drawable.img_limit_6)?.toBitmap()
            }
            BoxType.ACTIVITY -> {
                getDrawable(R.drawable.img_limit_6)?.toBitmap()
            }
        }
    }

    fun recycle() {
        bitmap0?.recycle()
        bitmap1?.recycle()
        bitmap4?.recycle()
        bitmap5?.recycle()
        bitmap6?.recycle()
    }
}