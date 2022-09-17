package com.mtime.bussiness.main.widget.tab

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.kotlin.android.core.entity.PageFlag
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.marginBottom
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.i
import com.mtime.R

/**
 * TabBar 支持Icon变化、红点（含文本）、换肤、fragment管理
 *
 * Created on 2022/1/6.
 *
 * @author o.s
 */
class TabBar : FrameLayout, ITabBar {
    constructor(context: Context) : super(context) { initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { initView() }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) { initView() }

    private val tabBarBgHeight = 54.dp
    @IdRes
    private var container: Int? = null
    private var fragmentManager: FragmentManager? = null
    private var prePosition = -1

    private val mItems by lazy {
        ArrayList<ITabBarItem>()
    }

    private var bgDrawable: Drawable? = null

    private val tabBarView: LinearLayout by lazy {
        LinearLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.BOTTOM
            }
            gravity = Gravity.BOTTOM
            orientation = LinearLayout.HORIZONTAL
        }
    }

    private val bgView: ImageView by lazy {
        ImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, tabBarBgHeight).apply {
                gravity = Gravity.BOTTOM
            }
            if (bgDrawable != null) {
                background = bgDrawable
            } else {
                setBackground(
                    colorRes = R.color.color_ffffff,
                    cornerRadius = 12.dpF,
                    direction = Direction.LEFT_TOP or Direction.RIGHT_TOP,
                )
            }
        }
    }

    private val lineView: View by lazy {
        View(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0.5f.dp).apply {
                gravity = Gravity.BOTTOM
                bottomMargin = tabBarBgHeight
            }
            setBackgroundResource(R.color.color_f3f3f4)
        }
    }

    var action: ((position: Int) -> Unit)? = null

    override fun addItem(item: ITabBarItem): ITabBar {
        mItems.add(item)
        tabBarView.addView(item.view)
        item.view.setOnClickListener {
            val position = tabBarView.indexOfChild(it)
            if (prePosition != position) {
                currentPosition = position
                action?.invoke(position)
            }
        }
        return this
    }

    /**
     * 获取对应 [position] 的 Item
     */
    override fun getItem(position: Int): ITabBarItem? {
        return if (position in 0 until mItems.size) {
            mItems[position]
        } else {
            null
        }
    }

    /**
     * 初始化
     */
    override fun init(container: Int, fragmentManager: FragmentManager) {
        this.container = container
        this.fragmentManager = fragmentManager
    }

    /**
     * 当前选择位置从 0 开始
     */
    override var currentPosition: Int = 0
        set(value) {
            if (value in 0 until mItems.size) {
                if (mItems[value].fragment != null) {
                    field = value
                    mItems.forEachIndexed { index, item ->
                        if (index == value) {
                            item.select(true)
                            changeFragment(value, prePosition)
                            prePosition = value
                        } else {
                            item.select(false)
                        }
                    }
                }
            }
        }

//    /**
//     * 选择位置从 0 开始
//     */
//    override fun selectPosition(position: Int) {
//        if (position in 0 until mItems.size) {
//            mItems.forEachIndexed { index, item ->
//                if (index == position) {
//                    item.select(true)
//                    changeFragment(position, prePosition)
//                    prePosition = position
//                } else {
//                    item.select(false)
//                }
//            }
//        }
//    }

    /**
     * Item [ITabBarItem] 列表
     */
    override val items: List<ITabBarItem>
        get() = mItems

    /**
     * 显示红点
     */
    override fun showHotView(position: Int, isShow: Boolean, title: CharSequence) {
        getItem(position)?.showHotView(isShow, title)
    }

    /**
     * 应用页面标示（跳转）
     */
    override fun applyPageFlag(flag: PageFlag?) {
        "${javaClass.simpleName} applyPageFlag flag=$flag".i()
        val position = flag?.position ?: 0
        currentPosition = position
        flag?.apply {
            subFlag?.let {
                getItem(position)?.applyPageFlag(it)
            }
        }
    }

//    /**
//     * 应用皮肤
//     */
//    override fun applySkin(skin: String?) {
//        skin?.apply {
//            loadImage(
//                data = File(this)
//            ) {
//                bgDrawable = it
//                syncBg()
//            }
//        }
//    }

//    private fun syncBg() {
//        if (bgDrawable != null) {
//            bgView.background = bgDrawable
//        } else {
//            bgView.setBackground(
//                colorRes = R.color.color_f5f7f9,
//                cornerRadius = 12.dpF,
//                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP,
//            )
//        }
//    }

    private fun initView() {
        addView(bgView)
        addView(lineView)
        addView(tabBarView)
        translationZ = 3.dpF
    }

    /**
     * 调度Fragment
     */
    private fun changeFragment(position: Int, prePosition: Int) {
            fragmentManager?.beginTransaction()?.apply {
                // 解决重启/恢复的重叠问题
                mItems.forEachIndexed { index, item ->
                    if (index != position) {
                        item.fragment?.apply {
                            if (isAdded) {
                                hide(this)
                            }
                        }
                    }
                }
                getItem(position)?.apply {
                    fragment?.let { fragment ->
                        if (fragment.isAdded) {
                            show(fragment)
                        } else {
                            container?.let {
                                add(it, fragment, tag)
                            }
                        }
                    }
                }
                commitAllowingStateLoss()
            }

    }
}