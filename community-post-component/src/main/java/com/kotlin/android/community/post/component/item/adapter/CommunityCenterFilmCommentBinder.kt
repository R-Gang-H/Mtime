package com.kotlin.android.community.post.component.item.adapter

import android.view.View
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.community.post.component.R
import com.kotlin.android.community.post.component.databinding.ItemCommunityCenterFilmCommentBinding
import com.kotlin.android.community.post.component.item.bean.CommunityPostHotComment
import com.kotlin.android.community.post.component.item.bean.CommunityPostItem
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 文章、帖子、日志
 *
 * 注：CommunityPostBinder 社区post
 * @date 2022/3/27
 */
open class CommunityCenterFilmCommentBinder(
    val post: CommunityPostItem
) : MultiTypeBinder<ItemCommunityCenterFilmCommentBinding>() {

    private val mFamilyProvider by lazy { getProvider(ICommunityFamilyProvider::class.java) }
    private val mUgcProvider by lazy { getProvider(IUgcProvider::class.java) }
    private val mTicketProvider: ITicketProvider? by lazy { getProvider(ITicketProvider::class.java) }
    private val mCommunityProvider: ICommunityPersonProvider? by lazy {
        getProvider(
            ICommunityPersonProvider::class.java
        )
    }

    //当前点击的热门评论(点赞、点踩，跳转等)
    var mCurClickComment: CommunityPostHotComment? = null

    override fun layoutId(): Int = R.layout.item_community_center_film_comment

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommunityCenterFilmCommentBinder && other.post.hashCode() == post.hashCode()
    }

    override fun onBindViewHolder(binding: ItemCommunityCenterFilmCommentBinding, position: Int) {
        binding.mCommunityPostCommentCountTv.text = formatCount(post.commentCount)
        binding.mCommunityPostCommentCountTv.text =
            String.format(
                getString(R.string.community_film_comment_count_des),
                formatCount(post.likeCount),
                formatCount(post.commentCount)
            )
        binding.bottom.setBackground(cornerRadius = 4f.dpF, colorRes = R.color.color_f2f2f2)
        binding.run {
            iv.loadImage(
                data = post.moviePic,
                width = 80.dp,
                height = 108.dp
            )
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mCommunityPostRoot -> {
                //UGC详情
                mUgcProvider?.launchDetail(post.id, post.type)
            }
            R.id.mCommunityPostImgCardView,
            R.id.iv -> {
                //影片详情
                mTicketProvider?.startMovieDetailsActivity(post.movieId)
            }
            R.id.mCommunityPostCommentCountTv -> {
                //评论点击，跳转到详情页面并定位到评论区域
                mUgcProvider?.launchDetail(
                    post.id,
                    post.type,
                   0L,
                    true
                )
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
            R.id.mCommunityPostUserNameTv,
            R.id.mCommunityPostUserProfileIv -> {
                mCommunityProvider?.startPerson(post.userId)
            }
            else -> {
                super.onClick(view)
            }
        }
    }
}
