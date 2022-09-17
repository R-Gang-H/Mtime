package com.kotlin.android.widget.titlebar.item

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.ktx.ext.span.toBold
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.widget.R
import com.kotlin.android.widget.titlebar.TextTouchListener

/**
 * 编辑页面中间视图
 *
 * Created on 2021/12/14.
 *
 * @author o.s
 */

class EditorCenterTitleView : LinearLayout {

    private var movieId: Long? = null
    private var movieName: CharSequence? = null

    var action: ((data: TextTouchListener.Data) -> Unit)? = null

    var title: CharSequence
        get() = titleView.text
        set(value) {
            titleView.isVisible = true
            linkTitleView.isVisible = false
            titleView.text = value
        }

    var subTitle: CharSequence
        get() = subTitleView.text
        set(value) {
            subTitleView.text = value
        }

    var isSubTitleVisible: Boolean = false
        set(value) {
            field = value
            subTitleView.isVisible = value
        }

    fun setMovieTitle(
        title: CharSequence,
        movieId: Long?,
        movieName: String?,
    ) {
        this.movieId = movieId
        this.movieName = movieName
        movieName?.apply {
            titleView.isVisible = false
            linkTitleView.apply {
                isVisible = true
                syncLinkPadding()
                text = title.toSpan()
                    .append(
                        movieName.toSpan().toBold()
                            .toColor(color = getColor(R.color.color_20a0da))
                    )
                action?.let {
                    setOnTouchListener(
                        TextTouchListener(
                            context = context,
                            textView = this,
                            link = movieName,
                            action = it
                        )
                    )
                }
            }
        }
    }

    private fun syncLinkPadding() {
        val len = movieName?.length.orZero()
        val paddingEnd = when {
            len > 9 -> {
                80.dp
            }
            len > 8 -> {
                65.dp
            }
            len > 7 -> {
                50.dp
            }
            len > 6 -> {
                35.dp
            }
            len > 5 -> {
                20.dp
            }
            len > 4 -> {
                10.dp
            }
            else -> {
                0
            }
        }
        linkTitleView.setPadding(0, 0, paddingEnd, 0)
    }

    constructor(context: Context) : super(context) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private fun initView() {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        }
        addView(titleView)
        addView(linkTitleView)
        addView(subTitleView)
    }

    private val titleView by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            textSize = 16F
            setTextColor(getColor(R.color.color_1d2736))
        }
    }

    private val linkTitleView by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
            gravity = Gravity.CENTER
            textSize = 14F
            setSingleLine()
            ellipsize = TextUtils.TruncateAt.END
            setTextColor(getColor(R.color.color_1d2736))
//            action?.let {
//                setOnTouchListener(
//                    TextTouchListener(
//                        context = context,
//                        textView = this,
//                        link = movieName?.toString(),
//                        action = it
//                    )
//                )
//            }
            isVisible = false
        }
    }

    private val subTitleView by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
            gravity = Gravity.CENTER
            textSize = 9F
            setTextColor(getColor(R.color.color_cbd0d7))
            isVisible = false
        }
    }
}