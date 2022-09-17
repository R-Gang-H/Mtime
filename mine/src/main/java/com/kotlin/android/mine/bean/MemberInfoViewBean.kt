package com.kotlin.android.mine.bean

import android.graphics.drawable.Drawable
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mine.*
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.mtime.ktx.getString

/**
 * create by lushan on 2020/10/24
 * description:会员中心上方用户及m豆信息
 */
data class MemberInfoViewBean(
        var userName: String = "",//昵称
        var userPic: String = "",//用户头像
        var vipLevel: Long = 0L,//会员等级
        var totalMBeanNum: Long = 0L,//总m豆数量
        var mBeanUrl: String = "",//什么是M豆h5
        var vipDesUrl: String = "",//会员等级说明h5
) : ProguardRule {

    /**
     * 会员中心 用户等级图标
     */
    fun getVipLevelDrawable():Drawable?{
        return when (vipLevel) {
            USER_LEVEL_ZHONGJI -> {//中级
                getDrawable(R.drawable.ic_zhongji)
            }
            USER_LEVEL_GAOJI -> {//高级
                getDrawable(R.drawable.ic_gaoji)
            }
            USER_LEVEL_ZISHEN -> {//资深
                getDrawable(R.drawable.ic_zishen)
            }
            USER_LEVEL_DIANTANG -> {//殿堂
                getDrawable(R.drawable.ic_diantang)
            }
            else -> {//入门
                getDrawable(R.drawable.ic_rumen)
            }
        }
    }

    /**
     * 获取vip等级对应文案
     */
    fun getVipLevelContent():String{
        return when (vipLevel) {
            USER_LEVEL_RUMEN -> getString(R.string.rumen)
            USER_LEVEL_ZHONGJI -> getString(R.string.zhongji)
            USER_LEVEL_GAOJI -> getString(R.string.gaoji)
            USER_LEVEL_ZISHEN -> getString(R.string.zishen)
            USER_LEVEL_DIANTANG -> getString(R.string.diantang)
            else -> getString(R.string.rumen)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MemberInfoViewBean

        if (userName != other.userName) return false
        if (userPic != other.userPic) return false
        if (vipLevel != other.vipLevel) return false
        if (totalMBeanNum != other.totalMBeanNum) return false
        if (mBeanUrl != other.mBeanUrl) return false
        if (vipDesUrl != other.vipDesUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userName.hashCode()
        result = 31 * result + userPic.hashCode()
        result = 31 * result + vipLevel.hashCode()
        result = 31 * result + totalMBeanNum.hashCode()
        result = 31 * result + mBeanUrl.hashCode()
        result = 31 * result + vipDesUrl.hashCode()
        return result
    }


}