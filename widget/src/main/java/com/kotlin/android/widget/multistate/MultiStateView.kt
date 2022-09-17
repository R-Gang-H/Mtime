package com.kotlin.android.widget.multistate

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.ShapeExt.setViewSelector
import com.kotlin.android.widget.R
import org.jetbrains.anko.find


/**
 * Created by Android Studio.
 * Author: zl
 * Date: 2019/4/3
 * Time: 12:44 PM
 * 多状态视图，配合列表控件使用
 */
class MultiStateView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val VIEW_STATE_UNKNOWN = -1
        const val VIEW_STATE_CONTENT = 0
        const val VIEW_STATE_ERROR = 1
        const val VIEW_STATE_EMPTY = 2
        const val VIEW_STATE_NO_NET = 3

    }

    @IntDef(VIEW_STATE_UNKNOWN, VIEW_STATE_CONTENT, VIEW_STATE_ERROR, VIEW_STATE_EMPTY, VIEW_STATE_NO_NET)
    annotation class ViewState


    private lateinit var mInflater: LayoutInflater
    private var mContentView: View? = null
    private lateinit var mEmptyView: View
    private lateinit var emptyIv: ImageView
    private lateinit var emptyTv: TextView
    private lateinit var mErrorView: View
    private lateinit var errorTv: TextView
    private lateinit var mNoNetView: View
    private lateinit var noNetTv: TextView
    private var mAnimateViewChanges: Boolean = false
    private var mListener: MultiStateListener? = null
    private var mViewState: Int = VIEW_STATE_UNKNOWN

    init {
        this.initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        mInflater = LayoutInflater.from(context)
        val a = context.obtainStyledAttributes(attrs, R.styleable.MultiStateView)

        val emptyViewResId = a.getResourceId(R.styleable.MultiStateView_emptyView, R.layout.layout_state_empty)
        if (emptyViewResId > -1) {
            mEmptyView = mInflater.inflate(emptyViewResId, this, false)
            emptyIv = mEmptyView.find(R.id.emptyIv)
            emptyTv = mEmptyView.find(R.id.emptyTv)
            addView(mEmptyView, mEmptyView.layoutParams)
        }
        val errorViewResId = a.getResourceId(R.styleable.MultiStateView_errorView, R.layout.layout_state_error)
        if (errorViewResId > -1) {
            mErrorView = mInflater.inflate(errorViewResId, this, false)
            errorTv = mErrorView.find(R.id.retryErrorTv)
            setViewSelector(view = errorTv, normalColor = R.color.color_20a0da,
                    pressColor = R.color.color_20a0da)
            addView(mErrorView, mErrorView.layoutParams)
        }
        val noNetViewResId = a.getResourceId(R.styleable.MultiStateView_netView, R.layout.layout_state_no_net)
        if (errorViewResId > -1) {
            mNoNetView = mInflater.inflate(noNetViewResId, this, false)
            noNetTv = mNoNetView.find(R.id.retryNetTv)
            setViewSelector(view = noNetTv, normalColor = R.color.color_20a0da,
                    pressColor = R.color.color_20a0da)
            addView(mNoNetView, mNoNetView.layoutParams)
        }
        val viewState = a.getInt(R.styleable.MultiStateView_viewState, VIEW_STATE_CONTENT)
        mAnimateViewChanges = a.getBoolean(R.styleable.MultiStateView_animateViewChanges, false)

        mViewState = when (viewState) {
            VIEW_STATE_CONTENT -> VIEW_STATE_CONTENT

            VIEW_STATE_ERROR -> VIEW_STATE_ERROR

            VIEW_STATE_EMPTY -> VIEW_STATE_EMPTY

            VIEW_STATE_NO_NET -> VIEW_STATE_NO_NET

            VIEW_STATE_UNKNOWN -> VIEW_STATE_UNKNOWN
            else -> VIEW_STATE_UNKNOWN
        }

        a.recycle()
        initListener()
    }

    private fun initListener() {
        errorTv.onClick {
            mListener?.apply {
                normalViewState()
                onMultiStateChanged(mViewState)
            }
        }

        noNetTv.onClick {
            mListener?.apply {
                normalViewState()
                onMultiStateChanged(mViewState)
            }
        }
    }

    private fun normalViewState() {
        mContentView?.visibility = View.VISIBLE
        mEmptyView.visibility = View.GONE
        mNoNetView.visibility = View.GONE
        mErrorView.visibility = View.GONE
    }

    fun setEmptyResource(
            @DrawableRes resId: Int = R.drawable.icon_exception,
            @StringRes resid: Int = R.string.empty_normal_tv) {
        emptyIv.setImageResource(resId)
        emptyTv.setText(resid)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mContentView == null) throw IllegalArgumentException("Content view is not defined")
        setView(VIEW_STATE_UNKNOWN)
    }

    override fun addView(child: View) {
        if (isValidContentView(child)) mContentView = child
        super.addView(child)
    }

    override fun addView(child: View, index: Int) {
        if (isValidContentView(child)) mContentView = child
        super.addView(child, index)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (isValidContentView(child)) mContentView = child
        super.addView(child, index, params)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        if (isValidContentView(child)) mContentView = child
        super.addView(child, params)
    }

    override fun addView(child: View, width: Int, height: Int) {
        if (isValidContentView(child)) mContentView = child
        super.addView(child, width, height)
    }

    override fun addViewInLayout(child: View, index: Int, params: ViewGroup.LayoutParams): Boolean {
        if (isValidContentView(child)) mContentView = child
        return super.addViewInLayout(child, index, params)
    }

    override fun addViewInLayout(child: View, index: Int, params: ViewGroup.LayoutParams, preventRequestLayout: Boolean): Boolean {
        if (isValidContentView(child)) mContentView = child
        return super.addViewInLayout(child, index, params, preventRequestLayout)
    }

    fun getView(@ViewState state: Int): View? {
        return when (state) {

            VIEW_STATE_CONTENT -> mContentView

            VIEW_STATE_EMPTY -> mEmptyView

            VIEW_STATE_ERROR -> mErrorView

            VIEW_STATE_NO_NET -> mNoNetView

            else -> null
        }
    }

    /**
     * Returns the current {@link com.library.widgets.MultiStateView.ViewState}
     *
     * @return
     */
    @ViewState
    fun getViewState(): Int {
        return mViewState
    }

    /**
     * Sets the current [com.library.widgets.MultiStateView.ViewState]
     *
     * @param state The [com.library.widgets.MultiStateView.ViewState] to set [MultiStateView] to
     */
    fun setViewState(@ViewState state: Int) {
        mViewState = state
        setView(mViewState)
    }

    /**
     * Shows the [View] based on the [com.library.widgets.MultiStateView.ViewState]
     */
    private fun setView(@ViewState previousState: Int) {
        when (mViewState) {
            VIEW_STATE_EMPTY -> {
                mErrorView.visibility = View.GONE
                mNoNetView.visibility = View.GONE
                mContentView?.visibility = View.GONE

                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState))
                } else {
                    mEmptyView.visibility = View.VISIBLE
                }
            }

            VIEW_STATE_ERROR -> {
                mContentView?.visibility = View.GONE
                mEmptyView.visibility = View.GONE
                mNoNetView.visibility = View.GONE
                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState))
                } else {
                    mErrorView.visibility = View.VISIBLE
                }
            }
            VIEW_STATE_NO_NET -> {
                mContentView?.visibility = View.GONE
                mEmptyView.visibility = View.GONE
                mErrorView.visibility = View.GONE

                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState))
                } else {
                    mNoNetView.visibility = View.VISIBLE
                }
            }

            VIEW_STATE_CONTENT -> {
                if (mContentView == null) {
                    // Should never happen, the view should throw an exception if no content view is present upon creation
                    throw NullPointerException("Content View")
                }

                mEmptyView.visibility = View.GONE
                mErrorView.visibility = View.GONE
                mNoNetView.visibility = View.GONE
                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState))
                } else {
                    mContentView?.visibility = View.VISIBLE
                }
            }
            else -> {
                if (mContentView == null) {
                    throw NullPointerException("Content View")
                }
                mEmptyView.visibility = View.GONE
                mErrorView.visibility = View.GONE
                mNoNetView.visibility = View.GONE
                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState))
                } else {
                    mContentView?.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Checks if the given [View] is valid for the Content View
     *
     * @param view The [View] to check
     * @return
     */
    private fun isValidContentView(view: View): Boolean {
        return if (mContentView != null && mContentView !== view) {
            false
        } else view !== mEmptyView && view !== mErrorView && view !== mNoNetView

    }

    /**
     * Sets the view for the given view state
     *
     * @param view          The [View] to use
     * @param state         The [com.library.widgets.MultiStateView.ViewState]to set
     * @param switchToState If the [com.library.widgets.MultiStateView.ViewState] should be switched to
     */
    fun setViewForState(view: View, @ViewState state: Int, switchToState: Boolean) {
        when (state) {

            VIEW_STATE_EMPTY -> {
                removeView(mEmptyView)
                mEmptyView = view
                addView(mEmptyView)
            }

            VIEW_STATE_ERROR -> {
                removeView(mErrorView)
                mErrorView = view
                addView(mErrorView)
            }

            VIEW_STATE_NO_NET -> {
                removeView(mNoNetView)
                mNoNetView = view
                addView(mNoNetView)
            }

            VIEW_STATE_CONTENT -> {
                if (mContentView != null) removeView(mContentView)
                mContentView = view
                mContentView?.let { addView(it) }
            }
        }

        setView(VIEW_STATE_UNKNOWN)
        if (switchToState) setViewState(state)
    }


    /**
     * Sets the [View] for the given [com.library.widgets.MultiStateView.ViewState]
     *
     * @param layoutRes     Layout resource id
     * @param state         The [com.library.widgets.MultiStateView.ViewState] to set
     * @param switchToState If the [com.library.widgets.MultiStateView.ViewState] should be switched to
     */
    private fun setViewForState(@LayoutRes layoutRes: Int, @ViewState state: Int, switchToState: Boolean) {
        val view = mInflater.inflate(layoutRes, this, false)
        setViewForState(view, state, switchToState)
    }

    /**
     * Sets the [View] for the given [com.library.widgets.MultiStateView.ViewState]
     *
     * @param layoutRes Layout resource id
     * @param state     The [View] state to set
     */
    fun setViewForState(@LayoutRes layoutRes: Int, @ViewState state: Int) {
        setViewForState(layoutRes, state, false)
    }

    /**
     * Sets whether an animate will occur when changing between [ViewState]
     *
     * @param animate
     */
    fun setAnimateLayoutChanges(animate: Boolean) {
        mAnimateViewChanges = animate
    }

    /**
     * Sets the [StateListener] for the view
     *
     * @param listener The [StateListener] that will receive callbacks
     */
    fun setMultiStateListener(listener: MultiStateListener) {
        mListener = listener
    }

    /**
     * Animates the layout changes between [ViewState]
     *
     * @param previousView The view that it was currently on
     */
    private fun animateLayoutChange(previousView: View?) {
        if (previousView == null) {
            getView(mViewState)?.visibility = View.VISIBLE
            return
        }

        previousView.visibility = View.VISIBLE
        val anim = ObjectAnimator.ofFloat(previousView, "alpha", 1.0f, 0.0f).setDuration(250L)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                previousView.visibility = View.GONE
                getView(mViewState)?.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(getView(mViewState), "alpha", 0.0f, 1.0f).setDuration(250L).start()
            }
        })
        anim.start()
    }

    interface MultiStateListener {
        /**
         * Callback for when the [ViewState] has changed
         *
         * @param viewState The [ViewState] that was switched to
         */
        fun onMultiStateChanged(@ViewState viewState: Int)
    }

}