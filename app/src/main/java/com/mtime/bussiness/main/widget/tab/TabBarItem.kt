package com.mtime.bussiness.main.widget.tab

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.core.entity.PageFlag
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.heightValue
import com.kotlin.android.ktx.ext.core.marginTop
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList

/**
 *
 * Created on 2022/1/6.
 *
 * @author o.s
 */
class TabBarItem : FrameLayout, ITabBarItem {
    constructor(
        context: Context,
        tag: String,
        fragment: Fragment?,
        title: CharSequence = "",
        @StringRes
        titleRes: Int? = null,
        hotTitle: CharSequence = "",
        @ColorInt titleColor: Int = Color.parseColor("#8798AF"),
        @ColorInt titleColorHighlight: Int = Color.parseColor("#1CACDF"),
        @ColorInt hotColor: Int = Color.RED,
        iconDrawable: Drawable? = null,
        iconDrawableHighlight: Drawable? = null,
        itemHeight: Int = 54.dp,
        iconWidth: Int = 30.dp,
        iconHeight: Int = 30.dp,
        hotHeight: Int = 9.dp,
        titleTextSize: Float = 10F,
        hotTextSize: Float = 10F,
    ): super(context) {
        this.mTag = tag
        this.mFragment = fragment
        this.title = if (titleRes != null) { getString(titleRes) } else { title }
        this.hotTitle = hotTitle
        this.titleColor = titleColor
        this.titleColorHighlight = titleColorHighlight
        this.hotColor = hotColor
        this.iconDrawable = iconDrawable
        this.iconDrawableHighlight = iconDrawableHighlight
        this.itemHeight = itemHeight
        this.iconWidth = iconWidth
        this.iconHeight = iconHeight
        this.hotHeight = hotHeight
        this.titleTextSize = titleTextSize
        this.hotTextSize = hotTextSize
        initView()
    }
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

    private var itemHeight = 54.dp
    private var iconWidth = 30.dp
    private var iconHeight = 30.dp
    private var hotHeight = 9.dp
    private var titleTextSize = 11F
    private var hotTextSize = 10F
    private var mTag: String = ""
    private var mFragment: Fragment? = null
    private var title: CharSequence = ""
    private var hotTitle: CharSequence = ""
    @ColorInt
    private var titleColor: Int = Color.parseColor("#8798AF") // getColor(resId = R.color.color_b8bfc7)
    @ColorInt
    private var titleColorHighlight: Int = Color.parseColor("#1CACDF") // getColor(resId = R.color.color_00479c)
    @ColorInt
    private var hotColor: Int = Color.RED // getColor(resId = R.color.color_00479c)
    private var iconDrawable: Drawable? = null
    private var iconDrawableHighlight: Drawable? = null

    private val itemCenterView: LinearLayout by lazy {
        LinearLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
            orientation = LinearLayout.VERTICAL
        }
    }
    private val iconView: ImageView by lazy {
        ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(iconWidth, iconHeight).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            setImageDrawable(
                getDrawableStateList(
                    normal = iconDrawable,
                    selected = iconDrawableHighlight,
                )
            )
        }
    }
    private val titleView: TextView by lazy {
        TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            gravity = Gravity.CENTER
            //不要加粗
//            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            textSize = titleTextSize
            text = title
            setTextColor(
                getColorStateList(
                    normalColor = titleColor,
                    pressColor = titleColorHighlight,
                    selectedColor = titleColorHighlight,
                )
            )
        }
    }
    private val hotView: TextView by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, hotHeight).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                marginStart = iconWidth / 2 - 2.dp
                topMargin = 9.dp - hotHeight / 2
            }
            minWidth = hotHeight
            setPadding(4.dp, 0, 4.dp, 0)
            gravity = Gravity.CENTER
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            textSize = hotTextSize
            text = hotTitle
            setBackground(
                color = hotColor,
                cornerRadius = hotHeight / 2F
            )
            setTextColor(Color.WHITE)
            isVisible = false
        }
    }

    override val view: View
        get() = this

    override val fragment: Fragment?
        get() = mFragment

    override val tag: String
        get() = mTag

    /**
     * 选择/取消选择
     */
    override fun select(isSelected: Boolean) {
        this.isSelected = isSelected
    }

    /**
     * 应用页面标示（跳转）
     */
    override fun applyPageFlag(flag: PageFlag) {
        "${javaClass.simpleName} applyPageFlag flag=$flag, fragment=${fragment?.javaClass?.simpleName}".i()
        (fragment as? BaseVMFragment<*, *>)?.setPageFlag(flag)
    }

    /**
     * 应用皮肤
     */
//    override fun applySkin(skin: SkinTabBarItem?) {
//        skin?.title?.apply {
//            title = this
//        }
//        skin?.titleColor?.apply {
//            val color = if (startsWith("0x")) {
//                "#${this.substring(2)}"
//            } else {
//                "#$this"
//            }
//            titleColor = Color.parseColor(color)
//        }
//        skin?.titleColorPressed?.apply {
//            val color = if (startsWith("0x")) {
//                "#${this.substring(2)}"
//            } else {
//                "#$this"
//            }
//            titleColorHighlight = Color.parseColor(color)
//        }
//        SkinManager.instance.getSkinFileName(skin?.icon)?.apply {
//            loadImage(
//                data = File(this)
//            ) {
//                iconDrawable = it
//                syncIcon()
//            }
//        }
//        SkinManager.instance.getSkinFileName(skin?.iconPressed)?.apply {
//            loadImage(
//                data = File(this)
//            ) {
//                iconDrawableHighlight = it
//                syncIcon()
//            }
//        }
//        syncTitle()
//    }

    /**
     * 显示红点
     */
    override fun showHotView(isShow: Boolean, title: CharSequence) {
        if (isShow) {
            syncHotView(title)
        }
        hotView.isVisible = isShow
    }

    /**
     * 同步Item状态
     */
    private fun syncIcon() {
        iconView.setImageDrawable(
            getDrawableStateList(
                normal = iconDrawable,
                selected = iconDrawableHighlight,
            )
        )
    }

    private fun syncTitle() {
        titleView.text = title
        titleView.setTextColor(
            getColorStateList(
                normalColor = titleColor,
                pressColor = titleColorHighlight,
                selectedColor = titleColorHighlight,
            )
        )
    }

    private fun initView() {
        layoutParams = LinearLayout.LayoutParams(0, itemHeight).apply {
            weight = 1F
        }
        addView(itemCenterView)
        addView(hotView)
        itemCenterView.addView(iconView)
        itemCenterView.addView(titleView)
    }

    /**
     * 同步红点状态
     */
    private fun syncHotView(title: CharSequence) {
        hotView.text = title
        if (title.isNotEmpty()) {
            hotView.marginTop = 2.dp
            hotView.heightValue = 16.dp
            hotView.setBackground(
                color = hotColor,
                cornerRadius = 8.dpF
            )
        } else {
            hotView.marginTop = 9.dp - hotHeight / 2
            hotView.heightValue = hotHeight
            hotView.setBackground(
                color = hotColor,
                cornerRadius = hotHeight / 2F
            )
        }
    }
}