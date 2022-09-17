package com.kotlin.android.mine.ui.authentication.organization

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.app.api.download.DownloadManager
import com.kotlin.android.app.api.download.listener.DownloadListener
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.entity.mine.CheckAuthPermission
import com.kotlin.android.mine.bean.AuthenticatonCardViewBean
import com.kotlin.android.mine.repoistory.AuthHomeRepository
import com.kotlin.android.mtime.ktx.FileEnv
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/9/9
 * description:身份认证-机构认证
 */
class OrganizationAuthViewModel : BaseViewModel() {

    private val repo by lazy {
        AuthHomeRepository()
    }

    //    保存路径
    private val saveAuthUIModel = BaseUIModel<CheckAuthPermission>()
    val saveAuthState = saveAuthUIModel.uiState

    private val downloadFileUIModel = BaseUIModel<String>()
    val downloadFileState = downloadFileUIModel.uiState


    /**
     * 提交机构认证信息
     */
    fun saveAuth(name: String, idCard: String, bussessPhotoInfo: PhotoInfo?, authletterdataPhotoInfo: PhotoInfo?) {
        viewModelScope.launch(main) {
            saveAuthUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.saveAuth(AuthenticatonCardViewBean.TYPE_ORGANIZATION, name, "", "", idCard, mutableListOf(), "", mutableListOf(), null, null, authletterdataPhotoInfo, bussessPhotoInfo)
            }

            saveAuthUIModel.checkResultAndEmitUIState(result)
        }
    }


    fun downloadAuthFile(url: String) {
        val lastIndexOf = url.lastIndexOf(".")
        if (lastIndexOf < 0) {
            downloadFileUIModel.emitUIState(showLoading = false, error = "链接不可为空")
            return
        }
        downloadFileUIModel.emitUIState(showLoading = true)
        DownloadManager.download(url, "auth_${System.currentTimeMillis()}${url.substring(lastIndexOf)}", FileEnv.downloadImageDir,object :DownloadListener{
            override fun onProgress(progress: Int) {
//                "downloadAuthFile下载进度：$progress".e()
            }

            override fun onFailed(msg: String?) {
                downloadFileUIModel.emitUIState(error = "下载失败请稍后重试")
            }

            override fun onComplete(filePath: String) {
                downloadFileUIModel.emitUIState(success = filePath)
            }


        })

    }
}