package com.kotlin.android.mine.ui.authentication.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.mine.CheckAuthPermission
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.AuthenPrivilegeViewBean
import com.kotlin.android.mine.bean.AuthenPrivilegeViewBean.Companion.PRIVILEGE_TYPE_BIAOZHI
import com.kotlin.android.mine.bean.AuthenPrivilegeViewBean.Companion.PRIVILEGE_TYPE_MORE
import com.kotlin.android.mine.bean.AuthenPrivilegeViewBean.Companion.PRIVILEGE_TYPE_RECOMMEND
import com.kotlin.android.mine.bean.AuthenticatonCardViewBean
import com.kotlin.android.mine.repoistory.AuthHomeRepository
import com.kotlin.android.mtime.ktx.getString
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/9/7
 * description:身份认证 卡片
 */
class AuthenticationViewModel : BaseViewModel() {

    private val repo by lazy{
        AuthHomeRepository()
    }

//    影评人认证校验接口
    private val authPermissionUIModel = BaseUIModel<CheckAuthPermission>()
     val authPermissionState = authPermissionUIModel.uiState

    //    身份认证上方卡片
    private val _cardDataListState = MutableLiveData<MutableList<AuthenticatonCardViewBean>>()

    val cardDataListState: LiveData<MutableList<AuthenticatonCardViewBean>>
        get() = _cardDataListState

    private val _authPrivilegeListState = MutableLiveData<MutableList<AuthenPrivilegeViewBean>>()

    //    身份认证 下方特权
    val authPrivilegeListState: LiveData<MutableList<AuthenPrivilegeViewBean>>
        get() = _authPrivilegeListState

    fun initData() {
        val cardList = mutableListOf<AuthenticatonCardViewBean>()
        cardList.add(AuthenticatonCardViewBean(AuthenticatonCardViewBean.TYPE_MOVIE_PERSON, 0L))
        cardList.add(AuthenticatonCardViewBean(AuthenticatonCardViewBean.TYPE_REVIEW_PERSON, 0L))
        cardList.add(AuthenticatonCardViewBean(AuthenticatonCardViewBean.TYPE_ORGANIZATION, 0L))
        _cardDataListState.value = cardList

        val privilegeList = mutableListOf<AuthenPrivilegeViewBean>()
        privilegeList.add(AuthenPrivilegeViewBean(getString(R.string.mine_authen_privilege_sole_symbol), getString(R.string.mine_authen_privilege_sole_symbol_des),PRIVILEGE_TYPE_BIAOZHI))
        privilegeList.add(AuthenPrivilegeViewBean(getString(R.string.mine_authen_privilege_recommend), getString(R.string.mine_authen_privilege_recommend_des),PRIVILEGE_TYPE_RECOMMEND))
        privilegeList.add(AuthenPrivilegeViewBean(getString(R.string.mine_authen_privilege_more_privilege), getString(R.string.mine_authen_privilege_more_privilege_des),PRIVILEGE_TYPE_MORE))
        _authPrivilegeListState.value = privilegeList
    }

    /**
     * 验证影评申请人是否符合条件
     */
    fun checkPermission(){
        viewModelScope.launch(main) {
            authPermissionUIModel.showLoading = true
            val result = withOnIO {
                repo.checkAuthPersmission()
            }
            authPermissionUIModel.checkResultAndEmitUIState(result)
        }
    }
}