package com.kotlin.android.card.monopoly.widget.search

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.View.OnFocusChangeListener
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.widget.search.SearchEditText

/**
 * 搜索卡片套装视图：
 *
 * Created on 2020/9/14.
 *
 * @author o.s
 */
class SearchCardSuitView : LinearLayout {

    private var mInputHeight = 36.dp
    private var mLabelWidth = 36.dp
    private var mLabelHeight = 36.dp
    private var mRecyclerHeight = 200.dp

    private val mRecyclerMarginTop = 10.dp
    private val mInputPaddingLeft = 16.dp

    private val mInputTextSize = 14F

    private var inputLayout: LinearLayout? = null
    private var editTextView: SearchEditText? = null
    private var cancelView: TextView? = null
    private var recyclerView: RecyclerView? = null

    private var mSuit: Suit? = null

    private val mAdapter by lazy {
        SearchCardSuitAdapter {
            mSuit = it
            editTextView?.setText(it.suitName)
            state = State.COLLAPSING
            editTextView?.hideSoftInput()

            action?.invoke(it)
        }
    }

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

    var action: ((data: Suit) -> Unit)? = null

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

    var data: List<Suit>? = null
        set(value) {
            field = value
            mAdapter.setData(value)
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
        recyclerView = addRecyclerView()

        notifyChange()
    }

    private fun addInputView(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mInputHeight)

            this@SearchCardSuitView.addView(this)
        }
    }

    private fun addEditTextView(): SearchEditText {
        return SearchEditText(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mInputHeight).apply {
                weight = 1F
            }
//            setBackground(
//                    colorRes = R.color.color_f4f6f8,
//                    cornerRadius = 18
//            )
//            gravity = Gravity.CENTER_VERTICAL
//
//            getStateListDrawable(
//                    normal = R.drawable.transparent,
//                    selected = R.drawable.ic_search_clear
//            ).apply {
//                setBounds(0, 0, mLabelWidth, mLabelHeight)
//                setCompoundDrawables(null, null, this, null)
//            }
//
//            setPadding(mInputPaddingLeft, 0, 0, 0)
//            setSingleLine()
//            setTextSize(TypedValue.COMPLEX_UNIT_SP, mInputTextSize)
//            setTextColor(getColor(R.color.color_4e5e73))
//            setHintTextColor(getColor(R.color.color_8798af))
//            setOnTouchListener(mOnTouchListener)
            setStartIcon()
            setEndIcon()
            onFocusChangeListener = mFocusChange
            setHint(R.string.hint_please_input_card_name)
            searchAction = this@SearchCardSuitView.searchAction

            inputLayout?.addView(this)
        }
    }

    private fun addCancelView(): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
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

    private fun addRecyclerView(): RecyclerView {
        return RecyclerView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                topMargin = mRecyclerMarginTop
            }
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter

            this@SearchCardSuitView.addView(this)
        }
    }

    private fun notifyChange() {
        when (state) {
            State.COLLAPSING -> {
                editTextView?.isSelected = false
                recyclerView?.gone()
                cancelView?.gone()
            }
            State.EXPANDING -> {
                editTextView?.isSelected = true
                recyclerView?.visible()
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

    private val mTextWatch by lazy {
        object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.apply {
                    state = if (s.isNotEmpty()) {
                        State.EXPANDING
                    } else {
                        State.COLLAPSING
                    }
                }
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
                            when (this@SearchCardSuitView.state) {
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