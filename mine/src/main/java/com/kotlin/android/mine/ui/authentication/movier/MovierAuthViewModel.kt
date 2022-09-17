package com.kotlin.android.mine.ui.authentication.movier

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.mine.CheckAuthPermission
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.mine.bean.AuthenticatonCardViewBean
import com.kotlin.android.mine.repoistory.AuthHomeRepository
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/9/9
 * description:身份认证-电影人认证
 */
class MovierAuthViewModel :BaseViewModel() {
    private val repo by lazy{
        AuthHomeRepository()
    }

//    电影角色
    private val movieRoleModel = BaseUIModel<MutableList<String>>()
    val movieRoleState = movieRoleModel.uiState

//    提交电影人认证信息
    private val saveAuthUIModel = BaseUIModel<CheckAuthPermission>()
    val saveAuthState = saveAuthUIModel.uiState


    /**
     * 获取影片角色信息
     */
    fun getRoleList(){
        viewModelScope.launch(main){
            movieRoleModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.getAuthRoleList()
            }
            movieRoleModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 电影人认证
     *
     */
    fun saveAuth(name:String, idCard:String, mobile:String, email:String, idCardPhoto: PhotoInfo?, workPhotoInfo: PhotoInfo?, tags:String, authrolelist:MutableList<String>){
        viewModelScope.launch(main){
            saveAuthUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.saveAuth(AuthenticatonCardViewBean.TYPE_MOVIE_PERSON,name,mobile,email,idCard, mutableListOf(),tags,authrolelist,idCardPhoto,workPhotoInfo,null,null)
            }

            saveAuthUIModel.checkResultAndEmitUIState(result)
        }
    }


}