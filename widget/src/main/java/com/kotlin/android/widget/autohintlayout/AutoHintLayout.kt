package com.kotlin.android.widget.autohintlayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/13
 */
class AutoHintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var mAutoHintHelper: AutoHintHelper? = null
    private var mInDrawableStateChanged = false
    private var mEditText: EditText? = null

    private var mHintDatas: List<String>? = null
    private var mCurHintIndex =  -1
    private var mRunnable : Runnable = object : Runnable {
        override fun run() {
            mHintDatas?.let {
                mCurHintIndex = ++mCurHintIndex % it.size
                mAutoHintHelper?.setHintText(it[mCurHintIndex], true)
                postDelayed(this, 1500)
            }
        }
    }

    init {
        setWillNotDraw(false)
        mAutoHintHelper = AutoHintHelper(this, DefaultAutoHintDrawer())
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        if (child is EditText) {
            setEditText(child)
        }
    }

    private fun setEditText(editText: EditText) {
        mEditText = editText
        mAutoHintHelper?.setHintTextColor(mEditText?.hintTextColors)
        mEditText?.textSize?.let { mAutoHintHelper?.setHintTextSize(it) }
        mAutoHintHelper?.setTypeFace(mEditText?.typeface)
        mEditText?.gravity?.let { mAutoHintHelper?.setGravity(it) }
        mEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                // 输入字符变化的时候判断是否需要显示hint
                if (TextUtils.isEmpty(mEditText?.text.toString())) {
                    mAutoHintHelper?.showHint(true)
                } else {
                    mAutoHintHelper?.showHint(false)
                }
            }
        })
        mEditText?.setOnFocusChangeListener { v, hasFocus ->
            removeCallbacks(mRunnable)
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        mAutoHintHelper?.draw(canvas)
    }

    fun setHint(text: String, anim: Boolean) {
        mAutoHintHelper?.setHintText(text, anim)
    }

    fun setHints(datas: List<String>) {
        mHintDatas = datas
        post(mRunnable)
    }

    fun getCurHintText(): String? {
        return mAutoHintHelper?.mHintText
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        mEditText?.let {
            val rect = Rect()
            // 获取EditText在本View中的位置
            setChildRect(it, rect)

            val left = rect.left + it.compoundPaddingLeft
            val right = rect.right - it.compoundPaddingRight
            val top = rect.top + it.compoundPaddingTop
            val bottom = rect.bottom - it.compoundPaddingBottom

            // 提供AutoHintHelper hint的绘制区域
            mAutoHintHelper?.setHintBounds(
                left, top,
                right, bottom
            )
        }
    }

    private fun setChildRect(child: View, out: Rect) {
        child.apply {
            out.set(0, 0, width, height)
            // 添加child在本布局中的offset到rect
            offsetDescendantRectToMyCoords(this, out)
        }
    }

    override fun drawableStateChanged() {
        if (mInDrawableStateChanged) {
            // Some of the calls below will update the drawable state of child views. Since we're
            // using addStatesFromChildren we can get into infinite recursion, hence we'll just
            // exit in this instance
            return
        }
        mInDrawableStateChanged = true
        super.drawableStateChanged()
        mAutoHintHelper!!.setState(drawableState)
        mInDrawableStateChanged = false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(mRunnable)
    }
}