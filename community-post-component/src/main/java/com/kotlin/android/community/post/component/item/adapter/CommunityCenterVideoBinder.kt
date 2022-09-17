package com.kotlin.android.community.post.component.item.adapter

import android.view.View
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_VIDEO
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.community.post.component.R
import com.kotlin.android.community.post.component.databinding.ItemCommunityCenterVideoBinding
import com.kotlin.android.community.post.component.item.bean.CommunityPostItem
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 个人主页 视频 binder*
 * @date 2022/3/27
 */
open class CommunityCenterVideoBinder(
    val post: CommunityPostItem
) : MultiTypeBinder<ItemCommunityCenterVideoBinding>() {

    private val mUgcProvider by lazy { getProvider(IUgcProvider::class.java) }
    private val mCommunityProvider by lazy {
        getProvider(
            ICommunityPersonProvider::class.java
        )
    }

    override fun layoutId(): Int = R.layout.item_community_center_video

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommunityCenterVideoBinder && other.post.hashCode() == post.hashCode()
    }

    override fun onBindViewHolder(binding: ItemCommunityCenterVideoBinding, position: Int) {
        binding.mCommunityPostCommentCountTv.text = formatCount(post.commentCount)
        binding.mCommunityPostLikeTv.text = formatCount(post.likeCount)
        binding.iv.loadImage(
            data = post.imgs?.let { it[0]?.pic },
            width = 327.dp,
            height = 143.dp
        )
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mCommunityPostRoot -> {
                // 跳转视频详情页
                getProvider(IUgcProvider::class.java) {
                    launchDetail(
                        contentId = post.id,
                        type = CONTENT_TYPE_VIDEO,
                        recId = 0L,
                        needToComment = false,
                    )
                }
            }
            R.id.mCommunityPostUserNameTv,
            R.id.mCommunityPostUserProfileIv -> {
                mCommunityProvider?.startPerson(post.userId)
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

            else -> {
                super.onClick(view)
            }
        }
    }


}
