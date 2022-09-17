package com.kotlin.android.card.monopoly.widget.dialog.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import kotlinx.android.synthetic.main.view_open_box_anim.view.*

/**
 * 开宝箱动画视图
 *
 * Created on 2021/5/19.
 *
 * @author o.s
 */
class OpenBoxAnimView : FrameLayout {

    private val bitmapWidth = 215.dp // 660
    private val bitmapHeight = 315.dp // 990
    private val animCommonDuration = 1700L
    private val animLimitDuration = 2500L
    private val mData by lazy { ArrayList<Card>() }
    private val mBitmap by lazy { HashMap<String, Bitmap>() }
    private var mBoxType: BoxType = BoxType.COPPER
    private var isStop: Boolean = false
    private var index: Int = 0
    private val boxResMap = HashMap<BoxType, BoxRes>()
    private var currentBitmap: Bitmap? = null

    private val rightArrowDrawable by lazy {
        getDrawable(R.drawable.ic_issued_arrow_right)?.let {
            it.setBounds(0, 0, 20.dp, 20.dp)
            DrawableCompat.wrap(it).apply {
                DrawableCompat.setTintList(this, ColorStateList.valueOf(getColor(R.color.color_d4d4d4)))
            }
        }
    }

    var action: ((List<Card>?) -> Unit)? = null
    var dismiss: (() -> Unit)? = null
    var complete: ((OpenBoxView.Data?) -> Unit)? = null

    var data: OpenBoxView.Data? = null // RewardInfo?
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
        mBoxType = BoxType.obtain(data?.box?.cardBoxId ?: 0)
        currentBitmap = null

        mData.clear()
        data?.info?.limitCardList?.apply {
            forEach {
                it.isLimit = true
            }
            mData.addAll(this)
        }
        data?.info?.commonCardList?.apply {
            mData.addAll(this)
        }

        isStop = false
        index = 0
        show()
        next()
    }

    private fun getBoxRes(): BoxRes? {
        if (!boxResMap.containsKey(mBoxType)) {
            boxResMap[mBoxType] = BoxRes(mBoxType)
        }
        return boxResMap[mBoxType]
    }

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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
        children.forEach {
            it.fitsSystemWindows = fitsSystemWindows
        }
    }

    private fun initView() {
        val view = inflate(context, R.layout.view_open_box_anim, null)
        addView(view)

        background = getShapeDrawable(
            colorRes = R.color.color_bb45717c,
            endColorRes = R.color.color_ee155f81
        )

        animaView?.apply {
            imageAssetsFolder = "images/common"
            repeatCount = 0 // ValueAnimator.INFINITE
        }
        animaLimitView?.apply {
            imageAssetsFolder = "images/limit"
            repeatCount = 0 // ValueAnimator.INFINITE
        }

        ignoreView?.apply {
            setBackground(
                colorRes = R.color.color_1d2736,
                cornerRadius = 18.dpF
            )
            setCompoundDrawables(null, null, rightArrowDrawable, null)

            setOnClickListener {
                hide()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

    fun hide() {
        gone()
        animaView.gone()
        animaLimitView.gone()
        removeCallbacks(nextRunnable)
        animaView?.cancelAnimation()
        animaView?.clearAnimation()
        isStop = true
        complete?.invoke(data)
    }

    fun show() {
        visible()
    }

    private val nextRunnable by lazy {
        Runnable {
            ++index
            next()
        }
    }
    private fun next() {
        if (isStop) {
            return
        }
        if (index in 0 until mData.size) {
            mData[index].apply {
                cardCover?.let { url ->
                    val animDuration = if (isLimit) {
                        animLimitDuration
                    } else {
                        animCommonDuration
                    }

                    val bitmap = mBitmap[url]
                    if (bitmap?.isRecycled == false) {
                        playAnim(bitmap = bitmap, isLimit = isLimit)
                        postDelayed(nextRunnable, animDuration)

                    } else {
                        loadImage(
                            data = url
                        ) {
                            val newBitmap = it.toBitmap(bitmapWidth, bitmapHeight)
                            mBitmap[url] = newBitmap
                            playAnim(bitmap = newBitmap, isLimit = isLimit)
                            postDelayed(nextRunnable, animDuration)
                        }
                    }
                } ?: hide()
            }
        } else {
            hide()
        }
    }

    private fun playAnim(bitmap: Bitmap, isLimit: Boolean = false) {
        if (isStop) {
            return
        }
        if (isLimit) {
            playLimitAnim(bitmap)
        } else {
            playCommonAnim(bitmap)
        }
    }

    private fun playLimitAnim(bitmap: Bitmap) {
        animaView.gone()
        animaLimitView?.apply {
            visible()
            val boxRes = getBoxRes()
            updateBitmap("image_0", boxRes?.bitmap0)
            updateBitmap("image_1", boxRes?.bitmap1)
            updateBitmap("image_2", bitmap)
            updateBitmap("image_4", boxRes?.bitmap4)
            updateBitmap("image_5", boxRes?.bitmap5)
            updateBitmap("image_6", boxRes?.bitmap6)

            playAnimation()
        }
    }

    private fun playCommonAnim(bitmap: Bitmap) {
        animaLimitView.gone()
        animaView?.apply {
            visible()
            val boxRes = getBoxRes()
            updateBitmap("image_0", boxRes?.bitmap0)
            updateBitmap("image_1", boxRes?.bitmap1)
            updateBitmap("image_2", bitmap)
            updateBitmap("image_4", boxRes?.bitmap4)
            updateBitmap("image_5", boxRes?.bitmap5)
            updateBitmap("image_6", boxRes?.bitmap6)

            playAnimation()
        }
    }

    fun recycle() {
        mBitmap.values.forEach {
            it.recycle()
        }
        mBitmap.clear()

        boxResMap.values.forEach {
            it.recycle()
        }
        boxResMap.clear()
    }

}