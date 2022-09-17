package com.kotlin.chat_component.inner.model

import com.kotlin.chat_component.R
import com.kotlin.chat_component.inner.utils.EaseSmileUtils
import com.kotlin.chat_component.inner.domain.EaseEmojicon

object EaseDefaultEmojiconDatas {
    private val emojis = arrayOf(
        EaseSmileUtils.ee_1,
        EaseSmileUtils.ee_2,
        EaseSmileUtils.ee_3,
        EaseSmileUtils.ee_4,
        EaseSmileUtils.ee_5,
        EaseSmileUtils.ee_6,
        EaseSmileUtils.ee_7,
        EaseSmileUtils.ee_8,
        EaseSmileUtils.ee_9,
        EaseSmileUtils.ee_10,
        EaseSmileUtils.ee_11,
        EaseSmileUtils.ee_12,
        EaseSmileUtils.ee_13,
        EaseSmileUtils.ee_14,
        EaseSmileUtils.ee_15,
        EaseSmileUtils.ee_16,
        EaseSmileUtils.ee_17,
        EaseSmileUtils.ee_18,
        EaseSmileUtils.ee_19,
        EaseSmileUtils.ee_20,
        EaseSmileUtils.ee_21,
        EaseSmileUtils.ee_22,
        EaseSmileUtils.ee_23,
        EaseSmileUtils.ee_24,
        EaseSmileUtils.ee_25,
        EaseSmileUtils.ee_26,
        EaseSmileUtils.ee_27,
        EaseSmileUtils.ee_28,
        EaseSmileUtils.ee_29,
        EaseSmileUtils.ee_30,
        EaseSmileUtils.ee_31,
        EaseSmileUtils.ee_32,
        EaseSmileUtils.ee_33,
        EaseSmileUtils.ee_34,
        EaseSmileUtils.ee_35
    )
    private val icons = intArrayOf(
        R.drawable.ee_1,
        R.drawable.ee_2,
        R.drawable.ee_3,
        R.drawable.ee_4,
        R.drawable.ee_5,
        R.drawable.ee_6,
        R.drawable.ee_7,
        R.drawable.ee_8,
        R.drawable.ee_9,
        R.drawable.ee_10,
        R.drawable.ee_11,
        R.drawable.ee_12,
        R.drawable.ee_13,
        R.drawable.ee_14,
        R.drawable.ee_15,
        R.drawable.ee_16,
        R.drawable.ee_17,
        R.drawable.ee_18,
        R.drawable.ee_19,
        R.drawable.ee_20,
        R.drawable.ee_21,
        R.drawable.ee_22,
        R.drawable.ee_23,
        R.drawable.ee_24,
        R.drawable.ee_25,
        R.drawable.ee_26,
        R.drawable.ee_27,
        R.drawable.ee_28,
        R.drawable.ee_29,
        R.drawable.ee_30,
        R.drawable.ee_31,
        R.drawable.ee_32,
        R.drawable.ee_33,
        R.drawable.ee_34,
        R.drawable.ee_35
    )
    val data = createData()
    private fun createData(): Array<EaseEmojicon?> {
        val datas = arrayOfNulls<EaseEmojicon>(icons.size)
        for (i in icons.indices) {
            datas[i] = EaseEmojicon(icons[i], emojis[i], EaseEmojicon.Type.NORMAL)
        }
        return datas
    }
}