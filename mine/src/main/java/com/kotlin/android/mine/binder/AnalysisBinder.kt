package com.kotlin.android.mine.binder

import com.kotlin.android.app.data.entity.mine.DataCenterBean
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mine.*
import com.kotlin.android.mine.databinding.MineItemAnalysisBinding
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.mine.binder
 * @ClassName:      AnalysisBinder
 * @Description:    单篇分析Item
 * @Author:         haoruigang
 * @CreateDate:     2022/3/18 17:30
 */
class AnalysisBinder(
    var bean: DataCenterBean.SingleAnalysisBean.Item,
    var type: Long,
    var amountShow: Boolean = true, // 默认显示
    val action: (() -> Unit)? = null,
) : MultiTypeBinder<MineItemAnalysisBinding>() {

    var imageUrl: String = ""

    override fun layoutId() = R.layout.mine_item_analysis

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is AnalysisBinder && other.bean != bean
    }

    override fun onBindViewHolder(binding: MineItemAnalysisBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.apply {
            analysisBinder = this@AnalysisBinder

            if (amountShow.not()) {
                amountBox.gone()
            } else {
                ShapeExt.setShapeCornerWithColor(view = amountBox,
                    solideColor = R.color.color_20a0da_alpha_5,
                    leftTopCorner = 0,
                    rightTopCorner = 6,
                    leftBottomCorner = 6,
                    rightBottomCorner = 6)
            }

            singleAnalys.apply {
                when (type) {
                    CONTENT_TYPE_ARTICLE, CONTENT_TYPE_POST, CONTENT_TYPE_JOURNAL -> { // 文章、帖子、日志
                        copyWritingPic.visible()
                        longCommentIma.gone()
                        audioIma.gone()
                        tipsTv.gone()

                        if (bean.mixImages.isNotEmpty()) {
                            this@AnalysisBinder.imageUrl =
                                if (bean.images.isNullOrEmpty().not())
                                    bean.images[0].imageUrl
                                else converterImgs(bean.mixImages).toString()
                        } else {
                            if (type == CONTENT_TYPE_POST || type == CONTENT_TYPE_JOURNAL) { // 帖子|日志 无封面，不展示
                                copyWritingPic.gone()
                            }
                        }

                    }
                    CONTENT_TYPE_COMMENT -> { // 影评
                        copyWritingPic.gone()
                        longCommentIma.visible()
                        audioIma.gone()
                        tipsTv.gone()

                        if (bean.fcMovie != null) {
                            this@AnalysisBinder.imageUrl =
                                if (bean.fcMovie != null)
                                    bean.fcMovie?.imgUrl.toString()
                                else converterImgs(bean.mixImages).toString()
                        }
                    }
                    CONTENT_TYPE_VIDEO -> { // 视频
                        copyWritingPic.visible()
                        longCommentIma.gone()
                        audioIma.gone()
                        tipsTv.visible()

                        if (bean.mixVideos.isNotEmpty()) {
                            this@AnalysisBinder.imageUrl =
                                if (bean.mixVideos.isNullOrEmpty().not())
                                    bean.mixImages[0].imageUrl  // TODO bean.mixVideos[0].posterUrl
                                else converterImgs(bean.mixImages).toString()
                        }
                    }
                    CONTENT_TYPE_AUDIO -> { // 音频
                        copyWritingPic.gone()
                        longCommentIma.gone()
                        audioIma.visible()
                        tipsTv.gone()

                        if (bean.mixImages.isNotEmpty()) {
                            this@AnalysisBinder.imageUrl =
                                if (bean.images.isNullOrEmpty().not())
                                    bean.images[0].imageUrl
                                else converterImgs(bean.mixImages).toString()
                        }
                    }
                }
            }

            rlItemAnalysis.setOnClickListener {
                action?.invoke()
            }
        }
    }

    //转换图片
    private fun converterImgs(mixImages: List<DataCenterBean.SingleAnalysisBean.Item.MixImage>?): List<String>? {
        mixImages?.run {
            return map {
                it.imageUrl
            }
        }
        return null
    }

}