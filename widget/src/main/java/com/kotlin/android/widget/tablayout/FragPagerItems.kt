package com.kotlin.android.widget.tablayout

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ogaclejapan.smarttablayout.utils.PagerItems

/**
 * xx
 */
class FragPagerItems(context: Context) : PagerItems<FragPagerItem>(context) {

    fun add(
        title: CharSequence = "",
        titleRes: Int = 0,
        tag: String = "",
        clazz: Class<out Fragment>,
        width: Float = 1.0f,
        args: Bundle = Bundle()
    ): FragPagerItems {
        add(
            FragPagerItem.of(
                if (titleRes > 0) {
                    context.getString(titleRes)
                } else {
                    title
                },
                tag,
                clazz,
                width,
                args
            )
        )
        return this
    }
}

fun FragPagerItems.add(
    title: CharSequence = "",
    titleRes: Int = 0,
    tag: String = "",
    clazz: Class<out Fragment>,
    width: Float = 1.0f,
    args: Bundle = Bundle()
): FragPagerItems {
    add(
        FragPagerItem.of(
            if (titleRes > 0) {
                context.getString(titleRes)
            } else {
                title
            },
            tag,
            clazz,
            width,
            args
        )
    )
    return this
}