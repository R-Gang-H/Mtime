package com.kotlin.android.publish.component.widget.editor

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import com.kotlin.android.ktx.ext.core.heightValue
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.ktx.ext.keyboard.showSoftInputWithActivity
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.EditorMenuBarBinding
import com.kotlin.android.publish.component.widget.article.view.EditorLayout
import com.kotlin.android.publish.component.widget.article.view.item.TextCard
import kotlinx.android.synthetic.main.editor_menu_bar.view.*

/**
 * 编辑器菜单
 *
 * Created on 2022/4/21.
 *
 * @author o.s
 */
class EditorMenuView : LinearLayout {

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

    private var mBinding: EditorMenuBarBinding? = null
    private var editorLayout: EditorLayout? = null
    private var isInitKeyboardHeight = false
    private var isFollowHide = true

    /**
     * 注册编辑器，自动处理获取光标问题
     */
    fun registerEditorLayout(editorLayout: EditorLayout) {
        this.editorLayout = editorLayout
    }

    /**
     * 菜单是否可用
     */
    private val isMenuEnable: Boolean
        get() = initKeyboardHeight > 0

    /**
     * 初始化菜单体高度为键盘高度
     */
    private var initKeyboardHeight: Int = 0
        set(value) {
            if (field != value) {
                field = value
                if (value > 0) {
                    if (!isInitKeyboardHeight) {
                        isInitKeyboardHeight = true
                        mBinding?.contentLayout?.heightValue = value
                    }
                }
            }
        }

    /**
     * 菜单是否显示
     */
    var isShowMenuBar: Boolean = true
        set(value) {
            field = value
            showMenuBar(isShow = value)
        }

    /**
     * 菜单体是否显示
     */
    var isShowContent: Boolean = false
        set(value) {
            if (isMenuEnable) {
                if (field != value) {
                    field = value
                    showContentView(isShow = value)
                }
            }
        }

    /**
     * 键盘按钮是否被选中
     */
    var isSelectedKeyboard: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                mBinding?.keyboardView?.isSelected = value
            }
        }

    /**
     * 字体按钮是否被选中
     */
    var isSelectedFont: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                mBinding?.fontView?.isSelected = value
            }
        }

    /**
     * 添加按钮是否被选中
     */
    var isSelectedAdd: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                mBinding?.addView?.isSelected = value
            }
        }

//    var activeFont: (() -> Unit)? = null
    var actionPhoto: (() -> Unit)? = null
    var actionPre: (() -> Unit)? = null
    var actionNext: (() -> Unit)? = null
//    var actionStyle: ((Int) -> Unit)? = null
//        set(value) {
//            field = value
//            mBinding?.menuFontView?.actionStyle = value
//        }
//    var actionAlign: ((TextAlign) -> Unit)? = null
//        set(value) {
//            field = value
//            mBinding?.menuFontView?.actionAlign = value
//        }
//    var actionSize: ((TextFontSize) -> Unit)? = null
//        set(value) {
//            field = value
//            mBinding?.menuFontView?.actionSize = value
//        }
//    var actionColor: ((TextColor) -> Unit)? = null
//        set(value) {
//            field = value
//            mBinding?.menuFontView?.actionColor = value
//        }
    var actionAdd: ((EditorMenuAddView.Action) -> Unit)? = null
        set(value) {
            field = value
            mBinding?.menuAddView?.action = value
        }

    var textCard: TextCard? = null
        set(value) {
            field = value
            fillMenuFont()
        }

    /**
     * 显示/隐藏添加菜单对应位置的按钮
     */
    fun showAddView(index: Int, isShow: Boolean) {
        mBinding?.menuAddView?.show(index = index, isShow = isShow)
    }

    /**
     * 键盘显示
     */
    fun keyboardShow(keyboardHeight: Int) {
        initKeyboardHeight = keyboardHeight
        isShowContent = true
        isSelectedKeyboard = true
        isSelectedFont = false
        isSelectedAdd = false
        isFollowHide = true
    }

    /**
     * 键盘隐藏
     */
    fun keyboardHide(keyboardHeight: Int) {
        isSelectedKeyboard = false
        if (isFollowHide) {
            isShowContent = false
        }

    }

    private fun initView() {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        mBinding = EditorMenuBarBinding.inflate(LayoutInflater.from(context))
        addView(mBinding?.root)

        showContentView(isShow = false)

        mBinding?.apply {
            keyboardView.apply {
                setImageDrawable(
                    getDrawableStateList(
                        normalRes = R.drawable.ic_publish_menu_keyboard,
                        pressedRes = R.drawable.ic_publish_menu_keyboard_reversed,
                        selectedRes = R.drawable.ic_publish_menu_keyboard_reversed,
                    )
                )
                setOnClickListener {
                    isSelectedFont = false
                    isSelectedAdd = false
                    isFollowHide = true
                    if (isSelectedKeyboard) {
                        hideSoftInput()
                    } else {
                        showSoftInput()
                    }
                }
            }

            fontView.apply {
                setImageDrawable(
                    getDrawableStateList(
                        normalRes = R.drawable.ic_publish_menu_font,
                        pressedRes = R.drawable.ic_publish_menu_font_reversed,
                        selectedRes = R.drawable.ic_publish_menu_font_reversed,
                    )
                )
                setOnClickListener {
                    if (isMenuEnable) {
                        isSelectedAdd = false
                        isSelectedFont = !isSelectedFont
                        if (isSelectedFont) {
                            isShowContent = true
                            isFollowHide = false
                            textCard = editorLayout?.currentTextItemLayout?.textCard
                            hideSoftInput()
                            showMenuFontView()
                        } else {
                            isFollowHide = true
                            showSoftInput()
                        }
                    }
//                    activeFont?.invoke()
//                    textCard = editorLayout?.currentTextItemLayout?.textCard
                }
            }

            photoView.apply {
                setImageDrawable(
                    getDrawableStateList(
                        normalRes = R.drawable.ic_publish_menu_photo,
                        pressedRes = R.drawable.ic_publish_menu_photo_reversed,
                        selectedRes = R.drawable.ic_publish_menu_photo_reversed,
                    )
                )
                setOnClickListener {
                    actionPhoto?.invoke()
                }
            }

            addView.apply {
                setImageDrawable(
                    getDrawableStateList(
                        normalRes = R.drawable.ic_publish_menu_add,
                        pressedRes = R.drawable.ic_publish_menu_add_reversed,
                        selectedRes = R.drawable.ic_publish_menu_add_reversed,
                    )
                )
                setOnClickListener {
                    if (isMenuEnable) {
                        isSelectedFont = false
                        isSelectedAdd = !isSelectedAdd
                        if (isSelectedAdd) {
                            isShowContent = true
                            isFollowHide = false
                            hideSoftInput()
                            showMenuAddView()
                        } else {
                            isFollowHide = true
                            showSoftInput()
                        }
                    }
                }
            }

            preView.apply {
                setImageDrawable(
                    getDrawableStateList(
                        normalRes = R.drawable.ic_publish_menu_pre,
                        pressedRes = R.drawable.ic_publish_menu_pre_reversed,
                        disableRes = R.drawable.ic_publish_menu_pre_disable,
                    )
                )
                setOnClickListener {
                    actionPre?.invoke()
                }
            }

            nextView.apply {
                setImageDrawable(
                    getDrawableStateList(
                        normalRes = R.drawable.ic_publish_menu_next,
                        pressedRes = R.drawable.ic_publish_menu_next_reversed,
                        disableRes = R.drawable.ic_publish_menu_next_disable,
                    )
                )
                setOnClickListener {
                    actionNext?.invoke()
                }
            }

            menuFontView.apply {
//                actionStyle = this@EditorMenuView.actionStyle
//                actionAlign = this@EditorMenuView.actionAlign
//                actionSize = this@EditorMenuView.actionSize
//                actionColor = this@EditorMenuView.actionColor
                actionStyle = {
                    textCard?.setTextStyle(it)
                }
                actionAlign = {
                    textCard?.setTextAlign(it)
                }
                actionSize = {
                    textCard?.setTextFontSize(it)
                }
                actionColor = {
                    textCard?.setTextColor(it)
                }
            }
            menuAddView.apply {
                action = this@EditorMenuView.actionAdd
            }
        }
    }

    private fun showMenuBar(isShow: Boolean) {
        mBinding?.apply {
            menuLayout.isVisible = isShow
        }
    }

    private fun showContentView(isShow: Boolean = true) {
        mBinding?.apply {
            contentLayout.isVisible = isShow
            if (!isShow) {
                isSelectedFont = false
                isSelectedAdd = false
            }
        }
    }

    private fun showMenuFontView() {
        mBinding?.apply {
            menuFontView.isVisible = true
            menuAddView.isVisible = false
        }
    }

    private fun showMenuAddView() {
        mBinding?.apply {
            menuFontView.isVisible = false
            menuAddView.isVisible = true
        }
    }

    /**
     * 显示键盘，如果没有获取焦点的编辑器，则自动获取焦点，从后向前遍历
     */
    private fun showSoftInput() {
        if (((context as? Activity)?.currentFocus as? EditText) == null) {
            requestFocusWithLastEditText()
        }
        showSoftInputWithActivity()
    }

    /**
     * 从后向前遍历，自动获取焦点
     */
    private fun requestFocusWithLastEditText() {
        editorLayout?.apply {
            val last = childCount - 1
            (0 .. last).forEach {
                if (findEditTextAndRequestFocus(getChildAt(last - it))) {
                    return@apply
                }
            }
        }
    }

    private fun findEditTextAndRequestFocus(view: View): Boolean {
        if (view is EditText) {
            view.requestFocus()
            return true
        } else {
            if (view is ViewGroup) {
                view.children.forEach {
                    return findEditTextAndRequestFocus(it)
                }
            }
        }
        return false
    }

    private fun fillMenuFont() {
        mBinding?.menuFontView?.apply {
            textCard?.apply {
                val textSpan = getTextSpan()
                textStyle = textSpan.textStyle
                textAlign = textSpan.textAlign
                textFontSize = textSpan.textFontSize
                textColor = textSpan.textColor
            }
        }
    }
}