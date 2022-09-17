package com.kotlin.android.card.monopoly.widget.coffer

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.card.adapter.CheckCardAdapter
import com.kotlin.android.card.monopoly.widget.card.view.CheckCardView
import com.kotlin.android.card.monopoly.widget.nested.NestedRecyclerView
import com.kotlin.android.card.monopoly.widget.coffer.tab.CofferTabAdapter
import com.kotlin.android.card.monopoly.widget.coffer.tab.CofferTabItemView
import com.kotlin.android.card.monopoly.widget.holder.CofferTabItemDecoration
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.StrongBoxPositionList
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import org.jetbrains.anko.firstChildOrNull

/**
 * 保险箱视图：
 *
 * Created on 2020/9/1.
 *
 * @author o.s
 */
class CofferView : LinearLayout {

    private val mHeight = 95.dp
    private val mActionHeight = 35.dp
    private val mMargin = 15.dp
    private val mMarginTop = 5.dp
    private val mMarginLeft = 10.dp
    private val mActionPadding = 20.dp
    private val mActionMargin = 20.dp
    private val mTextSize = 15F

    private var recyclerView: NestedRecyclerView? = null
    private var viewPager: ViewPager2? = null
    private var putPocketView: TextView? = null

    private var currentPosition = 0

    private val mAdapter by lazy {
        CofferTabAdapter().apply {
            action = {
                if (CofferTabItemView.ActionType.SELECT == it.type) {
                    currentPosition = it.index
                    viewPager?.currentItem = it.index
                    smoothScrollBetter(it)
                }
                actionTab?.invoke(it)
            }
        }
    }

    private val mCofferAdapter by lazy {
        CofferViewPagerAdapter {
            actionItem?.invoke(it)
        }.apply {
            selectChange = {
                selectedCards = it
                putPocketView?.isEnabled = (it.size > 0)
            }
        }
    }

    var selectedCards: ArrayList<Card>? = null

    /**
     * 点击"放入口袋"
     */
    var action: ((card: ArrayList<Card>) -> Unit)? = null

    /**
     * 点击Tab事件
     */
    var actionTab: ((event: CofferTabItemView.ActionEvent) -> Unit)? = null

    /**
     * 点击Item事件
     */
    var actionItem: ((event: CheckCardAdapter.ActionEvent) -> Unit)? = null

    /**
     * 保险箱数据
     */
    var data: StrongBoxPositionList = StrongBoxPositionList()
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
        data.positionList?.apply {
            mCofferAdapter.setData(this)
            mAdapter.setData(this)
            mAdapter.selectItem(currentPosition)
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
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL

        recyclerView = initRecyclerView()
        viewPager = initViewPager()
        putPocketView = initActionView()
    }

    private fun initRecyclerView(): NestedRecyclerView {
        return NestedRecyclerView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mHeight).apply {
                topMargin = mMargin
            }
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            addItemDecoration(CofferTabItemDecoration())
            adapter = mAdapter

            this@CofferView.addView(this)
        }
    }

    private fun initViewPager(): ViewPager2 {
        return ViewPager2(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(mMarginLeft, mMarginTop, mMarginLeft, 0)
            }
            adapter = mCofferAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    currentPosition = position
                    super.onPageSelected(position)
                    mAdapter.selectItem(position)

                    reset(this@apply)
                }
            })

            this@CofferView.addView(this)
        }
    }

    private fun reset(viewPage: ViewPager2) {
        viewPage.children.forEach { recyclerView ->
            if (recyclerView is ViewGroup) {
                recyclerView.firstChildOrNull { it is CheckCardView }?.let {
                    if (it is CheckCardView) {
                        "reset -> $it".e()
                        it.reset()
                    }
                }
            }
        }
    }

    private fun initActionView(): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mActionHeight).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = mActionMargin
                bottomMargin = mActionMargin
            }
            gravity = Gravity.CENTER
            setPadding(mActionPadding, 0, mActionPadding, 0)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setTextColor(getColor(R.color.color_ffffff))
            setText(R.string.put_pocket)
            isEnabled = false
            background = getDrawableStateList(
                    normal = getShapeDrawable(
                            colorRes = R.color.color_12c7e9,
                            cornerRadius = 18.dpF
                    ),
                    disable = getShapeDrawable(
                            colorRes = R.color.color_6612c7e9,
                            cornerRadius = 18.dpF
                    )
            )
            setOnClickListener {
                selectedCards?.apply {
                    action?.invoke(this)
                }
            }
            addView(this)
        }
    }

    private fun smoothScrollBetter(event: CofferTabItemView.ActionEvent) {
        recyclerView?.apply {
            event.let {
                if (it.left <= 60.dp) {
                    smoothScrollBy(-it.width, 0)
                } else if (it.right >= screenWidth - 60.dp) {
                    smoothScrollBy(it.width, 0)
                }
            }
        }
    }
}