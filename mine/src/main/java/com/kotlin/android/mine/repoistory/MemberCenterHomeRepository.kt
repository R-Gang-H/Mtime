package com.kotlin.android.mine.repoistory

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.member.ExchangeResult
import com.kotlin.android.app.data.entity.member.MemberExchangeList
import com.kotlin.android.app.data.entity.member.MemberHome

/**
 * create by lushan on 2020/11/11
 * description:会员中心首页
 */
class MemberCenterHomeRepository : BaseRepository() {

    /**
     * 获取会员中心首页数据
     */
    suspend fun getMemberCenterHome(): ApiResult<MemberHome> {
        return request { apiMTime.getMemberCenterHome() }
    }


    /**
     * 获取抽奖列表
     */
    suspend fun getExchangeList(): ApiResult<MemberExchangeList> {
        return request { apiMTime.getMemberLuckList() }
    }

    /**
     * 兑换商品
     */
    suspend fun exchangeGoods(configId: Long, mtimebQuantity: Long): ApiResult<ExchangeResult> {
        return request { apiMTime.exchangeGoods(configId, mtimebQuantity) }
    }
}