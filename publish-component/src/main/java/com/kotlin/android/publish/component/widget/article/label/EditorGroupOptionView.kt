package com.kotlin.android.publish.component.widget.article.label

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.publish.Group
import com.kotlin.android.app.data.entity.community.publish.RecommendType
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getCompoundDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.publish.component.R
import com.kotlin.android.widget.titlebar.TextTouchListener

/**
 * 选择家族和选择分类
 *
 * Created on 2022/4/18.
 *
 * @author o.s
 */
class EditorGroupOptionView : LinearLayout {
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
    private val mLabelHeight = 30.dp
    private val mDivideHeight = 1.dp
    private val mTitlePaddingStart = 15.dp
    private val mTitlePaddingEnd = 15.dp
    private val mLabelPadding = 10.dp
    private val mTitleTextSize = 15F

    var action: ((data: TextTouchListener.Data) -> Unit)? = null

    /**
     * 是否种草
     */
    var isAllowChanged = true
        set(value) {
            field = value
            if (value) {
                titleView.setCompoundDrawables(null, null, getCompoundDrawable(R.drawable.ic_editor_20_arrow_right), null)
            } else {
                titleView.setCompoundDrawables(null, null, null, null)
            }
        }

    /**
     * 编辑模式：回显内容
     */
    var content: CommunityContent? = null
        set(value) {
            field = value
            if (value != null) {
                isAllowChanged = false
            }
            labelView.content = value
            val isShow = value?.section?.sectionId.orZero() != 0L && value?.section?.sectionName.isNullOrEmpty().not()
            titleView2.isVisible = isShow
            labelView2.isVisible = isShow
            divideView.isVisible = isShow
            labelView2.content = value
        }

    /**
     * 选择标签
     */
    var selectGroupAction: ((String?) -> Unit)?
        get() = labelView.selectAction
        set(value) {
            labelView.selectAction = value
        }

    /**
     * 选择标签
     */
    var selectTypeAction: ((String?) -> Unit)?
        get() = labelView2.selectAction
        set(value) {
            labelView2.selectAction = value
        }

    val selectedGroup: Group?
        get() = labelView.selectedData as? Group

    val selectedType: RecommendType?
        get() = labelView2.selectedData as? RecommendType

    fun selectGroup(index: Int) {
        labelView.select(index = index)
    }

    fun selectGroup(id: String?) {
        labelView.select(id = id)
    }

    fun selectType(index: Int) {
        labelView2.select(index = index)
    }

    fun selectTypeById(id: String?) {
        labelView2.select(id = id)
    }

    fun getGroupData(id: String): Group? {
        return labelView.getData(id) as? Group
    }

    fun getTypeData(id: String): RecommendType? {
        return labelView2.getData(id) as? RecommendType
    }

    fun addGroup(group: Group) {
        labelView.addLabel(
            isFirst = true,
            id = group.groupId?.toString(),
            name = group.name,
            data = group,
        )
        selectGroup(id = group.groupId?.toString())
    }

    fun setGroups(groups: List<Group>) {
        labelView.setLabels(
            dataList = groups,
            name = {
                it.name
            },
            id = {
                it.groupId?.toString()
            }
        )
        selectGroup(index = 0)
        "添加:$groups".e()
    }

    fun setTypes(types: List<RecommendType>?) {
        val isShow = types.isNullOrEmpty().not()
        titleView2.isVisible = isShow
        labelView2.isVisible = isShow
        divideView.isVisible = isShow
        labelView2.setLabels(
            dataList = types,
            name = {
                it.name
            },
            id = {
                it.subTypeId?.toString()
            }
        )
        "添加:$types".e()
    }

    private fun initView() {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        addView(titleView)
        addView(labelView)
        addView(divideView)
        addView(titleView2)
        addView(labelView2)
    }

    private val titleView by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mTitleHeight)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(mTitlePaddingStart, 0, mTitlePaddingEnd, 0)
            textSize = mTitleTextSize
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(getColor(R.color.color_404c57))
            setCompoundDrawables(null, null, getCompoundDrawable(R.drawable.ic_editor_20_arrow_right), null)
            setText(R.string.publish_component_title_input_choose_group)
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

    private val titleView2 by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mTitleHeight)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(mTitlePaddingStart, 0, mTitlePaddingEnd, 0)
            textSize = mTitleTextSize
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(getColor(R.color.color_404c57))
            setText(R.string.publish_component_title_input_choose_category)
        }
    }

    private val labelView by lazy {
        LabelView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mLabelHeight).apply {
            }
            clipToPadding = false
            setPadding(mLabelPadding, 0, mLabelPadding, 0)
            isVisible = false
            allowInverseSelection = false
            type = LabelType.GROUP
            deleteAction = {
                "删除:$it".e()
            }
        }
    }

    private val labelView2 by lazy {
        LabelView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mLabelHeight).apply {
            }
            clipToPadding = false
            setPadding(mLabelPadding, 0, mLabelPadding, 0)
            isVisible = false
            allowInverseSelection = true
            type = LabelType.SECTION
            deleteAction = {
                "删除:$it".e()
            }
        }
    }

    private val divideView by lazy {
        View(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mDivideHeight).apply {
                setMargins(15.dp, 20.dp, 15.dp, 2.dp)
            }
            setBackgroundResource(R.color.color_f3f3f4)
        }
    }
}