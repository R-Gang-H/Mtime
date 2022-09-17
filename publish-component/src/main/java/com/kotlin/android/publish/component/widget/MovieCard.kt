package com.kotlin.android.publish.component.widget

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.ktx.ext.core.removeFromParent
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.setForeground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.log.w
import com.kotlin.android.publish.component.R
import kotlinx.android.synthetic.main.view_publish_movie_card.view.*
import java.lang.StringBuilder

/**
 * 电影卡片视图
 *
 * Created on 2020/7/10.
 *
 * @author o.s
 */
class MovieCard : FrameLayout {

    private val tag = "   4 ->"

    private val margin = 8.dp
    private val mHeight = 54.dp
    private val views = ArrayList<View>()
    private var itemView: PublishItemView? = null

    var movie: Movie? = null
        set(value) {
            field = value
            this.title?.text = value?.name ?: ""
        }

    val body: String
        get() {
            if ((movie?.movieId ?: 0) <= 0) {
                return ""
            }
            return """
                <figure class="movieCard" contenteditable="false" data-mtime-movie-id="${movie?.movieId}">
                    <div class="DRE-subject-wrapper">
                        <div class="DRE-subject-item" data-v-6d079e13="">
                            <div class="DRE-subject-cover" data-v-6d079e13="">
                                <img src="${movie?.img}" data-mtime-img="${movie?.movieId}"/>
                            </div>
                            <div class="DRE-subject-info" data-v-6d079e13="">
                                <div class="DRE-subject-title" data-v-6d079e13="">${movie?.name}</div>
                                <div class="DRE-subject-rating" data-v-6d079e13="" $ratingEmptyStyle>
                                    $ratingSpan
                                </div>
                                <div class="DRE-subject-desc" data-v-6d079e13="">
                                    <span data-v-6d079e13=""> $timeAndMovieType</span>
                                    <span data-v-6d079e13=""> $realTimeAndLocation </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </figure>
                """.trimIndent()
        }

    /**
     * 评分大于0的情况下，需要添加评分模块
     */
    private val ratingSpan: String
        get() {
            return if ((movie?.rating ?: 0.0) > 0.0) {
                """
                    <span data-v-6d079e13="">时光评分</span>
                    <span class="DRE-rating-score" data-v-6d079e13="">${movie?.rating}</span>
                """.trimIndent()
            } else {
                ""
            }
        }

    /**
     * 评分为空时需要加style
     */
    private val ratingEmptyStyle: String
        get() {
            return if(TextUtils.isEmpty(ratingSpan)) {
                """style="display: none;""""
            } else {
                ""
            }
        }

    /**
     * 181分钟-动作/冒险/奇幻/家庭/奇幻/家庭/奇幻/家庭/奇幻/家庭
     */
    private val timeAndMovieType: String
        get() {
            val sb = StringBuilder()
            movie?.run {
                if ((length ?: 0) > 0) {
                    sb.append(length)
                    sb.append("分钟-")
                }
                if (!movieType.isNullOrEmpty()) {
                    sb.append(movieType)
                }
            }
            return sb.toString()
        }

    /**
     * 2019年4月24日中国上映
     */
    private val realTimeAndLocation: String
        get() {
            val sb = StringBuilder()
            movie?.run {
                if (!realTime.isNullOrEmpty()) {
                    sb.append(realTime)
                }
                if (!rLocation.isNullOrEmpty()) {
                    sb.append(rLocation)
                    sb.append("上映")
                }
            }
            return sb.toString()
        }

    /**
     * 富文本视图状态
     */
    var state: State = State.NORMAL
        set(value) {
            if (field == value) {
                return
            }
            field = value
            changeState()
        }

    /**
     * 事件处理
     */
    private var action: ((v: PublishItemView, actionEvent: ActionEvent) -> Unit)? = null
        set(value) {
            field = value
            children.forEach {
                (it as? ActionMoveView)?.setAction(itemView, action)
                (it as? ActionLinkView)?.setAction(itemView, action)
            }
        }

    /**
     * 拖拽改变监听
     */
    var dragChange: ((v: View, dy: Float) -> Unit)? = null
        set(value) {
            field = value
            children.forEach {
                val v = it as? ActionMoveView
                v?.dragChange = value
            }
        }

    fun setAction(
            itemView: PublishItemView?,
            action: ((v: PublishItemView, actionEvent: ActionEvent) -> Unit)?
    ) {
        this.itemView = itemView
        this.action = action
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
        val params = MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, mHeight).apply {
            setMargins(margin, margin, margin, 0)
        }
        layoutParams = params
        setBackground(colorRes = R.color.color_f2f3f6, cornerRadius = 4.dpF)
        val view = View.inflate(context, R.layout.view_publish_movie_card, null) as? FrameLayout
        view?.run {
            children.forEach {
                views.add(it)
            }
        }
        views.forEach {
            it.removeFromParent()
            addView(it)
        }

        actionLink?.setAction(itemView, action)
        actionMove?.setAction(itemView, action)
    }

    private fun changeState() {
        when (state) {
            State.EDIT -> {
                editState()
            }
            State.MOVE -> {
                moveState()
            }
            State.NORMAL -> {
                normalState()
            }
        }
    }

    private fun editState() {
        "$tag editState".e()
        setForeground(
            strokeColorRes = R.color.color_20a0da,
            strokeWidth = 2.dp,
            cornerRadius = 4.dpF
        )
        actionLink?.visibility = View.GONE
        actionMove?.visibility = View.GONE
    }

    private fun moveState() {
        "$tag moveState".e()
        foreground = null
        actionLink?.visibility = View.GONE
        actionMove?.visibility = View.VISIBLE
    }

    private fun normalState() {
        "$tag normalState".e()
        foreground = null
        actionLink?.visibility = View.VISIBLE
        actionMove?.visibility = View.VISIBLE
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        "$tag onFocusChanged".w()
        state = if (gainFocus) {
            State.EDIT
        } else {
            State.NORMAL
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        "$tag dispatchTouchEvent".i()
        return super.dispatchTouchEvent(ev)
    }

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        "$tag requestDisallowInterceptTouchEvent".i()
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        "$tag onInterceptTouchEvent".i()
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        ev?.action?.let {
            when (it) {
                MotionEvent.ACTION_DOWN -> {
                    "$tag onTouchEvent ACTION_DOWN".e()
                }
                MotionEvent.ACTION_UP -> {
                    "$tag onTouchEvent ACTION_UP".e()
                }
                MotionEvent.ACTION_CANCEL -> {
                    "$tag onTouchEvent ACTION_CANCEL".e()
                }
                MotionEvent.ACTION_MOVE -> {
                    "$tag onTouchEvent ACTION_MOVE".e()
                }
            }
        }
        return super.onTouchEvent(ev)
    }

}
/**
"""
<!--电影卡片模版--->
<figure class="movieCard" contenteditable="false" data-mtime-movie-id="208116">
    <div class="DRE-subject-wrapper">
        <div class="DRE-subject-item" data-v-6d079e13="">
            <div class="DRE-subject-cover" data-v-6d079e13="">
                <img src="http://img5.mtime.cn/mg/2019/10/01/174800.67529085.jpg" data-mtime-img="208116"/>
            </div>
            <div class="DRE-subject-info" data-v-6d079e13="">
                <div class="DRE-subject-title" data-v-6d079e13="">LG测试影片A</div>
                <div class="DRE-subject-rating" data-v-6d079e13="">
                    <span data-v-6d079e13="">时光评分</span>
                    <span class="DRE-rating-score" data-v-6d079e13="">6.7</span>
                </div>
                <div class="DRE-subject-desc" data-v-6d079e13="">
                    <span data-v-6d079e13=""> 145分钟-动作 / 幻想 /科幻</span>
                    <span data-v-6d079e13=""> 2020年9月30日上映 </span>
                </div>
            </div>
        </div>
    </div>
</figure>
"""
 */

