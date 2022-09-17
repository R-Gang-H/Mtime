package com.kotlin.android.publish.component.widget.article.label

import android.content.Context
import android.text.TextUtils
import android.util.ArrayMap
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.isVisible
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.publish.Group
import com.kotlin.android.app.data.entity.community.publish.RecommendType
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.app.data.entity.search.Person
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.mtime.ktx.getCompoundDrawableOrNull
import com.kotlin.android.publish.component.Publish.EDITOR_EDITOR_NONE
import com.kotlin.android.publish.component.Publish.EDITOR_SOURCE_NONE
import com.kotlin.android.publish.component.R
import com.kotlin.android.widget.titlebar.TextTouchListener

/**
 * 标签类型
 */
enum class LabelType(
    @DrawableRes val startDrawableRes: Int? = null,
    @DrawableRes val endDrawableRes: Int? = null,
    @ColorRes val textColorRes: Int,
    @ColorRes val highlightTextColorRes: Int? = null,
    @ColorRes val backgroundColorRes: Int,
    @ColorRes val highlightBackgroundColorRes: Int? = null,
    val textSize: Float = 12F,
    val paddingStart: Int = 8.dp,
    val paddingEnd: Int = 6.dp,
    val drawablePadding: Int = 8.dp,
) {
    /**
     * 关联影人
     */
    ACTOR(
        startDrawableRes = R.drawable.ic_editor_label_12_actor,
        endDrawableRes = R.drawable.ic_editor_label_16_del,
        textColorRes = R.color.color_20a0da,
        backgroundColorRes = R.color.color_e9f6fc,
    ),

    /**
     * 关联影片
     */
    MOVIE(
        startDrawableRes = R.drawable.ic_editor_label_12_movie,
        endDrawableRes = R.drawable.ic_editor_label_16_del,
        textColorRes = R.color.color_20a0da,
        backgroundColorRes = R.color.color_e9f6fc,
    ),

    /**
     * 关联文章
     */
    ARTICLE(
        startDrawableRes = R.drawable.ic_editor_label_12_article,
        endDrawableRes = R.drawable.ic_editor_label_16_del,
        textColorRes = R.color.color_20a0da,
        backgroundColorRes = R.color.color_e9f6fc,
    ),

    /**
     * 关联标签
     */
    LABEL(
        endDrawableRes = R.drawable.ic_editor_label_16_del,
        textColorRes = R.color.color_20a0da,
        backgroundColorRes = R.color.color_e9f6fc,
        paddingStart = 12.dp
    ),

    /**
     * 家族
     */
    GROUP(
        textColorRes = R.color.color_20a0da,
        highlightTextColorRes = R.color.color_ffffff,
        backgroundColorRes = R.color.color_f2f3f6,
        highlightBackgroundColorRes = R.color.color_20a0da,
        paddingStart = 12.dp,
        paddingEnd = 12.dp,
    ),

    /**
     * 分类/分区
     */
    SECTION(
        textColorRes = R.color.color_20a0da,
        highlightTextColorRes = R.color.color_ffffff,
        backgroundColorRes = R.color.color_f2f3f6,
        highlightBackgroundColorRes = R.color.color_20a0da,
        paddingStart = 12.dp,
        paddingEnd = 12.dp,
    ),

    /**
     * 来源
     */
    SOURCE(
        textColorRes = R.color.color_20a0da,
        highlightTextColorRes = R.color.color_ffffff,
        backgroundColorRes = R.color.color_f2f3f6,
        highlightBackgroundColorRes = R.color.color_20a0da,
        paddingStart = 12.dp,
        paddingEnd = 12.dp,
    ),

    /**
     * 编辑
     */
    EDITOR(
        textColorRes = R.color.color_20a0da,
        highlightTextColorRes = R.color.color_ffffff,
        backgroundColorRes = R.color.color_f2f3f6,
        highlightBackgroundColorRes = R.color.color_20a0da,
        paddingStart = 12.dp,
        paddingEnd = 12.dp,
    ),
}

/**
 * 水平滚到的标签视图
 *
 * Created on 2022/4/14.
 *
 * @author o.s
 */
class LabelView : HorizontalScrollView {
    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private val labelViews by lazy {
        ArrayMap<String, TextView>()
    }

    val dataList by lazy {
        ArrayMap<String, Any>()
    }

    val labelList by lazy {
        ArrayMap<String, String>()
    }

    val length: Int
        get() = labelViews.size

    private val rootLayout by lazy {
        LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        cornerRadius = measuredHeight / 2F
    }

    private fun initView() {
        isHorizontalScrollBarEnabled = false
        addView(rootLayout)
    }

    private val itemMargin: Int = 5.dp
    private var cornerRadius = 15.dpF

    /**
     * 标签类型
     */
    var type: LabelType? = null

    /**
     * 支持点击输入的标签。如："自定义"
     */
    var clickInputLabel: CharSequence? = null

    /**
     * 编辑模式：回显内容
     */
    var content: CommunityContent? = null
        set(value) {
            field = value
            value?.apply {
                val movies = ArrayList<Movie>()
                val actors = ArrayList<Person>()
                reObjs?.forEach {
                    it.roMovie?.apply {
                        movies.add(
                            Movie(
                                movieId = id,
                                name = name,
                                nameEn = nameEn,
                            )
                        )
                    }
                    it.roPerson?.apply {
                        actors.add(
                            Person(
                                personId = id,
                                name = nameCn,
                                nameEn = nameEn,
                            )
                        )
                    }
                }
                when (this@LabelView.type) {
                    LabelType.SOURCE -> {
                        val label = if (source?.trim().isNullOrEmpty()) {
                            EDITOR_SOURCE_NONE
                        } else {
                            source
                        }
                        addLabel<String>(isFirst = true, id = label)
                        select(index = 0)
                    }
                    LabelType.EDITOR -> {
                        val label = if (editor?.trim().isNullOrEmpty()) {
                            EDITOR_EDITOR_NONE
                        } else {
                            editor
                        }
                        addLabel<String>(isFirst = true, id = label)
                        select(index = 0)
                    }
                    LabelType.MOVIE -> {
                        setLabels(
                            dataList = movies,
                            name = {
                                if (it.name?.trim().isNullOrEmpty()) {
                                    it.nameEn.orEmpty()
                                } else {
                                    it.name
                                }
                            },
                            id = {
                                it.movieId?.toString()
                            }
                        )
                    }
                    LabelType.ACTOR -> {
                        setLabels(
                            dataList = actors,
                            name = {
                                if (it.name?.trim().isNullOrEmpty()) {
                                    it.nameEn.orEmpty()
                                } else {
                                    it.name
                                }
                            },
                            id = {
                                it.personId?.toString()
                            }
                        )
                    }
                    LabelType.ARTICLE -> {
                        // TODO 单独的关联文章列表处理
//                        addLabels(dataList = keywords)
                    }
                    LabelType.LABEL -> {
                        val labels = ArrayList<String>()
                        keywords?.forEach {
                            val label = if (it.startsWith("#")) {
                                it
                            } else {
                                "#$it"
                            }
                            labels.add(label)
                        }
                        setLabels(
                            dataList = labels,
                            id = {
                                it
                            }
                        )
                    }
                    LabelType.GROUP -> {
                        addLabel(
                            id = group?.id?.toString(),
                            name = group?.name,
                            data = Group(
                                groupId = group?.id,
                                name = group?.name,
                                logoPath = group?.groupImgUrl,
                                memberCount = group?.memberCount,
                            )
                        )
                        select(index = 0)
                    }
                    LabelType.SECTION -> {
                        if (section?.sectionId.orZero() != 0L && section?.sectionName.isNullOrEmpty().not()) {
                            addLabel(
                                id = section?.sectionId?.toString(),
                                name = section?.sectionName,
                                data = RecommendType(
                                    subTypeId = section?.sectionId,
                                    name= section?.sectionName,
                                )
                            )
                            select(index = 0)
                        }
                    }
                }
            }
        }

    /**
     * 删除标签
     */
    var deleteAction: ((String) -> Unit)? = null

    /**
     * 选择标签
     */
    var selectAction: ((String?) -> Unit)? = null

    /**
     * 选中的标签内容
     */
    var selectedId: String? = null

    /**
     * 选中的标签内容
     */
    var selectedLabel: String? = null

    /**
     * 选中的标签内容
     */
    var selectedData: Any? = null

    /**
     * 允许反向选择
     */
    var allowInverseSelection: Boolean = true

    /**
     * 选中指定位置的标签
     */
    fun select(index: Int) {
        if (index in 0 until rootLayout.childCount) {
            (rootLayout[index] as? TextView)?.apply {
                if (isSelected) {
                    return
                }
                resetSelected()
                isSelected = true
                val id = tag as? String

                selectItem(id)
            }
        }
    }

    /**
     * 选中指定key的标签
     */
    fun select(id: String?) {
        if (id.isNullOrEmpty()) {
            return
        }
        rootLayout.children.forEach {
            (it as? TextView)?.apply {
                val key = tag as? String
                if (id == key) {
                    if (isSelected) {
                        return
                    }
                    resetSelected()
                    isSelected = true

                    selectItem(id)
                }
            }
        }
    }

    /**
     * 获取key对应的数据
     */
    fun getData(id: String): Any? {
        return dataList[id]
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
        if (id == null) {
            return
        }
        if (contains(id)) {
            return
        }
        val label = if (name.isNullOrEmpty()) {
            id
        } else {
            name
        }
        val value = data ?: id.toString()

        createLabel(id = id, name = label, data = value)?.apply {
            dataList[id] = value
            labelList[id] = label
            labelViews[id] = this
            if (isFirst) {
                rootLayout.addView(this, 0)
            } else {
                rootLayout.addView(this)
            }
        }

        isVisible = isNotEmpty()
    }

    /**
     * 添加标签列表
     */
    fun <T : Any> setLabels(
        dataList: List<T>?,
        name: ((T) -> String?)? = null,
        id: ((T) -> String?),
    ) {
        this.dataList.clear()
        this.labelList.clear()
        this.labelViews.clear()
        rootLayout.removeAllViews()
        dataList?.forEach { data ->
            val key = id(data)
            if (key.isNullOrEmpty() || contains(key)) {
                return@forEach
            }
            val value = name?.invoke(data) ?: key

            createLabel(id = key, name = value, data = data)?.apply {
                this@LabelView.dataList[key] = data
                this@LabelView.labelList[key] = value
                this@LabelView.labelViews[key] = this
                rootLayout.addView(this)
            }
        }
        isVisible = isNotEmpty()
    }

    /**
     * 删除标签
     */
    fun removeLabel(
        id: String,
    ) {
        if (contains(id)) {
            dataList.remove(id)
            labelList.remove(id)
            rootLayout.removeView(labelViews.remove(id))
        }
        isVisible = isNotEmpty()
    }

    fun removeLastLabel() {
        (rootLayout.children.last() as? TextView)?.apply {
            labelViews.remove(text)
            rootLayout.removeView(this)
        }
    }

    /**
     * 清空标签
     */
    fun clear() {
        labelViews.clear()
        rootLayout.removeAllViews()
    }

    /**
     * 是否为空
     */
    fun isEmpty(): Boolean {
        return labelViews.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return !labelViews.isEmpty()
    }

    /**
     * 是否包含标签
     */
    fun contains(id: String): Boolean {
        return labelViews.containsKey(id)
    }

    /**
     * 内部选中逻辑
     */
    private fun selectItem(id: String?) {
        id?.let { it ->
            selectedId = it
            selectedLabel = labelList[it]
            selectedData = dataList[it]
            selectAction?.invoke(it)
        }
    }

    private fun createLabel(
        id: String,
        name: String,
        data: Any,
    ): TextView? {
        val type = type ?: return null
        return TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(itemMargin, 0, itemMargin, 0)
            }
            gravity = Gravity.CENTER_VERTICAL
            setPadding(type.paddingStart, 0, type.paddingEnd, 0)
            compoundDrawablePadding = type.drawablePadding
            maxWidth = 260.dp
            setLines(1)
            ellipsize = TextUtils.TruncateAt.END
            textSize = type.textSize
            if (type.highlightTextColorRes != null) {
                setTextColor(
                    getColorStateList(
                        normalColorRes = type.textColorRes,
                        pressColorRes = type.highlightTextColorRes,
                        selectedColorRes = type.highlightTextColorRes,
                    )
                )
            } else {
                setTextColor(getColor(type.textColorRes))
            }
            if (type.highlightBackgroundColorRes != null) {
                background = getDrawableStateList(
                    normal = getShapeDrawable(
                        colorRes = type.backgroundColorRes,
                        cornerRadius = cornerRadius
                    ),
                    pressed = getShapeDrawable(
                        colorRes = type.highlightBackgroundColorRes,
                        cornerRadius = cornerRadius
                    ),
                    selected = getShapeDrawable(
                        colorRes = type.highlightBackgroundColorRes,
                        cornerRadius = cornerRadius
                    )
                )
            } else {
                setBackground(
                    colorRes = type.backgroundColorRes,
                    cornerRadius = cornerRadius
                )
            }
            setCompoundDrawables(
                getCompoundDrawableOrNull(type.startDrawableRes),
                null,
                getCompoundDrawableOrNull(type.endDrawableRes),
                null,
            )

            text = name

            tag = id

            setOnClickListener {
                when (type) {
                    LabelType.SOURCE,
                    LabelType.EDITOR,
                    LabelType.GROUP,
                    LabelType.SECTION -> {
                        if (id == clickInputLabel) {
                            selectAction?.invoke(id)
                        } else {
                            resetSelected(except = it)
                            if (allowInverseSelection) {
                                isSelected = !isSelected
                            } else {
                                if (isSelected) {
                                    return@setOnClickListener
                                }
                                isSelected = true
                            }
                            if (isSelected) {
                                selectedId = id
                                selectedLabel = name
                                selectedData = data
                                selectAction?.invoke(id)
                            } else {
                                // 取消选择
                                selectedLabel = null
                                selectedData = null
                                selectAction?.invoke(null)
                            }
                        }
                    }
                    else -> {
//                        removeLabel(label)
//                        deleteAction?.invoke(label)
                    }
                }
            }
            setOnTouchListener(
                TextTouchListener(
                    context = context,
                    textView = this,
                    action = {
                        when (type) {
                            LabelType.SOURCE,
                            LabelType.EDITOR,
                            LabelType.GROUP,
                            LabelType.SECTION -> {
//                                resetSelected()
//                                this.isSelected = true
//                                selectedLabel = label
//                                selectAction?.invoke(label)
                            }
                            else -> {
                                if (TextTouchListener.Position.END == it.position) {
                                    removeLabel(id)
                                    deleteAction?.invoke(id)
                                }
                            }
                        }
                    }
                )
            )
        }
    }

    /**
     * 重置选中状态
     */
    private fun resetSelected(except: View? = null) {
        rootLayout.children.forEach {
            if (it != except) {
                it.isSelected = false
            }
        }
    }
}