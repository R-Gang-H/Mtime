package com.kotlin.android.community.post.component.item.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import com.kotlin.android.community.post.component.R
import com.kotlin.android.community.post.component.databinding.ItemCommunityPostBinding
import com.kotlin.android.community.post.component.item.bean.CommunityPostHotComment
import com.kotlin.android.community.post.component.item.bean.CommunityPostItem
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.setTextWithFormat
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.widget.views.CommunityQuoteSpan

import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.android.synthetic.main.view_community_post_hot_comment.view.*

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/20
 */
open class CommunityPostBinder(
        val post: CommunityPostItem
) : MultiTypeBinder<ItemCommunityPostBinding>() {

    private val mFamilyProvider by lazy { getProvider(ICommunityFamilyProvider::class.java) }
    private val mUgcProvider by lazy { getProvider(IUgcProvider::class.java) }
    private val mTicketProvider: ITicketProvider? by lazy { getProvider(ITicketProvider::class.java) }
    private val mCommunityProvider: ICommunityPersonProvider? by lazy { getProvider(ICommunityPersonProvider::class.java) }

    //当前点击的热门评论(点赞、点踩，跳转等)
    var mCurClickComment: CommunityPostHotComment? = null

    override fun layoutId(): Int = R.layout.item_community_post

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommunityPostBinder && other.post.hashCode() == post.hashCode()
    }

    override fun onBindViewHolder(binding: ItemCommunityPostBinding, position: Int) {
        binding.mCommunityPostLikeTv.text = formatCount(post.likeCount)
        binding.mCommunityPostCommentCountTv.text = formatCount(post.commentCount)
        binding.mCommunityPostTitleTv.text = getTitle()
        bindImgs(binding, position)
        bindVoteOpinion(binding, position)
        bindHotComment(binding, position)
    }

    private fun getTitle(): SpannableString {
        val ssb = SpannableString(post.title.orEmpty())
        binding?.let {
            if (post.isTop) {
                ssb.setSpan(CommunityQuoteSpan(
                        it.root.context,
                        R.mipmap.icon_community_post_ding, 5.dp),
                        0, 0, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            if (post.isEssence) {
                ssb.setSpan(CommunityQuoteSpan(
                        it.root.context,
                        R.mipmap.icon_community_post_jing, 5.dp),
                        0, 0, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return ssb
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mCommunityPostRoot -> {
                //UGC详情
                mUgcProvider?.launchDetail(post.id, post.type)
            }
            R.id.mCommunityPostFamilyNameTv -> {
                //家族详情
                mFamilyProvider?.startFamilyDetail(post.familyId)
            }
            R.id.mCommunityPostMovieLayout -> {
                //影片详情
                mTicketProvider?.startMovieDetailsActivity(post.movieId)
            }
            R.id.mCommunityPostCommentRoot,
            R.id.mCommunityPostCommentCountTv -> {
                //评论点击，跳转到详情页面并定位到评论区域
                mUgcProvider?.launchDetail(post.id, post.type,0L, true)
            }
            //todo 暂时先不用图片点击查看图片，后期产品说再会加上，所以暂时保留代码
//            R.id.mCommunityPostImgCardView -> {
//                //图片详情
//                post.imgs?.apply {
//                    val list: ArrayList<String> = ArrayList()
//                    forEach {
//                        it.pic?.apply {
//                            list.add(this)
//                        }
//                    }
//                    mMainProvider?.startPhotoDetailActivity(list, 0)
//                }
//            }
            R.id.mCommunityPostMovieBtnFl -> {
                //预售、购票、想看、取消想看
                movieBtnClick(view)
            }
            R.id.mCommunityPostUserNameTv,
            R.id.mCommunityPostUserProfileIv -> {
                mCommunityProvider?.startPerson(post.userId)
            }
            R.id.mCommunityPostShareIv -> {
                //未审核帖子进行分享提示"正在审核中"
                if (!post.isPublished) {
                    showToast(R.string.community_post_is_checking2)
                } else {
                    super.onClick(view)
                }
            }
            else -> {
                super.onClick(view)
            }
        }
    }

    //影片的购票/想看按钮点击
    private fun movieBtnClick(view: View) {
        //1:预售2:购票3:想看4:已想看
        when (post.movieBtnState) {
            CommConstant.MOVIE_BTN_STATE_PRESELL,
            CommConstant.MOVIE_BTN_STATE_TICKET -> { //预售、购票
                mTicketProvider?.startMovieShowtimeActivity(movieId = post.movieId)
            }
            else -> {  //已想看 //想看
                super.onClick(view)
            }
        }
    }

    /**
     * 点赞、取消点赞改变
     */
    fun praiseUpChanged() {
        post.isLike = !post.isLike
        if (post.isLike) {
            post.likeCount++
        } else {
            post.likeCount--
        }
        notifyAdapterSelfChanged()
    }

    /**
     * 想看状态变动
     */
    fun wantToSeeChanged() {
        post.movieBtnState = if (post.movieBtnState
                == CommConstant.MOVIE_BTN_STATE_WANT_SEE) {
            CommConstant.MOVIE_BTN_STATE_WANT_SEEN
        } else {
            CommConstant.MOVIE_BTN_STATE_WANT_SEE
        }
        notifyAdapterSelfChanged()
    }

    /**
     * 热门评论的点赞、取消点赞改变
     */
    fun commentPraiseUpChanged() {
        mCurClickComment?.let {
            if (it.isDislike) {
                it.isDislike = false
                it.dislikeCount--
            }
            it.isLike = !it.isLike
            if (it.isLike) {
                it.likeCount++
            } else {
                it.likeCount--
            }
            notifyAdapterSelfChanged()
        }
    }

    /**
     * 热门评论的点踩、取消点踩改变
     */
    fun commentPraiseDownChanged() {
        mCurClickComment?.let {
            if (it.isLike) {
                it.isLike = false
                it.likeCount--
            }
            it.isDislike = !it.isDislike
            if (it.isDislike) {
                it.dislikeCount++
            } else {
                it.dislikeCount--
            }
            notifyAdapterSelfChanged()
        }
    }

    /**
     * 投票变化
     */
    fun voteChanged(voteId: Long) {
        post.opinions?.let {
            post.voteNumber++

            it.forEach { vote ->
                if (vote.id == voteId) {
                    vote.isCheck = true
                    vote.count++
                }
            }
            notifyAdapterSelfChanged()
        }
    }

    //显示投票区域
    private fun bindVoteOpinion(binding: ItemCommunityPostBinding, position: Int) {
        binding.mCommunityPostVoteOpinionLayout.run {

            if (post.isPkType()) {
                mCommunityPostPkVoteNumberTv.setTextWithFormat(
                        R.string.community_post_vote_count_format, formatCount(post.voteNumber))
                if (!post.isCheckVoteOpinion()) {
                    mCommunityPostPkBtnLayout.visible()
                    mCommunityPostPkPercentLayout.gone()
                    mCommunityPostPkPositiveBtn.text = post.opinions!![0].label
                    mCommunityPostPkNegativeBtn.text = post.opinions!![1].label
                } else {
                    post.opinions?.let {
                        val percent = (it[0].count * 100 / post.voteNumber).toInt()
                        val percent2 = 100 - percent

                        mCommunityPostPkBtnLayout.gone()
                        mCommunityPostPkPercentLayout.visible()
                        mCommunityPostPkBattlePercentView
                                .setPercent(percent, percent2)
                                .startAnim()
                        mCommunityPostPkPositivePercentTv.text = "${percent}%"
                        mCommunityPostPkNegativePercentTv.text = "${percent2}%"
                        mCommunityPostPkPositiveOpinionTv.text = post.opinions!![0].label
                        mCommunityPostPkNegativeOpinionTv.text = post.opinions!![1].label
                        if (post.opinions!![0].isCheck) {
                            mCommunityPostPkNegativePercentTv.setCompoundDrawablesAndPadding()
                            mCommunityPostPkPositivePercentTv
                                    .setCompoundDrawablesAndPadding(
                                            leftResId = R.drawable.ic_community_positive_check,
                                            padding = 5
                                    )
                        } else {
                            mCommunityPostPkPositivePercentTv.setCompoundDrawablesAndPadding()
                            mCommunityPostPkNegativePercentTv
                                    .setCompoundDrawablesAndPadding(
                                            leftResId = R.drawable.ic_community_negative_check,
                                            padding = 5
                                    )
                        }
                    }
                }
            }
        }
    }

    //显示热门评论
    private fun bindHotComment(binding: ItemCommunityPostBinding, position: Int) {
        binding.mCommunityPostCommentLayout.run {
            if (post.hasHotComment()) {
                visible()
                removeAllViews()
                var index = 0
                post.hotComments?.forEach {
                    val view = LayoutInflater.from(context)
                            .inflate(R.layout.view_community_post_hot_comment,
                                    this, false)
                    view.run {
                        val paddingTop = if (index == 0) 0 else 20.dp
                        setPadding(0, paddingTop, 0, 0)

                        //底部点赞区域
                        mCommunityPostCommentDislikeTv.visible(post.isPkType())
                        val ic_dislike = if (post.isFirstVoteOpinion(it.voteOpinionId))
                            R.drawable.ic_dislikeg else R.drawable.ic_dislikey
                        val color_dislike = if (post.isFirstVoteOpinion(it.voteOpinionId))
                            R.color.color_36c096 else R.color.color_feb12a
                        mCommunityPostCommentDislikeTv
                                .setCompoundDrawablesAndPadding(
                                        leftResId = if (it.isDislike) ic_dislike else R.drawable.ic_dislike,
                                        padding = 5
                                )
                                .setTextColorRes(if (it.isDislike) color_dislike else R.color.color_8798af)
                                .text = formatCount(it.dislikeCount)

                        val ic_like = if (!post.isPkType()) R.drawable.ic_likeb else
                            (if (post.isFirstVoteOpinion(it.voteOpinionId))
                                R.drawable.ic_likeg else R.drawable.ic_likey)
                        val color_like = if (!post.isPkType()) R.color.color_20a0da else
                            (if (post.isFirstVoteOpinion(it.voteOpinionId))
                                R.color.color_36c096 else R.color.color_feb12a)
                        mCommunityPostCommentLikeTv
                                .setCompoundDrawablesAndPadding(
                                        leftResId = if (it.isLike) ic_like else R.drawable.ic_like,
                                        padding = 5
                                )
                                .setTextColorRes(if (it.isLike) color_like else R.color.color_8798af)
                                .text = formatCount(it.likeCount)

                        //评论内容
                        val sb = StringBuilder()
                        var hotIcon = R.drawable.ic_community_burn
                        var opLabelLength = 0
                        var opLabelColor = 0
                        if (post.isPkType() && post.hasOpinions()) {
                            if (post.opinions!![0].id == it.voteOpinionId) {
                                hotIcon = R.drawable.ic_community_burn_a
                                opLabelColor = getColor(R.color.color_36c096)
                            } else {
                                hotIcon = R.drawable.ic_community_burn_b
                                opLabelColor = getColor(R.color.color_feb12a)
                            }
                            sb.append(post.getOpinionLabel(it.voteOpinionId) + " ")
                            opLabelLength = sb.length
                        }

                        val userName = it.userName ?: ""

                        sb.append("$userName：")
                        if (it.hasPic) {
                            sb.append("[图片] ")
                        }
                        sb.append(it.content ?: "")

                        val ssb = SpannableString(sb)
                        ssb.setSpan(CommunityQuoteSpan(context, hotIcon, 3.dp), 0, 0, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        if (opLabelLength > 0) {
                            ssb.setSpan(StyleSpan(Typeface.BOLD), 0, opLabelLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            ssb.setSpan(ForegroundColorSpan(opLabelColor), 0, opLabelLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        val nameLength: Int = userName.length + 1 + opLabelLength // +冒号+选项名
                        ssb.setSpan(StyleSpan(Typeface.BOLD), opLabelLength, nameLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        ssb.setSpan(ForegroundColorSpan(getColor(R.color.color_4e5e73)), opLabelLength, nameLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        mCommunityPostCommentContentTv.text = ssb
                    }
                    addView(view)
                    index++

                    //点击事件
                    view.onClick { clickView ->
                        onClick(clickView)
                    }
                    view.mCommunityPostCommentLikeTv.tag = it
                    view.mCommunityPostCommentLikeTv.onClick { clickView ->
                        mCurClickComment = clickView.tag as CommunityPostHotComment?
                        super.onClick(clickView)
                    }
                    view.mCommunityPostCommentDislikeTv.tag = it
                    view.mCommunityPostCommentDislikeTv.onClick { clickView ->
                        mCurClickComment = clickView.tag as CommunityPostHotComment?
                        super.onClick(clickView)
                    }
                }
            } else {
                gone()
            }
        }
    }

    //显示帖子图片
    private fun bindImgs(binding: ItemCommunityPostBinding, position: Int) {
        binding.run {
            mCommunityPostImgMoreTv.gone()
            mCommunityPostImgGifTv1.gone()
            mCommunityPostImgGifTv2.gone()
            mCommunityPostImgGifTv3.gone()
            mCommunityPostImgGifTv4.gone()
            mCommunityPostImgIv1.invisible()
            mCommunityPostImgIv21.invisible()
            mCommunityPostImgIv22.invisible()
            mCommunityPostImgIv41.invisible()
            mCommunityPostImgIv42.invisible()
            mCommunityPostImgIv43.invisible()
            mCommunityPostImgIv44.invisible()
            mCommunityPostImgCardView.visible(!post.imgs.isNullOrEmpty())

            post.imgs?.let {
                if (it.isNotEmpty()) {
                    when (it.size) {
                        1 -> {
                            mCommunityPostImgIv1.visible()
                            mCommunityPostImgIv1.loadImage(
                                    data = it[0].pic,

                                    width = 345.dp,
                                    height = 195.dp
                            )
                            mCommunityPostImgGifTv4.visible(it[0].isGif)
                        }
                        2 -> {
                            mCommunityPostImgIv21.visible()
                            mCommunityPostImgIv22.visible()
                            mCommunityPostImgIv21.loadImage(
                                    data = it[0].pic,

                                    width = 170.dp,
                                    height = 195.dp
                            )
                            mCommunityPostImgIv22.loadImage(
                                    data = it[1].pic,

                                    width = 170.dp,
                                    height = 195.dp
                            )
                            mCommunityPostImgGifTv3.visible(it[0].isGif)
                            mCommunityPostImgGifTv4.visible(it[1].isGif)
                        }
                        3 -> {
                            mCommunityPostImgIv21.visible()
                            mCommunityPostImgIv42.visible()
                            mCommunityPostImgIv44.visible()
                            mCommunityPostImgIv21.loadImage(
                                    data = it[0].pic,

                                    width = 170.dp,
                                    height = 195.dp
                            )
                            mCommunityPostImgIv42.loadImage(
                                    data = it[1].pic,

                                    width = 170.dp,
                                    height = 95.dp
                            )
                            mCommunityPostImgIv44.loadImage(
                                    data = it[2].pic,

                                    width = 170.dp,
                                    height = 95.dp
                            )
                            mCommunityPostImgGifTv3.visible(it[0].isGif)
                            mCommunityPostImgGifTv2.visible(it[1].isGif)
                            mCommunityPostImgGifTv4.visible(it[2].isGif)
                        }
                        else -> {
                            mCommunityPostImgIv41.visible()
                            mCommunityPostImgIv42.visible()
                            mCommunityPostImgIv43.visible()
                            mCommunityPostImgIv44.visible()
                            mCommunityPostImgIv41.loadImage(
                                    data = it[0].pic,

                                    width = 170.dp,
                                    height = 95.dp
                            )
                            mCommunityPostImgIv42.loadImage(
                                    data = it[1].pic,

                                    width = 170.dp,
                                    height = 95.dp
                            )
                            mCommunityPostImgIv43.loadImage(
                                    data = it[2].pic,

                                    width = 170.dp,
                                    height = 95.dp
                            )
                            mCommunityPostImgIv44.loadImage(
                                    data = it[3].pic,

                                    width = 170.dp,
                                    height = 95.dp
                            )
                            mCommunityPostImgGifTv1.visible(it[0].isGif)
                            mCommunityPostImgGifTv2.visible(it[1].isGif)
                            mCommunityPostImgGifTv3.visible(it[2].isGif)
                            mCommunityPostImgGifTv4.visible(it[3].isGif)
                            if (it.size > 4) {
                                mCommunityPostImgMoreTv.visible()
                                mCommunityPostImgMoreTv.text = "+${it.size - 4}"
                            }
                        }
                    }
                }
            }
        }
    }
}
