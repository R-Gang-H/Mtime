package com.kotlin.android.community.post.component.item.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.comment.component.bean.DetailBaseExtend
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.community.post.component.item.adapter.PkCommentBinder
import com.kotlin.android.community.post.component.item.adapter.PkVoteDetailBinder
import com.kotlin.android.community.post.component.item.bean.PKComentViewBean
import com.kotlin.android.community.post.component.item.repository.PostDetailRepository
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.user.afterLogin
import com.kotlin.android.ugc.detail.component.bean.UgcDetailViewBean
import com.kotlin.android.ugc.detail.component.bean.UgcTitleBarBean
import com.kotlin.android.ugc.detail.component.binder.FamilyBinder
import com.kotlin.android.ugc.detail.component.binder.UgcTitleBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/11/2
 * description:帖子详情 普通帖子和pk帖子
 */
class PostDetailViewModel : DetailBaseViewModel() {
    private val repo by lazy { PostDetailRepository() }
    private val _titleBar = MutableLiveData<UgcTitleBarBean>()

    val titleBar: LiveData<UgcTitleBarBean>
        get() = _titleBar

    private var detailUiModel: BaseUIModel<UgcDetailViewBean> = BaseUIModel()

    val detailState = detailUiModel.uiState

    //    投票
    private val voteUIModel = BaseUIModel<DetailBaseExtend<Any>>()
    val voteState = voteUIModel.uiState


    /**
     * 投票
     */
    fun vote(objType: Long,
             objId: Long,
             voteId: Long,
             extend: Any) {

        afterLogin {
            viewModelScope.launch(main) {
                voteUIModel.showLoading = true
                val result = withOnIO { repo.vote(1L, objId, voteId, extend) }
                voteUIModel.checkResultAndEmitUIState(result = result)
            }
        }
    }


    fun loadUgcDetail(contentId: Long, type: Long, recId:Long) {
        viewModelScope.launch(main) {
            detailUiModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.loadDetail(contentId, type, recId)
            }

            checkResult(result, error = { detailUiModel.emitUIState(error = it) },
                    netError = { detailUiModel.emitUIState(netError = it) },
                    success = { detailUiModel.emitUIState(success = it) },
                    empty = { detailUiModel.emitUIState(isEmpty = true) }
            )
        }
    }


    fun setTitleBar(type: Long, createUser: UgcCommonBarBean.CreateUser) {
//        日志帖子
        _titleBar.value = UgcTitleBarBean(createUser.userId, "${createUser.nikeName}${if (type == CommContent.TYPE_JOURNAL) "  发布的日志" else "  发布的帖子"}", createUser.avatarUrl, createUser.createTime, createUser.followed, false, createUser.score, userAuthType = createUser.authType)
    }

    fun updateTitleBar(isAlbum: Boolean) {
        _titleBar.value?.isAlbumTitle = isAlbum
    }


    /**
     * 判断帖子是否置顶
     */
    fun isTop(binderList: MutableList<MultiTypeBinder<*>>): Boolean {
        return binderList.any { it is UgcTitleBinder && it.titleBean.isTop }
    }

    /**
     * 帖子是否是加精
     */
    fun isEssence(binderList: MutableList<MultiTypeBinder<*>>): Boolean {
        return binderList.any { it is UgcTitleBinder && it.titleBean.isEssence }
    }

    /**
     * 帖子置顶获取取消成功后更新binder
     */
    fun updateTopOfUgcTitleBinder(mBinderList: MutableList<MultiTypeBinder<*>>, isTop: Boolean) {
        mBinderList.filter { it is UgcTitleBinder }.forEach {
            (it as UgcTitleBinder).titleBean.isTop = isTop
            it.notifyAdapterSelfChanged()
        }
    }

    /**
     * 帖子加精或取消加精后更新binder
     */
    fun updateEssenceOfUgcTitleBinder(binderList: MutableList<MultiTypeBinder<*>>, isEssence: Boolean) {
        binderList.filter { it is UgcTitleBinder }.forEach {
            (it as UgcTitleBinder).titleBean.isEssence = isEssence
            it.notifyAdapterSelfChanged()
        }
    }

    /**
     * 更新家族卡片
     */
    fun updateJoinFamilyBinder(binderList: MutableList<MultiTypeBinder<*>>, status: Long) {
        binderList.filter { it is FamilyBinder }.map {
            (it as FamilyBinder).bean.setJoinFamilyStatus(status)
            it.notifyAdapterSelfChanged()
        }
    }

    /**
     * 更新投票比例
     */
    fun updatePkStatusData(isPositive: Boolean, mBinderList: MutableList<MultiTypeBinder<*>>) {
        mBinderList.filter { it is PkVoteDetailBinder }.forEach {
            val value = (it as PkVoteDetailBinder).bean
            value?.joinCount = (value?.joinCount ?: 0L) + 1L
            if (isPositive) {//投支持票
                value?.positionCount = (value?.positionCount ?: 0L) + 1L
            } else {
                value?.nagivationCount = (value?.nagivationCount ?: 0L) + 1L
            }
            value?.hasPk = true
            it.notifyAdapterSelfChanged()
        }
    }

    fun addPkComment(pkCommentBean: PKComentViewBean, isCommentPositive: Boolean, isFirst: Boolean = false, mBinderList: MutableList<MultiTypeBinder<*>>) {
        mBinderList.filter { it is PkCommentBinder }.forEach {
            (it as PkCommentBinder).addPkComment(pkCommentBean,isCommentPositive,isFirst)
        }

    }
}