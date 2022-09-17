package com.kotlin.android.image.component.ui

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.annotation.UploadImageType
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.entity.mine.AvatarEdit
import com.kotlin.android.app.data.entity.mine.UserCenterBgEdit
import com.kotlin.android.image.component.repository.ImageRepository

/**
 * 图片处理
 *
 * Created on 2020/12/21.
 *
 * @author o.s
 */
class ImageViewModel : BaseViewModel() {

    private val repo by lazy {
        ImageRepository()
    }

    private val uiModel by lazy { BaseUIModel<PhotoInfo>() }
    private val avatarUiModel by lazy { BaseUIModel<AvatarEdit>() }
    private val userCenterUiModel by lazy { BaseUIModel<UserCenterBgEdit>() }
    val uiState by lazy { uiModel.uiState }
    val avatarUiState by lazy { avatarUiModel.uiState }
    val userCenterUiState by lazy { userCenterUiModel.uiState }

    /**
     * 上传图片：
     * 2019年以后，为了简化使用，如果是全新开发的系统，将采用通用的ImageFileType=14，不再新增ImageFileType类型。
     * 不过，历史悠久的现存系统，仍然会沿用以前的ImageFileType参数，所以老代码一般无需改动。
     * 如果某业务发现上传的图片无法展示，请首先咨询后端同学此参数设置是否正确。
     */
    fun uploadImage(
            photo: PhotoInfo,
            @UploadImageType imageType: Long = CommConstant.IMAGE_UPLOAD_COMMON
    ) {
        call(uiModel) {
            repo.uploadImage(photo, imageType)
        }
    }

    /**
     * 更新头像
     */
    fun updateAvatar(fileName: String) {
        call(avatarUiModel) {
            repo.updateAvatar(fileName)
        }
    }

    /**
     * 更新背景图
     */
    fun updateUserCenterBg(fileName: String) {
        call(userCenterUiModel) {
            repo.updateUserCenterUrl(fileName)
        }
    }
}