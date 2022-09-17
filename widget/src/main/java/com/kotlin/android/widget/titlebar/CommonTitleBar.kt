package com.kotlin.android.widget.titlebar

import android.app.Activity
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.widget.R
import java.util.*

/**
 * Created by zl
 * 通用的title
 *
 * @Deprecated
 * @see com.kotlin.android.widget.titlebar.TitleBarManager
 * @see com.kotlin.android.widget.titlebar.TitleBar
 */
@Deprecated(message = "替代", replaceWith = ReplaceWith("com.kotlin.android.widget.titlebar.TitleBarManager"))
class CommonTitleBar {
    private val mList = LinkedList<Activity>() //activity存入list中，每次关闭页面时删除最上面的activity
    private lateinit var view: View //整个标题栏view

    private var isLeftIconVisible = View.VISIBLE //默认左边的icon可见
    private var mTitleClickListener: View.OnClickListener? = null
    private var mRightClickListener: View.OnClickListener? = null
    private var mLeftClickListener: View.OnClickListener? = null
    private var mLeftSecondClickListener: View.OnClickListener? = null

    /**
     * 必须调用
     * isSystemSync SystemUI背景色是否要与TitleBar背景色保持一致
     */
    fun init(activity: Activity, isSystemSync: Boolean = false): CommonTitleBar {
        mList.add(activity)
        if (isSystemSync) {
            createAndBindView()
        } else {
            bindViewToActivityRoot()
        }
        return this
    }

    private fun createAndBindView() {
        val group = mList.last.window.decorView as ViewGroup
        val mParent = group.getChildAt(0) as ViewGroup
        view = LayoutInflater.from(mList.last).inflate(bindLayoutId(), mParent, false)
//                <!--        SystemUI高度为50px，TitleBar高度为88px，加起来69dp-->
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(69))
        view.layoutParams = lp
        mParent.addView(view, 0)
    }

    /**
     * 绑定View到ActivityRoot上
     */
    private fun bindViewToActivityRoot() {
        var activityRoot: ViewGroup? = null
        var rootViewGroup: ViewGroup? = null
        //创建View
        if (rootViewGroup == null || activityRoot == null) {
            //获取Activity的根布局
            activityRoot =
                (mList.last as Activity).findViewById<View>(android.R.id.content) as ViewGroup
            //获取我们在Activity中设置ContentView的View
            rootViewGroup = activityRoot.getChildAt(0) as ViewGroup
        }

        view = LayoutInflater.from(mList.last)
            .inflate(bindLayoutId(), activityRoot, false)
        //先构造一个线性布局,指定垂直排列
        val newRoot = LinearLayout(mList.last)
        newRoot.orientation = LinearLayout.VERTICAL

        //移除原有的activityRoot的parent，否则会报"The specified child already has a parent. " +
        //"You must call removeView() on the child's parent first. 异常
        val viewParent = rootViewGroup.parent as ViewGroup
        viewParent.removeAllViews()

        findViewById<AppCompatImageView>(R.id.leftIv)?.setImageDrawable(getDrawable(R.drawable.icon_back)?.mutate())
        findViewById<AppCompatImageView>(R.id.leftSecondIv).setImageDrawable(getDrawable(R.drawable.ic_title_bar_close)?.mutate())
        //将titleBar添加为第一个child,原来的activityRoot为第二个
        newRoot.addView(view, 0)
        newRoot.addView(rootViewGroup, 1)
        //将新的activityRoot添加到android.R.id.content中
        activityRoot.addView(newRoot, 0)

    }


    private fun bindLayoutId(): Int = R.layout.view_title_bar

    /**
     * 设置文本
     */
    private fun setText(viewId: Int, text: String): AppCompatTextView {
        val tv = findViewById<AppCompatTextView>(viewId)
        if (!TextUtils.isEmpty(text)) {
            tv.visibility = View.VISIBLE
            tv.text = text
        }
        return tv
    }

    /**
     * 设置文本
     */
    private fun setText(viewId: Int, res: Int): AppCompatTextView {
        val tv = findViewById<AppCompatTextView>(viewId)
        if (res != 0) {
            tv.visibility = View.VISIBLE
            tv.setText(res)
        }
        return tv
    }

    private fun <T : View?> findViewById(viewId: Int): T {
        return view.findViewById<View>(viewId) as T
    }

    private fun setOnClickListener(viewId: Int, listener: View.OnClickListener?) {
        findViewById<View>(viewId).setOnClickListener(listener)
    }

    private fun setVisibility(viewId: Int, visibility: Int) {
        findViewById<View>(viewId).visibility = visibility
    }

    /**
     * 设置标题栏背景
     * 推荐在xml中设置统一背景，如果有某个页面有特殊需求再调用此方法
     */
    fun setBackground(res: Int): CommonTitleBar {
        view.setBackgroundResource(res)
        return this
    }

    /**
     * 设置左侧侧图标
     *
     * @param res  R.mipmap.···
     */
    fun setLeftIcon(res: Int): CommonTitleBar {
        if (res != 0) {
            val leftIv = findViewById<AppCompatImageView>(R.id.leftIv)
            leftIv.setImageResource(res)
        }
        return this
    }

    /**
     * 设置左侧图标颜色
     */
    fun setLeftIconColor(@ColorRes color: Int): CommonTitleBar {
        val drawable = DrawableCompat.wrap(getDrawable(R.drawable.icon_back)!!).mutate()
        DrawableCompat.setTint(drawable, getColor(color))
        findViewById<AppCompatImageView>(R.id.leftIv).setImageDrawable(drawable)
        return this
    }

    /**
     * 设置右侧图标
     *
     * @param res  R.mipmap.···
     * @param seat 图标相对于文字的位置，可选： left，right
     */
    fun setRightIcon(res: Int, seat: String): CommonTitleBar {
        if (res != 0) {
            val tv = findViewById<AppCompatTextView>(R.id.rightTv)
            tv.visibility = View.VISIBLE
            val drawable = ContextCompat.getDrawable(mList.last, res)
            drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            when (seat) {
                "right" -> tv.setCompoundDrawables(null, null, drawable, null)
                "left" -> tv.setCompoundDrawables(drawable, null, null, null)
                else -> tv.setCompoundDrawables(drawable, null, null, null)
            }
        }
        return this
    }

    /**
     * 设置标题
     */
    fun setTitle(
        stringResource: Int,
        @ColorRes textColor: Int = R.color.color_000000
    ): CommonTitleBar {
        setText(R.id.titleTv, stringResource).apply {
            setTextColor(ContextCompat.getColor(context, textColor))
        }
        return this
    }

    /**
     * 设置标题
     */
    fun setTitle(mTitle: String, @ColorRes textColor: Int = R.color.color_000000): CommonTitleBar {
        setText(R.id.titleTv, mTitle).apply {
            setTextColor(ContextCompat.getColor(context, textColor))
        }
        return this
    }

    /**
     * 设置右标题
     */
    fun setRightTitle(mRightText: String): CommonTitleBar {
        setText(R.id.rightTv, mRightText)
        return this
    }

    /**
     * 设置右标题
     */
    fun setRightTitle(stringResource: Int): CommonTitleBar {
        setText(R.id.rightTv, stringResource)
        return this
    }
    /**
     * 设置标题颜色
     */
    fun setTitleColor(color: Int): CommonTitleBar {
        setColor(R.id.titleTv, color)
        return this
    }

    /**
     * 设置标题大小
     */
    fun setTitleSize(size: Int): CommonTitleBar {
        setSize(R.id.titleTv, size.toFloat())
        return this
    }

    /**
     * 设置右侧文字颜色
     */
    fun setRightTextColor(color: Int): CommonTitleBar {
        setColor(R.id.rightTv, color)
        return this
    }

    /**
     * 设置右侧文字字体样式
     */
    fun setRightTextStyle(style: Int): CommonTitleBar {
        val tv = findViewById<AppCompatTextView>(R.id.rightTv)
        tv.typeface = Typeface.defaultFromStyle(style)
        return this
    }

    /**
     * 设置右侧文字和图标间距
     */
    fun setDrawablePadding(dp: Int): CommonTitleBar {
        val tv = findViewById<AppCompatTextView>(R.id.rightTv)
        tv.compoundDrawablePadding = dp2px(dp)
        return this
    }

    /**
     * 隐藏左侧图标
     */
    fun hideLeftView(): CommonTitleBar {
        isLeftIconVisible = View.INVISIBLE
        return this
    }

    /**
     * 设置右侧文字，点击事件
     */
    fun setRightTextAndClick(mRightText: String, listener: View.OnClickListener): CommonTitleBar {
        setText(R.id.rightTv, mRightText)
        mRightClickListener = listener
        return this
    }

    fun setRightTextAndClick(stringResource: Int, listener: View.OnClickListener): CommonTitleBar {
        setText(R.id.rightTv, stringResource)
        mRightClickListener = listener
        return this
    }

    /**
     * 设置左侧图标，自定义点击事件
     */
    fun setLeftIconAndClick(res: Int, listener: View.OnClickListener): CommonTitleBar {
        val leftIv = findViewById<ImageView>(R.id.leftIv)
        leftIv?.setImageDrawable(getDrawable(res)?.mutate())
        mLeftClickListener = listener
        return this
    }

    /**
     * 设置右侧图标点击事件
     */
    fun setRightClickListener(mRightClickListener: View.OnClickListener): CommonTitleBar {
        this.mRightClickListener = mRightClickListener
        return this
    }

    /**
     * 设置中间标题点击事件
     */
    fun setTitleClickListener(mTitleClickListener: View.OnClickListener): CommonTitleBar {
        this.mTitleClickListener = mTitleClickListener
        return this
    }

    /**
     * 重新设置左侧图标点击事件
     */
    fun setLeftClickListener(mLeftClickListener: View.OnClickListener): CommonTitleBar {
        this.mLeftClickListener = mLeftClickListener
        return this
    }

    /**
     * 左侧关闭按钮，H5专用
     */
    fun setLeftSecondClickListener(mLeftSecondClickListener: View.OnClickListener): CommonTitleBar {
        this.mLeftSecondClickListener = mLeftSecondClickListener
        return this
    }

    /**
     * 设置颜色
     *
     * @param viewId
     * @param color
     */
    private fun setColor(viewId: Int, color: Int) {
        val tv = findViewById<AppCompatTextView>(viewId)
        tv.setTextColor(ContextCompat.getColor(tv.context, color))
    }

    /**
     * 设置大小
     *
     * @param viewId
     * @param color
     */
    private fun setSize(viewId: Int, size: Float) {
        val tv = findViewById<AppCompatTextView>(viewId)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,size)
    }

    /**
     * 获取TitleView
     */
    fun getTitleView(): AppCompatTextView? {
        return view.findViewById(R.id.titleTv)
    }

    fun create() {
        setOnClickListener(R.id.rightTv, mRightClickListener)
        setOnClickListener(R.id.titleTv, mTitleClickListener)
        setVisibility(R.id.leftView, isLeftIconVisible)
        if (mLeftClickListener == null) {
            setOnClickListener(R.id.leftView, View.OnClickListener {
                try {
//                    有可能出现在二级页面卡住又点击一次，last Node 为null抛出异常
                    mList.last.finish()
                    mList.removeLast()
                } catch (e: NoSuchElementException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        } else {
            setOnClickListener(R.id.leftView, mLeftClickListener)
        }
        if (mLeftSecondClickListener != null) {
            setVisibility(R.id.leftSecondView, View.VISIBLE)
            setOnClickListener(R.id.leftSecondView, mLeftSecondClickListener)
            setOnClickListener(R.id.leftSecondIv, mLeftSecondClickListener)
        }
    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(), mList.last.resources.displayMetrics
        ).toInt()
    }
}