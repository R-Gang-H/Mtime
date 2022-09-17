package com.kotlin.android.widget.tablayout

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import java.lang.ref.WeakReference

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/14
 */
@SuppressLint("WrongConstant")
open class FragPagerItemAdapter(fm: FragmentManager, private val pages: FragPagerItems) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val holder: SparseArrayCompat<WeakReference<Fragment>> = SparseArrayCompat(pages.size)

    override fun getCount(): Int {
        return pages.size
    }

    override fun getItem(position: Int): Fragment {
        var frag = getPage(position)
        if (null == frag) {
            frag = getPagerItem(position).instantiate(pages.context, position)
        }
        return frag as Fragment
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = super.instantiateItem(container, position)
        if (item is Fragment) {
            holder.put(position, WeakReference(item))
        }
        return item
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return getPagerItem(position).title
    }

    fun getPage(position: Int): Fragment? {
        val weakRefItem = holder[position]
        return weakRefItem?.get()
    }

    fun getPagerItem(position: Int): FragPagerItem {
        return pages[position]
    }

    fun getTag(position: Int) : String {
        return getPagerItem(position).getTag()
    }
}