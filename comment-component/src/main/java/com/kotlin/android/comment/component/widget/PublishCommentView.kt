package com.kotlin.android.comment.component.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.kotlin.android.comment.component.R
import com.kotlin.android.comment.component.bar.BarButton
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.ktx.ext.keyboard.isShowSoftInput
import com.kotlin.android.ktx.ext.keyboard.showSoftInput
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList

/**
 * 发布评论/回复视图：
 *
 * Created on 2020/8/3.
 *
 * @author o.s
 */
class PublishCommentView : LinearLayout {

    private val inputLimitCount: Int = 100
    private val mInputPaddingStart = 10.dp
    private val mInputPaddingEnd = 8.dp
    private val mInputPaddingTop = 6.dp
    private val mInputPaddingBottom = 6.dp
    private val mTipsMargin = 10.dp
    private val mTipsPadding = 2.dp
    private val mTipsWidth = 40.dp
    private val mInputHeight = 40.dp
    private val inputTextSize = 16F
    private val tipsTextSize = 12F
    private val mContentPaddingStart = 15.dp
    private val mContentPadding = 5.dp
    private val mEditMargin = 5.dp
    private val keyboardDelay = 200L

    private var inputView: TextView? = null
    private var editView: EditText? = null
    private var barButton: BarButton? = null
    private var tipsView: TextView? = null

    //    private var headerView: FrameLayout? = null
    private var contentView: LinearLayout? = null
    private var footerBarButton: BarButton? = null
    var editStyle: EditStyle = EditStyle.WITH_ALL
    var style: Style = Style.NOT_COMMENT
        set(value) {
            if (field != value) {
                if (isEditStyle(value)) {
                    styleStub = field
                }
                field = value
                notifyChangeStyle()
            }
        }

    var styleStub: Style = style

    var action: ((type: BarButtonItem.Type, isSelected: Boolean) -> Unit)? = null
        set(value) {
            field = value
            barButton?.action = value
            footerBarButton?.action = value
        }

    var text: String
        get() = editView?.text.toString()
        set(value) {
            editView?.setText(value)
        }


    var hintText: String
        get() = editView?.hint.toString()
        set(value) {
            editView?.hint = value
        }

    //    hint回调
    var hintTextCallBack: (() -> String)? = null
    var isWithoutMovie: Boolean = false
    var editAction: ((Unit) -> Unit)? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun isEditStyle(value: Style):Boolean{
        return value == Style.EDIT || value == Style.EDIT_WITHOUT_MOVIE || value == Style.EDIT_WITH_KEYBOARD_ONLY
                || value == Style.EDIT_WITHOUT_PHOTO_ONLY|| value == Style.EDIT_WITHOUT_EMOJI_ONLY|| value == Style.EDIT_WITHOUT_KEYBOARD_ONLY
                ||value == Style.EDIT_WITH_MOVIE_ONLY|| value == Style.EDIT_WITH_PHOTO|| value == Style.EDIT_WITH_EMOJI
                ||value == Style.EDIT_WITH_KEYBOARD|| value == Style.EDIT_WITH_KEYBOARD|| value == Style.EDIT_WITH_NONE
    }

    fun getEditTextView(): EditText? = editView

    private fun initView() {
        orientation = VERTICAL
//        background = null
        setBackgroundResource(R.color.color_ffffff)

        initHeaderView()
        initContentView()
        initFooterView()
    }

    /**
     * 根据类型 [type] 顺序依次添加工具条目（水平布局）
     * [isReverse]: 指示反向添加，
     * addItem(A).addItem(B).addItem(C)
     * .addItem(F, true).addItem(G, true)
     * 如图：
     * --A-B-C ... G-F--
     */
    fun addItem(type: BarButtonItem.Type, isReverse: Boolean = false): PublishCommentView {
        barButton?.addItem(type, isReverse)
        return this
    }

    /**
     * 删除指定类型的栏按钮项
     */
    fun removeItem(type: BarButtonItem.Type) {
        barButton?.removeItem(type)
    }

    /**
     * 删除所有的栏按钮项
     */
    fun removeAllItems() {
        barButton?.removeAllItems()
    }

    /**
     * 设置指定类型栅按钮项的提示信息
     */
    fun setTipsByType(type: BarButtonItem.Type, tips: Long) {
        barButton?.setTipsByType(type, tips)
    }

    /**
     * 获取制定类型栅按钮数字
     */
    fun getTipsByType(type: BarButtonItem.Type): Long {
        return barButton?.getTipsByType(type) ?: 0L
    }

    /**
     * 设置指定类型栅按钮项的选中状态
     */
    fun isSelectedByType(type: BarButtonItem.Type, isSelected: Boolean) {
        barButton?.isSelectedByType(type, isSelected)
    }

    /**
     * 获取制定类型栅按钮项的选中状态
     */
    fun getSelectedStatusByType(type: BarButtonItem.Type): Boolean {
        return barButton?.getSelectedStatusByType(type) ?: false
    }

    /**
     * 是否已经添加了某个type
     */
    fun hasType(type: BarButtonItem.Type): Boolean {
        return barButton?.hasType(type) ?: false
    }

    /**
     * 输入模式：
     * 否可以输入文本
     */
    fun inputEnable(enable: Boolean = true) {
        inputView?.apply {
            isEnabled = enable
            hint = if (enable) {
                getString(R.string.hint_leave_what_i_want_to_say)
            } else {
                getString(R.string.hint_text_not_comment)
            }
        }
    }

    /**
     * 重置输入状态
     */
    fun resetInput() {
        (context as? Activity).hideSoftInput()
        style = styleStub
    }

    /**
     * 处理键盘的显示/隐藏
     */
    fun keyboard() {
        editView?.apply {
            if (isVisible) {
                (context as? Activity)?.apply {
                    if (isShowSoftInput()) {
                        hideSoftInput()
                    } else {
                        showSoftInput()
                        hintText = hintTextCallBack?.invoke().orEmpty()
                    }
                }
            }
        }
    }

    /**
     * 表情 TODO
     */
    fun emoji() {
        editView?.apply {
            val key95 = KeyEvent.KEYCODE_PICTSYMBOLS
            val key96 = KeyEvent.KEYCODE_SWITCH_CHARSET
            val key38 = KeyEvent.KEYCODE_J
            dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, key38))
            dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, key95))
            dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, key96))
//            Runtime.getRuntime().exec("input keyevent $key38")
//            GlobalScope.launch {
//                withContext(Dispatchers.IO) {
//                    Instrumentation().sendKeyDownUpSync(key38)
//                }
//            }
        }
    }

    private fun initHeaderView() {
//        headerView = FrameLayout(context).apply {
//            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 100.dp)
//            background = null
//            gone()
//        }
//        addView(headerView)
    }

    private fun initContentView() {
        contentView = LinearLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            }
            setPadding(mContentPaddingStart, mContentPadding, 0, mContentPadding)
            setBackgroundColor(getColor(android.R.color.white))
            initContentInputView(this)
            initContentEditView(this)
            initContentBarButtonView(this)
        }
        addView(contentView)
    }

    private fun initFooterView() {
        footerBarButton = BarButton(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                topMargin = 2 // divider (px)
            }
            setBackgroundColor(getColor(android.R.color.white))
            addItem(BarButtonItem.Type.PHOTO)
            addItem(BarButtonItem.Type.EMOJI)
            addItem(BarButtonItem.Type.KEYBOARD)
            gone()
        }
        addView(footerBarButton)
    }

    private fun initContentInputView(parent: ViewGroup) {
        inputView = TextView(context).apply {
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
                weight = 1F
                topMargin = mEditMargin
                bottomMargin = mEditMargin
            }
            gravity = Gravity.CENTER_VERTICAL
            minHeight = mInputHeight
            maxLines = 1
            setPadding(mInputPaddingStart, mInputPaddingTop, 2.dp, mInputPaddingBottom)
            background = getDrawableStateList(
                    normal = getShapeDrawable(colorRes = R.color.color_f2f4f7, cornerRadius = 4.dpF),
                    pressed = getShapeDrawable(colorRes = R.color.color_20a0da, cornerRadius = 4.dpF),
                    disable = getShapeDrawable()
            )
            setHintTextColor(
                    getColorStateList(
                            normalColor = getColor(R.color.color_8798af),
                            pressColor = getColor(R.color.color_c9cedc),
                            disableColor = getColor(R.color.color_c9cedc)
                    )
            )
            setTextColor(getColor(R.color.color_303a47))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, inputTextSize)
            inputEnable()
            setOnClickListener {
                if (editAction == null) {
                    setEditStyle()
                } else {
                    editAction?.invoke(Unit)
                }
            }
        }
        parent.addView(inputView)
    }

    /**
     * 点击文本回调后的操作
     */
    fun setEditStyle() {
        style = when (editStyle) {
            EditStyle.WITHOUT_PHOTO_ONLY -> Style.EDIT_WITHOUT_PHOTO_ONLY
            EditStyle.WITHOUT_MOVIE_ONLY -> Style.EDIT_WITHOUT_MOVIE
            EditStyle.WITHOUT_EMOJI_ONLY -> Style.EDIT_WITHOUT_EMOJI_ONLY
            EditStyle.WITHOUT_KEYBOARD_ONLY -> Style.EDIT_WITHOUT_KEYBOARD_ONLY
            EditStyle.WITH_MOVIE_ONLY -> Style.EDIT_WITH_MOVIE_ONLY
            EditStyle.WITH_PHOTO -> Style.EDIT_WITH_PHOTO
            EditStyle.WITH_EMOJI -> Style.EDIT_WITH_EMOJI
            EditStyle.WITH_KEYBOARD -> Style.EDIT_WITH_KEYBOARD
            EditStyle.WITH_NONE -> Style.EDIT_WITH_NONE
            else -> Style.EDIT
        }
        if (isWithoutMovie) {
            style = Style.EDIT_WITHOUT_MOVIE
        }
    }

    private fun initContentEditView(parent: ViewGroup) {
        editView = EditText(context).apply {
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
                weight = 1F
                topMargin = mEditMargin
                bottomMargin = mEditMargin
            }
            minHeight = mInputHeight
            setPadding(mInputPaddingStart, mInputPaddingTop, mInputPaddingEnd, mInputPaddingBottom)
            setBackground(colorRes = R.color.color_f2f4f7, cornerRadius = 4.dpF)
            setHintTextColor(getColor(R.color.color_8798af))
            setTextColor(getColor(R.color.color_303a47))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, inputTextSize)
            setHint(R.string.hint_leave_what_i_want_to_say)
//            imeOptions = EditorInfo.IME_ACTION_SEND
            gone()
            addTextChangedListener(
                    afterTextChanged = {
                        val size = it?.length ?: 0
                        if (size > 50) {
                            tipsView?.apply {
                                gone()
                                text = "-${inputLimitCount - size}"
                            }
                        } else {
                            tipsView?.gone()
                        }
                    }
            )
        }
        parent.addView(editView)
    }

    private fun initContentBarButtonView(parent: ViewGroup) {
        FrameLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)

            initContentTipsView(this)
            initContentBarButton(this)
            parent.addView(this)
        }
    }

    private fun initContentTipsView(parent: ViewGroup) {
        tipsView = TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(mTipsWidth, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = mTipsMargin
            }
            gravity = Gravity.CENTER
            setPadding(0, mTipsPadding, 0, mTipsPadding)
            setBackground(colorRes = R.color.color_ff5a36, cornerRadius = 10.dpF)
            setTextColor(getColor(android.R.color.white))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, tipsTextSize)
            gone()
        }
        parent.addView(tipsView)
    }

    private fun initContentBarButton(parent: ViewGroup) {
        barButton = BarButton(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.BOTTOM
            }
        }
        parent.addView(barButton)
    }

    private fun setTips(tips: Int) {
        tipsView?.text = "-$tips"
    }

    /**
     * 通知样式改变
     */
    private fun notifyChangeStyle() {
        when (style) {
            Style.NOT_COMMENT -> {
                notCommentStyle()
            }
            Style.NOT_LONG_COMMENT -> {
                longNotCommentStyle()
            }
            Style.REPLY -> {
                replyStyle()
            }
            Style.COMMENT -> {
                commentStyle()
            }
            Style.LONG_COMMENT -> {
                longCommentStyle()
            }
            Style.SHARE_COMMENT -> {
                shareCommentStyle()
            }

            Style.TOPLIST -> {
                topListStyle()
            }
            Style.WITH_NONE->{
                noneStyle()
            }
            else->{
                editStyle(style)
            }
        }
    }

    private fun noneStyle() {
        editState(false)
        removeAllItems()
        inputEnable(false)
    }

    private fun longNotCommentStyle() {
        editState(false)
        removeAllItems()
        addItem(BarButtonItem.Type.DISPRAISE)
        addItem(BarButtonItem.Type.PRAISE)
        addItem(BarButtonItem.Type.FAVORITE)
        inputEnable(false)
    }

    private fun notCommentStyle() {
        editState(false)
        removeAllItems()
        addItem(BarButtonItem.Type.PRAISE)
        addItem(BarButtonItem.Type.FAVORITE)
        inputEnable(false)
    }

    private fun replyStyle() {
        editState(false)
        removeAllItems()
        addItem(BarButtonItem.Type.PRAISE)
        inputEnable()
    }

    private fun commentStyle() {
        editState(false)
        removeAllItems()
        addItem(BarButtonItem.Type.COMMENT)
        addItem(BarButtonItem.Type.PRAISE)
        addItem(BarButtonItem.Type.FAVORITE)
        inputEnable()
    }

    private fun longCommentStyle() {
        editState(false)
        removeAllItems()
        addItem(BarButtonItem.Type.COMMENT)
        addItem(BarButtonItem.Type.DISPRAISE)
        addItem(BarButtonItem.Type.PRAISE)
        addItem(BarButtonItem.Type.FAVORITE)
        inputEnable()
    }

    private fun shareCommentStyle() {
        editState(false)
        removeAllItems()
        addItem(BarButtonItem.Type.COMMENT)
        addItem(BarButtonItem.Type.PRAISE)
        addItem(BarButtonItem.Type.FAVORITE)
        addItem(BarButtonItem.Type.SHARE)
        inputEnable()
    }

    private fun editStyle() {
        editState()
        removeAllItems()
        addItem(BarButtonItem.Type.SEND)
        inputEnable()
        editView?.apply {
            postDelayed({
                showSoftInput()
            }, keyboardDelay)
        }
    }

    private fun topListStyle() {
        editState(false)
        removeAllItems()
        addItem(BarButtonItem.Type.COMMENT)
        addItem(BarButtonItem.Type.PRAISE)
        inputEnable()
    }

    private fun editWithoutMovieStyle() {
        footerBarButton?.removeItem(BarButtonItem.Type.MOVIE)
        footerBarButton?.removeItem(BarButtonItem.Type.EMOJI)
        editStyle()
    }

    private fun editStyle(style: Style){
        when(style){
            Style.EDIT_WITHOUT_MOVIE -> {
                footerBarButton?.removeItem(BarButtonItem.Type.MOVIE)
                footerBarButton?.removeItem(BarButtonItem.Type.EMOJI)
            }
            Style.EDIT_WITH_KEYBOARD_ONLY -> {
                footerBarButton?.removeItem(BarButtonItem.Type.KEYBOARD)
            }
            Style.EDIT_WITHOUT_PHOTO_ONLY->{
                footerBarButton?.removeItem(BarButtonItem.Type.PHOTO)
            }
            Style.EDIT_WITHOUT_EMOJI_ONLY-> {//仅没有表情
                footerBarButton?.removeItem(BarButtonItem.Type.EMOJI)
            }
            Style.EDIT_WITHOUT_KEYBOARD_ONLY-> {//仅没有软键盘
                footerBarButton?.removeItem(BarButtonItem.Type.KEYBOARD)
            }
            Style.EDIT_WITH_MOVIE_ONLY-> {//仅有视频
                footerBarButton?.removeItem(BarButtonItem.Type.KEYBOARD)
                footerBarButton?.removeItem(BarButtonItem.Type.EMOJI)
                footerBarButton?.removeItem(BarButtonItem.Type.PHOTO)
            }
            Style.EDIT_WITH_PHOTO-> {//仅有图片
                footerBarButton?.removeItem(BarButtonItem.Type.KEYBOARD)
                footerBarButton?.removeItem(BarButtonItem.Type.EMOJI)
                footerBarButton?.removeItem(BarButtonItem.Type.MOVIE)
            }
            Style.EDIT_WITH_EMOJI-> {//仅有表情
                footerBarButton?.removeItem(BarButtonItem.Type.KEYBOARD)
                footerBarButton?.removeItem(BarButtonItem.Type.PHOTO)
                footerBarButton?.removeItem(BarButtonItem.Type.MOVIE)
            }
            Style.EDIT_WITH_KEYBOARD->{//仅有键盘
                footerBarButton?.removeItem(BarButtonItem.Type.EMOJI)
                footerBarButton?.removeItem(BarButtonItem.Type.PHOTO)
                footerBarButton?.removeItem(BarButtonItem.Type.MOVIE)
            }
            Style.EDIT_WITH_NONE->{//都没有
                footerBarButton?.removeItem(BarButtonItem.Type.EMOJI)
                footerBarButton?.removeItem(BarButtonItem.Type.PHOTO)
                footerBarButton?.removeItem(BarButtonItem.Type.MOVIE)
                footerBarButton?.removeItem(BarButtonItem.Type.KEYBOARD)
            }
        }
        editStyle()
    }


    private fun editState(isEdit: Boolean = true) {
        if (isEdit) {
            inputView?.gone()
            editView?.visible()
//            headerView?.visible()
            footerBarButton?.visible()
        } else {
            inputView?.visible()
            editView?.gone()
            editView?.setText("")
//            headerView?.gone()
            footerBarButton?.gone()
        }
    }

    fun setEditContent(content:String){
        editView?.apply {
            setText(content)
            setSelection(content.length)
        }

    }

    enum class Style {
        NOT_COMMENT,
        REPLY,
        COMMENT,
        LONG_COMMENT,
        SHARE_COMMENT,
        TOPLIST,
        NOT_LONG_COMMENT,//长影评不可评论,
        WITH_NONE,
        EDIT,
        EDIT_WITHOUT_MOVIE,
        EDIT_WITH_KEYBOARD_ONLY,
        EDIT_WITHOUT_PHOTO_ONLY,//仅没有图片
        EDIT_WITHOUT_EMOJI_ONLY,//仅没有表情
        EDIT_WITHOUT_KEYBOARD_ONLY,//仅没有软键盘
        EDIT_WITH_MOVIE_ONLY,//仅有视频
        EDIT_WITH_PHOTO,//仅有图片
        EDIT_WITH_EMOJI,//仅有表情
        EDIT_WITH_KEYBOARD,//仅有键盘
        EDIT_WITH_NONE//都没有

    }

    enum class EditStyle {
        WITHOUT_MOVIE_ONLY,//仅没有视频
        WITHOUT_PHOTO_ONLY,//仅没有图片
        WITHOUT_EMOJI_ONLY,//仅没有表情
        WITHOUT_KEYBOARD_ONLY,//仅没有软键盘
        WITH_MOVIE_ONLY,//仅有视频
        WITH_PHOTO,//仅有图片
        WITH_EMOJI,//仅有表情
        WITH_KEYBOARD,//仅有键盘
        WITH_NONE,//都没有
        WITH_ALL//都有
    }
}