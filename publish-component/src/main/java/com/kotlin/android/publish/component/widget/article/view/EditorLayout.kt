package com.kotlin.android.publish.component.widget.article.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.children
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.core.ext.showDialogFragment
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.publish.component.*
import com.kotlin.android.publish.component.Publish.MAX_ADD_IMAGE_LIMIT
import com.kotlin.android.publish.component.Publish.MAX_ADD_MOVIE_LIMIT
import com.kotlin.android.publish.component.Publish.MAX_ADD_VIDEO_LIMIT
import com.kotlin.android.publish.component.logE
import com.kotlin.android.publish.component.logW
import com.kotlin.android.publish.component.scope.ContentInitScope
import com.kotlin.android.publish.component.widget.ActionEvent
import com.kotlin.android.publish.component.widget.article.sytle.TextAlign
import com.kotlin.android.publish.component.widget.article.sytle.TextColor
import com.kotlin.android.publish.component.widget.article.sytle.TextFontSize
import com.kotlin.android.publish.component.widget.article.view.event.PEvent
import com.kotlin.android.publish.component.widget.article.view.item.IEditorState
import com.kotlin.android.publish.component.widget.article.view.item.IFocusChanged
import com.kotlin.android.publish.component.widget.article.xml.entity.Body
import com.kotlin.android.publish.component.widget.article.xml.entity.Element
import com.kotlin.android.publish.component.widget.dialog.InputLabelDialog
import com.kotlin.android.publish.component.widget.selector.LocalMedia
import com.kotlin.android.widget.dialog.showDialog
import java.util.*

/**
 * 编辑器-布局（文章、日志、帖子、影评等）
 *
 * Created on 2022/3/29.
 *
 * @author o.s
 */
class EditorLayout : LinearLayout, IEditorState, IFocusChanged {

    private var currentIndex = 0 // 当前长按子视图下标
    private var currentItemView: EditorItemLayout? = null // 当前长按子视图
    private var currentItemViewHeight: Int = 0 // 当前长按子视图

    private var delayItem = 60L
    private var delayMove = 100L

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

    private val defLayoutParams by lazy {
        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun initView() {
        orientation = VERTICAL

        layoutParams = defLayoutParams
    }

    /**
     * 清空编辑器
     */
    fun clear() {
        body.elements = null
        removeAllViews()
        autoAddItemWithText()
        notifyTextCountChange()
    }

    var hint: CharSequence = ""
        set(value) {
            field = value
            syncHint()
        }

    var body: Body = Body()
        set(value) {
            field = value
            fillBody(value)
        }

    /**
     * 内容是否就绪（图片/视频是否上传成功）
     * 即没有成功，也没有错误，说明在上传中
     */
    val isReady: Boolean
        get() {
            var ready = true
            body.elements?.forEach {
                if (it.view?.isReady == false) {
                    ready = false
                }
            }
            return ready
        }

    /**
     * 是否有错误的元素（图片/视频上传失败的情况）
     * 即没有成功，也没有错误，说明在上传中
     */
    val isError: Boolean
        get() {
            var error = false
            body.elements?.forEach {
                if (it.view?.isError == true) {
                    error = true
                }
            }
            return error
        }

    val imageLimit: Int
        get() {
            val imageCount = body.elements?.filter { it.itemType == ItemType.IMAGE_CARD }?.size ?: 0
            return MAX_ADD_IMAGE_LIMIT - imageCount
        }

    val videoLimit: Int
        get() {
            val videoCount = body.elements?.filter { it.itemType == ItemType.VIDEO_CARD}?.size ?: 0
            return MAX_ADD_VIDEO_LIMIT - videoCount
        }

    val movieLimit: Int
        get() {
            val movieCount = body.elements?.filter { it.itemType == ItemType.MOVIE_CARD}?.size ?: 0
            return MAX_ADD_MOVIE_LIMIT - movieCount
        }

    override var state: EditorState = EditorState.NORMAL
        set(value) {
            field = value
            dispatchState(value)
        }

    var textCountChange: ((Int) -> Unit)? = null

    private fun notifyTextCountChange() {
        var count = 0
        body.elements?.forEach {
            count += (it.view?.count ?: 0)
        }
        textCountChange?.invoke(count)
    }

    override fun normalState() {
        removeAllViews()
        fillBody(body)
    }

    override fun editState() {
        // nothing
    }

    override fun moveState() {
        body.elements?.forEach {
            if (it.view != currentItemView) {
                if (it.view?.isEmptyTextCard != true) {
                    it.view?.state = EditorState.MOVE
                }
            }
        }
    }

    /**
     * 回显（填充编辑器）需要编辑的 Body
     */
    private fun fillBody(body: Body) {
        body.elements?.forEach {
            fillItem(it)
        }

        autoAddItemWithText()

        syncIndex()
        syncHint()
    }

    /**
     * 填充段落 Element
     */
    private fun fillItem(element: Element) {
        val type = element.itemType
        if (type != null) {
            if (element.view == null) {
                val item = createItem()
                item.element = element
            }

            addView(element.view)
            element.view?.state = EditorState.NORMAL
        }
    }

    /**
     * 添加段落到指定位置，默认 [position] = -1 添加到最后
     */
    fun addItem(type: ItemType, position: Int = -1): EditorItemLayout {
        val item = createItem()
        item.type = type
        body.add(element = item.element, position = position)
        if (position in 0 until childCount) {
            addView(item, position)
        } else {
            addView(item)
        }

        syncIndex()
        syncHint()
        return item
    }

    private fun removeItem(itemView: EditorItemLayout) {
        body.elements?.remove(itemView.element)
        removeView(itemView)

        syncIndex()
        syncHint()
    }

    private fun getItem(index: Int): EditorItemLayout? {
        return if (index in 0 until body.elements?.size.orZero()) {
            body.elements?.get(index)?.view
        } else {
            null
        }
    }

    private fun indexOfItem(item: EditorItemLayout): Int {
        return indexOfChild(item)
    }

    /**
     * 自动添加最后一个文本输入框
     */
    fun autoAddItemWithText() {
        if (!isLastTextCard) {
            logE("autoAddItemWithText", "last item not TextCard")
            addItem(ItemType.TEXT_CARD)
        }
    }

    /**
     * 最后一个是否时 TextCard
     */
    val isLastTextCard: Boolean
        get() = ItemType.TEXT_CARD == body.elements?.last()?.itemType


    /**
     * 最后一个是否时空文本
     */
    val isLastEmptyTextCard: Boolean
        get() {
            val item = body.elements?.last()
            return if (ItemType.TEXT_CARD == item?.itemType) {
                item.isEmpty
            } else {
                false
            }
        }

    /**
     * 添加影片
     */
    fun addMovieCard(movie: Movie) {
        addMovieCard(movie = movie, position = addBetterPosition)
        autoAddItemWithText()
    }

    /**
     * 添加视频
     */
    fun addVideoCard(data: LocalMedia) {
        addVideoCard(data = data, position = addBetterPosition)
        autoAddItemWithText()
    }

    /**
     * 添加图片
     */
    fun addImageCard(photoInfo: PhotoInfo) {
        addImageCard(photoInfo = photoInfo, position = addBetterPosition)
        autoAddItemWithText()
    }

    /**
     * 添加图片列表
     */
    fun addImageCards(photos: List<PhotoInfo>) {
        val position = addBetterPosition
        if (position == -1) {
            photos.forEach { photoInfo ->
                addImageCard(photoInfo)
            }
        } else {
            photos.forEachIndexed { index, photoInfo ->
                addImageCard(photoInfo, position + index)
            }
        }
        autoAddItemWithText()
    }

    /**
     * 添加文本
     */
    fun addTextCard(text: CharSequence) {
        addTextCard(text = text, position = addBetterPosition)
    }

    /**
     * 添加影片
     */
    private fun addMovieCard(movie: Movie, position: Int = -1) {
        addItem(type = ItemType.MOVIE_CARD, position = position).run {
            movieElementData?.movie = movie
            state = EditorState.NORMAL
        }
    }

    /**
     * 添加图片
     */
    private fun addImageCard(photoInfo: PhotoInfo, position: Int = -1) {
        addItem(type = ItemType.IMAGE_CARD, position = position).run {
            imageElementData?.photoInfo = photoInfo
            state = EditorState.NORMAL
        }
    }

    /**
     * 添加视频
     */
    private fun addVideoCard(data: LocalMedia, position: Int = -1) {
        addItem(type = ItemType.VIDEO_CARD, position = position).run {
            videoElementData?.localMedia = data
            state = EditorState.NORMAL
        }
    }

    /**
     * 添加文本
     */
    private fun addTextCard(text: CharSequence, position: Int = -1) {
        addItem(type = ItemType.TEXT_CARD, position = position).run {
            textElementData?.text = text
            state = EditorState.NORMAL
        }
    }

    /**
     * 添加的位置，插入在焦点之后
     */
    private val addBetterPosition: Int
        get() {
            val currPosition = currentPosition
            if (currentPosition == -1) {
                // 无焦点插入到最后
                return -1
            }
            val lastPosition = childCount - 1
            return if (currPosition == lastPosition) {
                if (isLastEmptyTextCard) {
                    lastPosition
                } else {
                    currPosition + 1
                }
            } else {
                currPosition + 1
            }
//            // 一律加到最后
//            return if (isLastEmptyTextCard) {
//                childCount - 1
//            } else {
//                -1
//            }
        }

    private fun createItem(): EditorItemLayout {
        return EditorItemLayout(context).apply {
            this.focusChanged = this@EditorLayout.focusChanged
            this.hasFocused = this@EditorLayout.hasFocused
            this.action = { itemView, event ->
                when (event) {
                    ActionEvent.EVENT_MOVE -> {
//                        move(itemView)
                        moveUI2(itemView)
                    }
                    ActionEvent.EVENT_LINK -> { link(itemView) }
                    ActionEvent.EVENT_DESC -> { desc(itemView) }
                    ActionEvent.EVENT_CLOSE -> { close(itemView) }
                }
            }
            this.notifyTextChanged = {
                notifyTextCountChange()
            }
            this.dragChange = { _, dy ->
                drag(dy)
            }
            this.pEvent = {
                when (it) {
                    PEvent.PRE -> {
                        getPreItemView(this)?.also { preItem ->
                            logW("createItem", "PEvent.PRE index:${preItem.element.index}")
                            when (preItem.type) {
                                ItemType.TEXT_CARD -> {
                                    preItem.textCard?.mergeToPre(textCard)
                                }
                                ItemType.IMAGE_CARD,
                                ItemType.VIDEO_CARD,
                                ItemType.MOVIE_CARD -> {
                                    this.requestFocus()
                                    close(preItem)
                                }
                            }
                            logW("createItem", "PEvent.PRE remove index:${element.index}")
                            removeItem(this)
                        }
                    }
                    PEvent.NEXT -> {
                        val index = this.element.index // itemIndexOf(this)
                        logE("createItem", "PEvent.NEXT index:$index")
                        addItem(ItemType.TEXT_CARD, index + 1).textCard?.mergeToNext(textCard)
                    }
                    PEvent.DONE -> {

                    }
                }
            }
        }
    }

    private fun getPreItemView(itemView: EditorItemLayout): EditorItemLayout? {
        val index = itemView.element.index // itemIndexOf(itemView)
        logE("createItem", "PEvent.PRE index:$index")
        return if (index > 0) {
            body.elements?.get(index - 1)?.view
        } else {
            null
        }
    }

    private fun itemIndexOf(itemView: EditorItemLayout): Int {
        body.elements?.indexOf(itemView.element)
        body.elements?.forEachIndexed { index, element ->
            if (element.view == itemView) {
                 return index
            }
        }
        return -1
    }

    /**
     * 同步提示语
     */
    private fun syncHint() {
        body.elements?.forEach {
            it.view?.hint = ""
        }
        body.elements?.firstOrNull { ItemType.TEXT_CARD == it.view?.type }?.view?.hint = hint
    }

    /**
     * 同步下标
     */
    private fun syncIndex() {
        body.elements?.forEachIndexed { index, element ->
            if (element.index != index) {
                element.index = index
                logW("syncIndex", "index:$index ${element.view}")
            } else {
                logD("syncIndex", "index:$index ${element.view}")
            }
        }
    }

    private fun drag(dy: Float) {
        if (dy > 2) {
            // 向下拖拽
            down(currentIndex + 1)
        } else if (dy < -2) {
            //向上拖拽
            up(currentIndex - 1)
        }
    }

    private fun move(itemView: EditorItemLayout) {
        currentItemView = itemView
        currentItemViewHeight = itemView.height
        currentIndex = indexOfChild(itemView)
        logE("move", "点击 currentIndex:$currentIndex size:${body.elements?.size} rect -> [${itemView.left}, ${itemView.top}, ${itemView.right}, ${itemView.bottom}]")
        state = EditorState.MOVE
//        moveUI2(itemView)
    }

    private fun link(itemView: EditorItemLayout) {
        itemView.apply {
            if (ItemType.MOVIE_CARD == type) {
                val movie = itemView.movieCard?.movieElementData?.movie
                val movieId = movie?.movieId
                val movieName = movie?.name ?: movie?.nameEn.orEmpty()
                val href = movie?.href
                if (movieId != null) {
                    val position = indexOfItem(this)
                    if (href.isNullOrEmpty()) {
                        ContentInitScope.instance.getMovieUrl(movieId = movieId) {
                            addMovieLink(
                                itemView = itemView,
                                movieName = movieName,
                                href = this,
                                position = position
                            )
                        }
                    } else {
                        addMovieLink(
                            itemView = itemView,
                            movieName = movieName,
                            href = href,
                            position = position
                        )
                    }
                }
            }
        }
    }

    /**
     * 真实的的添加电影连接
     */
    private fun addMovieLink(
        itemView: EditorItemLayout,
        movieName: String,
        href: String,
        position: Int,
    ) {
        addItem(type = ItemType.TEXT_CARD, position = position).apply {
            textCard?.addLink(title = movieName, url = href)
        }
        removeItem(itemView)
    }

    private fun desc(itemView: EditorItemLayout) {
        clearEditFocus()
        showDialogFragment(
            clazz = InputLabelDialog::class.java
        )?.apply {
            title = getString(R.string.publish_img_desc)
            hint = getString(R.string.publish_img_desc)
            maxLength = 20
            echo = itemView.desc
            event = {
                itemView.desc = it
            }
        }
    }

    private fun close(itemView: EditorItemLayout) {
        showDialog(
            context = context,
            content = when (itemView.type) {
                ItemType.MOVIE_CARD -> {
                    R.string.do_you_want_delete_movie
                }
                ItemType.VIDEO_CARD -> {
                    R.string.do_you_want_delete_video
                }
                else -> {
                    R.string.do_you_want_delete_image
                }
            }
        ) {
            removeItem(itemView)
        }
    }

    private fun up(targetIndex: Int) {
        getItem(targetIndex)?.apply {
            val preCenter = top + height / 2
            if (currentItemView?.top.orZero() < preCenter - 30) {
                // 交换位置
                Collections.swap(body.elements, currentIndex, targetIndex)
                // 移动位置
                layout(left, top + currentItemViewHeight, right, bottom + currentItemViewHeight)
                currentIndex = targetIndex
                up(currentIndex - 1)
            }
        }
    }

    private fun down(targetIndex: Int) {
        getItem(targetIndex)?.apply {
            if (childCount - 1 == targetIndex) {
                if (ItemType.TEXT_CARD == type) {
                    if (isEmpty) {
                        return
                    }
                }
            }

            val nextCenter = top + height / 2
            if (currentItemView?.bottom.orZero() > nextCenter + 30) {
                // 交换位置
                Collections.swap(body.elements, currentIndex, targetIndex)
                // 移动位置
                layout(left, top - currentItemViewHeight, right, bottom - currentItemViewHeight)
                currentIndex = targetIndex
                down(currentIndex + 1)
            }
        }
    }

//    /**
//     * 向上拖拽
//     */
//    private fun dragUp(v: View) {
//        "$tag dragUp currentIndex = $currentIndex, view : rect -> [${v.left}, ${v.top}, ${v.right}, ${v.bottom}], size = ${body.elements?.size}".i()
//        if (currentIndex <= 0) {
//            return
//        }
//        val preIndex = currentIndex - 1
//        getItemView(preIndex)?.apply {
//            val preCenter = top + height / 2
//            if (v.top < preCenter - 30) {
//                val offsetY = v.height
//                val rect = Rect(left, top + offsetY, right, bottom + offsetY)
//                if (preIndex > 0 && getItemView(preIndex - 1)?.isInRect(rect) == true) {
//                    return
//                }
//                // 交换位置
//                Collections.swap(body.elements, currentIndex, preIndex)
//                // 移动位置
//                layout(rect.left, rect.top, rect.right, rect.bottom)
//                currentIndex -= 1
//            }
//        }
//    }
//
//    /**
//     * 向下拖拽7
//     */
//    private fun dragDown(v: View) {
//        "$tag dragDown currentIndex = $currentIndex, view : rect -> [${v.left}, ${v.top}, ${v.right}, ${v.bottom}], size = ${body.elements?.size}".i()
//        val size = body.elements?.size ?: 0
//        if (currentIndex >= size - 1) {
//            return
//        }
//        val nextIndex = currentIndex + 1
//        getItemView(nextIndex)?.apply {
//            if (childCount == nextIndex + 1) {
//                if (ItemType.TEXT_CARD == type) {
//                    if (isEmpty) {
//                        return
//                    }
//                }
//            }
//
//            val nextCenter = top + height / 2
//            if (v.bottom > nextCenter + 30) {
//                val offsetY = -v.height
//                val rect = Rect(left, top + offsetY, right, bottom + offsetY)
//                if (nextIndex < size - 1 && getItemView(nextIndex + 1)?.isInRect(rect) == true) {
//                    return
//                }
//                // 交换位置
//                Collections.swap(body.elements, currentIndex, nextIndex)
//                // 移动位置
//                layout(rect.left, rect.top, rect.right, rect.bottom)
//                currentIndex += 1
//            }
//        }
//    }

    /**
     * 第二类：移动状态处理方案（向点击目标View靠拢）
     */
    private fun moveUI2(itemView: EditorItemLayout) {
        val index = indexOfChild(itemView)
        currentIndex = index
        currentItemView = itemView
        currentItemViewHeight = itemView.moveStateHeight

        // 拖拽的当前 itemView 优先进入移动状态
        itemView.state = EditorState.MOVE
        // 通知所有进入移动状态
        state = EditorState.MOVE
        itemView.run {
            val params = layoutParams as? MarginLayoutParams
            val topMargin = params?.topMargin ?: 0
            val bottomMargin = params?.bottomMargin ?: 0
            val b = bottom
            val t = b - moveStateHeight

            val preEndY = t - topMargin
            val nextStartY = b + bottomMargin
            logE("movieUI2", "index = $index :: rect -> [$left, $t, $right, $b] :: moveStateHeight = $height :: preEndY = $preEndY :: nextStartY = $nextStartY")
            this.layout(left, t, right, b)
            postDelayed({
                // 延时高亮，等待 itemView 进入移动状态就绪
                highlightView()
            }, delayItem)
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
            body.elements?.get(index)?.view?.run {
                val params = layoutParams as? MarginLayoutParams
                val topMargin = params?.topMargin ?: 0
                val bottomMargin = params?.bottomMargin ?: 0
                val b = endY - bottomMargin
                val t = b - moveStateHeight
                val preEndY = t - topMargin
                logE("pre", "index = $index :: rect -> [$left, $t, $right, $b] :: moveStateHeight = $height :: endY = $endY")
                this.layout(left, t, right, b)
                postDelayed({
                    pre(index - 1, preEndY)
                }, delayItem)
            }
        }
    }

    private fun next(index: Int, startY: Int) {
        val size = body.elements?.size ?: 0
        if (index < size) {
            body.elements?.get(index)?.view?.run {
                val params = layoutParams as? MarginLayoutParams
                val topMargin = params?.topMargin ?: 0
                val bottomMargin = params?.bottomMargin ?: 0
                val t = startY + topMargin
                val b = t + moveStateHeight
                val nextStartY = b + bottomMargin
                logE("next", "index = $index :: rect -> [$left, $t, $right, $b] :: moveStateHeight = $height :: startY = $startY")
                this.layout(left, t, right, b)
                postDelayed({
                    next(index + 1, nextStartY)
                }, delayItem)
            }
        }
    }

    var textStyle: Int? = null
    var textAlign: TextAlign? = null
    var textSize: TextFontSize? = null
    var textColor: TextColor? = null

    override var focusChanged: ((View, Boolean) -> Unit)? = null
        set(value) {
            field = value
            body.elements?.forEach {
                it.view?.focusChanged = value
            }
        }

    override var hasFocused: ((View, Boolean) -> Unit)? = null
        set(value) {
            field = value
            body.elements?.forEach {
                it.view?.hasFocused = value
            }
        }

    /**
     * 清空当前文本输入框焦点
     */
    fun clearEditFocus() {
        currentTextItemLayout?.textCard?.clearEditFocus()
    }

    /**
     * 是否有焦点
     */
    val hasFocus: Boolean
        get() = findPositionByFocused() >= 0

    /**
     * 当前焦点位置
     */
    val currentPosition: Int
        get() = findPositionByFocused()

    /**
     * 当前焦点文本位置
     */
    val currentTextPosition: Int
        get() = findPositionByFocusedEditText()

    /**
     * 当前焦点段落位置（全部可获取的段落）
     */
    var currentItemLayout: EditorItemLayout? = null
        get() {
            findPositionByFocused()
            return field
        }

    /**
     * 当前焦点（光标）文本段落位置（仅TextCard文本段落）
     */
    var currentTextItemLayout: EditorItemLayout? = null
        get() {
            findPositionByFocusedEditText()
            return field
        }

    /**
     * 从后向前遍历，发现 TextCard 焦点视图的位置
     */
    private fun findPositionByFocusedEditText(): Int {
        val last = childCount - 1
        (0 .. last).forEach {
            val position = last - it
            val view = getChildAt(position)
            if (findFocusedByEditText(view)) {
                currentTextItemLayout = view as? EditorItemLayout
                return position
            }
        }
        currentTextItemLayout = null
        return -1
    }

    /**
     * 从后向前遍历，焦点视图的位置
     */
    private fun findPositionByFocused(): Int {
        val last = childCount - 1
        (0 .. last).forEach {
            val position = last - it
            val view = getChildAt(position)
            if (findFocused(view)) {
                currentItemLayout = view as? EditorItemLayout
                return position
            }
        }
        currentItemLayout = null
        return -1
    }

    private fun findFocusedByEditText(view: View): Boolean {
        if (view is EditText) {
            return view.isFocused
        } else {
            if (view is ViewGroup) {
                view.children.forEach {
                    return findFocusedByEditText(it)
                }
            }
        }
        return false
    }

    private fun findFocused(view: View): Boolean {
        if (view.isFocused) {
            return true
        } else {
            if (view is ViewGroup) {
                view.children.forEach {
                    return findFocused(it)
                }
            }
        }
        return false
    }
}