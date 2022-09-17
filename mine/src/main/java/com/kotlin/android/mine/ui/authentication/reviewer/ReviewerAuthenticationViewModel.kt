package com.kotlin.android.mine.ui.authentication.reviewer

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.mine.CheckAuthPermission
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.mine.bean.AuthenReviewBean
import com.kotlin.android.mine.bean.AuthenticatonCardViewBean
import com.kotlin.android.mine.repoistory.AuthHomeRepository
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/9/8
 * description:影评人认证
 */
class ReviewerAuthenticationViewModel : BaseViewModel() {
    private val repo by lazy {
        AuthHomeRepository()
    }

    //    我的长影评
    private val myCommentListUIModel = BaseUIModel<MutableList<AuthenReviewBean>>()
    val myCommentListState = myCommentListUIModel.uiState

    //    保存认证消息
    private val saveAuthUIModel = BaseUIModel<CheckAuthPermission>()
    val saveAuthState = saveAuthUIModel.uiState

    fun getMyLongCommentList() {
        viewModelScope.launch(main) {
            myCommentListUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.getMyLongCommentList(1, 10)
            }

            myCommentListUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 提交影评人认证信息
     */
    fun saveAuth(name:String,idCard:String,commentIdList:MutableList<Long>,idCardPhotoInfo: PhotoInfo?){
        viewModelScope.launch(main){
            saveAuthUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.saveAuth(AuthenticatonCardViewBean.TYPE_REVIEW_PERSON,name,"","",idCard,commentIdList,"", mutableListOf(),idCardPhotoInfo,null,null,null)
            }

            saveAuthUIModel.checkResultAndEmitUIState(result)

        }
    }

}