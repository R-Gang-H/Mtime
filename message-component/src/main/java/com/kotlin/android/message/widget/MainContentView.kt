package com.kotlin.android.message.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.message.databinding.MessageViewCommentContentBinding
import com.kotlin.android.message.tools.ContentType
import com.kotlin.android.message.tools.MessageCenterJumper


@BindingAdapter("property")
fun setNoticeText(view: MainContentView, property: MainContentView.MainContentViewProperty) {
    view.setProperty(property)
}

@BindingAdapter("longClick")
fun longClick(view: MainContentView, longClick: () -> Unit) {
    view.setOnLongClick(longClick)
}

/**
 * Created by zhaoninglongfei on 2022/3/22
 * 评论主体样式\点赞主题样式
 */
class MainContentView : ConstraintLayout {

    private lateinit var binding: MessageViewCommentContentBinding

    //评论、点赞主题view的 property
    data class MainContentViewProperty(
        var contentId: Long? = 0L,
        var contentType: ContentType?,
        var content: String? = null,
        var img: String? = null,
        var commentCount: Long? = null,
        var praiseCount: Long = 0L,
        var insideContent: InsideContent? = null,
        var jumpType: JumpType = JumpType.Content
    ) : ProguardRule {
        data class InsideContent(
            var insideContentId: Long? = 0L,
            var insideContentType: ContentType?,
            var content: String? = null,
            var img: String? = null,
            var commentCount: Long = 0L,
            var praiseCount: Long = 0L
        ) : ProguardRule

        enum class JumpType {
            Content, Comment
        }

        //外层内容点击
        fun clickContent(view: View) {
            if (this.insideContent == null) {
                when (this.jumpType) {
                    JumpType.Content -> {
                        MessageCenterJumper.contentJump(
                            view.context,
                            this.contentId ?: 0L,
                            this.contentType
                        )
                    }
                    JumpType.Comment -> {
                        MessageCenterJumper.jumpToCommentDetail(
                            type = this.contentType?.type ?: 0L,
                            commentId = this.contentId ?: 0L
                        )
                    }
                }
            } else {
                MessageCenterJumper.jumpToCommentDetail(
                    type = this.contentType?.type ?: 0L,
                    commentId = this.contentId ?: 0L
                )
            }
        }

        //内层内容点击
        fun clickInsideContent(view: View) {
            if (this.insideContent != null) {
                MessageCenterJumper.contentJump(
                    view.context,
                    this.insideContent?.insideContentId ?: 0L,
                    this.insideContent?.insideContentType
                )
            }
        }

        //主体评论展示
        fun commentVisible(): Int {
            return if (this.insideContent == null) {
                if (commentCount == null) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            } else {
                View.GONE
            }
        }

        //主体图片展示
        fun contentImgVisible(): Int {
            return if (this.img.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        //主题内容行数
        fun contentMaxLines(): Int {
            return if (insideContent == null) {
                2
            } else {
                if (this.img.isNullOrEmpty()) {
                    1
                } else {
                    3
                }
            }
        }
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        binding = MessageViewCommentContentBinding.inflate(LayoutInflater.from(context))
        binding.root.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(binding.root)
    }

    fun setProperty(property: MainContentViewProperty) {
        binding.mainContent = property
    }

    fun setOnLongClick(longClick: () -> Unit) {
        binding.longClick = OnLongClickListener {
            longClick.invoke()
            true
        }
    }
}