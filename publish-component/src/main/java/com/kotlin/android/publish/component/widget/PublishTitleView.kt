package com.kotlin.android.publish.component.widget

import android.content.Context
import android.icu.text.DecimalFormat
import android.text.InputFilter
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Range
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.annotation.RECORD_TAG_ORIGINAL
import com.kotlin.android.app.data.annotation.RECORD_TAG_SPOILER
import com.kotlin.android.app.data.entity.common.MovieSubItemRating
import com.kotlin.android.app.data.entity.community.record.Image
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.getRatingLevelHintText
import com.kotlin.android.publish.component.Publish
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.ui.adapter.FamilyImageAdapter
import com.kotlin.android.publish.component.ui.adapter.PublishSubRatingAdapter
import com.kotlin.android.publish.component.ui.decoration.PublishFamilyItemDecoration
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.sdk.IJsSDKProvider
import com.kotlin.android.widget.rating.RatingView
import kotlinx.android.synthetic.main.view_publish_title_long_comment.view.*

/**
 * 发布头部视图：
 * [style] 样式有：发布短评、长评、家族
 *
 * Created on 2020/7/20.
 *
 * @author o.s
 */
class PublishTitleView : LinearLayout {

    companion object {
        const val maxImageCount = 10L // 最多10张图片
    }

    private val maxTitleLength = 100 // 标题字数限制
    private val defaultScore = 0.0 // 默认评分10分
    private val mRecyclerViewHeight = 90.dp // dp
    private val mShadowHeight = 10.dp // dp
    private val mLineHeight = 2 // px
    private val mMargin = 18.dp // dp
    private val mTextSize = 19F // sp
    private var shadowView: View? = null
    private var shadowView2: View? = null
    private var lineView: View? = null
    private var commentView: View? = null
    private var coverView: RecyclerView? = null
    private val colorHighlight by lazy { getColor(R.color.color_20a0da) }
    private val color by lazy { getColor(R.color.color_e5e5e5) }
    var isSubRatingModel = false
    private val decimalFormat = DecimalFormat("##0.0")

    private var titleTextView: TextView? = null

    var titleEditText: EditText? = null
        private set

    val mAdapter: FamilyImageAdapter by lazy {
        FamilyImageAdapter(context as FragmentActivity, maxImageCount).apply {
            itemChange = {
                postDelayed({
                    coverView?.smoothScrollBy(it, 0)
                }, 300)
            }
        }
    }

    val mSubRatingAdapter: PublishSubRatingAdapter by lazy {
        PublishSubRatingAdapter()
    }

    var coverChange: ((hasCover: Boolean) -> Unit)? = null
        set(value) {
            field = value
            mAdapter.coverChange = value
        }

    var ratingChange: ((hasRating: Boolean) -> Unit)? = null
        set(value) {
            field = value
            post {
                ratingView?.ratingChange = value
                mSubRatingAdapter.ratingChange = value
            }
        }

    /**
     * 封面
     */
    val covers: List<Image>
        get() {
            return mAdapter.getImages().filterIndexed { index, _ -> index == 0 }
        }

    /**
     * 图集
     */
    val images: List<Image>
        get() {
            return mAdapter.getImages().filterIndexed { index, _ -> index != 0 }
        }

    /**
     * 标签 ORIGINAL(1, "原创"), SPOILER(2, "剧透"), COPYRIGHT(3, "版权声明"), DISCLAIMER(4, "免责声明");
     */
    val tags: List<Long>?
        get() {
            val list = ArrayList<Long>()
            if (commentView?.statementTips?.isSelected == true) {
                list.add(RECORD_TAG_ORIGINAL)
            }
            if (commentView?.spoilerTips?.isSelected == true) {
                list.add(RECORD_TAG_SPOILER)
            }
            return if (list.isEmpty()) {
                null
            } else {
                list
            }
        }

    /**
     * 评分
     */
    var rating: Double
        set(value) {
            ratingView?.level = value
        }
        get() {
            val num = score?.text?.toString()
            return if (TextUtils.isEmpty(num)) {
                defaultScore
            } else {
                num!!.toDouble()
            }
        }

    /**
     * 分项评分数据
     */
    var subRatings: List<MovieSubItemRating>? = null
        set(value) {
            field = value
            isSubRatingModel = false
            run breaking@{
                value?.forEach {
                    val rating = it.rating ?: 0f
                    if (rating > 0f) {
                        isSubRatingModel = true
                        return@breaking
                    }
                }
            }
            switchRatingModel(true)
        }

    /**
     * 发布视图头部样式
     */
    var style: PublishStyle = PublishStyle.JOURNAL
        set(value) {
            field = value
            changeStyle()
        }

    /**
     * 标题
     */
    var title: String = ""
        get() = titleEditText?.text.toString()
        set(value) {
            field = value
            titleEditText?.setText(value)
        }

    fun setData(data: ArrayList<PhotoInfo>) {
        mAdapter.setData(data)
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

    private fun initView() {
        orientation = VERTICAL
        coverView = initRecyclerView()
        commentView = initCommentView()
        shadowView = initShadowView()
        titleEditText = initTitleView()
        titleTextView = initTitleTextView()
        shadowView2 = initShadowView()
        lineView = initLineView()

        addView(coverView)
        addView(commentView)
        addView(shadowView)
        addView(titleEditText)
        addView(titleTextView)
        addView(shadowView2)
        addView(lineView)

        initRatingView(ratingView)
        initSubRatingView()
        ratingSwitchTv.onClick {
            isSubRatingModel = !isSubRatingModel
            switchRatingModel(false)
        }

        changeStyle()
    }

    private fun initRecyclerView(): RecyclerView {
        return RecyclerView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mRecyclerViewHeight)
            layoutManager = LinearLayoutManager(context).apply {
                orientation = RecyclerView.HORIZONTAL
            }
            addItemDecoration(PublishFamilyItemDecoration())
            adapter = mAdapter
        }
    }

    private fun initTitleView(): EditText {
        return EditText(context).apply {
            layoutParams = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                marginStart = mMargin
                marginEnd = mMargin
            }
            gravity = Gravity.CENTER_VERTICAL
            background = null
            filters = arrayOf(InputFilter.LengthFilter(maxTitleLength))
            setTextColor(getColor(R.color.color_4e5e73))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setHint(R.string.publish_title_hint)
            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    when (style) {
                        PublishStyle.LONG_COMMENT,
                        PublishStyle.JOURNAL -> {
                            titleTextView?.let {
                                it.text = text
                                gone()
                                it.visible()
                            }
                        }
                        else -> {
                            titleTextView?.gone()
                        }
                    }
                }
            }
        }
    }

    private fun initTitleTextView(): TextView {
        return TextView(context).apply {
            layoutParams = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                marginStart = mMargin
                marginEnd = mMargin
            }
            setPadding(4.dp, 10.dp, 4.dp, 11.dp)
            gravity = Gravity.CENTER_VERTICAL
            background = null
            setTextColor(getColor(R.color.color_4e5e73))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setHint(R.string.publish_title_hint)
            setOnClickListener {
                titleEditText?.let {
                    gone()
                    it.visible()
                    it.requestFocus(text.length)
                }
            }
        }
    }

    private fun initCommentView(): View? {
        return View.inflate(context, R.layout.view_publish_title_long_comment, null)?.apply {
            statementTips?.apply {
                setTextWithLink(getString(R.string.publish_original_statement_tips), Range(8, 16))
                action = {
                    getProvider(IJsSDKProvider::class.java)
                            ?.startH5Activity(BrowserEntity(
                                    title = "《原创内容声明》",
                                    url = "https://m.mtime.cn/original" // http://h5.mt-dev.com/original
                            ))
                }
                isSelected = true
            }
            spoilerTips?.apply {
                setText(R.string.publish_including_spoiler_tips)
            }
        }
    }

    private fun initShadowView(): View {
        return View(context).apply {
            layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, mShadowHeight)
            setBackground(
                colorRes = R.color.color_f9f9f9,
                endColorRes = R.color.color_ffffff
            )
        }
    }

    private fun initLineView(): View {
        return View(context).apply {
            layoutParams = MarginLayoutParams(LayoutParams.MATCH_PARENT, mLineHeight).apply {
                marginStart = mMargin
                marginEnd = mMargin
            }
            setBackgroundResource(R.color.color_f3f3f4)
        }
    }

    /**
     * 样式改变
     */
    private fun changeStyle() {
        when (style) {
            PublishStyle.FILM_COMMENT -> {
                filmCommentStyle()
            }
            PublishStyle.LONG_COMMENT -> {
                longCommentStyle()
            }
            PublishStyle.JOURNAL -> {
                journalStyle()
            }
        }
    }

    /**
     * 短评样式
     */
    private fun filmCommentStyle() {
        coverView?.gone()
        commentView?.visible()
        shadowView?.visible()
        titleEditText?.gone()
        titleTextView?.gone()
        shadowView2?.gone()
        lineView?.gone()
        commentView?.apply {
            statementTips?.gone()
            spoilerTips?.gone()
        }
    }

    /**
     * 长评样式
     */
    private fun longCommentStyle() {
        coverView?.gone()
        commentView?.visible()
        shadowView?.visible()
        titleEditText?.gone()
        titleTextView?.visible()
        shadowView2?.gone()
        lineView?.visible()
        commentView?.apply {
            statementTips?.visible()
            spoilerTips?.visible()
        }
    }

    /**
     * 家族样式
     */
    private fun journalStyle() {
        coverView?.visible()
        commentView?.gone()
        shadowView?.gone()
        titleEditText?.gone()
        titleTextView?.visible()
        shadowView2?.visible()
        lineView?.gone()
    }

    /**
     * 初始化平分视图
     */
    private fun initRatingView(ratingView: RatingView?) {
        ratingView?.let {
            it.action = { level ->
                changedRating(level)
            }
            it.level = defaultScore // 默认
        }
    }

    private fun initSubRatingView() {
        subRatingRv?.apply {
            adapter = mSubRatingAdapter
            mSubRatingAdapter.action = {
                changedRating(it)
            }
        }
    }

    /**
     * 选择评分模式：评分、分项评分
     * @param isInit 初始化数据时则不清空历史项分
     */
    private fun switchRatingModel(isInit: Boolean) {
        if (!isInit) {
            rating = 0.0
            subRatings?.forEach {
                it.rating = 0.0f
            }
            changedRating(rating)
        }

        if (isSubRatingModel) {
            ratingSwitchTv.setText(R.string.publish_rating_changed_to_rating)
            ratingSwitchTv.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_arrow_up, 0)
            subRatingRv.visible()
            ratingView.gone()
            mSubRatingAdapter.setData(subRatings)
        } else {
            ratingSwitchTv.setText(R.string.publish_rating_changed_to_sub_rating)
            ratingSwitchTv.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_arrow_down, 0)
            subRatingRv.gone()
            ratingView.visible()
            ratingView.level = rating
        }
    }

    private fun changedRating(level: Double) {
        ratingColor()
        val res = when (level.toInt()) {
            Publish.PUBLISH_SCORE_LEVEL_0 -> {
                ratingColor(false)
                getString(R.string.publish_score_level_0)
            }
            else -> getRatingLevelHintText(level.toInt())
        }
        score?.text = decimalFormat.format(level)
        scoreDesc?.text = res
    }

    private fun ratingColor(isHighlight: Boolean = true) {
        if (isHighlight) {
            score?.setTextColor(colorHighlight)
            scoreDesc?.setTextColor(colorHighlight)
        } else {
            score?.setTextColor(color)
            scoreDesc?.setTextColor(color)
        }
    }
}