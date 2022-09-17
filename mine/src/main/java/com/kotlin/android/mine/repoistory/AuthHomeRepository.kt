package com.kotlin.android.mine.repoistory

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.entity.mine.CheckAuthPermission
import com.kotlin.android.mine.bean.AuthenReviewBean
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * create by lushan on 2020/10/13
 * description: 认证接口
 */
class AuthHomeRepository : BaseRepository() {

    /**
     * 校验影评认真是否符合条件
     */
    suspend fun checkAuthPersmission(): ApiResult<CheckAuthPermission> {
        return request { apiMTime.checkAuthPermission() }
    }

    /**
     * 获取电影认证人角色列表
     */
    suspend fun getAuthRoleList(): ApiResult<MutableList<String>> {
        return request(
            converter = {
                it.roleList ?: mutableListOf()
            }
        ) {
            apiMTime.getAuthRoleList()
        }
    }

    /**
     * 获取我的长影评列表
     */
    suspend fun getMyLongCommentList(
        pageIndex: Long,
        pageSize: Long
    ): ApiResult<MutableList<AuthenReviewBean>> {
        return request(
            converter = { comment ->
                comment.userCommtentList?.map { AuthenReviewBean(it.commentId, it.title.orEmpty()) }
                    ?.toMutableList()
            }
        ) {
            apiMTime.getMovieMyComments(pageIndex, pageSize)
        }
    }

    /**
     * 保存时光媒体人认证信息
     */
    suspend fun saveAuth(
        authtype: Long,
        name: String,
        mobile: String,
        email: String,
        idcard: String,
        filmcommentlist: MutableList<Long>,
        tags: String,
        authrolelist: MutableList<String>,
        idcarddata: PhotoInfo?,
        workcarddata: PhotoInfo?,
        authletterdata: PhotoInfo?,
        businessData: PhotoInfo?
    ): ApiResult<CheckAuthPermission> {
        var idcarddataFilePart: MultipartBody.Part? = null//身份证附件
        idcarddata?.apply {
            val requestBody: RequestBody =
                File(this.path).asRequestBody("image/jpg".toMediaTypeOrNull())
            idcarddataFilePart =
                MultipartBody.Part.createFormData("idcarddata", this.path, requestBody)
        }

        var workcarddataFilePart: MultipartBody.Part? = null//工作证明附件
        workcarddata?.apply {
            val requestBody: RequestBody =
                File(this.path).asRequestBody("image/jpg".toMediaTypeOrNull())
            workcarddataFilePart =
                MultipartBody.Part.createFormData("workcarddata", this.path, requestBody)
        }

        var authletterdataFilePart: MultipartBody.Part? = null//机构附件
        authletterdata?.apply {
            val requestBody: RequestBody =
                File(this.path).asRequestBody("image/jpg".toMediaTypeOrNull())
            authletterdataFilePart =
                MultipartBody.Part.createFormData("authletterdata", this.path, requestBody)
        }

        var businessDataFilePart: MultipartBody.Part? = null//营业执照
        businessData?.apply {
            val requestBody: RequestBody =
                File(this.path).asRequestBody("image/jpg".toMediaTypeOrNull())
            businessDataFilePart =
                MultipartBody.Part.createFormData("businessData", this.path, requestBody)
        }
        val filmcommentListStr = filmcommentlist.joinToString(",", "", "")
        val authrolelistStr = authrolelist.joinToString(",", "", "")

        return request {
            apiMTime.saveAuth(
                authtype,
                name,
                mobile,
                email,
                idcard,
                filmcommentListStr,
                tags,
                authrolelistStr,
                idcarddataFilePart,
                workcarddataFilePart,
                authletterdataFilePart,
                businessDataFilePart
            )
        }
    }

}