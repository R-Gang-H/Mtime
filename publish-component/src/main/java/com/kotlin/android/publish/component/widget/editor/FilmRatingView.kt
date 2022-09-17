package com.kotlin.android.publish.component.widget.editor

import android.content.Context
import android.icu.text.DecimalFormat
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.kotlin.android.app.data.annotation.RECORD_TAG_ORIGINAL
import com.kotlin.android.app.data.annotation.RECORD_TAG_SPOILER
import com.kotlin.android.app.data.entity.common.MovieSubItemRating
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.app.router.provider.sdk.IJsSDKProvider
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.getRatingLevelHintText
import com.kotlin.android.publish.component.Publish
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.ViewPublishTitleLongCommentBinding
import com.kotlin.android.publish.component.ui.adapter.PublishSubRatingAdapter
import com.kotlin.android.router.ext.getProvider

/**
 *
 * Created on 2022/5/3.
 *
 * @author o.s
 */
class FilmRatingView : LinearLayout {
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

    private var mBinding: ViewPublishTitleLongCommentBinding? = null

    private val mSubRatingAdapter: PublishSubRatingAdapter by lazy {
        PublishSubRatingAdapter()
    }

    private val colorHighlight by lazy { getColor(R.color.color_20a0da) }
    private val color by lazy { getColor(R.color.color_e5e5e5) }
    private val decimalFormat = DecimalFormat("##0.0")
    private val defaultScore = 0.0 // 默认评分10分

    private var isSubRatingModel = false

    var ratingChange: ((hasRating: Boolean) -> Unit)? = null
        set(value) {
            field = value
            post {
                mBinding?.ratingView?.ratingChange = value
                mSubRatingAdapter.ratingChange = value
            }
        }

    /**
     * 是否长影评
     */
    var isLongComment = false
        set(value) {
            field = value
            mBinding?.titleFooter?.isVisible = value
        }

    /**
     * 标签 ORIGINAL(1, "原创"), SPOILER(2, "剧透"), COPYRIGHT(3, "版权声明"), DISCLAIMER(4, "免责声明");
     */
    var tags: List<Long>?
        get() {
            val list = ArrayList<Long>()
            if (mBinding?.statementTips?.isSelected == true) {
                list.add(RECORD_TAG_ORIGINAL)
            }
            if (mBinding?.spoilerTips?.isSelected == true) {
                list.add(RECORD_TAG_SPOILER)
            }
            return if (list.isEmpty()) {
                null
            } else {
                list
            }
        }
        set(value) {
            mBinding?.statementTips?.isSelected = value?.contains(RECORD_TAG_ORIGINAL) == true
            mBinding?.spoilerTips?.isSelected = value?.contains(RECORD_TAG_SPOILER) == true
        }

    /**
     * 评分
     */
    var rating: Double
        set(value) {
            mBinding?.ratingView?.level = value
        }
        get() {
            val num = mBinding?.score?.text.toString()
            return if (TextUtils.isEmpty(num)) {
                defaultScore
            } else {
                num.toDouble()
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
     * 分项评分描述，请求接口字段
     */
    val subRatingsDesc: String
        get() {
            val sb = StringBuilder()
            subRatings?.forEach {
                if (sb.isNotEmpty()) {
                    sb.append(",")
                }
                sb.append(it.rating?.toInt() ?: 0)
            }
            return sb.toString()
        }

    private fun initView() {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        mBinding = ViewPublishTitleLongCommentBinding.inflate(LayoutInflater.from(context))
        addView(mBinding?.root)
        addView(shadowView)

        mBinding?.apply {
            ratingView.let {
                it.action = { level ->
                    changedRating(level)
                }
                it.level = defaultScore // 默认
            }

            subRatingRv.apply {
                adapter = mSubRatingAdapter
                mSubRatingAdapter.action = {
                    changedRating(it)
                }
            }

            ratingSwitchTv.onClick {
                isSubRatingModel = !isSubRatingModel
                switchRatingModel(false)
            }

            statementTips.apply {
                setTextWithLink(getString(R.string.publish_original_statement_tips), Range(8, 16))
                action = {
                    getProvider(IJsSDKProvider::class.java)?.startH5Activity(
                        BrowserEntity(
                            title = "《原创内容声明》",
                            url = "https://m.mtime.cn/original" // http://h5.mt-dev.com/original
                        )
                    )
                }
                isSelected = true
            }

            spoilerTips.apply {
                setText(R.string.publish_including_spoiler_tips)
            }
        }
    }

    private val shadowView by lazy {
        View(context).apply {
            layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, 10.dp)
            setBackground(
                colorRes = R.color.color_f9f9f9,
                endColorRes = R.color.color_ffffff
            )
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
        mBinding?.apply {
            score.text = decimalFormat.format(level)
            scoreDesc.text = res
        }
    }

    private fun ratingColor(isHighlight: Boolean = true) {
        mBinding?.apply {
            if (isHighlight) {
                score.setTextColor(colorHighlight)
                scoreDesc.setTextColor(colorHighlight)
            } else {
                score.setTextColor(color)
                scoreDesc.setTextColor(color)
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

        mBinding?.apply {
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
    }
}