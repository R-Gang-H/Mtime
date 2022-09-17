package com.kotlin.android.mine.ui.member

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.member.ExchangeResult
import com.kotlin.android.app.data.entity.member.MemberExchangeList
import com.kotlin.android.app.data.entity.member.MemberHome
import com.kotlin.android.mine.bean.GoodsViewBean
import com.kotlin.android.mine.bean.MemberDesViewBean
import com.kotlin.android.mine.bean.MemberInfoViewBean
import com.kotlin.android.mine.binder.MemberDesBinder
import com.kotlin.android.mine.binder.MemberInfoBinder
import com.kotlin.android.mine.repoistory.MemberCenterHomeRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/10/24
 * description:会员中心
 */
class MemberCenterViewModel : BaseViewModel() {
    private val repo by lazy {
        MemberCenterHomeRepository()
    }

    private val memberCenterHomeUIModel = BaseUIModel<Pair<MemberHome?, MemberExchangeList?>>()
    val memberCenterHomeStatus = memberCenterHomeUIModel.uiState

//    兑换奖品
    private val exchangeGoodsUIModel = BaseUIModel<ExchangeResult>()
    val exchangeGoodsStatus = exchangeGoodsUIModel.uiState


    fun loadHomeCenterData(showLoading:Boolean = true) {
        viewModelScope.launch(main) {
            memberCenterHomeUIModel.emitUIState(showLoading = showLoading)

            withOnIO {
                var memberCenterHome = repo.getMemberCenterHome()

                var exchangeList = repo.getExchangeList()
                if (memberCenterHome is ApiResult.Success) {
                    var exchangeListBean: MemberExchangeList? = null
                    if (exchangeList is ApiResult.Success) {
                        if (exchangeList.data?.list?.isNotEmpty() == true) {
                            exchangeListBean = exchangeList.data
                        }
                    }

                    withOnUI {
                        //交给上层处理
                        checkResult(memberCenterHome, error = { memberCenterHomeUIModel.emitUIState(error = it) },
                                netError = { memberCenterHomeUIModel.emitUIState(netError = it) },
                                success = { memberCenterHomeUIModel.emitUIState(success = Pair(memberCenterHome.data, exchangeListBean)) },
                                empty = { memberCenterHomeUIModel.emitUIState(isEmpty = true) })
                    }

                } else {//处理失败
                    withOnUI {
                        checkResult(memberCenterHome, error = { memberCenterHomeUIModel.emitUIState(error = it) },
                                netError = { memberCenterHomeUIModel.emitUIState(netError = it) },
                                empty = { memberCenterHomeUIModel.emitUIState(isEmpty = true) })
                    }
                }
            }

        }
    }


    fun getBinderList(pair: Pair<MemberHome?, MemberExchangeList?>): MutableList<MultiTypeBinder<*>> {

        var memberHome = pair.first
        var exchangeList = pair.second

        memberHome ?: return mutableListOf()

        val binderList = mutableListOf<MultiTypeBinder<*>>()
        binderList.add(MemberInfoBinder(getMemberInfoBean(memberHome), getGoodsList(exchangeList)))
        binderList.addAll(getMemberDesBinderList(memberHome))
        return binderList


    }

    private fun getGoodsList(bean: MemberExchangeList?): MutableList<GoodsViewBean> {
        bean ?: return mutableListOf()
        var list = bean.list
        list ?: return mutableListOf()
        var isAllExplainEmpty = list.all { TextUtils.isEmpty(it.explain) }

        return list.map {
            GoodsViewBean(
                    id = it.configId,
                    pic = it.appImage.orEmpty(),
                    name = it.exchangeName.orEmpty(),
                    des = it.explain.orEmpty(),
                    mNeedNum = it.mtimebPrice,
                    isHiddenDes = isAllExplainEmpty
            )
        }.toMutableList()

    }

    /**
     * 获取会员中心用户相关数据
     */
    private fun getMemberInfoBean(memberHome: MemberHome): MemberInfoViewBean {
        return MemberInfoViewBean(memberHome.personal?.name.orEmpty(), memberHome.personal?.headImg.orEmpty(), memberHome.personal?.level?:0L, totalMBeanNum = memberHome.growUp?.mtimeCoin
                ?: 0L, mBeanUrl = memberHome.growUp?.mtimeCoinUrl.orEmpty(), vipDesUrl = memberHome.personal?.rightUrl.orEmpty())
    }

    private fun getMemberDesBinderList(memberHome: MemberHome): MutableList<MultiTypeBinder<*>> {
        val list = mutableListOf<MultiTypeBinder<*>>()
        val moreGroupUrl = memberHome.growUp?.mtimeCoinUrl.orEmpty()//更多方法
        list.add(MemberDesBinder(MemberDesViewBean(mBeanUrl = moreGroupUrl, showBigTitle = true, title = "购买电影票", type = MemberDesViewBean.TYPE_BUY, desList = mutableListOf("多买多赚", "1元奖励1M豆"))))
        list.add(MemberDesBinder(MemberDesViewBean(mBeanUrl = moreGroupUrl, showBigTitle = false, title = "参与家族互动", type = MemberDesViewBean.TYPE_COMMUNITY, desList = mutableListOf("多写多享", "找到你的M+"))))
        list.add(MemberDesBinder(MemberDesViewBean(mBeanUrl = moreGroupUrl, showBigTitle = false, title = "参与卡片大富翁", type = MemberDesViewBean.TYPE_GAME, desList = mutableListOf("多玩多得", "玩卡片的都是大富翁"))))
        return list
    }


    fun exchangeGoods(configId:Long,mtimebQuantity:Long){
        viewModelScope.launch(main){
            exchangeGoodsUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.exchangeGoods(configId, mtimebQuantity)
            }

            exchangeGoodsUIModel.checkResultAndEmitUIState(result)
        }
    }

}