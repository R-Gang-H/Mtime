package com.kotlin.android.community.ui.home

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.community.repository.CommunityHomeRepository
import com.kotlin.android.community.ui.home.adapter.JoinFamilyItemBinder
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.message.UnReadMessage
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/17
 */
class CommunityViewModel: BaseViewModel() {

    private val repo by lazy { CommunityHomeRepository() }

    private val mCommunityMyFamilyUIModel = BaseUIModel<List<MultiTypeBinder<*>>>()
    val uiState = mCommunityMyFamilyUIModel.uiState

    fun loadMyFamilyData() {
        viewModelScope.launch(main) {
            var result = if (!isLogin()) {
                ApiResult.Success(listOf(
                        // 未登录则显示加入item
                        JoinFamilyItemBinder()
                ))
            } else {
                withOnIO {
                    repo.loadMyFamily()
                }
            }

            // 请求错误显示加入item
            if (result is ApiResult.Error) {
                result = ApiResult.Success(listOf(
                        JoinFamilyItemBinder()
                ))
            }

            checkResult(result, error = {
                mCommunityMyFamilyUIModel.emitUIState(error = it)
            }, netError = {
                mCommunityMyFamilyUIModel.emitUIState(netError = it)
            }, success = {
                mCommunityMyFamilyUIModel.emitUIState(success = it)
            })
        }
    }
}