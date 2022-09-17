package com.kotlin.android.ugc.detail.component.ui.widget

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.core.setCompoundDrawablesAndPadding
import com.kotlin.android.ktx.ext.core.setTextColorRes
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.ext.ShapeExt

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.UgcTitleBarBean
import com.kotlin.android.user.UserManager
import kotlinx.android.synthetic.main.view_ugc_title.view.*

/**
 * Created by lushan on 2020/8/5
 * Ugc详情及文章详情titleView
 */
class UgcTitleView @JvmOverloads constructor(var ctx: Context, var attributes: AttributeSet? = null, var defStyleAttr: Int = 0) : FrameLayout(ctx, attributes, defStyleAttr) {

    val ugcRootView = LayoutInflater.from(ctx).inflate(R.layout.view_ugc_title, null)
    private var moreClick: ((View) -> Unit)? = null//点击更多
    private var attentionClick: ((View, Long) -> Unit)? = null//点击关注
    private var backClick: ((View) -> Unit)? = null
    private var titleBean: UgcTitleBarBean? = null

    init {

        removeAllViews()
        LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT).let {
            it.gravity = Gravity.CENTER_VERTICAL
            addView(ugcRootView, it)
        }

        attentionFL?.onClick {//关注按钮
            attentionClick?.invoke(it, titleBean?.userId ?: 0L)
        }

        moreIv?.onClick {//更多按钮
            moreClick?.invoke(it)
        }

        backIv?.onClick {//返回按钮
            backClick?.invoke(it)
        }

        userHeadIv?.onClick {//跳转到用户主页页面
            if (titleBean?.userId ?: 0L != 0L) {
                val communityPersonProvider: ICommunityPersonProvider? = getProvider(
                    ICommunityPersonProvider::class.java)
                communityPersonProvider?.startPerson(titleBean?.userId ?: 0L)
            }
        }

        attentionFL?.gone()

    }

    fun setListener(back: ((View) -> Unit)? = null, moreClick: ((View) -> Unit)? = null, attentionClick: ((View, Long) -> Unit)? = null) {
        this.backClick = back
        this.moreClick = moreClick
        this.attentionClick = attentionClick
    }

    fun setTitleBackground(@ColorRes colorInt: Int) {
        ugcRootView?.setBackgroundColor(getColor(colorInt))
        setBackgroundColor(getColor(colorInt))
    }

    private var titleBackgroundColor: Int = 0

    fun getTitleBackgroundColor():Int = titleBackgroundColor

    fun setTitleBackgroundColor(@ColorInt colorInt: Int) {
        this.titleBackgroundColor = colorInt
//        ugcRootView?.setBackgroundColor(colorInt)
        setBackgroundColor(colorInt)
    }

    /**
     * 根据前深色设置对应title颜色
     */
    fun setTitleColor(isDark: Boolean) {
        getDrawable(R.drawable.icon_back)?.mutate()?.apply {
            setTint(getColor(if (isDark) R.color.color_ffffff else R.color.color_c9cedc))
        }?.also {
            backIv?.setImageDrawable(it)
        }

        userNameTv?.setTextColor(getColor(if (isDark) R.color.color_ffffff else R.color.color_4e5e73))
        publishDateTv?.setTextColor(getColor(if (isDark) R.color.color_ffffff else R.color.color_8798af))

        getDrawable(R.drawable.ic_ver_more)?.mutate()?.apply {
            setTint(getColor(if (isDark) R.color.color_ffffff else R.color.color_aab7c7))
        }?.also {
            moreIv?.setImageDrawable(it)
        }

    }

    fun updateFollow(isFollow: Boolean) {
        titleBean?.isAttention = isFollow
        titleBean?.let {
            setData(it)
        }
    }

    /**
     * 设置显示隐藏更多按钮
     */
    fun setMoreVisible(show:Boolean){
        moreIv?.visible(show)
    }


    /**
     * 是否展示评分
     */
    fun isShowScore(mTimeScore: String): Boolean {
        val score = mTimeScore.trim()
        if (TextUtils.isEmpty(score)) {
            return false
        }
        return try {
            score.toDouble() > 0.0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    fun setData(bean: UgcTitleBarBean) {
        titleBean = bean
        with(bean) {
            userId = bean.userId
            scoreTv?.apply {
                visible(isReview && isShowScore(score))
                text = "${score.trim()}"
            }

            publishDateTv?.text = publishTime

            userHeadIv?.loadImage(
                data = headPic,
                width = 29.dp,
                height = 29.dp,
                circleCrop = true,
                defaultImgRes = R.drawable.default_user_head
            )
            userNameTv?.text = userName

//            头像认证图标
            authIv?.setImageResource(if (isInstitutionAuthUser()) R.drawable.ic_jigourenzheng else R.drawable.ic_yingrenrenzheng)
            authIv?.visible(isAuthUser())
            attentionFL?.apply {
                if (isAttention || userId == UserManager.instance.userId || userId == 0L) {//关注或者是自己的都不展示
                    gone()
                    ShapeExt.setShapeCorner2Color2Stroke(
                            this,
                            R.color.color_ffffff,
                            30,
                            R.color.color_20a0da,
                            1
                    )

                } else {
                    visible()
                    ShapeExt.setGradientColor(
                            this,
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            R.color.color_20a0da,
                            R.color.color_1bafe0,
                            30)
                }
            }

            attentionBtn?.apply {
                if (isAttention) {
                    setTextColorRes(R.color.color_feb12a)
                    setCompoundDrawablesAndPadding(
                            leftResId = R.drawable.ic_checkb,
                            padding = 3
                    )
                } else {
                    setTextColorRes(R.color.color_ffffff)
                }
            }
        }

    }

    fun showOnlyBackAndMore(){
        userHeadIv?.gone()
        authIv?.gone()
        userNameTv?.gone()
        scoreTv?.gone()
        publishDateTv?.gone()
        attentionFL?.gone()
    }


}