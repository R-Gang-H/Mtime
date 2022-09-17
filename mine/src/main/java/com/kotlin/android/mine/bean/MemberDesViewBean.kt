package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mine.R
import com.kotlin.android.mtime.ktx.getString

/**
 * create by lushan on 2020/10/24
 * description: 会员中心 获取M豆说明
 */
data class MemberDesViewBean(var mBeanUrl: String = "",//什么是m豆
                             var showBigTitle: Boolean = false,//是否显示获取m豆大标题
                             var title: String = "",
                             var type: Long = 0L,//显示item类型
                             var desList: MutableList<String> = mutableListOf()//描述类型
) : ProguardRule {
    companion object{
        const val TYPE_BUY = 1L//去购票
        const val TYPE_COMMUNITY = 2L//去社区
        const val TYPE_GAME = 3L//去游戏
    }

    /**
     * 获取跳转按钮文案
     */
    fun getGotoContent():String{
        return when(type){
            TYPE_BUY-> getString(R.string.mine_member_goto_buy)//去购票
            TYPE_COMMUNITY-> getString(R.string.mine_member_goto_community)//去社区
            TYPE_GAME-> getString(R.string.mine_member_goto_game)//去游戏
            else-> getString(R.string.mine_member_goto_buy)

        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MemberDesViewBean

        if (mBeanUrl != other.mBeanUrl) return false
        if (showBigTitle != other.showBigTitle) return false
        if (title != other.title) return false
        if (type != other.type) return false
        if (desList != other.desList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mBeanUrl.hashCode()
        result = 31 * result + showBigTitle.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + desList.hashCode()
        return result
    }
}