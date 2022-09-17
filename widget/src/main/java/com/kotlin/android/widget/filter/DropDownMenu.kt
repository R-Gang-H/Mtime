package com.kotlin.android.widget.filter

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.widget.R
import com.kotlin.android.widget.filter.adapter.MenuAdapter
import com.kotlin.android.widget.filter.view.FixedTabIndicator

/**
 * 筛选器
 */
class DropDownMenu : RelativeLayout, View.OnClickListener, FixedTabIndicator.OnItemClickListener {
    private var fixedTabIndicator: FixedTabIndicator? = null
    private var frameLayoutContainer: FrameLayout? = null
    private var currentView: View? = null
    private var dismissAnimation: Animation? = null
    private var occurAnimation: Animation? = null
    private var alphaDismissAnimation: Animation? = null
    private var alphaOccurAnimation: Animation? = null
    private var mMenuAdapter: MenuAdapter? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        setBackgroundColor(Color.WHITE)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    fun setFrameLayoutContainerView(frameLayoutContainer: FrameLayout?) {
        removeAllViews()
        this.frameLayoutContainer?.removeAllViews()

        //顶部筛选条
        fixedTabIndicator = FixedTabIndicator(context)
        fixedTabIndicator!!.id = R.id.fixedTabIndicator
        addView(fixedTabIndicator, -1, 50.dp)

        //添加展开页面
        val params = LayoutParams(
            -1, Resources.getSystem().displayMetrics.heightPixels - 50.dp - 44.dp
        )
        params.addRule(BELOW, R.id.fixedTabIndicator)
        this.frameLayoutContainer = frameLayoutContainer
        this.frameLayoutContainer!!.visibility = GONE

        initListener()
        initAnimation()
    }

    fun setMenuAdapter(adapter: MenuAdapter?) {
        verifyContainer()
        mMenuAdapter = adapter
        verifyMenuAdapter()

        //1.设置title
        fixedTabIndicator!!.setTitles(mMenuAdapter)

        //2.添加view
        setPositionView()
    }

    /**
     * 可以提供两种方式:
     * 1.缓存所有view,
     * 2.只保存当前view
     *
     *
     * 此处选择第二种
     */
    private fun setPositionView() {
        val count = mMenuAdapter!!.menuCount
        for (position in 0 until count) {
            setPositionView(
                position,
                findViewAtPosition(position),
                mMenuAdapter!!.getBottomMargin(position)
            )
        }
    }

    private fun findViewAtPosition(position: Int): View? {
        verifyContainer()
        var view = frameLayoutContainer!!.getChildAt(position)
        if (view == null) {
            view = mMenuAdapter!!.getView(position, frameLayoutContainer)
        }
        return view
    }

    private fun setPositionView(position: Int, view: View?, bottomMargin: Int) {
        verifyContainer()
        check(!(view == null || position > mMenuAdapter!!.menuCount || position < 0)) { "the view at $position cannot be null" }
        val params = FrameLayout.LayoutParams(-1, -2)
        //添加距离底部高度
        params.bottomMargin = bottomMargin
        frameLayoutContainer!!.addView(view, position, params)
        view.visibility = GONE
    }

    val isShowing: Boolean
        get() {
            verifyContainer()
            return frameLayoutContainer!!.isShown
        }
    val isClosed: Boolean
        get() = !isShowing

    fun close() {
        if (isClosed) {
            return
        }
        frameLayoutContainer!!.startAnimation(alphaDismissAnimation)
        fixedTabIndicator!!.resetCurrentPos()
        if (currentView != null) {
            currentView!!.startAnimation(dismissAnimation)
        }
    }

    fun setPositionIndicatorText(position: Int, text: String?) {
        verifyContainer()
        fixedTabIndicator!!.setPositionText(position, text)
    }

    fun setCurrentIndicatorText(text: String?) {
        verifyContainer()
        fixedTabIndicator!!.setCurrentText(text)
    }

    //=======================之上对外暴漏方法=======================================
    private fun initListener() {
        frameLayoutContainer!!.setOnClickListener(this)
        fixedTabIndicator!!.setOnItemClickListener(this)
    }

    //条目点击的操作
     var clickTabOperate: (() -> Unit)? = null

    override fun onClick(v: View) {
        clickTabOperate?.invoke()
        if (isShowing) {
            close()
        }
    }

    var tabCurrentPosition = 0
    override fun onItemClick(v: View?, position: Int, open: Boolean) {
        clickTabOperate?.invoke()
        tabCurrentPosition = position
        if (open) {
            close()
        } else {
            currentView = frameLayoutContainer!!.getChildAt(position)
            if (currentView == null) {
                return
            }
            frameLayoutContainer!!.getChildAt(fixedTabIndicator!!.lastIndicatorPosition).visibility =
                GONE
            frameLayoutContainer!!.getChildAt(position).visibility = VISIBLE
            if (isClosed) {
                frameLayoutContainer!!.visibility = VISIBLE
                frameLayoutContainer!!.startAnimation(alphaOccurAnimation)

                //可移出去,进行每次展出
                currentView!!.startAnimation(occurAnimation)
            }
        }
    }

    private fun initAnimation() {
        occurAnimation = AnimationUtils.loadAnimation(context, R.anim.top_in)
        val listener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation) {
                frameLayoutContainer!!.visibility = GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        }
        dismissAnimation = AnimationUtils.loadAnimation(context, R.anim.top_out)
        dismissAnimation?.setAnimationListener(listener)
        alphaDismissAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha_to_zero)
        alphaDismissAnimation?.duration = 300
        alphaDismissAnimation?.setAnimationListener(listener)
        alphaOccurAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha_to_one)
        alphaOccurAnimation?.duration = 300
    }

    private fun verifyMenuAdapter() {
        checkNotNull(mMenuAdapter) { "the menuAdapter is null" }
    }

    private fun verifyContainer() {
        checkNotNull(frameLayoutContainer) { "you must initiation setContentView() before" }
    }
}