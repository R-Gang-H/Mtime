package com.kotlin.android.widget.tablayout

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ogaclejapan.smarttablayout.utils.PagerItem

/**
 * xx
 */
class FragPagerItem(
    title: CharSequence,
    tag: String = "",
    val className: String,
    width: Float = DEFAULT_WIDTH,
    val args: Bundle = Bundle()) : PagerItem(title, width) {

    private val TAG = "FragmentPagerItem"
    private val KEY_POSITION = "$TAG:Position"
    private val mTag = tag

    companion object {
        fun of(
            title: CharSequence,
            tag: String = "",
            clazz: Class<out Fragment>,
            width: Float = DEFAULT_WIDTH,
            args: Bundle = Bundle()
        ): FragPagerItem {
            return FragPagerItem(title, tag, clazz.name, width, args)
        }
    }

    fun hasPosition(argss: Bundle): Boolean {
        return argss.containsKey(KEY_POSITION)
    }

    fun getPosition(argss: Bundle): Int {
        return if (hasPosition(argss)) argss.getInt(KEY_POSITION) else 0
    }

    fun setPosition(argss: Bundle, position: Int) {
        argss.putInt(KEY_POSITION, position)
    }

    fun instantiate(context: Context, position: Int): Fragment? {
        setPosition(args, position)
        return Fragment.instantiate(context, className, args)
    }

    fun getTag(): String {
        return mTag
    }
}