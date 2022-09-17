package com.kotlin.android.card.monopoly.widget.search

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.View.OnFocusChangeListener
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Friend
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.widget.search.SearchEditText

/**
 * @desc 搜索好友搜索框
 * @author zhangjian
 * @date 2021-05-13 16:14:21
 */
class SearchFriendView : LinearLayout {

    private var mInputHeight = 36.dp
    private var mLabelWidth = 36.dp

    private val mInputTextSize = 14F

    private var inputLayout: LinearLayout? = null
    private var editTextView: SearchEditText? = null
    private var cancelView: TextView? = null

    private var friend: Friend? = null


    var text: String? = null
        set(value) {
            field = value
            editTextView?.setText(value)
        }

    /**
     * 键盘监听搜索事件回调
     */
    var searchAction: ((event: SearchEditText.SearchEvent) -> Unit)? = null
        set(value) {
            field = value
            editTextView?.searchAction = value
        }

    var stateChange: ((state: State) -> Unit)? = null

    var action: ((data: Friend) -> Unit)? = null

    var cancel: (() -> Unit)? = null

    var state: State = State.COLLAPSING
        set(value) {
            if (field == value) {
                return
            }
            stateChange?.invoke(value)
            field = value
            notifyChange()
        }

    var data: List<Friend>? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }


    fun setSearchViewFocus(state:Boolean){
        editTextView?.isFocusable = state
    }
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
        children.forEach {
            it.fitsSystemWindows = fitsSystemWindows
        }
    }

    private fun initView() {
        orientation = VERTICAL

        inputLayout = addInputView()
        editTextView = addEditTextView()
        cancelView = addCancelView()

        notifyChange()
    }

    private fun addInputView(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mInputHeight)
            this@SearchFriendView.addView(this)
        }
    }

    private fun addEditTextView(): SearchEditText {
        return SearchEditText(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mInputHeight).apply {
                weight = 1F
            }
            setStartIcon()
            setEndIcon()
            onFocusChangeListener = mFocusChange
            setHint(R.string.hint_please_input_friend_name)
            searchAction = this@SearchFriendView.searchAction
            isAutoSearch = false
            inputLayout?.addView(this)
        }
    }

    private fun addCancelView(): TextView {
        return TextView(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    marginStart = 10.dp
                    gravity = Gravity.CENTER_VERTICAL
                }
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mInputTextSize)
            setTextColor(getColor(R.color.color_4e5e73))
            setText(R.string.cancel)
            setOnClickListener {
                editTextView?.setText("")
                editTextView?.hideSoftInput()
                cancel?.invoke()
            }

            inputLayout?.addView(this)
        }
    }


    private fun notifyChange() {
        when (state) {
            State.COLLAPSING -> {
                editTextView?.isSelected = false
                cancelView?.gone()
            }
            State.EXPANDING -> {
                editTextView?.isSelected = true
                cancelView?.visible()
            }
        }
    }

    private val mFocusChange by lazy {
        OnFocusChangeListener { _, hasFocus ->
            state = if (hasFocus) {
                State.EXPANDING
            } else {
                State.COLLAPSING
            }
        }
    }

    private val mOnTouchListener by lazy {
        object : OnTouchListener {

            val gestureDetector by lazy {
                GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapUp(e: MotionEvent?): Boolean {
                        e?.apply {
                            editTextView?.apply {
                                val limit = width - paddingEnd - mLabelWidth
                                if (e.x > limit) {
                                    return true
                                }
                            }
                        }
                        return false
                    }

                })
            }

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                editTextView?.compoundDrawables?.apply {
                    this[2]?.apply {
                        if (gestureDetector.onTouchEvent(event)) {
                            when (this@SearchFriendView.state) {
                                State.EXPANDING -> {
                                    editTextView?.setText("")
                                }
                            }
                        }
                    }
                }
                return false
            }

        }
    }

    /**
     * 展开，收起状态
     */
    enum class State {

        /**
         * 收起状态
         */
        COLLAPSING,

        /**
         * 展开状态
         */
        EXPANDING
    }
}