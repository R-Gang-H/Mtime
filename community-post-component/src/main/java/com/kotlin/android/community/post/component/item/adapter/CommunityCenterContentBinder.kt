package com.kotlin.android.community.post.component.item.adapter

import android.text.Spannable
import android.text.SpannableString
import android.view.View
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.community.post.component.R
import com.kotlin.android.community.post.component.databinding.ItemCommunityCenterContentBinding
import com.kotlin.android.community.post.component.item.bean.CommunityPostHotComment
import com.kotlin.android.community.post.component.item.bean.CommunityPostItem
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.invisible
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.views.CommunityQuoteSpan

/**
 * 文章、帖子、日志
 *
 * 注：CommunityPostBinder 社区post
 * @date 2022/3/27
 */
open class CommunityCenterContentBinder(
        val post: CommunityPostItem
) : MultiTypeBinder<ItemCommunityCenterContentBinding>() {

    private val mFamilyProvider by lazy { getProvider(ICommunityFamilyProvider::class.java) }
    private val mUgcProvider by lazy { getProvider(IUgcProvider::class.java) }
    private val mTicketProvider: ITicketProvider? by lazy { getProvider(ITicketProvider::class.java) }
    private val mCommunityProvider: ICommunityPersonProvider? by lazy { getProvider(ICommunityPersonProvider::class.java) }

    //当前点击的热门评论(点赞、点踩，跳转等)
    var mCurClickComment: CommunityPostHotComment? = null

    override fun layoutId(): Int = R.layout.item_community_center_content

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommunityCenterContentBinder && other.post.hashCode() == post.hashCode()
    }

    override fun onBindViewHolder(binding: ItemCommunityCenterContentBinding, position: Int) {
        binding.mCommunityPostLikeTv.text = formatCount(post.likeCount)
        binding.mCommunityPostCommentCountTv.text = formatCount(post.commentCount)
        binding.title.text = post.title
        binding.mCommunityPostTitleTv.text = post.mixWord
        binding.bgFamily.setBackground(cornerRadius = 4f.dpF,colorRes = R.color.color_f2f2f2)
        bindImgs(binding, position)
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
            R.id.mCommunityPostUserNameTv,
            R.id.mCommunityPostUserProfileIv -> {
                mCommunityProvider?.startPerson(post.userId)
            }
            else -> {
                super.onClick(view)
            }
        }
    }


    //显示帖子图片
    private fun bindImgs(binding: ItemCommunityCenterContentBinding, position: Int) {
        binding.run {
            mCommunityPostImgMoreTv.gone()
            mCommunityPostImgGifTv1.gone()
            mCommunityPostImgGifTv2.gone()
            mCommunityPostImgGifTv3.gone()
            mCommunityPostImgGifTv4.gone()
            mCommunityPostImgIv1.gone()
            mCommunityPostImgIv31.gone()
            mCommunityPostImgIv41.gone()
            mCommunityPostImgIv42.gone()
            mCommunityPostImgIv43.gone()
            mCommunityPostImgIv44.gone()
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
                            mCommunityPostImgIv41.visible()
                            mCommunityPostImgIv42.visible()
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
                            mCommunityPostImgGifTv1.visible(it[0].isGif)
                            mCommunityPostImgGifTv2.visible(it[1].isGif)
                        }
                        3 -> {
                            mCommunityPostImgIv31.visible()
                            mCommunityPostImgIv42.visible()
                            mCommunityPostImgIv44.visible()
                            mCommunityPostImgIv41.invisible()
                            mCommunityPostImgIv43.invisible()
                            mCommunityPostImgIv31.loadImage(
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
