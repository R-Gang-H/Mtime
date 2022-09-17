package com.kotlin.android.community.post.component.item.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.community.post.component.R
import com.kotlin.android.community.post.component.item.bean.*
import com.kotlin.android.app.data.annotation.GROUP_ROLE_BLACKLIST
import com.kotlin.android.app.data.constant.CommConstant.PRAISE_OBJ_TYPE_POST
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.formatCount

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.comment.ICommentProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.android.synthetic.main.item_pk_post_comment.view.*

/**
 * create by lushan on 2020/8/13
 * description:pk帖子评论列表
 */
class PkCommentListView @JvmOverloads constructor(var ctx: Context, var attrs: AttributeSet? = null, var defStyleAttr: Int = 0) : LinearLayout(ctx, attrs, defStyleAttr) {
    private val layoutInter = LayoutInflater.from(ctx)
    private var positiveLL: LinearLayout //支持方评论
    private var navigationLL: LinearLayout//反对方评论
    private var listener: ((PKComentViewBean, Boolean) -> Unit)? = null
    private var pkListener: ((View, PKComentViewBean) -> Unit)? = null
    private val commentProvider: ICommentProvider? = getProvider(ICommentProvider::class.java)
    private val positiveCommentList: MutableList<PKComentViewBean> = mutableListOf()//支持评论列表
    private val navigationCommentList: MutableList<PKComentViewBean> = mutableListOf()//反对评论列表
    private var isBlackUser:Boolean = false
    private var familyStatus:Long = 0L//当前用户是否加入此家族 0:未加入1:已加入成功2:加入中（待审核）3:黑名单
    private var userGroupRole:Long = 0L//当前用户家族权限 APPLICANT(-1, "申请者"), OWNER(1, "族长"), ADMINISTRATOR(2, "管理员"), MEMBER(3, "普通成员"), BLACKLIST(4, "黑名单");
    private var familyPostStatus:Long? =0L//家族的发帖和评论的权限 JOIN_POST_JOIN_COMMENT(1, "加入发帖加入评论"), FREE_POST_FREE_COMMENT(2, "自由发帖自由评论"), ADMIN_POST_FREE_COMMENT(3, "管理员发帖自由评论");
    private var groupId:Long? = 0L//家族id
    private var commentPmsn: Long? = 1L//评论权限 1允许任何人 2禁止所有人

    fun setBlackUser(familyStatus:Long,userGroupRole:Long, familyPostStatus: Long, groupId: Long,commentPmsn: Long?){
        this.isBlackUser = familyStatus == CommunityContent.GROUP_JOIN_BLACK_NAME || userGroupRole == GROUP_ROLE_BLACKLIST
        this.familyStatus = familyStatus
        this.userGroupRole = userGroupRole
        this.familyPostStatus = familyPostStatus
        this.groupId = groupId
        this.commentPmsn = commentPmsn
    }
    init {
        orientation = HORIZONTAL
        weightSum = 2f
        positiveLL = LinearLayout(ctx).apply {
            orientation = VERTICAL
        }

        navigationLL = LinearLayout(ctx).apply {
            orientation = VERTICAL
        }

        val leftLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            weight = 1f
            this.setMargins(0, 0, 7.5F.dp, 0)
        }
        val rightLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            weight = 1f
            this.setMargins(7.5F.dp, 0, 0, 0)
        }
        addView(positiveLL, leftLayoutParams)
        addView(navigationLL, rightLayoutParams)

    }

    fun getPositiveList() = positiveCommentList
    fun getNavigationList() = navigationCommentList

    fun getPositiveSize() = positiveCommentList.size

    fun getNaviSize() = navigationCommentList.size


    fun addCommentBinderList(list: MutableList<MultiTypeBinder<*>>, isCommentPositive: Boolean) {
        if (isCommentPositive) {
            positiveCommentList.clear()
        } else {
            navigationCommentList.clear()
        }
        val commentList = list.filter { it is CommentListBinder }.map {
            val binder = it as CommentListBinder
            val bean = binder.bean
            PKComentViewBean.create(bean, isCommentPositive)
        }.toMutableList()
        addCommentList(commentList, isCommentPositive)
    }

    fun setCommentList(list: MutableList<PKComentViewBean>, isCommentPositive: Boolean) {
        if (isCommentPositive) {
            positiveCommentList.clear()
        } else {
            navigationCommentList.clear()
        }
        addCommentList(list, isCommentPositive)
    }

    /**
     * 添加评论列表
     */
    private fun addCommentList(list: MutableList<PKComentViewBean>, isCommentPositive: Boolean) {
        list.forEach { addComment(it, isCommentPositive) }

    }

    private val itemLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
        setMargins(0, 5.dp, 0, 0)
    }

    fun addComment(pkCommentBean: PKComentViewBean, isCommentPositive: Boolean, isFirst: Boolean = false) {

        addCommentByIndex(pkCommentBean, isCommentPositive, if (isFirst) 0 else -1)
    }

    /**
     * 根据索引添加item
     */
    private fun addCommentByIndex(pkCommentBean: PKComentViewBean, isCommentPositive: Boolean, index: Int) {
//        添加评论数据
        addPkCommentBean(isCommentPositive, index, pkCommentBean)
        val inflate = layoutInter.inflate(R.layout.item_pk_post_comment, if (isCommentPositive) positiveLL else navigationLL, false)
        inflate.layoutParams = itemLayoutParams
//            设置背景
        setBackGroundAndTextColor(inflate, pkCommentBean, isCommentPositive)
        setContent(inflate, pkCommentBean)
        setListener(inflate, pkCommentBean, isCommentPositive)
//添加评论View
        addPkViewByIndex(isCommentPositive, index, inflate)
    }

    private fun addPkViewByIndex(isCommentPositive: Boolean, index: Int, inflate: View?) {
        if (isCommentPositive) {
            if (index != -1) {
                positiveLL.addView(inflate, index)
            } else {
                positiveLL.addView(inflate)
            }
        } else {
            if (index != -1) {
                navigationLL.addView(inflate, index)
            } else {
                navigationLL.addView(inflate)
            }
        }
    }

    private fun addPkCommentBean(isCommentPositive: Boolean, index: Int, pkCommentBean: PKComentViewBean) {
        if (isCommentPositive) {
            if (index != -1) {
                positiveCommentList.add(index, pkCommentBean)
            } else {
                positiveCommentList.add(pkCommentBean)
            }
        } else {
            if (index != -1) {
                navigationCommentList.add(index, pkCommentBean)
            } else {
                navigationCommentList.add(pkCommentBean)
            }
        }
    }

    fun setListener(listener: ((PKComentViewBean, Boolean) -> Unit)? = null) {
        this.listener = listener
    }

    fun setPkListener(pkListener: ((View, PKComentViewBean) -> Unit)? = null) {
        this.pkListener = pkListener
    }

    /**
     * 设置监听事件
     */
    private fun setListener(inflate: View?, pkCommentBean: PKComentViewBean, isCommentPositive: Boolean) {
//        整个item
        inflate?.apply {
            setTag(R.string.app_name, pkCommentBean)
            onClick {
//                跳转到评论详情
                commentProvider?.startComment(pkCommentBean.commentId, PRAISE_OBJ_TYPE_POST, isCommentPositive,isBlackUser,familyStatus, userGroupRole, commentPmsn,familyPostStatus?:0L,groupId?:0L)
            }
        }

//        点赞
        inflate?.praiseTv?.apply {
            setTag(R.string.app_name, pkCommentBean)
            onClick {
                listener?.invoke(pkCommentBean, true)
                pkListener?.invoke(it, pkCommentBean)
            }
        }

//        踩
        inflate?.unPraiseTv?.apply {
            setTag(R.string.app_name, pkCommentBean)
            onClick {
                listener?.invoke(pkCommentBean, false)
                pkListener?.invoke(it, pkCommentBean)
            }
        }


    }

    /**
     * 设置信息内容
     */
    private fun setContent(rootView: View, pkCommentBean: PKComentViewBean) {
        with(pkCommentBean) {
            rootView.authIv?.visible(pkCommentBean.userAuthType > 1L)
//            4机构认证
            rootView.authIv?.setImageResource(if (pkCommentBean.userAuthType == 4L) R.drawable.ic_jigourenzheng else R.drawable.ic_yingrenrenzheng)
            rootView.userHeadIv?.loadImage(
                data = userHeadPic,
                width = 24.dp,
                height = 24.dp,
                circleCrop = true,
                defaultImgRes = R.drawable.default_user_head
            )
            rootView.userNameTv?.text = userNickName
            rootView.commentContentTv?.text = comment
            rootView.praiseTv?.text = formatCount(praiseCount)
            rootView.unPraiseTv?.text = formatCount(unPraiseCount)

        }
    }

    /**
     * @param likeState 自己点赞状态
     * @param isPraiseTv 是否是点赞按钮
     * @param isCommentPositive 是否是正方评论
     * @return 返回点赞和踩的颜色
     */
    @ColorRes
    private fun getParisColor(likeState: Long, isPraiseTv: Boolean, isCommentPositive: Boolean): Int {
        return when (likeState) {
            PRAISE_STATE_PRAISE -> {//自己点了赞
                if (isCommentPositive) {
                    if (isPraiseTv) R.color.color_36c096 else R.color.color_8798af
                } else {
                    if (isPraiseTv) R.color.color_feb12a else R.color.color_8798af
                }
            }
            PRAISE_STATE_UNPRAISE -> {//自己点了踩
                if (isCommentPositive) {
                    if (isPraiseTv) R.color.color_8798af else R.color.color_36c096
                } else {
                    if (isPraiseTv) R.color.color_8798af else R.color.color_feb12a
                }
            }
            else -> {
                R.color.color_8798af
            }
        }
    }


    /**
     * 获取点赞和踩的样式
     */
    private fun getCommentDrawable(likeState: Long, isPraiseTv: Boolean, isCommentPositive: Boolean): Drawable? {
        return getDrawable(if (isPraiseTv) R.drawable.ic_like else R.drawable.ic_dislike)?.apply {
            val color = getColor(getParisColor(likeState, isPraiseTv, isCommentPositive))
            setTint(color)
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }

    }

    /**
     * 设置评论背景色和文案、点赞颜色
     */
    private fun setBackGroundAndTextColor(rootView: View, bean: PKComentViewBean, isCommentPositive: Boolean) {
        with(bean) {
            ShapeExt.setShapeCorner2Color2Stroke(rootView, corner = 4.dp, strokeColor = if (isCommentPositive) R.color.color_6636c096 else R.color.color_66feb12a, strokeWidth = 1.dp)
//            rootView.alpha = 0.4f
            rootView.userNameTv?.setTextColor(getColor(if (isCommentPositive) R.color.color_36c096 else R.color.color_feb12a))
            rootView.praiseTv?.apply {
                setTextColor(getColor(getParisColor(likeState, true, isCommentPositive)))
                setCompoundDrawables(getCommentDrawable(likeState, true, isCommentPositive), null, null, null)
            }
            rootView.unPraiseTv?.apply {
                setTextColor(getColor(getParisColor(likeState, false, isCommentPositive)))
                setCompoundDrawables(getCommentDrawable(likeState, false, isCommentPositive), null, null, null)
            }
        }

    }

    private fun getIndexFromCommentList(list: MutableList<PKComentViewBean>, bean: PKComentViewBean): Int {
        var resultIndex = -1
        list.forEachIndexed { index, pkComentViewBean ->
            if (pkComentViewBean.commentId == bean.commentId) {
                resultIndex = index
                return@forEachIndexed
            }
        }
        return resultIndex
    }

    /**
     * 更新点赞信息
     */
    fun updatePraiseUp(bean: PKComentViewBean) {
        var resultIndex = getIndexFromCommentList(if (bean.isCommentPositive) positiveCommentList else navigationCommentList, bean)
        if (resultIndex != -1) {//在支持列表中
            handlePraiseUp(bean)
            if (bean.isCommentPositive) {
                positiveCommentList.removeAt(resultIndex)
                positiveLL.removeViewAt(resultIndex)
            } else {
                navigationCommentList.removeAt(resultIndex)
                navigationLL.removeViewAt(resultIndex)
            }
            addCommentByIndex(bean, bean.isCommentPositive, resultIndex)
            return
        }

    }

    /**
     * 处理点赞信息
     */
    private fun handlePraiseUp(bean: PKComentViewBean) {
        when {
            bean.isPariseUp() -> {//当前是点赞，对应就是取消点赞
                bean.likeState = PRAISE_STATE_INIT
                bean.praiseCount--
            }
            bean.isPraiseDown() -> {
                bean.likeState = PRAISE_STATE_PRAISE
                bean.praiseCount++
                bean.unPraiseCount--
            }
            else -> {
                bean.likeState = PRAISE_STATE_PRAISE
                bean.praiseCount++
            }
        }
    }

    /**
     * 更新点踩信息
     */
    fun updatePraiseDown(bean: PKComentViewBean) {
        var resultIndex = getIndexFromCommentList(if (bean.isCommentPositive) positiveCommentList else navigationCommentList, bean)
        if (resultIndex != -1) {//在支持列表中
            handlePraiseDown(bean)
            if (bean.isCommentPositive) {
                positiveCommentList.removeAt(resultIndex)
                positiveLL.removeViewAt(resultIndex)
            } else {
                navigationCommentList.removeAt(resultIndex)
                navigationLL.removeViewAt(resultIndex)
            }
            addCommentByIndex(bean, bean.isCommentPositive, resultIndex)
        }

    }

    /**
     * 更新点踩
     */
    private fun handlePraiseDown(bean: PKComentViewBean) {
        when {
            bean.isPraiseDown() -> {//原来是点踩
                bean.likeState = PRAISE_STATE_INIT
                bean.unPraiseCount--
            }
            bean.isPariseUp() -> {//原来是点赞
                bean.likeState = PRAISE_STATE_UNPRAISE
                bean.praiseCount--
                bean.unPraiseCount++
            }
            else -> {//原来无状态
                bean.likeState = PRAISE_STATE_UNPRAISE
                bean.unPraiseCount++
            }
        }
    }

    fun clear() {
        positiveLL.removeAllViews()
        navigationLL.removeAllViews()
        positiveCommentList.clear()
        navigationCommentList.clear()
        removeAllViews()
    }

    fun clearNavi() {
        navigationLL.removeAllViews()
        navigationCommentList.clear()
    }

    fun clearPositivie() {
        positiveLL.removeAllViews()
        positiveCommentList.clear()
    }

    /**
     * 从详情回来删除了某条评论
     */
    fun deleteComment(commentId: Long, isCommentPositive: Boolean) {
        deleteCommentItem(commentId, isCommentPositive)
        deleteCommentFromList(commentId, if (isCommentPositive) positiveCommentList else navigationCommentList)
    }

    private fun deleteCommentFromList(commentId: Long, list: MutableList<PKComentViewBean>) {
        list.removeIf { commentId == it.commentId }
    }

    private fun deleteCommentItem(commentId: Long, isCommentPositive: Boolean) {
        val list: MutableList<PKComentViewBean> = if (isCommentPositive) positiveCommentList else navigationCommentList
        val indexOfFirst = list.indexOfFirst { it.commentId == commentId }
        if (indexOfFirst < 0) {
            return
        }
        if (isCommentPositive) {
            positiveLL.removeViewAt(indexOfFirst)
        } else {
            navigationLL.removeViewAt(indexOfFirst)
        }

    }

}