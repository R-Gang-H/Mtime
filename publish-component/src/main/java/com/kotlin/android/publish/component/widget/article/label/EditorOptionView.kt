package com.kotlin.android.publish.component.widget.article.label

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.search.Article
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getCompoundDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.publish.component.Publish.MAX_LABEL_LIMIT
import com.kotlin.android.publish.component.R
import com.kotlin.android.widget.titlebar.TextTouchListener

/**
 *
 * Created on 2022/4/18.
 *
 * @author o.s
 */
class EditorOptionView : LinearLayout {
    constructor(context: Context?) : super(context) { initView() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { initView() }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) { initView() }

    private val mTitleHeight = 50.dp
    private val mLabelHeight = 26.dp
    private val mTitlePaddingStart = 15.dp
    private val mTitlePaddingEnd = 16.dp
    private val mLabelPadding = 10.dp
    private val mMarginBottom = 15.dp
    private val mTitleTextSize = 15F

    var action: ((data: TextTouchListener.Data) -> Unit)? = null

    /**
     * 构建对象之后，在使用之前第一时间设置type。
     */
    var type: LabelType? = null
        set(value) {
            field = value
            labelView.type = value
        }

    /**
     * 编辑模式：回显内容
     */
    var content: CommunityContent? = null
        set(value) {
            field = value
            labelView.content = value
            syncAddView()
        }

    /**
     * 编辑模式：回显内容
     */
    var articles: List<Article>? = null
        set(value) {
            field = value
            if (LabelType.ARTICLE == type) {
                addLabels(
                    dataList = value,
                    name = {
                        it.articleTitle
                    },
                    id = {
                        it.articleId?.toString()
                    }
                )
            }
        }

    /**
     * 删除标签
     */
    var deleteAction: ((String) -> Unit)?
        get() = labelView.deleteAction
        set(value) {
            labelView.deleteAction = value
        }

    /**
     * 选择标签
     */
    var selectAction: ((String?) -> Unit)?
        get() = labelView.selectAction
        set(value) {
            labelView.selectAction = value
        }

    val dataList: List<Any>
        get() = labelView.dataList.map { it.value }

    val length: Int
        get() = labelView.length

    val selectLabel: CharSequence?
        get() = labelView.selectedLabel

    val selectData: Any?
        get() = labelView.selectedData

    var title: CharSequence = ""
        set(value) {
            field = value
            titleView.text = value
        }

    @StringRes
    var titleRes: Int = R.string.publish_component_title_input_article
        set(value) {
            field = value
            titleView.setText(value)
        }

    /**
     * 选中指定位置的标签
     */
    fun select(index: Int) {
        labelView.select(index = index)
    }

    /**
     * 添加标签列表
     */
    fun <T : Any> addLabels(
        dataList: List<T>?,
        name: ((T) -> String?)? = null,
        id: ((T) -> String?),
    ) {
        labelView.setLabels(
            dataList = dataList,
            name = name,
            id = id
        )
        "添加:$dataList".e()
        syncAddView()
    }

    /**
     * 添加标签
     */
    fun <T : Any> addLabel(
        isFirst: Boolean = false,
        id: String? = null,
        name: String? = null,
        data: T? = null
    ) {
        labelView.addLabel(
            isFirst = isFirst,
            id = id,
            name = name,
            data = data,
        )
        "添加:$data".e()
        syncAddView()
    }

    private fun initView() {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        addView(titleView)
        addView(labelView)

        syncAddView()
    }

    private val titleView by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mTitleHeight)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(mTitlePaddingStart, 0, mTitlePaddingEnd, 0)
            textSize = mTitleTextSize
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(getColor(R.color.color_404c57))
            setOnTouchListener(
                TextTouchListener(
                    context = context,
                    textView = this,
                    action = {
                        action?.invoke(it)
                    }
                )
            )
        }
    }

    private val labelView by lazy {
        LabelView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mLabelHeight).apply {
                bottomMargin = mMarginBottom
            }
            clipToPadding = false
            setPadding(mLabelPadding, 0, mLabelPadding, 0)
            isVisible = false
            type = this@EditorOptionView.type
            deleteAction = {
                "删除:$it".e()
                syncAddView()
            }
        }
    }

    /**
     * 同步添加视图的显示隐藏
     */
    private fun syncAddView() {
        val addDrawable = if (labelView.length < MAX_LABEL_LIMIT) {
            getCompoundDrawable(R.drawable.ic_editor_18_add_2)
        } else {
            null
        }
        titleView.setCompoundDrawables(null, null, addDrawable, null)
    }
}