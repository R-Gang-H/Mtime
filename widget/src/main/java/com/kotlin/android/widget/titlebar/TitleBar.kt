package com.kotlin.android.widget.titlebar

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.text.util.Linkify
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.ContentFrameLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.core.ITitleBar
import com.kotlin.android.core.ITitleBarOfFragment
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.immersive.findContentFrameLayout
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.R
import com.kotlin.android.widget.titlebar.item.ButtonItem
import com.kotlin.android.widget.titlebar.item.TitleView
import java.io.File

/**
 * 主题样式标准/沉浸式
 */
enum class ThemeStyle {
    /**
     * 标准标题，无穿透效果
     */
    STANDARD,

    /**
     * 状态栏，线性排列
     */
    STATUS_BAR,

    /**
     * 标题全屏，线性排列
     */
    STANDARD_STATUS_BAR,

    /**
     * 标题全屏，附带沉浸式穿透效果
     */
    IMMERSIVE
}

/**
 * 标题状态正常/反色
 */
enum class State {
    /**
     * 标准色状态
     */
    NORMAL,

    /**
     * 反色状态
     */
    REVERSE
}

/**
 * 标题栏icon Item
 *
 * 例如：
 * class MyImageView : ImageView, IButtonItem {
 *      override val view: View
 *          get() = this
 *
 *      //是否显示红点
 *      override fun showRedPoint(isShow: Boolean) {
 *          ...
 *      }
 *
 *      override fun setState(state: State) {
 *          ...
 *      }
 * }
 */
interface IButtonItem {
    /**
     * 当前View对象本身
     * 重写：    override val view: View
     *              get() = this
     */
    val view: View

    /**
     * 通知Item更新状态
     */
    fun setState(state: State)

    /**
     * 通知小红点
     */
    fun showRedPoint(isShow: Boolean, title: CharSequence? = null)
}

/**
 * 标题栏：支持沉浸式，支持自动动态刷新，反转标题栏和状态栏，支持小红点显示，支持动态更新icon，支持文本左右标示Drawable，支持换肤。
 * 支持添加自定义 [View] [IButtonItem] 等。
 *
 * 使用方法：
 * 方式一：xml布局中直接使用。
 * 方式二：【推荐】使用 [TitleBarManager.with] 直接内嵌 Activity、Fragment 标题栏，无需应用布局。
 *
 * Created on 2019-05-29.
 *
 * @author o.s
 */
class TitleBar : FrameLayout, ITitleBar, ITitleBarOfFragment {

    companion object {
        val iconWidth = 36.dp
        val iconMargin = 2.dp
    }

    /**
     * 定义标题栏标准高度 44dp
     */
    private val titleBarHeight = 44.dp
    private val buttonBarLayoutMarginStart = 4.dp
    private val buttonBarLayoutMarginEnd = 8.dp
    private val centerViewMargin = 48.dp
    private val startOffset = 10.dp
    private val targetOffset = statusBarHeight + titleBarHeight

    /**
     * TitleBar 主题样式
     */
    private var themeStyle = ThemeStyle.STANDARD
    private var state = State.NORMAL

    private val startViews by lazy { ArrayList<IButtonItem>() }
    private val endViews by lazy { ArrayList<IButtonItem>() }
    private var startLayout: LinearLayout? = null
    private var centerLayout: FrameLayout? = null
    private var endLayout: LinearLayout? = null

    private var mContentFrameLayout: ContentFrameLayout? = null

    var titleView: TitleView? = null

    private var titleTextSize = 16F // SP
    private var iconTextSize = 14F // SP

    private var title: CharSequence? = ""
    private var alwaysShow: Boolean = false
    private var bgColorRes = R.color.color_ffffff
    private var reverseBgColorRes = R.color.color_00000000
    private var bgDrawable: Drawable? = null

    /**
     * 初始化 [State.NORMAL] 背景颜色
     */
    fun initBackgroundColor(
        @ColorRes normalBgColorRes: Int? = null,
    ): TitleBar {
        normalBgColorRes?.apply {
            bgColorRes = this
        }
        return this
    }

    /**
     * 初始化标题栏 左右按钮 距离屏幕的左右边距
     *
     * 注意：icon 默认宽度为36dp 且不可调整，对应设计稿时需要注意设计的宽度的平均转换，如设计稿宽度30dp，对应36dp，则左右要减去3dp
     */
    fun initMargin(
        start: Int = buttonBarLayoutMarginStart,
        end: Int = buttonBarLayoutMarginEnd
    ) {
        startLayout?.marginStart = start
        endLayout?.marginEnd = end
    }

    /**
     * 自动切换StatusBar主题
     */
    private var auto = true

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        // fitsSystemWindows = false 不给系统状态栏预留空间
//        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
//        children.forEach {
//            it.fitsSystemWindows = fitsSystemWindows
//        }
//    }

    fun init() {
//        translationZ = 1.dpF
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleBarHeight)
//        setBackground(
//            colorRes = R.color.color_663a4a5e,
////            color = Color.GREEN,
//        )
        addCenterLayout()
        addStartLayout()
        addEndLayout()
    }

    /**
     * 将标题栏内嵌到 Activity、Fragment
     * 1，应用 Activity 内嵌时，遍历 Activity 中 Window 的 DecorView 找到 [ContentFrameLayout] 进行内嵌。
     * 2，应用 Fragment 内嵌时，需要明确指定 [fragment]，在适当的时机 [com.kotlin.android.core.BaseVMFragment.initTitleBar] 获取 [Fragment.getView] 进行内嵌。
     *
     * 注意：在xml文件中手动设置布局的，不要调用该方法，否则会有两个标题栏。
     */
    fun of(themeStyle: ThemeStyle, fragment: Fragment? = null): TitleBar {
        this.removeFromParent()
        this.themeStyle = themeStyle
        refreshLayout()
        if (fragment != null) {
//            ofFragment(fragment)
        } else {
            ofActivity()
        }
        return this
    }

    /**
     * 创建 Fragment 视图容器
     * 由 [com.kotlin.android.core.BaseVMFragment.initTitleBar] 在恰当的时机调用
     */
    override fun createViewOfFragment(root: View?): View {
        val newLayout: View
        when (themeStyle) {
            ThemeStyle.IMMERSIVE -> {
                newLayout = createFrameLayout()
                root?.let {
                    newLayout.addView(it)
                }
                newLayout.addView(this@TitleBar)
            }
            else -> {
                newLayout = createFrameLayout()
                root?.let {
                    newLayout.addView(it)
                }
                newLayout.addView(this@TitleBar)
                // 线性排列，root marginTop 一个 titleBar 的高度
                root?.marginTop = layoutParams.height
//                refreshLayoutOfFragment(root)
            }
        }
        return newLayout
    }

    fun setThemeStyle(themeStyle: ThemeStyle) {
        this.themeStyle = themeStyle
        refreshLayout()
    }

    private fun refreshLayout() {
        when (themeStyle) {
            ThemeStyle.IMMERSIVE -> fullscreenLayout()
            ThemeStyle.STANDARD -> standardLayout()
            ThemeStyle.STANDARD_STATUS_BAR -> fullscreenLayout()
            ThemeStyle.STATUS_BAR -> statusBarLayout()
        }
    }

    private fun fullscreenLayout() {
        layoutParams.height = titleBarHeight + statusBarHeight
    }

    private fun standardLayout() {
        layoutParams.height = titleBarHeight
    }

    private fun statusBarLayout() {
        layoutParams.height = statusBarHeight
    }

    private fun refreshLayoutOfFragment(root: View?) {
        when (themeStyle) {
            ThemeStyle.STANDARD -> root?.marginTop = titleBarHeight
            ThemeStyle.STANDARD_STATUS_BAR -> root?.marginTop = titleBarHeight + statusBarHeight
            ThemeStyle.STATUS_BAR -> root?.marginTop = statusBarHeight
            else -> {}
        }
    }

    private fun addStartLayout() {
        startLayout = LinearLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, titleBarHeight).apply {
                gravity = Gravity.START or Gravity.BOTTOM
                marginStart = buttonBarLayoutMarginStart
            }
            gravity = Gravity.CENTER_VERTICAL
//            addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//                " startLayout >>> old($oldLeft, $oldTop, $oldRight, $oldBottom) -> new($left, $top, $right, $bottom)".w()
//            }
        }
        addView(startLayout)
    }

    private fun addCenterLayout() {
        centerLayout = FrameLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, titleBarHeight).apply {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                marginEnd = centerViewMargin
                marginStart = centerViewMargin
            }
//            addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//                "centerLayout >>> old($oldLeft, $oldTop, $oldRight, $oldBottom) -> new($left, $top, $right, $bottom)".e()
//            }
        }
        addView(centerLayout)
    }

    private fun addEndLayout() {
        endLayout = LinearLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, titleBarHeight).apply {
                gravity = Gravity.END or Gravity.BOTTOM
                marginEnd = buttonBarLayoutMarginEnd
            }
            gravity = Gravity.CENTER_VERTICAL
            addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//                "   endLayout >>> old($oldLeft, $oldTop, $oldRight, $oldBottom) -> new($left, $top, $right, $bottom)".i()
                startLayout?.widthValue = left - buttonBarLayoutMarginEnd
            }
        }
        addView(endLayout)
    }

    /**
     * 允许标题阴影（即：分割线）
     *
     * 注意，只有在normal状态下才能表现出来（此时有背景颜色）
     */
    fun allowShadow(z: Float = 1.dpF): TitleBar {
        translationZ = z
        return this
    }
    /**
     * 自动同步状态栏
     *
     * 注意：调用顺序，在 [setState] 之前
     */
    fun autoSyncStatusBar(isAuto: Boolean): TitleBar {
        auto = isAuto
        return this
    }

    /**
     * 支持外部 [view] 滚动时，动态更新、切换标题栏状态显示。
     * 滚动监听内部调用 [change] 方法。
     *
     * 目标可滚动View形式可扩展。
     */
    fun target(view: View?): TitleBar {
        when (view) {
            is RecyclerView -> {
                view.setOnScrollChangeListener { v, _, _, _, _ ->
                    val offset = (v as RecyclerView).computeVerticalScrollOffset()
                    change(offset)
                }
            }
            is NestedScrollView,
            is ScrollView -> {
                view.setOnScrollChangeListener { _, _, y, _, _ ->
                    change(y)
                }
            }
        }
        return this
    }

    /**
     * 支持添加中部自定义View
     */
    fun addCenterView(
        view: View,
    ): TitleBar {
        alwaysShow = true
        centerLayout?.removeAllViews()
        centerLayout?.isVisible = true

        view.layoutParams = LayoutParams(
            view.layoutParams ?: ViewGroup.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)).apply {
            gravity = Gravity.CENTER
        }

        centerLayout?.addView(view)
        return this
    }

    /**
     * 支持添加左右侧 [IButtonItem] 视图
     */
    fun addItemView(
        item: IButtonItem,
        isReversed: Boolean = false,
        isReset: Boolean = false,
    ): TitleBar {
        var views = startViews
        var layout = startLayout
        if (isReversed) {
            views = endViews
            layout = endLayout
        }

        if (isReset) {
            layout?.removeAllViews()
            views.clear()
        }

        if (isReversed) {
            views.add(0, item)
            layout?.addView(item.view, 0)
        } else {
            views.add(item)
            layout?.addView(item.view)
        }
        return this
    }

    /**
     * 设置标题：
     * 支持 [title] 标题文字、[titleRes] 标题资源、[colorRes] 字体颜色、[reverseColorRes] 字体反色、
     * [textSize] 标题大小(sp)、[isBold] 是否粗体、[gravity] 标题位置、[alwaysShow] 标题是否一直显示、
     * [link] 超链接文本 [touchClick] 触摸点击事件处理（包含左、中、右）、 [click] 标题点击事件、
     * [startDrawable] [reverseStartDrawable] 左侧icon、[endDrawable] [reverseEndDrawable] 右侧icon。
     */
    fun setTitle(
        title: CharSequence? = null,
        @StringRes titleRes: Int? = null,
        @ColorRes colorRes: Int = R.color.color_1d2736,
        @ColorRes reverseColorRes: Int = R.color.color_ffffff,
        textSize: Float = titleTextSize, // sp
        isBold: Boolean = true,
        gravity: Int = Gravity.CENTER,
        alwaysShow: Boolean = false,
        titlePaddingStart: Int = 0,
        titlePaddingEnd: Int = 0,
        drawablePadding: Int = 0,
        startDrawable: Drawable? = null,
        reverseStartDrawable: Drawable? = null,
        endDrawable: Drawable? = null,
        reverseEndDrawable: Drawable? = null,
        link: String? = null,
        touchClick: ((data: TextTouchListener.Data) -> Unit)? = null,
        click: ((v: View) -> Unit)? = null
    ): TitleBar {
        val safeTitle = title ?: if (titleRes != null) getString(titleRes) else ""
        this.title = safeTitle
        this.alwaysShow = alwaysShow
        if (alwaysShow) {
            centerLayout?.isVisible = true
        }
        centerLayout?.apply {
            removeAllViews()
            titleView = TitleView(context).apply {
                layoutParams =
                    FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT).apply {
                        this.gravity = gravity
                        maxWidth = screenWidth - centerViewMargin * 2
                    }
                this.gravity = Gravity.CENTER
                setPadding(titlePaddingStart, 0, titlePaddingEnd, 0)
                setSingleLine()
                ellipsize = TextUtils.TruncateAt.END
                autoLinkMask = Linkify.ALL
                (layoutParams as? LayoutParams)?.gravity = gravity
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
                setTextColor(colorRes, reverseColorRes)
                setStartDrawable(startDrawable ?: reverseStartDrawable, reverseStartDrawable ?: startDrawable)
                setEndDrawable(endDrawable ?: reverseEndDrawable, reverseEndDrawable ?: endDrawable)
                if (drawablePadding > 0) {
                    compoundDrawablePadding = drawablePadding
                }
                typeface = if (isBold) {
                    Typeface.defaultFromStyle(Typeface.BOLD)
                } else {
                    Typeface.defaultFromStyle(Typeface.NORMAL)
                }
                touchClick?.let {
                    setOnTouchListener(
                        TextTouchListener(
                            context = context,
                            textView = this,
                            link = link,
                            action = it
                        )
                    )
                }
//                compoundDrawablePadding = 1.dp
                onClick {
                    click?.invoke(it)
                }
//                this.text = safeTitle // displayTitle()
                setState(state)
            }
            addView(titleView)
            displayTitle()
        }
        return this
    }

    /**
     * 添加按钮：icon和文字二选一，优先icon。
     *
     *      ----------------------------------------------------------
     *      | start                  center                      end | 标题位置区域
     *      ----------------------------------------------------------
     *      | back | close |     ... title ... | save | share | more | icon图标按钮
     *      ----------------------------------------------------------
     *      |  0   |   1   |           |           4  |   3   |  2   | addItem顺序
     *      ----------------------------------------------------------
     *      |  0   |   1   |           |           0  |   1   |  2   | index下标
     *      ----------------------------------------------------------
     *      | false (startViews)       |             (endViews) true | isReversed
     *      ----------------------------------------------------------
     *
     * icon图标按钮：高度44dp（icon垂直局中） icon高度36dp 高度可指定，如：文本高度设置28dp等，可指定文本背景。
     * addItem顺序：依次添加顺序
     * [index] 下标：即 [startViews] [endViews] 的下标，用于 [findButtonItemByIndex] [updateRedPoint] 等 updateXxx 方法。
     * [isReversed] 按钮位置：默认false添加左侧按钮 [startViews]，true为反向添加右侧按钮 [endViews]。
     * [isReset] 重置按钮，根据 [isReversed] 指定的位置进行重置一侧的按钮。
     *
     * 1，设置icon：[drawableRes] icon资源、[reverseDrawableRes] 反色icon资源。
     *
     * 2，设置文字：[title] 标题文字、[titleRes] 标题资源、[colorRes] 字体颜色、[reverseColorRes] 字体反色、
     * [colorState] 颜色选择器、[reverseColorState] 发色选择器、[textSize] 标题大小(sp)、[isBold] 是否粗体。
     * [titleHeight] 文本高度、[titlePaddingStart] [titlePaddingEnd] 文本padding、[drawablePadding] 标示文字padding
     * [startDrawable] [reverseStartDrawable] 文本前标示icon、[endDrawable] [reverseEndDrawable] 文本后标示icon、
     * [bgDrawable] [reverseBgDrawable] 文本背景、
     *
     * 3，[click] 标题点击事件。
     *
     * 注意：所有的icon，使用规格命名: ic_title_bar_36_xxx，其中36为icon宽高dp，不符合规则的可以对照现有方案转换（查看 ic_title_bar_36_xxx.xml）。
     */
    fun addItem(
        isReversed: Boolean = false,
        isReset: Boolean = false,

        @DrawableRes drawableRes: Int? = null,
        @DrawableRes reverseDrawableRes: Int? = null,
        title: CharSequence? = null,
        @StringRes titleRes: Int? = null,
        @ColorRes colorRes: Int = R.color.color_30333b,
        @ColorRes reverseColorRes: Int = R.color.color_ffffff,
        colorState: ColorStateList? = null,
        reverseColorState: ColorStateList? = null,
        textSize: Float = iconTextSize, // sp
        isBold: Boolean = false,
        isSingleLine: Boolean = true,
        isEllipsizeEnd: Boolean = false,
        gravity: Int = Gravity.CENTER,

        maxWidth: Int = 0,
        titleWidth: Int = 0,
        titleHeight: Int = iconWidth,
        titleMarginStart: Int = 0,
        titleMarginEnd: Int = 0,
        titlePaddingStart: Int = 0,
        titlePaddingEnd: Int = 0,
        titlePaddingBottom: Int = 0,
        titlePaddingTop: Int = 0,
        drawablePadding: Int = 0,

        startDrawable: Drawable? = null,
        reverseStartDrawable: Drawable? = null,
        endDrawable: Drawable? = null,
        reverseEndDrawable: Drawable? = null,
        bgDrawable: Drawable? = null,
        reverseBgDrawable: Drawable? = null,

        click: ((v: View) -> Unit)? = null
    ): TitleBar {
        val buttonItem = ButtonItem(context).apply {
            update(
                drawableRes = drawableRes,
                reverseDrawableRes = reverseDrawableRes,
                title = title,
                titleRes = titleRes,
                colorRes = colorRes,
                reverseColorRes = reverseColorRes,
                colorState = colorState,
                reverseColorState = reverseColorState,
                textSize = textSize,
                isBold = isBold,
                isSingleLine = isSingleLine,
                isEllipsizeEnd = isEllipsizeEnd,
                gravity = gravity,

                maxWidth = maxWidth,
                titleWidth = titleWidth,
                titleHeight = titleHeight,
                titleMarginStart = titleMarginStart,
                titleMarginEnd = titleMarginEnd,
                titlePaddingStart = titlePaddingStart,
                titlePaddingEnd = titlePaddingEnd,
                titlePaddingBottom = titlePaddingBottom,
                titlePaddingTop = titlePaddingTop,
                drawablePadding = drawablePadding,

                startDrawable = startDrawable,
                reverseStartDrawable = reverseStartDrawable,
                endDrawable = endDrawable,
                reverseEndDrawable = reverseEndDrawable,
                bgDrawable = bgDrawable,
                reverseBgDrawable = reverseBgDrawable,

                click = click,
            )
            setState(state)
        }

        var views = startViews
        var layout = startLayout
        if (isReversed) {
            views = endViews
            layout = endLayout
        }

        if (isReset) {
            layout?.removeAllViews()
            views.clear()
        }

        if (isReversed) {
            views.add(0, buttonItem)
            layout?.addView(buttonItem, 0)
        } else {
            views.add(buttonItem)
            layout?.addView(buttonItem)
        }

        return this
    }

    /**
     * 更新按钮：icon和文字二选一，优先icon。
     *
     *      ----------------------------------------------------------
     *      | start                  center                      end | 标题位置区域
     *      ----------------------------------------------------------
     *      | back | close |     ... title ... | save | share | more | icon图标按钮
     *      ----------------------------------------------------------
     *      |  0   |   1   |           |           4  |   3   |  2   | addItem顺序
     *      ----------------------------------------------------------
     *      |  0   |   1   |           |           0  |   1   |  2   | index下标
     *      ----------------------------------------------------------
     *      | false (startViews)       |             (endViews) true | isReversed
     *      ----------------------------------------------------------
     *
     * icon图标按钮：高度44dp（icon垂直局中） icon高度36dp 高度可指定，如：文本高度设置28dp等，可指定文本背景。
     * addItem顺序：依次添加顺序
     * [index] 下标：即 [startViews] [endViews] 的下标，用于 [findButtonItemByIndex] [updateRedPoint] 等 updateXxx 方法。
     * [isReversed] 按钮位置：默认false添加左侧按钮 [startViews]，true为反向添加右侧按钮 [endViews]。
     * [isReset] 重置按钮，根据 [isReversed] 指定的位置进行重置一侧的按钮。
     *
     * 1，设置icon：[drawableRes] icon资源、[reverseDrawableRes] 反色icon资源。
     *
     * 2，设置文字：[title] 标题文字、[titleRes] 标题资源、[colorRes] 字体颜色、[reverseColorRes] 字体反色、
     * [colorState] 颜色选择器、[reverseColorState] 发色选择器、[textSize] 标题大小(sp)、[isBold] 是否粗体。
     * [titleHeight] 文本高度、[titlePaddingStart] [titlePaddingEnd] 文本padding、[drawablePadding] 标示文字padding
     * [startDrawable] [reverseStartDrawable] 文本前标示icon、[endDrawable] [reverseEndDrawable] 文本后标示icon、
     * [bgDrawable] [reverseBgDrawable] 文本背景、
     *
     * 3，[click] 标题点击事件。
     */
    fun updateItem(
        index: Int,
        isReversed: Boolean = false,

        @DrawableRes drawableRes: Int? = null,
        @DrawableRes reverseDrawableRes: Int? = null,
        title: CharSequence? = null,
        @StringRes titleRes: Int? = null,
        @ColorRes colorRes: Int = R.color.color_30333b,
        @ColorRes reverseColorRes: Int = R.color.color_ffffff,
        colorState: ColorStateList? = null,
        reverseColorState: ColorStateList? = null,
        textSize: Float = iconTextSize, // sp
        isBold: Boolean = false,
        isSingleLine: Boolean = true,
        isEllipsizeEnd: Boolean = false,
        gravity: Int = Gravity.CENTER,

        maxWidth: Int = 0,
        titleWidth: Int = 0,
        titleHeight: Int = iconWidth,
        titleMarginStart: Int = 0,
        titleMarginEnd: Int = 0,
        titlePaddingStart: Int = 0,
        titlePaddingEnd: Int = 0,
        drawablePadding: Int = 0,

        startDrawable: Drawable? = null,
        reverseStartDrawable: Drawable? = null,
        endDrawable: Drawable? = null,
        reverseEndDrawable: Drawable? = null,
        bgDrawable: Drawable? = null,
        reverseBgDrawable: Drawable? = null,

        click: ((v: View) -> Unit)? = null
    ): TitleBar {
        val item = findButtonItemByIndex(index, isReversed)
        val buttonItem: ButtonItem?
        if (item == null) {
            // 如果需要更新的item不存在，则在index对应位置添加一个新的item。
            buttonItem = ButtonItem(context)

            var views = startViews
            var layout = startLayout
            if (isReversed) {
                views = endViews
                layout = endLayout
            }

            if (isReversed) {
                views.add(index, buttonItem)
                layout?.addView(buttonItem, index)
            } else {
                views.add(buttonItem)
                layout?.addView(buttonItem)
            }
        } else {
            // 如果存在item，但是不是 ButtonItem 类型，则什么都不做。
            buttonItem = (item as? ButtonItem)
        }

        buttonItem?.update(
            drawableRes = drawableRes,
            reverseDrawableRes = reverseDrawableRes,
            title = title,
            titleRes = titleRes,
            colorRes = colorRes,
            reverseColorRes = reverseColorRes,
            colorState = colorState,
            reverseColorState = reverseColorState,
            textSize = textSize,
            isBold = isBold,
            isSingleLine = isSingleLine,
            isEllipsizeEnd = isEllipsizeEnd,
            gravity = gravity,

            maxWidth = maxWidth,
            titleWidth = titleWidth,
            titleHeight = titleHeight,
            titleMarginStart = titleMarginStart,
            titleMarginEnd = titleMarginEnd,
            titlePaddingStart = titlePaddingStart,
            titlePaddingEnd = titlePaddingEnd,
            drawablePadding = drawablePadding,

            startDrawable = startDrawable,
            reverseStartDrawable = reverseStartDrawable,
            endDrawable = endDrawable,
            reverseEndDrawable = reverseEndDrawable,
            bgDrawable = bgDrawable,
            reverseBgDrawable = reverseBgDrawable,

            click = click,
        )
        buttonItem?.setState(state)

        return this
    }

    /**
     * 仅更新Item文本
     */
    fun updateItemTitle(
        index: Int,
        isReversed: Boolean = false,
        title: CharSequence? = null,
        @StringRes titleRes: Int? = null,
    ): TitleBar {
        (findButtonItemByIndex(index, isReversed) as? ButtonItem)?.apply {
            updateTitle(title, titleRes)
        }
        return this
    }

    /**
     * 更新Item的红点：
     *
     *      ----------------------------------------------------------
     *      | start                  center                      end | 标题位置区域
     *      ----------------------------------------------------------
     *      | back | close |     ... title ... | save | share | more | icon图标按钮
     *      ----------------------------------------------------------
     *      |  0   |   1   |           |           4  |   3   |  2   | addItem顺序
     *      ----------------------------------------------------------
     *      |  0   |   1   |           |           0  |   1   |  2   | index下标
     *      ----------------------------------------------------------
     *      | false (startViews)       |             (endViews) true | isReversed
     *      ----------------------------------------------------------
     *
     * icon图标按钮：高度44dp（icon垂直局中） icon高度36dp 高度可指定，如：文本高度设置28dp等，可指定文本背景。
     * addItem顺序：依次添加顺序
     * [index] 下标：即 [startViews] [endViews] 的下标，用于 [findButtonItemByIndex] [updateRedPoint] 等 updateXxx 方法。
     * [isReversed] 按钮位置：默认false添加左侧按钮 [startViews]，true为反向添加右侧按钮 [endViews]。
     * [title] 红点文本
     */
    fun updateRedPoint(
        index: Int = 0,
        isReversed: Boolean,
        isShow: Boolean,
        title: CharSequence? = null,
    ) {
        findButtonItemByIndex(index, isReversed)?.showRedPoint(isShow = isShow, title = title)
    }

    /**
     * 更新Item是否可用：
     *
     *      ----------------------------------------------------------
     *      | start                  center                      end | 标题位置区域
     *      ----------------------------------------------------------
     *      | back | close |     ... title ... | save | share | more | icon图标按钮
     *      ----------------------------------------------------------
     *      |  0   |   1   |           |           4  |   3   |  2   | addItem顺序
     *      ----------------------------------------------------------
     *      |  0   |   1   |           |           0  |   1   |  2   | index下标
     *      ----------------------------------------------------------
     *      | false (startViews)       |             (endViews) true | isReversed
     *      ----------------------------------------------------------
     *
     * icon图标按钮：高度44dp（icon垂直局中） icon高度36dp 高度可指定，如：文本高度设置28dp等，可指定文本背景。
     * addItem顺序：依次添加顺序
     * [index] 下标：即 [startViews] [endViews] 的下标，用于 [findButtonItemByIndex] [updateRedPoint] 等 updateXxx 方法。
     * [isReversed] 按钮位置：默认false添加左侧按钮 [startViews]，true为反向添加右侧按钮 [endViews]。
     */
    fun updateEnable(
        index: Int = 0,
        isReversed: Boolean,
        isEnabled: Boolean
    ) {
        findButtonItemByIndex(index, isReversed)?.view?.isEnabled = isEnabled
    }

    /**
     * 更新Item的可见性：
     *
     *      ----------------------------------------------------------
     *      | start                  center                      end | 标题位置区域
     *      ----------------------------------------------------------
     *      | back | close |     ... title ... | save | share | more | icon图标按钮
     *      ----------------------------------------------------------
     *      |  0   |   1   |           |           4  |   3   |  2   | addItem顺序
     *      ----------------------------------------------------------
     *      |  0   |   1   |           |           0  |   1   |  2   | index下标
     *      ----------------------------------------------------------
     *      | false (startViews)       |             (endViews) true | isReversed
     *      ----------------------------------------------------------
     *
     * icon图标按钮：高度44dp（icon垂直局中） icon高度36dp 高度可指定，如：文本高度设置28dp等，可指定文本背景。
     * addItem顺序：依次添加顺序
     * [index] 下标：即 [startViews] [endViews] 的下标，用于 [findButtonItemByIndex] [updateRedPoint] 等 updateXxx 方法。
     * [isReversed] 按钮位置：默认false添加左侧按钮 [startViews]，true为反向添加右侧按钮 [endViews]。
     */
    fun updateVisibility(
        index: Int = 0,
        isReversed: Boolean,
        isShow: Boolean
    ) {
        findButtonItemByIndex(index, isReversed)?.view?.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    private fun findButtonItemByIndex(index: Int = 0, isReversed: Boolean): IButtonItem? {
        val views = if (isReversed) {
            endViews
        } else {
            startViews
        }
        val size = views.size
        return if (index in 0 until size) {
            views[index]
        } else {
            "TitleBar IButtonItem List size=$size but index=$index".e()
            null
        }
    }

    fun change(offset: Int) {
        when {
            offset < startOffset -> changeReverse()
            offset >= targetOffset -> changeNormal()
            offset >= startOffset -> changeNormal(offset / targetOffset.toFloat())
        }
    }

    private fun changeReverse() {
        if (this.state == State.REVERSE) {
            return
        }
        setState(State.REVERSE)
    }

    private fun changeNormal(alpha: Float = 1F) {
        if (this.alpha == alpha) {
            if (this.state == State.NORMAL) {
                return
            }
            displayTitle()
            setState(State.NORMAL)
            return
        }
        this.alpha = alpha
        displayTitle()
        if (this.state == State.NORMAL) {
            return
        }
        setState(State.NORMAL)
    }

    fun setState(state: State): TitleBar {
        this.state = state
        when (state) {
            State.REVERSE -> stateReverse()
            State.NORMAL -> stateNormal()
        }
        return this
    }

    private fun displayTitle() {
        titleView?.displayTitle(title, alpha > 0.7)
    }

    private fun stateReverse() {
        setBackgroundResource(reverseBgColorRes)
        if (!alwaysShow) {
            centerLayout?.isVisible = false
        }
        alpha = 1F

        startViews.forEach {
            it.setState(state)
        }
        endViews.forEach {
            it.setState(state)
        }
        titleView?.setState(state)

        if (auto) {
            setStatusBar(false)
        }
    }

    private fun stateNormal() {
        if (themeStyle == ThemeStyle.STATUS_BAR) {
            // 状态栏模式下不设置换肤背景
            bgDrawable = null
        }
        if (bgDrawable != null) {
            background = bgDrawable
        } else {
            setBackgroundResource(bgColorRes)
        }
//        if (!alwaysShow) {
//        }
        centerLayout?.isVisible = true

        startViews.forEach {
            it.setState(state)
        }
        endViews.forEach {
            it.setState(state)
        }
        titleView?.setState(state)

        if (auto) {
            setStatusBar(true)
        }
    }

    /**
     * 设置状态栏样式，
     */
    private fun setStatusBar(isDarkFont: Boolean, auto: Boolean = true) {
        this.auto = auto
        val activity = context
        if (activity is Activity) {
            activity.immersive().statusBarDarkFont(isDarkFont)
        }
    }

    /**
     * 同步状态栏样式
     */
    override fun syncStatusBar() {
        if (auto) {
            val activity = context
            if (activity is Activity) {
                activity.immersive().statusBarDarkFont(state == State.NORMAL)
            }
        }
    }

    /**
     * 应用皮肤
     */
    override fun applySkin(skin: String?) {
        skin?.apply {
            loadImage(
                data = File(this)
            ) {
                bgDrawable = it
                setState(state)
            }
        }
    }

    /**
     * 应用 Activity 内嵌时，遍历 Activity 中 Window 的 DecorView 找到 [ContentFrameLayout] 进行内嵌。
     */
    private fun ofActivity() {
        mContentFrameLayout = (context as? Activity)?.window?.decorView.findContentFrameLayout()
        when (themeStyle) {
            ThemeStyle.IMMERSIVE -> {
                mContentFrameLayout?.addView(this)
            }
            else -> {
                val xmlRootLayout = mContentFrameLayout?.getChildAt(0)
                if (xmlRootLayout == null) {
                    showToast("TitleBar xml root is null #ofActivity")
                    return
                }
                xmlRootLayout.removeFromParent()
                val newContentLayout = createContentLayout()
                newContentLayout.addView(this)
                newContentLayout.addView(xmlRootLayout)

                mContentFrameLayout?.addView(newContentLayout)
            }
        }
    }

    /**
     * 应用 Fragment 内嵌时，需要明确指定 [fragment]，在适当的时机获取 [Fragment.getView] 进行内嵌。
     */
    private fun ofFragment(fragment: Fragment) {
        val xmlRootLayout = fragment.view
        val xmlParent = xmlRootLayout?.parent
        (xmlParent as? ViewGroup)?.apply {
            val index = indexOfChild(xmlRootLayout)
            val lp = xmlRootLayout.layoutParams
            when (themeStyle) {
                ThemeStyle.IMMERSIVE -> {
                    val newLayout = createFrameLayout()
                    newLayout.layoutParams = lp

                    xmlParent.removeViewAt(index)
                    xmlParent.addView(newLayout, index)

                    newLayout.addView(xmlRootLayout)
                    newLayout.addView(this@TitleBar)
                }
                else -> {
                    val newLayout = createLinearLayout()
                    newLayout.layoutParams = lp

                    xmlParent.removeViewAt(index)
                    xmlParent.addView(newLayout, index)

                    newLayout.addView(this@TitleBar)
                    newLayout.addView(xmlRootLayout)
                }
            }
        }
    }

    /**
     * 需要时创建新的内容布局（作用于Activity），用于线性排列 [TitleBar] 标题栏和已有的 xml 布局
     * 注意：在代码中动态管理标题栏时使用，前提条件 [ThemeStyle.STANDARD]
     */
    private fun createContentLayout(): LinearLayout {
        return LinearLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            }
            orientation = LinearLayout.VERTICAL
        }
    }

    /**
     * 创建 Fragment 沉浸式布局容器。
     * 前提条件 [ThemeStyle.IMMERSIVE]
     */
    private fun createFrameLayout(): FrameLayout {
        return FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    /**
     * 创建 Fragment 线性布局容器。
     * 前提条件 [ThemeStyle.STANDARD]
     */
    private fun createLinearLayout(): LinearLayout {
        return LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
        }
    }
}