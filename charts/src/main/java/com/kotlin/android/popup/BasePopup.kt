package com.kotlin.android.popup

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.transition.Transition
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import androidx.annotation.*
import androidx.core.widget.PopupWindowCompat

/**
 * Created by zyyoona7 on 2017/8/3.
 *
 *
 * PopupWindow封装
 */
abstract class BasePopup<T : Any?> : PopupWindow.OnDismissListener {

    /**
     * 获取PopupWindow对象
     *
     * @return
     */
    //PopupWindow对象
    var popupWindow: PopupWindow? = null
        private set

    //context
    private var mContext: Context? = null

    //contentView
    private var mContentView: View? = null

    //布局id
    private var mLayoutId = 0

    //获取焦点
    private var mFocusable = true

    //设置PopupWindow是否能响应点击事件
    private var mTouchable = true

    //是否触摸之外dismiss
    private var mOutsideTouchable = true

    /**
     * 获取PopupWindow 宽
     *
     * @return
     */
    //宽高
    var width: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        private set

    /**
     * 获取PopupWindow 高
     *
     * @return
     */
    var height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        private set
    private var mAnimationStyle = 0
    private var mOnDismissListener: PopupWindow.OnDismissListener? = null

    //弹出pop时，背景是否变暗
    private var isBackgroundDim = false

    //背景变暗时透明度
    private var mDimValue = DEFAULT_DIM

    //背景变暗颜色
    @ColorInt
    private var mDimColor = Color.BLACK

    //背景变暗的view
    @NonNull
    private var mDimView: ViewGroup? = null
    private var mEnterTransition: Transition? = null
    private var mExitTransition: Transition? = null
    private var mFocusAndOutsideEnable = true
    private var mAnchorView: View? = null

    /**
     * 获取横向Gravity
     *
     * @return
     */
    @YGravity
    var yGravity: Int = YGravity.BELOW
        private set

    /**
     * 获取纵向Gravity
     *
     * @return
     */
    @XGravity
    var xGravity: Int = XGravity.LEFT
        private set

    /**
     * 获取x轴方向的偏移
     *
     * @return
     */
    var offsetX = 0
        private set

    /**
     * 获取y轴方向的偏移
     *
     * @return
     */
    var offsetY = 0
        private set
    private var mInputMethodMode = PopupWindow.INPUT_METHOD_FROM_FOCUSABLE
    private var mSoftInputMode: Int = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED

    //是否重新测量宽高
    private var isNeedReMeasureWH = false

    /**
     * 是否精准的宽高获取完成
     *
     * @return
     */
    //真实的宽高是否已经准备好
    var isRealWHAlready = false
        private set
    private var isAtAnchorViewMethod = false
    private var mOnRealWHAlreadyListener: OnRealWHAlreadyListener? = null
    protected fun self(): T {
        return this as T
    }

    fun apply(): T {
        if (popupWindow == null) {
            popupWindow = PopupWindow()
        }
        onPopupWindowCreated()
        initContentViewAndWH()
        onPopupWindowViewCreated(mContentView)
        if (mAnimationStyle != 0) {
            popupWindow?.animationStyle = mAnimationStyle
        }
        initFocusAndBack()
        popupWindow?.setOnDismissListener(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mEnterTransition != null) {
                popupWindow?.enterTransition = mEnterTransition
            }
            if (mExitTransition != null) {
                popupWindow?.exitTransition = mExitTransition
            }
        }
        return self()
    }

    private fun initContentViewAndWH() {
        if (mContentView == null) {
            mContentView = if (mLayoutId != 0 && mContext != null) {
                LayoutInflater.from(mContext).inflate(mLayoutId, null)
            } else {
                throw IllegalArgumentException("The content view is null,the layoutId=$mLayoutId,context=$mContext")
            }
        }
        popupWindow?.contentView = mContentView
        if (width > 0 || width == ViewGroup.LayoutParams.WRAP_CONTENT || width == ViewGroup.LayoutParams.MATCH_PARENT) {
            popupWindow?.width = width
        } else {
            popupWindow?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        if (height > 0 || height == ViewGroup.LayoutParams.WRAP_CONTENT || height == ViewGroup.LayoutParams.MATCH_PARENT) {
            popupWindow?.height = height
        } else {
            popupWindow?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        //测量contentView大小
        //可能不准
        measureContentView()
        //获取contentView的精准大小
        registerOnGlobalLayoutListener()
        popupWindow?.inputMethodMode = mInputMethodMode
        popupWindow?.softInputMode = mSoftInputMode
    }

    private fun initFocusAndBack() {
        if (!mFocusAndOutsideEnable) {
            //from https://github.com/pinguo-zhouwei/CustomPopwindow
            // 点击外部区域不消失须要设置底下三个方法
            popupWindow?.isOutsideTouchable = false
            popupWindow?.isFocusable = false
            popupWindow?.isTouchable = true
            popupWindow?.setBackgroundDrawable(null)
            //注意下面这三个是contentView 不是PopupWindow，响应返回按钮事件
            popupWindow?.contentView?.isFocusable = true
            popupWindow?.contentView?.isFocusableInTouchMode = true
            popupWindow?.contentView?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popupWindow?.dismiss()
                    return@OnKeyListener true
                }
                false
            })
            //在Android 6.0以上 ，只能通过拦截事件来解决
            popupWindow?.setTouchInterceptor(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    val x = event.x.toInt()
                    val y = event.y.toInt()
                    if (event.action == MotionEvent.ACTION_DOWN
                            && (x < 0 || x >= width || y < 0 || y >= height)
                    ) {
                        //outside
                        Log.d(TAG, "onTouch outside:mWidth=" + width + ",mHeight=" + height)
                        return true
                    } else if (event.action == MotionEvent.ACTION_OUTSIDE) {
                        //outside
                        Log.d(TAG, "onTouch outside event:mWidth=" + width + ",mHeight=" + height)
                        return true
                    }
                    return false
                }
            })
        } else {
            popupWindow?.isFocusable = mFocusable
            popupWindow?.isTouchable = mTouchable
            popupWindow?.isOutsideTouchable = mOutsideTouchable
            popupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
    /****自定义生命周期方法 */
    /**
     * PopupWindow对象创建完成
     */
    protected fun onPopupWindowCreated() {
        //执行设置PopupWindow属性也可以通过Builder中设置
        //setContentView(x,x,x);
        //...
        initAttributes()
    }

    protected fun onPopupWindowViewCreated(contentView: View?) {
        initViews(contentView, self())
    }

    protected fun onPopupWindowDismiss() {}

    /**
     * 可以在此方法中设置PopupWindow需要的属性
     */
    protected abstract fun initAttributes()

    /**
     * 初始化view {@see getView()}
     *
     * @param view
     */
    protected abstract fun initViews(view: View?, popup: T)

    /**
     * 是否需要测量 contentView的大小
     * 如果需要重新测量并为宽高赋值
     * 注：此方法获取的宽高可能不准确 MATCH_PARENT时无法获取准确的宽高
     */
    private fun measureContentView() {
        val contentView = contentView
        if (width <= 0 || height <= 0) {
            //测量大小
            contentView?.measure(0, View.MeasureSpec.UNSPECIFIED)
            if (width <= 0) {
                if (contentView != null) width = contentView.measuredWidth
            }
            if (height <= 0) {
                if (contentView != null) height = contentView.measuredHeight
            }
        }
    }

    /**
     * 注册GlobalLayoutListener 获取精准的宽高
     */
    private fun registerOnGlobalLayoutListener() {
        contentView?.apply {
            viewTreeObserver?.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    this@BasePopup.width = width
                    this@BasePopup.height = height
                    isRealWHAlready = true
                    isNeedReMeasureWH = false
                    if (mOnRealWHAlreadyListener != null) {
                        mAnchorView?.apply {
                            mOnRealWHAlreadyListener?.onRealWHAlready(this@BasePopup,
                                    width,
                                    height,
                                    if (mAnchorView == null) 0 else width,
                                    if (mAnchorView == null) 0 else height)
                        }
                    }
                    // Log.d(TAG, "onGlobalLayout finished. isShowing=" + isShowing());
                    if (isShowing && isAtAnchorViewMethod) {
                        updateLocation(width,
                                height,
                                mAnchorView,
                                yGravity,
                                xGravity,
                                offsetX,
                                offsetY)
                    }
                }
            })
        }
    }

    /**
     * 更新 PopupWindow 到精准的位置
     *
     * @param width
     * @param height
     * @param anchor
     * @param yGravity
     * @param xGravity
     * @param x
     * @param y
     */
    private fun updateLocation(
            width: Int,
            height: Int,
            @NonNull anchor: View?,
            @YGravity yGravity: Int,
            @XGravity xGravity: Int,
            x: Int,
            y: Int,
    ) {
        var x = x
        var y = y
        if (popupWindow == null) {
            return
        }
        x = calculateX(anchor, xGravity, width, x)
        y = calculateY(anchor, yGravity, height, y)
        popupWindow?.update(anchor, x, y, width, height)
    }

    /****设置属性方法 */
    fun setContext(context: Context?): T {
        mContext = context
        return self()
    }

    fun setContentView(contentView: View?): T {
        mContentView = contentView
        mLayoutId = 0
        return self()
    }

    fun setContentView(@LayoutRes layoutId: Int): T {
        mContentView = null
        mLayoutId = layoutId
        return self()
    }

    fun setContentView(context: Context?, @LayoutRes layoutId: Int): T {
        mContext = context
        mContentView = null
        mLayoutId = layoutId
        return self()
    }

    fun setContentView(contentView: View?, width: Int, height: Int): T {
        mContentView = contentView
        mLayoutId = 0
        this.width = width
        this.height = height
        return self()
    }

    fun setContentView(@LayoutRes layoutId: Int, width: Int, height: Int): T {
        mContentView = null
        mLayoutId = layoutId
        this.width = width
        this.height = height
        return self()
    }

    fun setContentView(context: Context?, @LayoutRes layoutId: Int, width: Int, height: Int): T {
        mContext = context
        mContentView = null
        mLayoutId = layoutId
        this.width = width
        this.height = height
        return self()
    }

    fun setWidth(width: Int): T {
        this.width = width
        return self()
    }

    fun setHeight(height: Int): T {
        this.height = height
        return self()
    }

    fun setAnchorView(view: View?): T {
        mAnchorView = view
        return self()
    }

    fun setYGravity(@YGravity yGravity: Int): T {
        this.yGravity = yGravity
        return self()
    }

    fun setXGravity(@XGravity xGravity: Int): T {
        this.xGravity = xGravity
        return self()
    }

    fun setOffsetX(offsetX: Int): T {
        this.offsetX = offsetX
        return self()
    }

    fun setOffsetY(offsetY: Int): T {
        this.offsetY = offsetY
        return self()
    }

    fun setAnimationStyle(@StyleRes animationStyle: Int): T {
        mAnimationStyle = animationStyle
        return self()
    }

    fun setFocusable(focusable: Boolean): T {
        mFocusable = focusable
        return self()
    }

    fun setTouchable(touchable: Boolean): T {
        mTouchable = touchable
        return self()
    }

    fun setOutsideTouchable(outsideTouchable: Boolean): T {
        mOutsideTouchable = outsideTouchable
        return self()
    }

    /**
     * 是否可以点击PopupWindow之外的地方dismiss
     *
     * @param focusAndOutsideEnable
     * @return
     */
    fun setFocusAndOutsideEnable(focusAndOutsideEnable: Boolean): T {
        mFocusAndOutsideEnable = focusAndOutsideEnable
        return self()
    }

    /**
     * 背景变暗支持api>=18
     *
     * @param isDim
     * @return
     */
    fun setBackgroundDimEnable(isDim: Boolean): T {
        isBackgroundDim = isDim
        return self()
    }

    fun setDimValue(@FloatRange(from = 0.0, to = 1.0) dimValue: Float): T {
        mDimValue = dimValue
        return self()
    }

    fun setDimColor(@ColorInt color: Int): T {
        mDimColor = color
        return self()
    }

    fun setDimView(@NonNull dimView: ViewGroup?): T {
        mDimView = dimView
        return self()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun setEnterTransition(enterTransition: Transition?): T {
        mEnterTransition = enterTransition
        return self()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun setExitTransition(exitTransition: Transition?): T {
        mExitTransition = exitTransition
        return self()
    }

    fun setInputMethodMode(mode: Int): T {
        mInputMethodMode = mode
        return self()
    }

    fun setSoftInputMode(mode: Int): T {
        mSoftInputMode = mode
        return self()
    }

    /**
     * 是否需要重新获取宽高
     *
     * @param needReMeasureWH
     * @return
     */
    fun setNeedReMeasureWH(needReMeasureWH: Boolean): T {
        isNeedReMeasureWH = needReMeasureWH
        return self()
    }

    /**
     * 检查是否调用了 apply() 方法
     *
     * @param isAtAnchorView 是否是 showAt
     */
    private fun checkIsApply(isAtAnchorView: Boolean) {
        if (isAtAnchorViewMethod != isAtAnchorView) {
            isAtAnchorViewMethod = isAtAnchorView
        }
        if (popupWindow == null) {
            apply()
        }
    }

    /**
     * 使用此方法需要在创建的时候调用setAnchorView()等属性设置{@see setAnchorView()}
     */
    fun showAsDropDown() {
        if (mAnchorView == null) {
            return
        }
        showAsDropDown(mAnchorView, offsetX, offsetY)
    }

    /**
     * PopupWindow自带的显示方法
     *
     * @param anchor
     * @param offsetX
     * @param offsetY
     */
    fun showAsDropDown(anchor: View?, offsetX: Int, offsetY: Int) {
        //防止忘记调用 apply() 方法
        checkIsApply(false)
        handleBackgroundDim()
        mAnchorView = anchor
        this.offsetX = offsetX
        this.offsetY = offsetY
        //是否重新获取宽高
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener()
        }
        popupWindow?.showAsDropDown(anchor, this.offsetX, this.offsetY)
    }

    fun showAsDropDown(anchor: View?) {
        //防止忘记调用 apply() 方法
        checkIsApply(false)
        handleBackgroundDim()
        mAnchorView = anchor
        //是否重新获取宽高
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener()
        }
        popupWindow?.showAsDropDown(anchor)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun showAsDropDown(anchor: View?, offsetX: Int, offsetY: Int, gravity: Int) {
        //防止忘记调用 apply() 方法
        checkIsApply(false)
        handleBackgroundDim()
        mAnchorView = anchor
        this.offsetX = offsetX
        this.offsetY = offsetY
        //是否重新获取宽高
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener()
        }
        popupWindow?.let {
            if (anchor != null) {
                PopupWindowCompat.showAsDropDown(it, anchor, this.offsetX, this.offsetY, gravity)
            }
        }
    }

    fun showAtLocation(parent: View?, gravity: Int, offsetX: Int, offsetY: Int) {
        //防止忘记调用 apply() 方法
        checkIsApply(false)
        handleBackgroundDim()
        mAnchorView = parent
        this.offsetX = offsetX
        this.offsetY = offsetY
        //是否重新获取宽高
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener()
        }
        popupWindow?.showAtLocation(parent, gravity, this.offsetX, this.offsetY)
    }

    /**
     * 相对anchor view显示
     *
     *
     * 使用此方法需要在创建的时候调用setAnchorView()等属性设置{@see setAnchorView()}
     *
     *
     * 注意：如果使用 VerticalGravity 和 HorizontalGravity 时，请确保使用之后 PopupWindow 没有超出屏幕边界，
     * 如果超出屏幕边界，VerticalGravity 和 HorizontalGravity 可能无效，从而达不到你想要的效果。
     */
    fun showAtAnchorView() {
        if (mAnchorView == null) {
            return
        }
        showAtAnchorView(mAnchorView, yGravity, xGravity)
    }
    /**
     * 相对anchor view显示，适用 宽高不为match_parent
     *
     *
     * 注意：如果使用 VerticalGravity 和 HorizontalGravity 时，请确保使用之后 PopupWindow 没有超出屏幕边界，
     * 如果超出屏幕边界，VerticalGravity 和 HorizontalGravity 可能无效，从而达不到你想要的效果。
     *
     * @param anchor
     * @param vertGravity  垂直方向的对齐方式
     * @param horizGravity 水平方向的对齐方式
     * @param x            水平方向的偏移
     * @param y            垂直方向的偏移
     */
    /**
     * 相对anchor view显示，适用 宽高不为match_parent
     *
     *
     * 注意：如果使用 VerticalGravity 和 HorizontalGravity 时，请确保使用之后 PopupWindow 没有超出屏幕边界，
     * 如果超出屏幕边界，VerticalGravity 和 HorizontalGravity 可能无效，从而达不到你想要的效果。     *
     *
     * @param anchor
     * @param vertGravity
     * @param horizGravity
     */
    @JvmOverloads
    fun showAtAnchorView(
            @NonNull anchor: View?,
            @YGravity vertGravity: Int,
            @XGravity horizGravity: Int,
            x: Int = 0,
            y: Int = 0,
    ) {
        //防止忘记调用 apply() 方法
        var x = x
        var y = y
        checkIsApply(true)
        mAnchorView = anchor
        offsetX = x
        offsetY = y
        yGravity = vertGravity
        xGravity = horizGravity
        //处理背景变暗
        handleBackgroundDim()
        x = calculateX(anchor, horizGravity, width, offsetX)
        y = calculateY(anchor, vertGravity, height, offsetY)
        //是否重新获取宽高
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener()
        }
        //        Log.i(TAG, "showAtAnchorView: w=" + measuredW + ",y=" + measuredH);
        popupWindow?.let {
            if (anchor != null) {
                PopupWindowCompat.showAsDropDown(it, anchor, x, y, Gravity.NO_GRAVITY)
            }
        }
    }

    /**
     * 根据垂直gravity计算y偏移
     *
     * @param anchor
     * @param vertGravity
     * @param measuredH
     * @param y
     * @return
     */
    private fun calculateY(anchor: View?, vertGravity: Int, measuredH: Int, y: Int): Int {
        var y = y
        when (vertGravity) {
            YGravity.ABOVE ->                 //anchor view之上
                if (anchor != null) y -= measuredH + anchor.height
            YGravity.ALIGN_BOTTOM ->                 //anchor view底部对齐
                y -= measuredH
            YGravity.CENTER ->                 //anchor view垂直居中
                if (anchor != null) y -= anchor.height / 2 + measuredH / 2
            YGravity.ALIGN_TOP ->                 //anchor view顶部对齐
                if (anchor != null) y -= anchor.height
            YGravity.BELOW -> {
            }
        }
        return y
    }

    /**
     * 根据水平gravity计算x偏移
     *
     * @param anchor
     * @param horizGravity
     * @param measuredW
     * @param x
     * @return
     */
    private fun calculateX(anchor: View?, horizGravity: Int, measuredW: Int, x: Int): Int {
        var x = x
        when (horizGravity) {
            XGravity.LEFT ->                 //anchor view左侧
                x -= measuredW
            XGravity.ALIGN_RIGHT ->                 //与anchor view右边对齐
                if (anchor != null) x -= measuredW - anchor.width
            XGravity.CENTER ->                 //anchor view水平居中
                if (anchor != null) x += anchor.width / 2 - measuredW / 2
            XGravity.ALIGN_LEFT -> {
            }
            XGravity.RIGHT ->                 //anchor view右侧
                if (anchor != null) x += anchor.width
        }
        return x
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    fun setOnDismissListener(listener: PopupWindow.OnDismissListener?): T {
        mOnDismissListener = listener
        return self()
    }

    fun setOnRealWHAlreadyListener(listener: OnRealWHAlreadyListener?): T {
        mOnRealWHAlreadyListener = listener
        return self()
    }

    /**
     * 处理背景变暗
     * https://blog.nex3z.com/2016/12/04/%E5%BC%B9%E5%87%BApopupwindow%E5%90%8E%E8%AE%A9%E8%83%8C%E6%99%AF%E5%8F%98%E6%9A%97%E7%9A%84%E6%96%B9%E6%B3%95/
     */
    private fun handleBackgroundDim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!isBackgroundDim) {
                return
            }
            if (mDimView != null) {
                applyDim(mDimView)
            } else {
                if (contentView != null && contentView?.context != null &&
                        contentView?.context is Activity
                ) {
                    val activity: Activity = contentView?.context as Activity
                    applyDim(activity)
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun applyDim(activity: Activity) {
        val parent: ViewGroup = activity.window.decorView.rootView as ViewGroup
        //activity跟布局
//        ViewGroup parent = (ViewGroup) parent1.getChildAt(0);
        val dimDrawable: Drawable = ColorDrawable(mDimColor)
        dimDrawable.setBounds(0, 0, parent.width, parent.height)
        dimDrawable.alpha = (255 * mDimValue).toInt()
        val overlay: ViewGroupOverlay = parent.overlay
        overlay.add(dimDrawable)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun applyDim(dimView: ViewGroup?) {
        val dimDrawable: Drawable = ColorDrawable(mDimColor)
        dimView?.width?.let { dimDrawable.setBounds(0, 0, it, dimView.height) }
        dimDrawable.alpha = (255 * mDimValue).toInt()
        val overlay: ViewGroupOverlay? = dimView?.overlay
        overlay?.add(dimDrawable)
    }

    /**
     * 清除背景变暗
     */
    private fun clearBackgroundDim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (isBackgroundDim) {
                if (mDimView != null) {
                    clearDim(mDimView)
                } else {
                    if (contentView != null) {
                        val activity: Activity = contentView?.context as Activity
                        if (activity != null) {
                            clearDim(activity)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun clearDim(activity: Activity) {
        val parent: ViewGroup = activity.window.decorView.rootView as ViewGroup
        //activity跟布局
//        ViewGroup parent = (ViewGroup) parent1.getChildAt(0);
        val overlay: ViewGroupOverlay = parent.overlay
        overlay.clear()
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun clearDim(dimView: ViewGroup?) {
        val overlay: ViewGroupOverlay? = dimView?.overlay
        overlay?.clear()
    }

    /**
     * 获取PopupWindow中加载的view
     *
     * @return
     */
    val contentView: View?
        get() = if (popupWindow != null) {
            popupWindow?.contentView
        } else {
            null
        }

    /**
     * 是否正在显示
     *
     * @return
     */
    val isShowing: Boolean
        get() = popupWindow != null && popupWindow?.isShowing == true

    /**
     * 获取view
     *
     * @param viewId
     * @param <T>
     * @return
    </T> */
    fun <T : View?> findViewById(@IdRes viewId: Int): T? {
        var view: View? = null
        if (contentView != null) {
            view = contentView?.findViewById(viewId)
        }
        return view as T?
    }

    /**
     * 消失
     */
    fun dismiss() {
        if (popupWindow != null) {
            popupWindow?.dismiss()
        }
    }

    override fun onDismiss() {
        handleDismiss()
    }

    /**
     * PopupWindow消失后处理一些逻辑
     */
    private fun handleDismiss() {
        if (mOnDismissListener != null) {
            mOnDismissListener?.onDismiss()
        }

        //清除背景变暗
        clearBackgroundDim()
        if (popupWindow != null && popupWindow?.isShowing == true) {
            popupWindow?.dismiss()
        }
        onPopupWindowDismiss()
    }

    /**
     * PopupWindow是否显示在window中
     * 用于获取准确的PopupWindow宽高，可以重新设置偏移量
     */
    interface OnRealWHAlreadyListener {
        /**
         * 在 show方法之后 updateLocation之前执行
         *
         * @param popWidth  PopupWindow准确的宽
         * @param popHeight PopupWindow准确的高
         * @param anchorW   锚点View宽
         * @param anchorH   锚点View高
         */
        fun onRealWHAlready(
                basePopup: BasePopup<*>?,
                popWidth: Int,
                popHeight: Int,
                anchorW: Int,
                anchorH: Int,
        )
    }

    /**
     * 获取屏幕宽高度
     */
    open fun getScreenWidthHeight(): IntArray {
        val intArray = IntArray(2)
        mContext?.apply {
            intArray[0] = resources.displayMetrics.widthPixels
            intArray[1] = resources.displayMetrics.heightPixels
        }
        return intArray
    }

    companion object {
        private const val TAG = "EasyPopup"
        private const val DEFAULT_DIM = 0.7f
    }
}