package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IntRange
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.CurrentIssueSuitList
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import kotlinx.android.synthetic.main.view_current_issue.view.*

/**
 * 本期发行视图
 *
 * Created on 2021/5/19.
 *
 * @author o.s
 */
class CurrentIssueView : FrameLayout {

    var action: ((List<Card>?) -> Unit)? = null
    var dismiss: (() -> Unit)? = null

    var data: CurrentIssueSuitList? = null
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
        data?.apply {
            suitImageView?.apply {
                val limit = limitSuitList?.firstOrNull()
                data = limit
                showLimitView(limit != null)
            }
            issueSuitsView?.apply {
                data = commonSuitList
            }
        }
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
        val view = inflate(context, R.layout.view_current_issue, null)
        addView(view)

        background = getShapeDrawable(
                colorRes = R.color.color_bb45717c,
                endColorRes = R.color.color_ee155f81
        )

        backView?.apply {
            (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = statusBarHeight + 4.dp
            setOnClickListener {
                close()
            }
        }

        titleLabel?.apply {
            (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = statusBarHeight
            setOnClickListener {
                close()
            }
        }

        view?.closeView?.apply {
            setOnClickListener {
                close()
            }
        }

//        suitImageView?.apply {
//            data = Suit(suitCover = "http://img5.mtime.cn/game/card/2021/02/12/165525.23589826_o.png")
//        }
//
//        issueSuitsView?.apply {
//            val list = ArrayList<Suit>()
//            list.add(Suit(suitCover = "http://img31.mtime.cn/game/card/2010/47/261bbea6-c5cd-4d60-b79d-f0ee284bcf21.png"))
//            list.add(Suit(suitCover = "http://img5.mtime.cn/game/card/2021/02/01/130617.73495528_o.png"))
//            list.add(Suit(suitCover = "http://img5.mtime.cn/game/card/2021/05/20/091403.39370655_o.png"))
//            list.add(Suit(suitCover = "http://img5.mtime.cn/game/card/2020/04/01/195416.26438331_o.png"))
//            list.add(Suit(suitCover = "http://img31.mtime.cn/game/card/2010/45/98ea74e8-6dd9-4b0f-8601-d7d06287a4de.png"))
//            list.add(Suit(suitCover = "http://img31.mtime.cn/game/card/2010/37/85406399-bc27-4b4a-93a7-f852f00340da.png"))
//            data = list
//        }
    }

    private fun showLimitView(show: Boolean = true) {
        if (show) {
            limitLabel.visible()
            limitLayout.visible()
        } else {
            limitLabel.gone()
            limitLayout.gone()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

    fun hide() {
        gone()
    }

    fun show() {
        visible()
    }

    private fun close() {
        dismiss?.invoke()
        hide()
    }

    data class Data(
            @IntRange(from = 0, to = 4) var position: Int = 0,
            var gold: Long = 0L,
            var cardList: List<Card>? = null
    )
}