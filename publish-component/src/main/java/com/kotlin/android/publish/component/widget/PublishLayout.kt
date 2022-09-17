package com.kotlin.android.publish.component.widget

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.widget.doOnTextChanged
import com.kotlin.android.app.data.entity.common.MovieSubItemRating
import com.kotlin.android.app.data.entity.community.record.Image
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.isInRect
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.ktx.ext.keyboard.isShowSoftInput
import com.kotlin.android.ktx.ext.keyboard.showSoftInput
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.log.w
import com.kotlin.android.publish.component.R
import com.kotlin.android.widget.dialog.showDialog
import java.lang.StringBuilder

/**
 * 发布富文本布局
 *
 * Created on 2020/7/10.
 *
 * @author o.s
 */
class PublishLayout : LinearLayout {

    private val tag = " 2 ->"

    val maxMovieLimit = 20
    val maxImageLimit = 20

    private var delayItem = 0L
    private var delayMove = 0L
    private var downY: Float = 0F
    private val filmCommentTextLimit = 210
    private var currentIndex = 0 // 当前长按子视图下标
    private val itemViews = ArrayList<PublishItemView>() // 子视图缓存集合
    private var publishState = PublishState() // 发布状态，是否可以发布
    private var showChangeLongComment: Boolean = true // 是否可以显示转长评

    var titleView: PublishTitleView? = null

    var actionLink: ((movie: Movie) -> Unit)? = null // 超链接点击事件
    var changeStyle: ((style: PublishStyle) -> Unit)? = null // 发布样式变化监听
    var changePublishState: ((state: PublishState) -> Unit)? = null // 发布状态改变监听

    /**
     * 短评：单文本结构
     */
    private val isSingle: Boolean
        get() = (style == PublishStyle.FILM_COMMENT)

    /**
     * 剩余关联图片限制数量
     */
    var imageLimit: Int? = 20
        private set
        get() {
            var count = 0
            itemViews.forEach {
                if (ItemType.IMAGE_CARD == it.itemType) {
                    count++
                }
            }
            return maxImageLimit - count
        }

    /**
     * 剩余关联电影限制数量
     */
    var movieLimit: Int? = 20
        private set
        get() {
            var count = 0
            itemViews.forEach {
                if (ItemType.MOVIE_CARD == it.itemType) {
                    count++
                }
            }
            return maxMovieLimit - count
        }

    /**
     * 标题视图数量
     */
    private val titleCount: Int
        get() {
            return if (titleView != null) {
                1
            } else {
                0
            }
        }

    /**
     * 标题是否就绪
     */
    private var isTitleReady = false
        set(value) {
            if (field != value) {
                field = value
                syncPublishState()
            }
        }

    /**
     * 封面是否就绪
     */
    private var isCoverReady = false
        set(value) {
            if (field != value) {
                field = value
                syncPublishState()
            }
        }

    /**
     * 评分是否就绪
     */
    private var isRatingReady = false
        set(value) {
            field = value
            syncPublishState()
        }

    /**
     * 文本内容是否就绪
     */
    private var isBodyReady = false
        set(value) {
            if (field != value) {
                if (value || (!value && isBodyEmpty)) {
                    field = value
                    syncPublishState()
                }
            }
        }

    /**
     * 内容是否为空
     */
    private val isBodyEmpty: Boolean
        get() {
            children.forEach {
                (it as? PublishItemView)?.apply {
                    if (itemType == ItemType.TEXT) {
                        if (!isBodyEmpty) {
                            return false
                        }
                    }
                }
            }
            return true
        }

    /**
     * 发布视图样式：短评、长评、家族等
     */
    var style: PublishStyle = PublishStyle.FILM_COMMENT
        set(value) {
            field = value
            titleView?.style = value
            changeStyle()
        }

    /**
     * 发布视图状态
     */
    var state: State = State.NORMAL
        set(value) {
//            if (field == value) {
//                return
//            }
            field = value
            changeState()
        }

    /**
     * 标题
     */
    var title: String = ""
        get() = titleView?.title.orEmpty()
        set(value) {
            field = value
            titleView?.title = value
        }

    /**
     * 编辑内容提示
     */
    var hint: String = getString(R.string.publish_talk_about_your_opinion_hint)
        set(value) {
            field = value
            getFirstItemView()?.richEditText?.hint = value
        }

    /**
     * 封面
     */
    val covers: List<Image>?
        get() {
            return titleView?.covers
        }

    /**
     * 图集
     */
    val images: List<Image>?
        get() {
            return titleView?.images
        }

    /**
     * 标签 ORIGINAL(1, "原创"), SPOILER(2, "剧透"), COPYRIGHT(3, "版权声明"), DISCLAIMER(4, "免责声明");
     */
    val tags: List<Long>?
        get() {
            return titleView?.tags
        }

    /**
     * 发布内容体
     */
    val body: String
        get() {
            val sb = StringBuilder()
//            if (style == PublishStyle.FILM_COMMENT) {
//                itemViews.forEach {
//                    if (it.itemType == ItemType.TEXT) {
//                        sb.append(it.richEditText?.text ?: "")
//                    }
//                }
//                return sb.toString()
//            }
            itemViews.forEach {
                sb.append(it.body)
            }
            return sb.toString()
        }

    /**
     * 评分
     */
    var rating: Double
        set(value) {
            titleView?.rating = value
        }
        get() {
            return titleView?.rating ?: 0.0
        }

    /**
     * 分项评分
     */
    var subRatings: List<MovieSubItemRating>?
        set(value) {
            titleView?.subRatings = value
        }
        get() {
            return titleView?.subRatings
        }

    fun getSubItemRatings(): String {
        subRatings?.let {
            val sb: StringBuffer = StringBuffer()
            it.forEachIndexed { index, itemRating ->
                sb.append(itemRating.rating?.toInt() ?: 0)
                if (index < it.size - 1) {
                    sb.append(",")
                }
            }
            return sb.toString()
        }
        return ""
    }

    /**
     * 设置封面
     */
    fun setCover(data: ArrayList<PhotoInfo>) {
        titleView?.setData(data)
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        orientation = VERTICAL
        initTitleView()
        state = State.NORMAL
    }

    private fun initTitleView() {
        titleView = PublishTitleView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            titleEditText?.doOnTextChanged { text, start, before, count ->
                isTitleReady = (text?.length ?: 0) > 0
            }
            coverChange = {
                isCoverReady = it
            }
            ratingChange = {
                isRatingReady = it
            }
        }
    }

    /**
     * 添加条目视图
     */
    fun addItemView(type: ItemType, position: Int = -1): PublishItemView {
        val itemView = PublishItemView(context)
        itemView.itemType = type
        when {
            position > titleCount -> {
                itemViews.add(position - titleCount, itemView)
                addView(itemView, position)
            }
            isAddLast() -> {
                addView(itemView)
                itemViews.add(itemView)
            }
            position < titleCount -> {
                itemViews.add(itemViews.size - 1, itemView)
                addView(itemView, childCount - 1)
            }
        }

        postDelayed({
            (parent as? ScrollView)?.smoothScrollBy(0, itemView.height + itemView.marginTop + itemView.marginBottom)
        }, 300)

        itemView.action = { v, event ->
            "$tag action :: ActionEvent = $event".d()
            when (event) {
                ActionEvent.EVENT_CLOSE -> {
                    removeItemViewDialog(v)
                }
                ActionEvent.EVENT_DESC -> {

                }
                ActionEvent.EVENT_LINK -> {
                    linkEvent(v)
                }
                ActionEvent.EVENT_MOVE -> {
                    currentIndex = indexOfChild(v) - titleCount
                    state = State.MOVE
                    postDelayed({
                        moveUI2(v)
                    }, delayMove)
                }
            }
        }
        itemView.dragChange = { v, dy ->
//                "$tag dragChange dy = $dy, view : rect -> [${v.left}, ${v.top}, ${v.right}, ${v.bottom}]".w()
//                v.layout(v.left, (v.top + dy).toInt(), v.right, (v.bottom + dy).toInt())
            if (dy > 3) {
                // 向下拖拽
                dragDown(v)
            } else if (dy < -3) {
                //向上拖拽
                dragUp(v)
            }
        }
        if (ItemType.TEXT == type) {
            setupParagraphEvent(itemView)
        }
        autoAddItemWithText()
        return itemView
    }

    /**
     * 设置段落事件
     */
    private fun setupParagraphEvent(itemView: PublishItemView) {
        itemView.single = isSingle
        itemView.paragraphEvent = {
            when (it) {
                RichEditText.ParagraphEvent.PREVIOUS -> {
                    // 获取上一个ItemView
                    getPreItemView(itemView)?.apply {
                        when (itemType) {
                            ItemType.TEXT -> {
                                // 获取文本，并拼接到上一个输入框内
                                richEditText?.apply {
                                    val len = text?.length ?: 0
                                    text?.append(itemView.richEditText?.text)
                                    showSoftInput()
                                    setSelection(len)
                                }
                                // 删除当前段落
                                removeItemView(itemView)
                            }
                            ItemType.MOVIE_CARD,
                            ItemType.IMAGE_CARD -> {
                                this.requestFocus()
                                removeItemViewDialog(this)
                            }
                        }
                    }
                }
                RichEditText.ParagraphEvent.NEXT -> {
                    var nextText: CharSequence = ""
                    itemView.richEditText?.apply {
                        text?.let { editableText ->
                            if (selectionStart <= selectionEnd && selectionEnd >= 0) {
                                nextText = editableText.subSequence(selectionEnd, editableText.length)
                                setText(editableText.subSequence(0, selectionStart))
                            }
                        }
                    }
                    // 当前位置后面添加新文本输入框
                    val index = getItemViewPosition(itemView)
                    if (index >= 0) {
                        addItemView(ItemType.TEXT,  index + 1).apply {
                            richEditText?.apply {
                                setText(nextText)
                                showSoftInput()
                                setSelection(0)
                            }
                        }
                    }
                }
                RichEditText.ParagraphEvent.DONE -> {}
            }
        }
        itemView.bodyState = {
            isBodyReady = !it
        }
    }

    private fun getPreItemView(itemView: PublishItemView): PublishItemView? {
        val index = getItemViewPosition(itemView)
        if (index > titleCount) {
            return getChildAt(index - 1) as? PublishItemView
        }
        return null
    }

    /**
     * 获取指定ItemView的位置
     */
    private fun getItemViewPosition(itemView: PublishItemView): Int {
        children.forEachIndexed { index, view ->
            if (itemView == view) {
                return index
            }
        }
        return -1
    }

    private fun removeItemViewDialog(itemView: PublishItemView) {
        showDialog(
                context = context,
                content = if (itemView.itemType == ItemType.MOVIE_CARD) {
                    R.string.do_you_want_delete_movie
                } else {
                    R.string.do_you_want_delete_image
                }
        ) {
            removeItemView(itemView)
        }
    }

    /**
     * 处理超链接点击事件
     */
    private fun linkEvent(itemView: PublishItemView) {
        itemView.apply {
            if (itemType == ItemType.MOVIE_CARD) {
                itemType = ItemType.TEXT
                setupParagraphEvent(this)
                movie?.run {
                    richEditText?.addLink(this)
                }
            }
        }
    }

    /**
     * 删除条目视图
     */
    private fun removeItemView(index: Int) {
        itemViews.removeAt(index)
        removeViewAt(index)
    }

    /**
     * 删除条目视图
     */
    private fun removeItemView(view: PublishItemView) {
        itemViews.remove(view)
        removeView(view)
    }

    /**
     * 根据状态刷新视图
     */
    private fun changeState() {
        when (state) {
            State.EDIT -> {
                editState()
            }
            State.MOVE -> {
                moveState()
            }
            State.NORMAL -> {
                normalState()
            }
        }
    }

    /**
     * 编辑状态（获取焦点）
     */
    private fun editState() {
        "$tag editState".d()

    }

    /**
     * 移动状态
     */
    private fun moveState() {
        "$tag editState".d()
        itemViews.forEachIndexed { index, itemView ->
            if (index == itemViews.size - 1) {
                itemView.state = if (isLastItemTextEmpty(itemView)) {
                    State.NORMAL
                } else {
                    State.MOVE
                }
            } else {
                itemView.state = State.MOVE
            }
        }
    }

    /**
     * 正常状态
     */
    private fun normalState() {
        "$tag normalState".d()
        removeAllViews()
        titleView?.let {
            addView(it)
        }
        itemViews.forEach {
            addView(it)
            it.state = State.NORMAL
        }
        autoAddItemWithText()
    }

    /**
     * 清空页面数据
     */
    fun clear() {
        removeAllViews()
        titleView?.let {
            addView(it)
            rating = 0.0
        }
        itemViews.clear()
        autoAddItemWithText()
    }

    /**
     * 最后一个视图是否问空文本视图
     */
    private fun isLastItemTextEmpty(itemView: PublishItemView): Boolean {
        return ItemType.TEXT == itemView.itemType
                && TextUtils.isEmpty(itemView.richEditText?.text)
    }

    /**
     * 是否添加到最后
     */
    private fun isAddLast(): Boolean {
        if (childCount > titleCount) {
            return (children.last() as? PublishItemView)?.run {
                // !(ItemType.TEXT == itemType && TextUtils.isEmpty(richEditText?.text))
                !isLastItemTextEmpty(this)
            } ?: true
        }
        return true
    }

    /**
     * 根据逻辑要求自动添加文本输入框
     */
    private fun autoAddItemWithText() {
        if (childCount > titleCount) {
            val lastChild = children.last()
            (lastChild as? PublishItemView)?.run {
                if (ItemType.TEXT != itemType) {
                    addItemView(ItemType.TEXT)
                }
            }
        } else {
            addItemView(ItemType.TEXT).richEditText?.hint = hint
        }
    }

    private fun getFirstItemView(): PublishItemView? {
        return getChildAt(titleCount) as? PublishItemView
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        "$tag dispatchTouchEvent".i()
        return super.dispatchTouchEvent(ev)
    }

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        "$tag requestDisallowInterceptTouchEvent".i()
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        "$tag onInterceptTouchEvent".i()
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        super.onTouchEvent(ev)
        ev?.action?.let {
            when (it) {
                MotionEvent.ACTION_DOWN -> {
                    "$tag onTouchEvent ACTION_DOWN".d()
                    downY = ev.y
                }
                MotionEvent.ACTION_UP -> {
                    "$tag onTouchEvent ACTION_UP".d()
                    handleUp(ev)
                }
                MotionEvent.ACTION_CANCEL -> {
                    "$tag onTouchEvent ACTION_CANCEL".d()
                }
                MotionEvent.ACTION_MOVE -> {
                    "$tag onTouchEvent ACTION_MOVE".i()
                }
            }
        }
        return true
    }

    /**
     * 处理点击布局底部空白区域事件，使最后一个文本视图获取焦点弹起键盘：
     * 如果最后一个视图的 [PublishItemView.itemType] 不是 [ItemType.TEXT] 则创建并添加一个文本编辑视图，执行上述逻辑。
     */
    private fun handleUp(ev: MotionEvent) {
        // 判断是否触发底部空白区域事件
        val upY = ev.y

        if (childCount > titleCount) {
            (children.last() as? PublishItemView)?.run {
                "$tag handleUp :: [downY, upY] -> [$downY, $upY] :: lastChildBottom = $bottom".w()
                if (upY >= bottom) {
                    // 获取焦点弹起键盘
                    if (ItemType.TEXT != itemType) {
                        // 如果最后一个视图不是文本编辑视图，则添加一个文本编辑视图
                        addItemView(ItemType.TEXT).state = State.EDIT
                    } else {
                        // 文本视图编辑模式（获取焦点弹起键盘）
                        state = State.EDIT
                    }
                }
            }
        }
    }

    /**
     * 向上拖拽
     */
    private fun dragUp(v: View) {
//        "$tag dragUp currentIndex = $currentIndex, view : rect -> [${v.left}, ${v.top}, ${v.right}, ${v.bottom}]".i()
        if (currentIndex <= 0) {
            return
        }
        val preIndex = currentIndex - 1
        val preView = itemViews[preIndex]
        val preCenter = preView.top + preView.height / 2
        if (v.top < preCenter) {
            val offsetY = v.height
            val rect = Rect(preView.left, preView.top + offsetY, preView.right, preView.bottom + offsetY)
            if (preIndex > 0 && itemViews[preIndex - 1].isInRect(rect)) {
                return
            }
            // 交换位置
            val currentView = itemViews.removeAt(currentIndex)
            itemViews.add(preIndex, currentView)
            // 移动位置
            preView.layout(rect.left, rect.top, rect.right, rect.bottom)
            currentIndex -= 1
        }
    }

    /**
     * 向下拖拽
     */
    private fun dragDown(v: View) {
//        "$tag dragDown currentIndex = $currentIndex, view : rect -> [${v.left}, ${v.top}, ${v.right}, ${v.bottom}], size = ${itemViews.size}".i()
        if (currentIndex >= itemViews.size - 1) {
            return
        }
        val nextIndex = currentIndex + 1
        val nextView = itemViews[nextIndex]
        if (childCount == nextIndex + 1 + titleCount) {
            if (ItemType.TEXT == nextView.itemType) {
                if (TextUtils.isEmpty(nextView.richEditText?.text)) {
                    return
                }
            }
        }
        val nextCenter = nextView.top + nextView.height / 2
        if (v.bottom > nextCenter) {
            val offsetY = -v.height
            val rect = Rect(nextView.left, nextView.top + offsetY, nextView.right, nextView.bottom + offsetY)
            if (nextIndex < itemViews.size - 1 && itemViews[nextIndex + 1].isInRect(rect)) {
                return
            }
            // 交换位置
            val nextV = itemViews.removeAt(nextIndex)
            itemViews.add(currentIndex, nextV)
            // 移动位置
            nextView.layout(rect.left, rect.top, rect.right, rect.bottom)
            currentIndex += 1
        }
    }

    /**
     * 第二类：移动状态处理方案（向点击目标View靠拢）
     */
    private fun moveUI2(itemView: PublishItemView) {
        val index = indexOfChild(itemView) - titleCount

        itemView.run {
            val params = layoutParams as? MarginLayoutParams
            val topMargin = params?.topMargin ?: 0
            val bottomMargin = params?.bottomMargin ?: 0
            val b = bottom
            val t = if (moveStateHeight <= 0) {
                top
            } else {
                b - moveStateHeight
            }
            val preEndY = t - topMargin
            val nextStartY = b + bottomMargin
            "${this@PublishLayout.tag} move index = $index :: rect -> [$left, $t, $right, $b] :: moveStateHeight = $moveStateHeight :: preEndY = $preEndY :: nextStartY = $nextStartY".d()
            this.layout(left, t, right, b)
            postDelayed({
                pre(index - 1, preEndY)
            }, delayItem)
            postDelayed({
                next(index + 1, nextStartY)
            }, delayItem)
        }

    }

    private fun pre(index: Int, endY: Int) {
        if (index >= 0) {
            itemViews[index].run {
                val params = layoutParams as? MarginLayoutParams
                val topMargin = params?.topMargin ?: 0
                val bottomMargin = params?.bottomMargin ?: 0
                val b = endY - bottomMargin
                val t = if (moveStateHeight <= 0) {
                    b - height
                } else {
                    b - moveStateHeight
                }
                val preEndY = t - topMargin
                "${this@PublishLayout.tag} pre  index = $index :: rect -> [$left, $t, $right, $b] :: moveStateHeight = $moveStateHeight :: endY = $endY".d()
                this.layout(left, t, right, b)
                postDelayed({
                    pre(index - 1, preEndY)
                }, delayItem)
            }
        }
    }

    private fun next(index: Int, startY: Int) {
        val size = itemViews.size
        if (index < size) {
            itemViews[index].run {
                val params = layoutParams as? MarginLayoutParams
                val topMargin = params?.topMargin ?: 0
                val bottomMargin = params?.bottomMargin ?: 0
                val t = startY + topMargin
                val b = if (moveStateHeight <= 0) {
                    t + height
                } else {
                    t + moveStateHeight
                }
                val nextStartY = b + bottomMargin
                "${this@PublishLayout.tag} next index = $index :: rect -> [$left, $t, $right, $b] :: moveStateHeight = $moveStateHeight :: startY = $startY".d()
                this.layout(left, t, right, b)
                postDelayed({
                    next(index + 1, nextStartY)
                }, delayItem)
            }
        }
    }

    /**
     * 第一类：移动状态处理方案（重新布局绘制）
     */
    private fun moveUI(itemView: PublishItemView) {
        val scrollView = parent as PublishScrollView
//        val parentY = itemView.parentY()
        // 布局前后高度偏移量
//        val half = (itemView.moveStateHeight - itemView.height) / 2
        val itemTop = itemView.top
        val itemScrollY = scrollView.scrollY
        var totalHeight = 0
        itemViews.forEach {
            if (it == itemView) {
                val dy = totalHeight - itemTop // + half
                val afterY = itemScrollY + dy
                val offsetY = if (afterY > 0) {
                    afterY
                } else {
                    0
                }
                "$tag 长按 editUI :: [dy = totalHeight - itemTop] -> [$dy = $totalHeight - $itemTop] :: [offsetY = itemScrollY + dy] ->[$afterY = $itemScrollY + $dy]".d()
                scrollView.scrollTo(0, offsetY)

                "$tag 长按 editUI :: scrollView.scrollY = ${scrollView.scrollY}".w()
            }

            val params = it.layoutParams as? MarginLayoutParams
            val topMargin = params?.topMargin ?: 0
            val bottomMargin = params?.bottomMargin ?: 0
            totalHeight += it.moveStateHeight + topMargin + bottomMargin
        }
        itemViews.forEach {
            val params = it.layoutParams

            params.height = it.moveStateHeight

            it.layoutParams = params
        }

        "$tag 长按 editUI :: scrollView.scrollY = ${scrollView.scrollY}".d()
    }

    /**
     * 处理键盘事件，显示时则执行隐藏，隐藏时则执行显示
     */
    fun keyboard(activity: Activity?) {
        if (activity.isShowSoftInput()) {
            activity.hideSoftInput()
        } else {
            itemViews.last().apply {
                if (ItemType.TEXT == itemType) {
                    richEditText?.apply {
                        showSoftInput()
                        val len = text?.length ?: 0
                        setSelection(len)
                    }
                }
            }
        }
    }

    /**
     * 样式变化
     */
    private fun changeStyle() {
        getFirstItemView()?.richEditText?.doOnTextChanged { text, start, before, count ->
            changeLongComment(text, start, before, count)
        }
        children.forEach {
            if (it is PublishItemView && it.itemType == ItemType.TEXT) {
                it.single = isSingle
            }
        }
        when (style) {
            PublishStyle.JOURNAL -> {
                journalStyle()
            }
            PublishStyle.FILM_COMMENT -> {
                filmCommentStyle()
            }
            PublishStyle.LONG_COMMENT -> {
                longCommentStyle()
            }
        }
    }

    private fun journalStyle() {
        hint = getString(R.string.publish_say_something_hint)
    }

    private fun filmCommentStyle() {
        hint = getString(R.string.publish_talk_about_your_opinion_hint)
    }

    private fun longCommentStyle() {
        hint = getString(R.string.publish_share_with_you_hint)
    }

    private fun changeLongComment(text: CharSequence?, start: Int, before: Int, count: Int) {
        if (!showChangeLongComment) {
            return
        }
        if (style == PublishStyle.FILM_COMMENT) {
            val len = text?.length ?: 0
            if (len > filmCommentTextLimit) {
//                isBodyReady = false
                if (count > 0) {
                    showChangeLongComment = false
                    showDialog(
                            context = context,
                            content = R.string.publish_too_many_words_please_change_to_long_film_review,
                            positive = R.string.publish_change_to_long_film_review,
                            negative = R.string.publish_deletion_content
                    ) {
//                        isBodyReady = true
                        changeStyle?.invoke(PublishStyle.LONG_COMMENT)
                    }
                }
            }
        }
    }

    /**
     * 同步发布内容状态
     */
    private fun syncPublishState() {
        publishState.isTitleReady = isTitleReady
        publishState.isCoverReady = isCoverReady
        publishState.isRatingReady = isRatingReady
        publishState.isBodyReady = isBodyReady
        changePublishState?.invoke(publishState)
    }

    /**
     * 发布视图各区域内容状态
     */
    data class PublishState(
            var isTitleReady: Boolean = false, // 标题是否就绪
            var isCoverReady: Boolean = false, // 封面是否就绪
            var isRatingReady: Boolean = false, // 评分是否就绪
            var isBodyReady: Boolean = false // 内容师傅就绪
    )
}
