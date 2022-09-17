package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mine.R
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString

/**
 * create by lushan on 2020/9/7
 * description: 身份认证 卡片ViewBean
 */
data class AuthenticatonCardViewBean(var type: Long = 0L,//身份认证类型 2.影评人认证 3.电影人认证 4.机构认证
                                     var authStatus: Long = 0L//认证的状态 需要认证，已经认证过
) : ProguardRule {
    companion object {
        const val TYPE_REVIEW_PERSON = 2L//影评人认证
        const val TYPE_MOVIE_PERSON = 3L//电影人
        const val TYPE_ORGANIZATION = 4L//机构认证
    }

    fun getAuthBtnColor(): Int {
        return when (type) {
            TYPE_REVIEW_PERSON -> getColor(R.color.color_20a0da)
            TYPE_MOVIE_PERSON -> getColor(R.color.color_19b3c2)
            TYPE_ORGANIZATION -> getColor(R.color.color_feb12a)
            else -> getColor(R.color.color_20a0da)
        }
    }

    /**
     * 获取标题类型
     */
    fun getAuthTitle(): String {
        return when (type) {
            TYPE_REVIEW_PERSON -> getString(R.string.mine_authen_reviewer)
            TYPE_MOVIE_PERSON -> getString(R.string.mine_authen_movier)
            TYPE_ORGANIZATION -> getString(R.string.mine_authen_orgnization)
            else -> getString(R.string.mine_authen_reviewer)
        }
    }

    /**
     * 获取标题描述
     */
    fun getAuthDes(): String {
        return when (type) {
            TYPE_REVIEW_PERSON -> getString(R.string.mine_authen_reviewer_des)
            TYPE_MOVIE_PERSON -> getString(R.string.mine_authen_movier_des)
            TYPE_ORGANIZATION -> getString(R.string.mine_authen_orgnization_des)
            else -> getString(R.string.mine_authen_reviewer_des)
        }
    }
}

/**
 * 特权item卡片
 */
data class AuthenPrivilegeViewBean(
        var title: String = "",
        var des: String = "",
        var type: Long = 0L//类型
) : ProguardRule {
    companion object {
        const val PRIVILEGE_TYPE_BIAOZHI = 1L//标志
        const val PRIVILEGE_TYPE_RECOMMEND = 2L//优先推荐
        const val PRIVILEGE_TYPE_MORE = 3L//更多
    }
}